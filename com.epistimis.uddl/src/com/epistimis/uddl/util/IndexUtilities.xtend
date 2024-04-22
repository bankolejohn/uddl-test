package com.epistimis.uddl.util

import com.epistimis.uddl.exceptions.NameCollisionException
import com.epistimis.uddl.exceptions.NamedObjectNotFoundException
import com.epistimis.uddl.uddl.UddlPackage
import com.google.inject.Inject
import java.text.MessageFormat
import java.util.ArrayList
import java.util.Collection
import java.util.Collections
import java.util.HashSet
import java.util.LinkedList
import java.util.List
import java.util.Map
import java.util.Set
import org.eclipse.acceleo.query.runtime.EvaluationResult
import org.eclipse.acceleo.query.runtime.IQueryBuilderEngine.AstResult
import org.eclipse.acceleo.query.runtime.IQueryEnvironment
import org.eclipse.acceleo.query.runtime.Query
import org.eclipse.acceleo.query.runtime.impl.QueryBuilderEngine
import org.eclipse.acceleo.query.runtime.impl.QueryEvaluationEngine
import org.eclipse.emf.common.notify.Adapter
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EStructuralFeature
import org.eclipse.emf.ecore.EStructuralFeature.Setting
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.emf.ecore.util.ECrossReferenceAdapter
import org.eclipse.emf.ecore.util.EcoreUtil
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.naming.QualifiedName
import org.eclipse.xtext.resource.IContainer
import org.eclipse.xtext.resource.IEObjectDescription
import org.eclipse.xtext.resource.impl.ResourceDescriptionsProvider
import static java.util.Objects.requireNonNull;
import java.util.HashMap
import com.epistimis.uddl.scoping.ECrossReferenceAdapterCrossReferenceProvider
import com.epistimis.uddl.scoping.ResourceSetRootEObjectProvider
import java.util.stream.Collectors

//import org.eclipse.acceleo.query.validation.type.IType
//import org.eclipse.acceleo.query.validation.type.EClassifierType
//import org.eclipse.acceleo.query.parser.AstValidator
//import org.eclipse.acceleo.query.runtime.IValidationResult
//import org.eclipse.acceleo.query.runtime.impl.ValidationServices
/**
 * This is modified from the book (See https://github.com/LorenzoBettini/packtpub-xtext-book-2nd-examples)
 * Smalljava = SmallJavaIndex.xtend
 */
class IndexUtilities {
	@Inject ResourceDescriptionsProvider rdp
	@Inject IContainer.Manager cm
	@Inject IQualifiedNameProvider qnp
//	@Inject IPackageProvider pkgProvider;

	/**
	 * Get all the EObjectDescriptions of the specified type visible from the context object
	 * @param context The root object where we start the search. Should never be null.
	 */
	def getVisibleEObjectDescriptions(EObject context, EClass type) {
		requireNonNull(context,"You must specify the context from which the search for visible objects will start");
		context.getVisibleContainers.map [ container |
			container.getExportedObjectsByType(type)
		].flatten
	}

	/**
	 * Get all the objects of the specified type visible from the context object
	 * @param context The root object where we start the search. Should never be null.
	 */
	def getVisibleObjects(EObject context, EClass type) {
		requireNonNull(context,"You must specify the context from which the search for visible objects will start");
		context.getVisibleEObjectDescriptions(type).map([
			context.eResource.objectFromDescription(it)
		]);
	}

	/**
	 * Find all containers visible from this object 
	 * @param context The root object where we start the search. Should never be null.
	 * 
	 */
	def getVisibleContainers(EObject context) {
		requireNonNull(context,"You must specify the context where the search will start");
		val index = rdp.getResourceDescriptions(context.eResource)
		val rd = index.getResourceDescription(context.eResource.URI)
		// TODO: rd should never be null - but occasionally it is
		// For reasons I don't yet understand. This avoids an NPE.
		if (rd !== null) {
			cm.getVisibleContainers(rd, index)
		} else {
			return new ArrayList();
		}
	}

	/**
	 * Get the description of the Resource containing this object
	 * 
	 * @param context An EObject. Should never be null.
	 */
	def getResourceDescription(EObject context) {
		requireNonNull(context,"You must specify the context whose Resource Description you want");
		//if (context === null) { return null; }
		val index = rdp.getResourceDescriptions(context.eResource)
		index.getResourceDescription(context.eResource.URI)
	}

