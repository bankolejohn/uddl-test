package com.epistimis.uddl;

import static java.util.Objects.requireNonNull;

import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.xtext.conversion.impl.QualifiedNameValueConverter;
import org.eclipse.xtext.naming.DefaultDeclarativeQualifiedNameProvider;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
//import org.eclipse.xtext.xbase.scoping.XbaseQualifiedNameProvider;
import org.eclipse.emf.ecore.util.EContentsEList;

import com.epistimis.uddl.util.IndexUtilities;
import com.epistimis.uddl.uddl.ConceptualCharacteristic;
import com.epistimis.uddl.uddl.ConceptualCompositeQuery;
import com.epistimis.uddl.uddl.ConceptualEntity;
import com.epistimis.uddl.uddl.ConceptualQueryComposition;
import com.epistimis.uddl.uddl.LogicalCharacteristic;
import com.epistimis.uddl.uddl.LogicalCompositeQuery;
import com.epistimis.uddl.uddl.LogicalEntity;
import com.epistimis.uddl.uddl.LogicalMeasurement;
import com.epistimis.uddl.uddl.LogicalMeasurementAttribute;
import com.epistimis.uddl.uddl.LogicalQueryComposition;
import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformCompositeQuery;
import com.epistimis.uddl.uddl.PlatformEntity;
import com.epistimis.uddl.uddl.PlatformQueryComposition;
import com.epistimis.uddl.uddl.PlatformStruct;
import com.epistimis.uddl.uddl.PlatformStructMember;
import com.google.inject.Inject;

public class UddlQNP  extends  DefaultDeclarativeQualifiedNameProvider  { // XbaseQualifiedNameProvider
	
	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

	@Inject IndexUtilities ndxUtils;
	// Because the base class one is private
	@Inject protected IQualifiedNameConverter converter = new IQualifiedNameConverter.DefaultImpl();
	
	@Inject protected QualifiedNameValueConverter qualifiedNameValueConverter = new QualifiedNameValueConverter();
	
	public QualifiedNameValueConverter getQualifiedNameValueConverter() { return qualifiedNameValueConverter; }
	
	/**
	 * Check for null name - and log an error if it occurs
	 * @param <T>
	 * @param name
	 * @param obj
	 */
	private <T extends EObject> void maybeLogAndThrow(QualifiedName name, T obj) {
		if (name == null) {
			String msg = "getFullyQualifiedName not properly implemented for: "+ obj.getClass().getCanonicalName();
			logger.error(msg);
			throw new NullPointerException(msg);
		}
	}
	/**
	 * Determine the QualifiedName of obj relative to ctx
	 * @param obj
	 * @param ctx
	 * @return
	 */
	public <T extends EObject,U extends EObject> QualifiedName relativeQualifiedName(@NonNull T obj, U ctx) {
		requireNonNull(obj,"You must specify the EObject you want an RQN for");
		T o =  IndexUtilities.unProxiedEObject(obj,ctx);
		QualifiedName oName = getFullyQualifiedName(o);
		maybeLogAndThrow(oName,o);
		if (ctx == null) {
			return oName;
		}
		// TODO: Is it possibly this one is a proxy as well?
		U c =  IndexUtilities.unProxiedEObject(ctx,obj);
		QualifiedName ctxName = getFullyQualifiedName(c);
		maybeLogAndThrow(ctxName,c);

		int skipSegs = -1;
		// If we get here, we didn't throw, so neither name is null
		int maxSegsToCompare = Math.min(oName.getSegmentCount(), ctxName.getSegmentCount());
		for (int i = 0; i < maxSegsToCompare; i++) {
			if (!oName.getSegment(i).equals(ctxName.getSegment(i))) {
				skipSegs = i;
				break;
			}
		}			
		if (skipSegs > 0) {
			// Return only the name back to the common ancestor 
			return oName.skipFirst(skipSegs);
		} else {
			return oName;
		}
	}

	/**
	 * Return the QN of the object, potentially shortened by the specified prefix
	 * @param obj the object whose QN is to be returned. If the prefix matches the entire
	 * name, then return only the last segment. If the prefix ends with a wildcard,
	 * then match all prior segments.
	 * @param qnPrefix a prefix that may match part of the name of this object
	 * @return A Qualified name
	 */
	public QualifiedName minimalQualifiedName(EObject obj, String qnPrefix) {
		QualifiedName oName = getFullyQualifiedName(obj);
		String prefix2use = qnPrefix;
		if (qnPrefix.lastIndexOf('*') == qnPrefix.length()-1) {
			prefix2use = qnPrefix.substring(0,qnPrefix.length()-2);
		}
		QualifiedName testQN = converter.toQualifiedName(prefix2use);
		
		int ndx = 0;
		for (String seg: testQN.getSegments()) {
			// If there is a match failure at any point, return the entire oName
			if (!oName.getSegment(ndx).equals(seg)) {
				return oName;
			}
			ndx+=1;
		}
		// If we get here, we matched all the testQN segments.
		int testSegCnt = testQN.getSegmentCount();
		if (oName.getSegmentCount() == testSegCnt) {
			testSegCnt--; // reduce count by 1 so we return the last segment
		}
		return oName.skipFirst(testSegCnt);
		
	}

