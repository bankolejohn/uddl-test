package com.epistimis.uddl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
//import org.eclipse.xtext.xbase.lib.IteratorExtensions;

import com.epistimis.uddl.exceptions.NameCollisionException;
import com.epistimis.uddl.exceptions.NamedObjectNotFoundException;
import com.epistimis.uddl.util.IndexUtilities;
import com.epistimis.uddl.uddl.Taxonomy;
import com.epistimis.uddl.util.NavigationUtilities;
import com.google.inject.Inject;

/**
 * This generic base class is for when we want something more than an enum but
 * less than a full class system. Taxonomies that specialize this processor
 * consist of four grammar rules. In Uddl.xtext, see these rules as an example:
 * LogicalEnumeratedBase: LogicalEnumeratedSet | LogicalEnumerationLabel |
 * LogicalEnumerated
 * 
 * @param <Base> This is the type returned by the base rule (by default spelled
 *               the same as the base rule name)
 */
public abstract class TaxonomyProcessor<Base extends Taxonomy> {
	@Inject
	IndexUtilities ndxUtil;

	@Inject
	UddlQNP qnp;

	@Inject
	IQualifiedNameConverter qnc;

	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
	

	public TaxonomyProcessor() {
		// TODO Auto-generated constructor stub
	}

	public static String DFLT_QN_DELIMITER = "\\.";

	public enum TriBool {
		FALSE, SOMETIMES, TRUE, UNNKNOWN
	};

	abstract public EClass getBaseMetaClass();
	abstract public Map<String, Base> getCache();

	public boolean isCastableToBase(EObject obj) {
		return getBaseType().isInstance(obj);
	}
	
	public String getBaseName(EObject obj) {
		return qnp.getFullyQualifiedName(obj).getLastSegment();
	}