	/**
	 * Get all the exported EObjectDescriptions from the Resource containing this object
	 * @param context The root object where we start the search. Should never be null.
	 */
	def getExportedEObjectDescriptions(EObject context) {
		requireNonNull(context,"You must specify the context whose exported descriptions you want");
		context.getResourceDescription.getExportedObjects
	}

	/**
	 * Get all the exported EObjectDescriptions from the Resource containing this object,
	 * filtered by the specified EClass
	 * 
	 * @param context The root object where we start the search. Should never be null.
	 */
	def getExportedEObjectDescriptionsByType(EObject context, EClass type) {
		requireNonNull(context,"You must specify the context whose exported descriptions you want");
		requireNonNull(type,"You must specify the type you want to search for");
		var Iterable<IEObjectDescription> descs = context.getResourceDescription?.getExportedObjectsByType(type);
		if (descs === null) {
			// return an empty list to avoid a null pointer
			return new ArrayList<IEObjectDescription>();
		}
		else {
			return descs;
		}
	}

	/**
	 * Get *only* the external EObjectDescriptions visible from 'o' of 'type'
	 * @param context The root object where we start the search. Should never be null.
	 */
	def getVisibleExternalEObjectDescriptionsByType(EObject context, EClass type) {
		requireNonNull(context,"You must specify the context from which visible objects will be identified");
		requireNonNull(type,"You must specify the type you want to search for");
		val allVisibleEObjectDescriptions = context.getVisibleEObjectDescriptions(type)
		val allExportedEObjectDescriptions = context.getExportedEObjectDescriptionsByType(type)
		val difference = allVisibleEObjectDescriptions.toSet
		if (allExportedEObjectDescriptions !== null) {
			difference.removeAll(allExportedEObjectDescriptions?.toSet)
		}
		return difference.toMap[qualifiedName]
	}

	/*	
	 * The original version of this method is below. Specific implementations found below use
	 * the general implementation above.
	 */
	/*
	 * 	def getVisibleExternalClassesDescriptions(EObject o) {
	 * 		val allVisibleClasses =
	 * 			o.getVisibleClassesDescriptions
	 * 		val allExportedClasses =
	 * 			o.getExportedClassesEObjectDescriptions
	 * 		val difference = allVisibleClasses.toSet
	 * 		difference.removeAll(allExportedClasses.toSet)
	 * 		return difference.toMap[qualifiedName]
	 * 	}
	 */
	def getVisibleExternalEObjectsByType(EObject context, EClass type) {
		requireNonNull(context,"You must specify the context from which visible objects will be identified");
		requireNonNull(type,"You must specify the type you want to search for");
		context.getVisibleExternalEObjectDescriptionsByType(type).values.map([
			context.eResource.objectFromDescription(it)
		]);
	}

	/**
	 * Find all visible EObjectDescriptionss that match this type and name. The name may be 
	 * a leaf, RQN, or FQN. Note the following:
	 * 1) We are checking the name from the leaf up. The assumption here is that
	 * in most cases, a leaf or RQN will be used - so starting with that comparison
	 * will return success more quickly.
	 * 2) If the leaf is not in the name, then the name won't match at all - so 
	 * there is no point in continuing to check.
	 * 
	 * NOTE: This defaults to ignoring case. You can specify if you want to use case with
	 * an optional 4th parameter.
	 * @param context The context to search
	 * @param type The EClass to search for
	 * @param name The name of the instance to search for
	 * 
	 * @return A list of all the all the objects visible in this context of that type
	 * with that name
	 */
	def searchAllVisibleEObjectDescriptions(EObject context, EClass type, String name) {
		return searchAllVisibleEObjectDescriptions(context, type, name, true);
	}