	/**
	 * Per this (https://www.eclipse.org/forums/index.php?t=msg&th=1084491&goto=1776641&)
	 * we can't use cross references in a QNP. Instead, we can grab the actual text
	 * of the cross reference using NodeModelUtils (https://archive.eclipse.org/modeling/tmf/xtext/javadoc/2.3/org/eclipse/xtext/nodemodel/util/NodeModelUtils.html)
	 * (which is what we want anyway).
	 * 
	 * Note this is likely to be a QN - and we need the entire thing to make sure we don't have name
	 * collisions.
	 * 
	 * NOTE also: When using a QN as a node in a larger qualified name, we can't use it in its default
	 * format because it will include '.' separators that would make it look like multiple segments.
	 * That means we need to convert the entire thing into a string that uses a different separator.
	 * Ideally, the replacement separator should not be something that would otherwise be used in 
	 * a single node (e.g. '_') because that could result in name collisions.
	 */
	public static String getReferenceAsString(EObject obj, String featureName) {
		EStructuralFeature refFeature = obj.eClass().getEStructuralFeature(featureName);
		// Should only be 1 node
		List<INode> nodes = NodeModelUtils.findNodesForFeature(obj, refFeature);
		INode node = nodes.get(0);
		return node.getText().trim();
	}

	public QualifiedName getReferenceAsQN(EObject obj, String featureName) {
		String qnString = getReferenceAsString(obj,featureName);

		// Since the string may itself be a qualified name, we need to parse it into segments
		QualifiedName refQN = converter.toQualifiedName(qnString);
		return refQN;
	}

	/**
	 * Return the shortest QN that will work as a valid reference, taking
	 * into account all the imported namespaces in the resource where the reference
	 * will go.
	 * 
	 * @param ref The object to which we want the reference
	 * @param ctx The context object - where the reference will go
	 * @return The reference QualifiedName
	 */
	public QualifiedName minimalReferenceQN(EObject ref, EObject ctx) {
		// A minimal reference can be created based on the RQN - and then we can look at
		// the imports
		// in the Resource containing the context object and shorten it further from
		// there.
		QualifiedName result = relativeQualifiedName(ref, ctx);

		Resource res = ctx.eResource();
		EList<EObject> content = res.getContents();
		// Get the root instance, then all the includes. The challenge here is that the
		// type
		// of the root object can differ depending on the file type. So we need to query
		// for the includes using
		// AQL
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("self", content.get(0));

		// This works because all grammars use the same 'includes' feature name in the top level/ 
		// starter rule.
		Collection<EObject> importedNamespaces = ndxUtils.processAQL(res.getResourceSet(), variables,
				"self.eGet('includes').eGet('importedNamespace')");

		// Now get the 'importedNamespace' from each, remove the wildcard (if it's
		// there) and convert the remainder
		// to a QualifiedName. Then compare to the rqn to see if we can shorten the RQN.
		for (Object o : importedNamespaces) {
			String str = o.toString();
			QualifiedName testQN = minimalQualifiedName(ref, str);
			if (testQN.getSegmentCount() < result.getSegmentCount()) {
				result = testQN;
			}
		}
		return result;
	}

	/**
	 * Return the shortest QN as string that will work as a valid reference, taking
	 * into account all the imported namespaces in the resource where the reference
	 * will go.
	 * 
	 * @param ref The object to which we want the reference
	 * @param ctx The context object - where the reference will go
	 * @return The reference string
	 */
	public String minimalReferenceString(EObject ref, EObject ctx) {
		QualifiedName qn = minimalReferenceQN(ref,ctx);
		return qn.toString();
	}