	/**
	 * For generic classes, I sometimes need to know the type parameters. This
	 * general method gets them for me
	 * 
	 * @param ndx The 0-based index of the type parameter I want
	 * @return The Class instance of the type
	 */
	public Class<?> returnedTypeParameter(int ndx) {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<?>) parameterizedType.getActualTypeArguments()[ndx];
	}

	/**
	 * 
	 * @return The taxonomy base class underpinning the specializing type
	 */
	public Class<?> getBaseType() {
		return returnedTypeParameter(0);
	}

	/**
	 * Is the test value anywhere in the ancestry of start. In other words, is
	 * 'start' contained in the 'test' hierarchy?
	 * 
	 * If this returns true, it also means that 'start' is a valid value for the
	 * 'test' label (if you treat 'test' as defining a set of values, then 'start'
	 * can be one of them).
	 * 
	 * NOTE: Because these are EObjects with names, we compare qualified names,
	 * which may be relative	 * 
	 * @param start
	 * @param test
	 * @return
	 */
	public boolean containedIn(@NonNull Base start, @NonNull String test) {
		return containedIn(start,test,qnp);
	}

	/**
	 * Is the test value anywhere in the ancestry of start. In other words, is
	 * 'start' contained in the 'test' hierarchy?
	 * 
	 * If this returns true, it also means that 'start' is a valid value for the
	 * 'test' label (if you treat 'test' as defining a set of values, then 'start'
	 * can be one of them).
	 * 
	 * NOTE: Because these are EObjects with names, we compare qualified names,
	 * which may be relative	 * 
	 * @param start
	 * @param test
	 * @return
	 */
	public boolean containedIn(@NonNull Base start, @NonNull QualifiedName testName) {
		return containedIn(start,testName,qnp);
	}

	/**
	 * Is the test value anywhere in the ancestry of start. In other words, is
	 * 'start' contained in the 'test' hierarchy?
	 * 
	 * If this returns true, it also means that 'start' is a valid value for the
	 * 'test' label (if you treat 'test' as defining a set of values, then 'start'
	 * can be one of them.
	 * 
	 * NOTE: Because these are EObjects with names, we compare qualified names,
	 * which may be relative
	 * 
	 * This version can be used if you want a different QNP
	 * 
	 * @param start
	 * @param test
	 * @return
	 */
	public boolean containedIn(@NonNull Base start, @NonNull String test, @NonNull UddlQNP qnp) {
		QualifiedName testName = qnc.toQualifiedName(test);
		return containedIn(start,testName,qnp);
	}

	/**
	 * Is the test value anywhere in the ancestry of start. In other words, is
	 * 'start' contained in the 'test' hierarchy?
	 * 
	 * If this returns true, it also means that 'start' is a valid value for the
	 * 'test' label (if you treat 'test' as defining a set of values, then 'start'
	 * can be one of them.
	 * 
	 * NOTE: Because these are EObjects with names, we compare qualified names,
	 * which may be relative
	 * 
	 * This version can be used if you want a different QNP
	 * 
	 * @param start
	 * @param test
	 * @return
	 */
	public boolean containedIn(@NonNull Base start, @NonNull QualifiedName testName, @NonNull UddlQNP qnp) {
		Collection<Base> ancestors = collectAncestors(start);
		return ancestors.stream().anyMatch(a -> {
			QualifiedName aqn = qnp.getFullyQualifiedName(a);
			return qnp.partialMatch(aqn, testName);
		});
	}

	/**
	 * Standardized error message for use when the invalidValue is not in the
	 * taxonomic hierarchy starting at the container
	 * 
	 * @param container
	 * @param invalidValue
	 */
	public static String msgInvalidValue(String container, String invalidValue) {
		String msg = MessageFormat.format("Container {0} contains an invalid value: {1}", container, invalidValue);
		logger.error(msg);
		return msg;
	}

	/**
	 * Look up the object with the specified name, as visible from the context
	 * instance (In case we get a RQN and there is more than one)
	 * 
	 * @param name
	 * @return The found Object. If no object found, throws NamedObjectNotFoundException. Throws
	 * 		NameCollisionException if multiple found with specified name (e.g. name is ambiguous)
	 */
	@SuppressWarnings("unchecked") // isCastableToBase ensures the cast will work
	public Base getObjectForName(String name, EObject context) throws NamedObjectNotFoundException, NameCollisionException {
			
			EObject inst = ndxUtil.getUniqueObjectForName(context, getBaseMetaClass(), name);
			if (inst == null) {
				return null;
			}
			if (isCastableToBase(inst)) {
				return (Base) inst;
			}
	
			/** If we get here, this isn't a purpose */
			msgInvalidValue(name, inst.toString());
			return null;
	}

	/**
	 * Get a list of Objects, one per name. Will never be null or contain nulls but
	 * the list itself may be empty.
	 * 
	 * @param names
	 * @return a (possibly empty) list of objects. If no object found for a name, it is ignored. Throws
	 * 		NameCollisionException if multiple found with specified name (e.g. name is ambiguous)
	 */
	public List<Base> getObjectsForNames(List<String> names, EObject context)  throws NameCollisionException{
		List<Base> result = new ArrayList<Base>();
		for (String n : names) {
			try {
				Base p = getObjectForName(n, context);
				if (p != null) {
					result.add(p);
				}
			} catch (NamedObjectNotFoundException excp) {
				// Just ignore it
			}

		}
		return result;
	}

	/**
	 * From start, walk up containment hierarchy. This needs the
	 * 
	 * @param start    The starting point in the taxonomy
	 * @param realType The base type of this taxonomy. Needed because eContainment
	 *                 can continue walking up through unrelated types. We need to
	 *                 stop when we exit this type.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Base> collectAncestors(Base start) {
		Class<?> realType = getBaseType();
		List<Base> ancestors = new ArrayList<>();
		EObject current = start;
		while ((current != null) && realType.isInstance(current)) {
			ancestors.add((Base) current);
			current = current.eContainer();
		}

		return ancestors;
	}

	/**
	 * When we want to check to see if an invariant affects something related to a
	 * taxonomy, we need to check for invariants associated with the taxonomy member
	 * under consideration. However, because taxonomies are defined & used
	 * hierarchically, we also need to consider:
	 * 
	 * A) The entire containment hierarchy for the specified element - because
	 * 'this' element 'is-a' element for each of its containers in that taxonomy.
	 * Because each taxonomy element specializes its containers, every invariant
	 * that applies to one of its containers necessarily applies to it as well.
	 * 
	 * B) Its entire contents (everything contained within the element under
	 * consideration) - because any code used for 'this' element could be used for
	 * any of its specializing elements. Since invariants / constraints must be true
	 * for the specified element, they must be true for all instances of that
	 * element, which include all specializations of that element. A) differs from
	 * B) in that A) is a guaranteed problem, whereas B) could be a problem.
	 * 
	 * This implies several things: 1) Each hierarchy should be constructed
	 * assiduously. 2) Constraints/invariants/checks should be associated with an
	 * element carefully
	 * 
	 * Without both of these, constraints can have unintended far reaching impact.
	 * 
	 * @param p
	 * @return
	 */
	public List<Base> getAffectingElements(Base p) {

		List<Base> results = collectAncestors(p);
		results.addAll(collectDescendants(p));
		return results;

	}