	/**
	 * Find all visible EObjectDescriptions that match this type and name. The name may be 
	 * a leaf, RQN, or FQN. Note the following:
	 * 1) We are checking the name from the leaf up. The assumption here is that
	 * in most cases, a leaf or RQN will be used - so starting with that comparison
	 * will return success more quickly.
	 * 2) If the leaf is not in the name, then the name won't match at all - so 
	 * there is no point in continuing to check.
	 * 
	 * @param context The context to search
	 * @param type The EClass to search for
	 * @param name The name of the instance to search for
	 * @param ignoreCase true to ignore case, false to use case
	 * 
	 * NOTE: This method returns a synchronized list just in case there is a threading issue
	 * 
	 * @return A list of all the all the objects visible in this context of that type
	 * with that name
	 */
	def searchAllVisibleEObjectDescriptions(EObject context, EClass type, String name, boolean ignoreCase) {
		requireNonNull(context,"You must specify the context from which visible objects will be identified");
		requireNonNull(type,"You must specify the type you want to search for");
		requireNonNull(name,"You must specify the name you want to search for");

		Collections.synchronizedList(context.getVisibleEObjectDescriptions(type).filter [
			val QualifiedName qn = it.getQualifiedName();
			for (var i = qn.getSegmentCount() - 1; i >= 0; i--) {
				val rqn = qn.skipFirst(i).toString();

				val int ndx = ignoreCase ? name.toLowerCase.indexOf(rqn.toLowerCase) : name.indexOf(rqn);
				if (ndx == -1) {
					return false; // can't possibly match, so exit without trying anything more
				} else if (ndx == 0) {
					return true; // matches - return success
				} else {
					// keep going - and try a longer QN
				}
			}
			/**
			 * If we get here, there wasn't a match on this description
			 */
			return false;
		].toList);
	}

	/**
	 * Find all visible EObjects that match this type and name. The name may be 
	 * a leaf, RQN, or FQN. Note the following:
	 * 1) We are checking the name from the leaf up. The assumption here is that
	 * in most cases, a leaf or RQN will be used - so starting with that comparison
	 * will return success more quickly.
	 * 2) If the leaf is not in the name, then the name won't match at all - so 
	 * there is no point in continuing to check.
	 * 
	 * @param context The context to search
	 * @param type The EClass to search for
	 * @param name The name of the instance to search for
	 * @param ignoreCase true to ignore case, false to use case
	 * 
	 * NOTE: This method returns a synchronized list just in case there is a threading issue
	 * @return A list of all the all the objects visible in this context of that type
	 * with that name
	 */
	def searchAllVisibleObjects(EObject context, EClass type, String name) {
		requireNonNull(context,"You must specify the context from which visible objects will be identified");
		requireNonNull(type,"You must specify the type you want to search for");
		requireNonNull(name,"You must specify the name you want to search for");
		context.searchAllVisibleEObjectDescriptions(type, name).map([
			context.eResource.objectFromDescription(it)
		]);
	}
	
	/**
	 * Convert a list of descriptions into a set of EObjects. Needed because, apparently, we can have
	 * multiple IEObjectDescriptions for the same EObject
	 */
	def static uniqueObjectsFromDescriptions(List<IEObjectDescription> descriptions) {
		return descriptions.stream().map([d|d.EObjectOrProxy]).collect(Collectors.toSet());
	}

	/**
	 * Get the EObject from the EObjectDescription
	 */
	def static objectFromDescription(Resource res, IEObjectDescription desc) {
		if (desc === null)
			return null;
		var EObject o = desc.getEObjectOrProxy();
		if (o.eIsProxy()) {
			o = res.getResourceSet().getEObject(desc.getEObjectURI(), true);
		}
		return o;
	}

	/**
	 * Resolve any proxy (if there) and return the object
	 */
	def static <T extends EObject> unProxiedEObject(T obj, EObject ctx) {
		if (obj === null) {
			return null; // nothing to deProxy
		}
		if (obj.eIsProxy()) {
				return EcoreUtil.resolve(obj,ctx) as T;
		} else {
			return obj;
		}
	}

	/**
	 * Resolve any proxy (if there) and return the object
	 */
	def static <T extends EObject> unProxiedEObject(T obj, Resource res) {
		if (obj === null) {
			return null; // nothing to deProxy
		}
		if (obj.eIsProxy()) {
				return EcoreUtil.resolve(obj,res) as T;
		} else {
			return obj;
		}
	}

	/**
	 * Resolve any proxy (if there) and return the object
	 */
	def static <T extends EObject> unProxiedEObject(T obj, ResourceSet rs) {
		if (obj === null) {
			return null; // nothing to deProxy
		}
		if (obj.eIsProxy()) {
				return EcoreUtil.resolve(obj,rs) as T;
		} else {
			return obj;
		}
	}