	/**
	 * Do these two QNs have a sequence of matching segments?
	 * @param aqn The first QualifiedName
	 * @param bqn The second QualifiedName
	 * @return true if the shorter of the two names completely matches a sequence of segments
	 * somewhere in the longer QN.  Note that the ignored segments in the longer QN could be
	 * at the beginning, at the end, or some of both.
	 */
	public boolean partialMatch(QualifiedName aqn, QualifiedName bqn) {
		if (aqn == null || bqn == null) {
			// For some reason this didn't resolve properly? In any case, it can't match
			return false;
		}
		int diff = aqn.getSegmentCount() - bqn.getSegmentCount();
		QualifiedName longer = aqn;
		QualifiedName shorter = bqn;
		if (diff < 0) {
			// swap the QNs
			longer = bqn;
			shorter = aqn;
			diff = -diff; 
		}
		if (diff >= 0) {
			// The test name may be an RQN - so skip as needed on the aqn - but it could also be that the 
			// test name is higher in the taxonomy - or both
			boolean found = false;
			int i = 0;
			// exit the loop if we find it or if we are done with the count
			while (!found && ( i < diff)) {
				if (longer.getSegment(i).equalsIgnoreCase(shorter.getFirstSegment())) {
					boolean partialFind = true;
					// We've found the start - keep going with the rest
					for (int j = i+1; j < shorter.getSegmentCount(); j++) {
						if (!longer.getSegment(j).equalsIgnoreCase(shorter.getSegment(j-i))) {
							partialFind = false;
							break;
						}
					}
					found = partialFind;
				}
				i++;
			}
			return found;
		} else {
			return false; // it can't possibly match
		}
	
	}

	/**
	 * Containers with multiple values must create names that include indices to make the names unique. This
	 * is a general approach to do that.
	 * @param obj The object we need the index name for
	 * @param elemList The list containing the object.
	 * @return A string with the container name + the index, or null if obj isn't in the list
	 */
	protected <T extends EObject> String containerIndexAsString(T obj) { //,EObjectContainmentEList<T> elemList ) {
		// TODO: Is it possible to have the same data flowing over multiple endpoints?
		// If so, we then need to using indexing into the input list as part of the name
		EStructuralFeature feature = obj.eContainingFeature();
		EObject container = obj.eContainer();
		Object content = container.eGet(feature);
		try {
			BasicEList<T> list = (BasicEList<T>)content;
			int ndx = list.indexOf(obj); // returns -1 if not found in the list
			if (ndx == -1) {
				return null; // This should never happen
			}
			return  feature.getName()+Integer.toString(ndx);
			
		} catch (ClassCastException excp) {
			// Must be single valued
			return feature.getName(); // single valued
		}
//		EContentsEList<EObject>contents = (EContentsEList<EObject>) feature.eContents();
		
	}

	/* Conceptual */
	public  QualifiedName qualifiedName(ConceptualCharacteristic obj) {
		ConceptualEntity ce = (ConceptualEntity) obj.eContainer();

		return getFullyQualifiedName(ce).append(obj.getRolename());	
	}

	public  QualifiedName qualifiedName(ConceptualQueryComposition obj) {
		ConceptualCompositeQuery ce = (ConceptualCompositeQuery) obj.eContainer();
		return getFullyQualifiedName(ce).append(obj.getRolename());	
	}
	
	/* Logical */
	public  QualifiedName qualifiedName(LogicalCharacteristic obj) {
		LogicalEntity ce = (LogicalEntity) obj.eContainer();
		return getFullyQualifiedName(ce).append(obj.getRolename());	
	}

	public  QualifiedName qualifiedName(LogicalMeasurementAttribute obj) {
		LogicalMeasurement ce = (LogicalMeasurement) obj.eContainer();
		return getFullyQualifiedName(ce).append(obj.getRolename());	
	}

	public  QualifiedName qualifiedName(LogicalQueryComposition obj) {
		LogicalCompositeQuery ce = (LogicalCompositeQuery) obj.eContainer();
		return getFullyQualifiedName(ce).append(obj.getRolename());	
	}
		
	/* Platform */
	public  QualifiedName qualifiedName(PlatformCharacteristic obj) {
		PlatformEntity ce = (PlatformEntity) obj.eContainer();
		return getFullyQualifiedName(ce).append(obj.getRolename());	
	}
	
	public  QualifiedName qualifiedName(PlatformQueryComposition obj) {
		PlatformCompositeQuery ce = (PlatformCompositeQuery) obj.eContainer();
		return getFullyQualifiedName(ce).append(obj.getRolename());	
	}

	public  QualifiedName qualifiedName(PlatformStructMember obj) {
		PlatformStruct ce = (PlatformStruct) obj.eContainer();
		return getFullyQualifiedName(ce).append(obj.getRolename());	
//		return QualifiedName.create(ce.getName(),obj.getRolename());		
	}
	

}