//	public List<Base> getAffectingElements(Base p) {
//		List<Base> results = new ArrayList<Base>();
//		results.add(p);
//		/**
//		 * Get all the containment hierarchy
//		 */
//		EObject current = (EObject) p;
//		while ((current.eContainer() != null) && (isCastableToBase(current.eContainer()))) {
//			current = current.eContainer();
//			results.add((Base) current);
//		}
//		/**
//		 * We also get all the content of the original concept, if it is a set.
//		 * eAllContents returns a TreeIterator so we don't need to recurse separately
//		 */
//		for (EObject tp : IteratorExtensions.<EObject>toIterable(((EObject) p).eAllContents())) {
//			if (isCastableToBase(tp)) {
//				results.add((Base) tp);
//			} else {
//				msgInvalidValue(qnp.getFullyQualifiedName(tp).toString(), tp.toString());
//			}
//		}
//		return results;
//	}
	
	/**
	 * Get all the descendants of the starting point (including the starting point)
	 * 
	 * TODO: Do we need to check the contents to select only those of the correct
	 * type? I don't think so since taxonomies contain only the correct type by
	 * definition
	 * 
	 * @param start
	 * @return
	 */
	public List<Base> collectDescendants(Base start) {
		return collectDescendants(start,true);
	}
	/**
	 * Get all the descendants of the starting point (including the starting point)
	 * 
	 * TODO: Do we need to check the contents to select only those of the correct
	 * type? I don't think so since taxonomies contain only the correct type by
	 * definition
	 * 
	 * @param start
	 * @param includeStart - whether to include the starting point
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Base> collectDescendants(Base start, boolean includeStart) {
		List<Base> descendants = new ArrayList<>();
		if (includeStart) {
			descendants.add(start);
		}
		TreeIterator<EObject> titer = start.eAllContents();
		while (titer.hasNext()) {
			Base jb = (Base) titer.next();
			descendants.add(jb);
		}

		return descendants;
	}

	/**
	 * Get the flattened set of contents of the parent. 
	 * This recursive call walks down the subtree - when it returns the contained LogicalEnumeratedBase
	 * instances are in a single set (to avoid duplicates). NOTE that this eliminates duplicated 
	 * instances, it does not eliminate duplicated leaf names. A separate constraint must deal with that.
	 * 
	 * @param parent The subtree root for which we will get the flattened contents
	 * @return The flattened contents of the subtree
	 */
	 public Set<Base> flattenedContents(Base parent) {
		Set<Base> result = new HashSet<Base>(Arrays.asList(parent));
		
		result.addAll(collectDescendants(parent));
		return result;		
	}

	
	/**
	 * Check to see if the testedHierarchy overlaps with the hierarchyToCheck. There
	 * are three possible results: 1) FALSE - the testedHierarchy is completely
	 * unrelated to the hierarchyToCheck - this occurs when they have no common
	 * ancestor or when they have a common ancestor but one is not the ancestor of
	 * the other 2) UNNKNOWN - When TRUE and FALSE are not appropriate 3) TRUE - if
	 * the testedHierarchy is wholly contained in the hierarchyToCheck (includes
	 * when they are they same)
	 * 
	 * TODO: A strictly hierarchical definition of Hierarchies cannot handle
	 * EU/Schengen area (https://en.wikipedia.org/wiki/Schengen_Area) - or anywhere
	 * hierarchies intersect but aren't nested. This must be addressed at some
	 * point.
	 * 
	 * @param testedHierarchy
	 * @param hierarchyToCheck
	 * @return
	 */
	public TriBool intersectingHierarchies(Base testedHierarchy, Base hierarchyToCheck) {

		/**
		 * Quickly check to see if they are equal
		 */
		if (testedHierarchy == hierarchyToCheck)
			return TriBool.TRUE;

		/**
		 * Get the entire lineage of both - if the hierarchyToCheck is an ancestor of
		 * the testedHierarchy, then yes
		 */
		Collection<Base> tHAncestors = collectAncestors(testedHierarchy);
		if (tHAncestors.contains(hierarchyToCheck))
			return TriBool.TRUE;

		/**
		 * If the testedHierarchy is an ancestor of the hierarchyToCheck, then MAYBE
		 * (because we might be dealing with something in the hierarchyToCheck)
		 */
		Collection<Base> hTCAncestors = collectAncestors(hierarchyToCheck);
		if (hTCAncestors.contains(testedHierarchy))
			return TriBool.SOMETIMES;

		/**
		 * Should this be FALSE or UNKNOWN?
		 */
		return TriBool.FALSE;
	}