	/**
	 * Find a unique object of the specified type and name. Log errors if none or more
	 * than one is found.
	 */
	def getUniqueObjectForName(EObject context, EClass type, String name) {
		requireNonNull(context,"You must specify the context from which the search will start");
		requireNonNull(type,"You must specify the type you want to search for");
		requireNonNull(name,"You must specify the name you want to search for");
		val List<EObject> objList = searchAllVisibleObjects(context, type, name);
		switch objList.size() {
			case 0: {
				val msg = MessageFormat.format("No EObject found with name {0}", name)
				throw new NamedObjectNotFoundException(msg);
			}
			case 1:
				objList.get(0)
			default: {
				throw new NameCollisionException(printEObjectNameCollisions(name, type.getName(), objList));
			// return null;
			}
		}
	}

	static String COLLISION_MSG_FMT = "{0} makes ambiguous reference to instances of type {1}. It could be: \n";

	/**
	 * It is possible that there are name collisions from searches. This is a standard way to list them.
	 * @param qn A string version of a possibly qualified name
	 * @param typeName The type we were looking for
	 * @param descriptions What we found
	 */
	def printIEObjectDescriptionNameCollisions(String qn, String typeName, Collection<IEObjectDescription> descriptions) {
		var msg = MessageFormat.format(COLLISION_MSG_FMT, qn, typeName);
		for (IEObjectDescription d : descriptions) {
			msg += MessageFormat.format("\t {0}\n", d.getQualifiedName().toString());
		}
		return msg;
	}

	/**
	 * It is possible that there are name collisions from searches. This is a standard way to list them.
	 * @param qn A string version of a possibly qualified name
	 * @param typeName The type we were looking for
	 * @param objects What we found
	 */
	def printEObjectNameCollisions(String qn, String typeName, Collection<EObject> objects) {
		var msg = MessageFormat.format(COLLISION_MSG_FMT, qn, typeName);
		for (EObject o : objects) {
			msg += MessageFormat.format("\t {0}\n", qnp.getFullyQualifiedName(o).toString());
		}
		return msg;
	}

	/**
	 * Get the closure of following the feature recursively. This works even if we change types - as long as 
	 * the feature exists, we'll get something. This has a risk of recursive infinite looping.
	 * TODO: This could be optimized by parsing the query once and then just recursing using the same
	 * parsed query.
	 * @param context Used to identify the ResourceSet that scopes the search
	 * @param root The object whose feature will be inspected
	 * @param featureName The feature of that inspected object whose values we want.
	 * @return a flattened list of closure values
	 */
	def Collection<EObject>  closure(EObject context, EObject root, String featureName) {
		var Map<String,Object> variables = new HashMap<String,Object>();
		variables.put("self",root);
		val ResourceSet res = context.eResource().getResourceSet();
		var Collection<EObject> results = processAQL(res,variables,"self.eGet('"+featureName+"')");
		for (EObject obj: results) {
			results.addAll(closure(context,obj,featureName));
		}
		return results;
		
	}

	/**
	 * Identify the EObjects in the closure starting at root and following the specified 
	 * feature. This assumes the feature points from parent to child.
	 * 
	 * @param context  The context from which we start - we search for everything
	 *                 visible in to this context
	 * @param root The root of the hierarchy
	 * @param type The type of object to find
	 * @param feat The feature used to determine the hierarchy
	 * 
	 * Note that to get the full hierarchy, we need the closure that comes from following the specified 
	 * feature through an unknown number of levels. This means we need to loop on all the objects and keep
	 * adding to the found list until the list doesn't change after a complete pass.
	 * 
	 * This version handles features of all cardinality
	 * 
	 * 
	 * @return A set of the EObjects of the specified type in the specified hierarchy
	 *         that are visible from the context
	 */
	def Set<EObject> closure(EObject context, EObject root, EClass type, EStructuralFeature feat) {
		requireNonNull(context,"You must specify the context in which the search will operate");
		requireNonNull(root,"You must specify the root of the hierarchy");
		requireNonNull(type,"You must specify the type you want to search for");
		requireNonNull(feat,"You must specify the feature to follow");
		// If this is a collection feature, handle that differently
		if (feat.isMany) {
			return closureColn(context, root, type, feat);
		}

		var Set<EObject> found = new HashSet<EObject>();
		found.add(root);
		var EObject toCheck = root.eGet(feat) as EObject;
		while ((toCheck !== null) && !found.contains(toCheck)) {
			val EObject temp = toCheck.eGet(feat) as EObject;
			// Log that we found this one, remove it from the list to inspect and add it's children to the
			found.add(toCheck);
			toCheck = temp;
		}
		return found;

	}