//	 /**
//	  * Find the root
//	  */
//	 def: root(): ProcessingBasisEnumerated =
//	let container = self.oclContainer.oclAsType(ProcessingBasisBase) in
//	container.root()
	public EObject root(EObject obj) {
		return NavigationUtilities.root(obj, getBaseMetaClass(), false);
	}

	/**
	 * Find the object visible from the context of the specified type and name.
	 * This just renames an existing function so the name matches what we're using
	 * in OCL. Note that getUniqueObjectForName (which this calls) can process RQNs,
	 * not just leaf names. In that sense it is more powerful than the OCL equivalent
	 * @param context
	 * @param type
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked") // Use of BaseMetaClass ensures the cast will work
	public Base findByName(EObject context, String name ) {
		Map<String,Base> c = getCache();
		Base found = c.get(name);
		if (found == null) {
			found = (Base) ndxUtil.getUniqueObjectForName(context,getBaseMetaClass(),name);
			if (found != null) {
				c.put(name, found);					
			}
		}
		return found;
		
	}

	/**
	 * Find specific instances. Note that this looks through all of them. Use it sparingly.
	 * Further, it does that for each name. Need to make getUniqueObjectForName more efficient. 
	 */	
	@SuppressWarnings("unchecked") // Use of BaseMetaClass ensures the cast will work
	public Set<Base> findByNames(EObject context,  Set<String> names) {
		Map<String,Base> c = getCache();
		Set<Base> result = new HashSet<Base>();
		for (String name: names) {
			Base found = c.get(name);
			if (found == null) {
				found = (Base) ndxUtil.getUniqueObjectForName(context,getBaseMetaClass(),name);
				if (found != null) {
					c.put(name, found);						
				}
			}
			if (found != null) {
				result.add(found);
			}
		}
		return result;
	}


}

///**
//* Returns a collection rooted in starting object
//* @param context
//* @param bazeClz
//* @return
//*/
//static List<EObject> flatten(EObject start, EClass bazeClz) {
//	
//}

//context ProcessingBasisBase
//  /**
//	 * Declare a helper operation to map an ok/warning verdict to ok/error.
//	 */
//	def: asError(verdict : Boolean) : Boolean = if verdict then true else null endif
//

//    /*
//    * Helper method that reroutes based on derived type
//    */      
//   def: flattenLabels(): Collection(ProcessingBasisBase) =
//     if self.oclIsTypeOf(ProcessingBasisSet) then
//       self.oclAsType(ProcessingBasisSet).flattenLabels()
//     else -- 
//       self.oclAsType(ProcessingBasisLabel).flattenLabels()
//     endif    	



//	/**
//	 * All ProcessingBase classes can check containment using this method
//	 * This determines if obj is contained in self's set labels (which includes self)
//	 * 
//	 * The check of the name is an optimization to avoid flattening if not needed
//	 */
//	def: contains(obj: ProcessingBasisBase): Boolean = 
//		(self.name.toLowerCase() = obj.name.toLowerCase()) or
//		self.flattenLabels()->includes(obj)
//
//	def: contains(s: String): Boolean = 
//		(self.name.toLowerCase() = s.toLowerCase()) or
//		self.flattenLabels()->exists(l|l.name.toLowerCase() = s.toLowerCase())
//
//	/**
//	 * Is self contained in the passed in set (including any subelements)
//	 */
//	def: containedIn(s: Set(ProcessingBasisBase)): Boolean =
//		s->includes(self) or
//		s->collect(flattenLabels())->includes(self)
//
//	/**
//	 * Is self contained in the hierarchy of the passed in element?
//	 */
//	def: containedIn(s: ProcessingBasisBase): Boolean =
//		s.flattenLabels()->includes(self)
//
//
//	def: containedIn(s: String): Boolean =
//		findSetRootedInName(s)->includes(self)
//
//	def: containedIn(s: Set(String)): Boolean =
//		s->collect(o|findSetRootedInName(o))->includes(self)
//
//
//	/**
//	 * Find specific instances. Note that this looks through all of them. Use it sparingly
//	 * NOTE: static methods are invoked using '::' instead of '.'
//	 */	
//	static def: findByNames(names: Set(String)): Set(ProcessingBasisBase) =
//		let lnames = names->collect(toLowerCase()) in
//		ProcessingBasisBase.allInstances()->select(o|lnames->includes(o.name.toLowerCase())) 
//
//	/**
//	 * Find the set of instances rooted at this instance. Use this version if you
//	 * know self is not null
//	 */	
//	def: findSetRootedIn(): Set(ProcessingBasisBase) =
//			self->collect(flattenLabels())->asSet()
//
//	/**
//	 * Find the set of instances rooted at this instance. Use this version if you
//	 * aren't sure if root is null or not
//	 */	
//	static def: findSetRootedIn(root: ProcessingBasisBase): Set(ProcessingBasisBase) =
//		if (root->notEmpty()) then
//			root->collect(flattenLabels())->asSet()
//		else
//			Set{}
//		endif
//	/**
//	 * Find the set of instances rooted at this name. Note that this looks through all of them. Use it sparingly
//	 * NOTE: static methods are invoked using '::' instead of '.'
//	 */	
//	static def: findSetRootedInName(n: String): Set(ProcessingBasisBase) =
//		let root = findByName(n) in
//			findSetRootedIn(root)
//
//	/**
//	 * Find the set of instances rooted at this set of names. Note that this looks through all of them. Use it sparingly
//	 * NOTE: static methods are invoked using '::' instead of '.'
//	 */	
//	static def: findSetRootedInNames(names: Set(String)): Set(ProcessingBasisBase) =
//		findByNames(names)->collect(flattenLabels())->asSet()
//
//