	/**
	 * Identify the EObjects in the closure starting at root and following the specified 
	 * feature. This assumes the feature points from parent to child.
	 * 
	 * @param context  The context from which we start - we search for everything
	 *                 visible in to this context
	 * @param root The root of the hierarchy
	 * @param type The type of object to find
	 * @param feat The feature used to determine the hierarchy
	 * 
	 * Note that to get the full hierarchy, we need the closure that comes from following the specified 
	 * feature through an unknown number of levels. This means we need to loop on all the objects and keep
	 * adding to the found list until the list doesn't change after a complete pass.
	 * 
	 * This version is for features that are cardinality > 1,
	 * 
	 * 
	 * @return A set of the EObjects of the specified type in the specified hierarchy
	 *         that are visible from the context
	 */
	def Set<EObject> closureColn(EObject context, EObject root, EClass type, EStructuralFeature feat) {
		var Set<EObject> found = new HashSet<EObject>();
		found.add(root);
		val EList<EObject> toCheck = root.eGet(feat) as EList<EObject>;
		for (EObject e : toCheck) {
			if (!found.contains(e)) {
				val EList<EObject> temp = e.eGet(feat) as EList<EObject>;
				// Log that we found this one, remove it from the list to inspect and add it's children to the
				found.add(e);
				toCheck.addAll(temp);
			}
			// Whether this one was new or not, remove it from the set to check. Because we do this here,
			// we also ensure that it can't be added back in if it is found in a another reference (because we are adding
			// newly found elements to a Set (so duplicates just overwrite/ don't get added), and then we remove just once.
			toCheck.remove(e);
		}
		return found;

	}


	/**
	 * Identify the EObjects in the closure starting at root and following the specified 
	 * feature. This assumes the feature points from child to parent, so we follow the inverse of the 
	 * feature.
	 * 
	 * @param context  The context from which we start - we search for everything
	 *                 visible in to this context
	 * @param root The root of the hierarchy
	 * @param type The type of object to find
	 * @param feat The feature used to determine the hierarchy
	 * 
	 * Note that to get the full hierarchy, we need the closure that comes from following the specified 
	 * feature through an unknown number of levels. This means we need to loop on all the objects and keep
	 * adding to the found list until the list doesn't change after a complete pass.
	 * 
	 * use Acceleo Query Language to navigate the EMF model.
	 * 
	 * This assumes the evaluation will return a set of objects
	 * 
	 * 
	 * @return A set of the EObjects of the specified type in the specified hierarchy
	 *         that are visible from the context
	 * 
	 * Implement this using "self.eInverse('<featurename>')" or "self.eInverse(type)".
	 */
//	def Set<EObject> visibleInverseClosure(EObject context, EObject root, EClass type, EStructuralFeature feat) {
//		val IQueryEnvironment queryEnvironment = Query.newEnvironmentWithDefaultServices(null);
//		queryEnvironment.registerEPackage(UddlPackage.eINSTANCE);
//		val QueryEvaluationEngine engine = new QueryEvaluationEngine(queryEnvironment);
//		val QueryBuilderEngine builder = new QueryBuilderEngine(queryEnvironment);
//		var Map<String, Object> variables = Maps.newHashMap();
//		val AstResult astResult = builder.build("root.eInverse("+feat.name+")");
//		/* From here we loop through all all found objects drilling down. Always check to see if something found is already
//		 * in the list so we don't infinite loop.
//		 */
//		variables.put("root", root);
//		var found = new HashSet<EObject>();
//		found.add(root);
//		var EvaluationResult evaluationResult = engine.eval(astResult, variables);
//		var Set<EObject> toCheck = evaluationResult.result as Set<EObject>;
//		for (e: toCheck) {
//			if (!found.contains(e)) {
//				variables.put("root",e);
//				evaluationResult = engine.eval(astResult, variables);
//				val Set<EObject> temp =  evaluationResult.result as Set<EObject>;
//				// Log that we found this one, remove it from the list to inspect and add it's children to the
//				found.add(e);
//				toCheck.addAll(temp);				
//			}
//			// Whether this one was new or not, remove it from the set to check. Because we do this here,
//			// we also ensure that it can't be added back in if it is found in a another reference (because we are adding
//			// newly found elements to a Set (so duplicates just overwrite/ don't get added), and then we remove just once.
//			toCheck.remove(e);
//			// Note that we could use removeAll here to remove everything that has already been found from the toCheck set.
//			// I think this would be, on average, less efficient. Eventually, each item in toCheck will be compared to found.
//			// If we remove them all here, it is possible that they will be added back in and removed multiple times. At worst
//			// this will be just as efficient this way as the removeAll way
//			
//		}		
//		return found;		
//		
//		// An alternative approach
////		/**
////		 * To find all specializations, we must find all references to the root in the collection of all
////		 * EObjects of the appropriate type. Those references will be in the specified' feature.
////		 */
////		val List<EObject> allEObjects = IterableExtensions.toList(getVisibleObjects(context, type));
////
////		/** find the instance */
////		try {
////				found.add(root);
////				val EList<EObject> xrefs = root.eCrossReferences;
////				for (EObject xref: xrefs) {
////					if (root == xref.eGet(feat)) {
////						found.add(xref);
////					}
////				}
////				val java.util.Collection<EStructuralFeature.Setting> usages = EcoreUtil.UsageCrossReferencer
////						.find(root, allEObjects);
////				// Now filter those usages for only the specializes feature - loop
////				for (EStructuralFeature.Setting usage : usages) {
////					if (usage.getEStructuralFeature().equals(feat)) {
////						found.add(usage.getEObject());
////					}
////				}
////			}
////
////		// Once we get here, that set should contain all the CEntity
////		// hierarchy
////		return allEObjects;
//	}
	/**
	 * Register subpackages - recursive since we don't know how deep this goes
	 * @param pkg
	 * @param rs
	 */
	def void registerSubpackages(IQueryEnvironment env, EPackage pkg) {
		for (EPackage spkg : pkg.getESubpackages()) {
			env.registerEPackage(spkg);
			registerSubpackages(env, spkg);
		}
	}

	/**
	 * Register all the EPackages we care about.
	 */
	def registerPackages(Collection<EPackage> pkgs, IQueryEnvironment queryEnvironment) {

		for (EPackage pkg : pkgs) {
			queryEnvironment.registerEPackage(pkg);
			registerSubpackages(queryEnvironment, pkg);
		}
	}

	/**
	 * Adapted from plugins/org.obeonetwork.m2doc.genconf/src/org/obeonetwork/m2doc/genconf/GenconfUtils.java
	 * Gets the initialized {@link IQueryEnvironment} for the given {@link Generation}.
	 * 
	 * @param resourceSetForModel
	 *            the {@link ResourceSet} for model elements
	 * @return the initialized {@link IQueryEnvironment} 
	 */
	def static IQueryEnvironment getQueryEnvironment(ResourceSet resourceSetForModel) {
		val ECrossReferenceAdapter crossReferenceAdapter = new ECrossReferenceAdapter();
		resourceSetForModel.eAdapters().add(crossReferenceAdapter);
		crossReferenceAdapter.setTarget(resourceSetForModel);
		val ECrossReferenceAdapterCrossReferenceProvider crossReferenceProvider = new ECrossReferenceAdapterCrossReferenceProvider(
			ECrossReferenceAdapter.getCrossReferenceAdapter(resourceSetForModel));
		val ResourceSetRootEObjectProvider rootProvider = new ResourceSetRootEObjectProvider(resourceSetForModel);

		return Query.newEnvironmentWithDefaultServices(crossReferenceProvider,
			rootProvider);
	}

	/**
	 * Use Acceleo Query Language to get all the objects that have the specified relationship with the context object.
	 * See https://eclipse.dev/acceleo/documentation/ for doc on how to write queries.
	 * 
	 * @param pkgs This provides access to the list of packages to register.  
	 * @param variables A set of variables that are used to map to parts of the query. In most cases, 
	 * there will be a single element in the map with they key 'self'.
	 * @param queryText The Acceleo query text to navigate 
	 * 
	 */
	def Collection<EObject> processAQL(ResourceSet resourceSet,  Map<String, Object> variables,
		String queryText) {

		val List<EPackage> pkgs = new ArrayList<EPackage>();   //pkgProvider.getEPackages();
		pkgs.add(UddlPackage.eINSTANCE);
		
		val IQueryEnvironment queryEnvironment = getQueryEnvironment(resourceSet);
		registerPackages(pkgs, queryEnvironment);

		val QueryBuilderEngine builder = new QueryBuilderEngine(queryEnvironment);
		val AstResult astResult = builder.build(queryText);
		val QueryEvaluationEngine engine = new QueryEvaluationEngine(queryEnvironment);
		var EvaluationResult evaluationResult = engine.eval(astResult, variables);
		return evaluationResult.result as Collection<EObject>;
	}

//	def validateQuery(IQueryEnvironment queryEnvironment) {
//		var Map<String, Set<IType>> variableTypes = new LinkedHashMap<String, Set<IType>>();
//		var Set<IType> selfTypes = new LinkedHashSet<IType>();
//		selfTypes.add(new EClassifierType(queryEnvironment, EcorePackage.eINSTANCE.getEPackage()));
//		variableTypes.put("self", selfTypes);
//		var ValidationServices valSvcs = new ValidationServices(queryEnvironment);
//		var AstValidator validator = new AstValidator(valSvcs);
//		val IValidationResult validationResult = validator.validate(astResult);
//	}
	/**
	 * Find all the resources that reference the referenceTarget
	 * from: https://www.eclipse.org/forums/index.php/t/165076/
	 * NOTE: This is from an old post - UsageCrossReferencer is likely newer
	 * Deprecated? - use processAQL instead - it already does this.
	 * This would be faster because it doesn't parse an expression - does that matter?
	 */
	def List<Resource> getReferencingResources(EObject referenceTarget) {
		val Resource res = referenceTarget.eResource();
		val ResourceSet rs = res.getResourceSet();
		var ECrossReferenceAdapter crossReferencer = null;
		var List<Adapter> adapters = rs.eAdapters();
		for (Adapter adapter : adapters) {
			if (adapter instanceof ECrossReferenceAdapter) {
				crossReferencer = adapter as ECrossReferenceAdapter;
			// break;
			}
		}
		if (crossReferencer === null) {
			crossReferencer = new ECrossReferenceAdapter();
			rs.eAdapters().add(crossReferencer);
		}
		var List<Resource> referers = new LinkedList<Resource>();
		val Collection<Setting> referencers = crossReferencer.getInverseReferences(referenceTarget, true);
		for (Setting setting : referencers) {
			val EObject referer = setting.getEObject();
			val Resource resource = referer.eResource();
			if (!resource.equals(res)) {
				// Only need to check the other resources not containing the referenceTarget
				var URI uri = resource.getURI();
				uri = rs.getURIConverter().normalize(uri);
				if (uri.isPlatformResource()) {
					referers.add(resource);
				}
			}
		}
		return referers;
	}

	/**
	 * Find all the cross references to the specified target, no matter
	 * where they are in the resource set
	 * 
	 * Deprecated? processAQL does this with the query "self.eInverse()"
	 * 
	 * See EcoreUtil.delete() for an example of why it might be done this way
	 */
	def Collection<EStructuralFeature.Setting> getReferencingObjects(EObject referenceTarget) {
		val Resource res = referenceTarget.eResource();
		val ResourceSet rs = res.getResourceSet();
		return EcoreUtil.UsageCrossReferencer.find(referenceTarget, rs);
	}

//	/**
//	 * ================ These methods are specific to this package
//	 */
	/**
	 * Get all the DataModel Descriptions visible from this object
	 */
	def getVisibleDataModelDescriptions(EObject o) {
		o.getVisibleEObjectDescriptions(UddlPackage.eINSTANCE.dataModel)
	}

	def getExportedDataModelEObjectDescriptions(EObject o) {
		o.getResourceDescription.getExportedObjectsByType(UddlPackage.eINSTANCE.dataModel)
	}

	def getVisibleExternalDataModelDescriptions(EObject o) {
		o.getVisibleExternalEObjectDescriptionsByType(UddlPackage.eINSTANCE.dataModel)
	}

}
