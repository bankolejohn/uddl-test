/**
 * 
 */
package com.epistimis.uddl.validation;

import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.ocl.pivot.Constraint;
import org.eclipse.ocl.pivot.internal.utilities.EnvironmentFactoryInternal;
import org.eclipse.ocl.pivot.resource.ASResource;
import org.eclipse.ocl.pivot.utilities.OCL;
import org.eclipse.ocl.pivot.utilities.ThreadLocalExecutor;
import org.eclipse.ocl.xtext.completeocl.utilities.CompleteOCLLoader;
import org.eclipse.ocl.xtext.completeocl.validation.CompleteOCLEObjectValidator;

/**
 * 
 */
public class OCLLoader extends FileLoadingSupport {
	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

//	protected final @NonNull ResourceSet resourceSet;
	private final @NonNull EnvironmentFactoryInternal environmentFactory = 		ThreadLocalExecutor.basicGetEnvironmentFactory();
	boolean conditionalsRegistered = false;
	protected static Map<String,ResourceSet> resourceSets = new HashMap<>();


	/**
	 * Loads a set of resources (per URIs)
	 * 
	 * From org.eclipse.ocl.xtext.completeocl.ui.commands.LoadCompleteOCLResourceHandler.java
	 * 
	 * @param uris
	 * @return
	 */
	protected boolean loadAllIdentifiedOCLResources(List<URI> uris) {
		// 
		CompleteOCLLoader helper = new CompleteOCLLoader(environmentFactory) {
			@Override
			protected boolean error(@NonNull String primaryMessage, @Nullable String detailMessage) {
				logger.error(MessageFormat.format("{0} : {1}",primaryMessage,detailMessage));
				return false;
			}
		};
		if (!helper.loadMetamodels()) {
			return false;
		}
		//
		//	Load all the documents
		//
		for (URI oclURI : uris) {
			assert oclURI != null;
			try {
				if (!helper.loadDocument(oclURI)) {
					return false;
				};
			}
			catch (Throwable e) {
				logger.error(MessageFormat.format("Failed to load {0}",oclURI.toPlatformString(false)));
				return false;
			}
		}
		helper.installPackages();
		return true;
	}
	
	/**
	 * index all known resources in this resourceSet
	 */
	private synchronized boolean indexResources(ResourceSet rs) {
		boolean found = true;
		for (Resource res: rs.getResources()) {
			String resName = res.getURI().toPlatformString(false);
			ResourceSet testRs = resourceSets.get(resName);
			if (testRs == null) {
				found = false;
				synchronized(resourceSets) {
					resourceSets.put(resName, rs);
				}
			}
		}
		return found;
	}
	/**
	 * Loading OCL requires a resourceSet. We want to apply it to any resource. One way to do that is to 
	 * trigger the load when any validation is done. We only have to have 1 EObject to trigger to capture the context
	 * 
	 * 
	 * @param obj
	 */
	public void checkOCLConstraints(EObject obj) {
		String resName = obj.eResource().getURI().toPlatformString(false);
		// Check to see if this resource is registered
		boolean found = true;
		ResourceSet rs = resourceSets.get(resName);
		if (rs == null) {
			found = indexResources(rs);
		}
		/**
		 * If the resource was found, then the constraints have alreaday been loaded. Do we reload them?
		 * The issue here is whether or not the resources have been modified. The simple approach is to reload
		 * the constraints but that means they would get reloaded for every call to any EXPENSIVE check.
		 * What we want is to reload only when we start a new validation - at most
		 * 
		 * Once we have the resourceSet, we can load OCL docs
		 */
		if (!found) {
			// Load OCL
		}
		
	}

	public CompleteOCLEObjectValidator loadOCLValidator(EPackage pkg, String resourceAddress, String pluginID) {
		URI oclURI = getInputURI(pluginID,resourceAddress);
		CompleteOCLEObjectValidator validator = new CompleteOCLEObjectValidator(pkg, oclURI);
		boolean success = validator.initialize(ThreadLocalExecutor.basicGetEnvironmentFactory());
		if (success) {
			logger.info(MessageFormat.format("Created validator for pkg {0} and URI {1}", pkg.toString(),
					oclURI.toPlatformString(true)));			
			return validator;
		}
		else {
			logger.error(MessageFormat.format("Calidator for pkg {0} and URI {1} not initialized", pkg.toString(),
					oclURI.toPlatformString(true)));						
			return null;
		}
	}
	

	/**
	 * Load an OCL file from the specified URI and register any constraints found
	 * therein. Note that this loads the entire file so contained operations should
	 * be visible as well.
	 * 
	 * @param ocl The OCL instance associated the ResourceSet we are currently
	 *            processing
	 * @param uri The URI of the file to load
	 * @return A map of constraints. The key is the class name the invariant is
	 *         associated with.
	 */
	protected synchronized Map<String, Set<Constraint>> loadConstraintsFromFile(OCL ocl, URI uri) {

		// parse the contents as an OCL document
		ASResource asResource = ocl.parse(uri);
		// accumulate the document constraints in constraintMap and print all
		// constraints
		Map<String, Set<Constraint>> constraintMap = new HashMap<String, Set<Constraint>>();
		for (TreeIterator<EObject> tit = asResource.getAllContents(); tit.hasNext();) {
			EObject next = tit.next();
			
			// Operations are functions. Constraints are invariants
			if (next instanceof Constraint) {
				Constraint constraint = (Constraint) next;
				String clzName =  constraint.getContext().getClass().getCanonicalName();
				Set<Constraint> cSet = constraintMap.get(clzName);
				if (cSet == null) {
					cSet = new HashSet<Constraint>();
					constraintMap.put(clzName, cSet);
				}
				cSet.add(constraint);
//				ExpressionInOCL expressionInOCL;
//				try {
//					expressionInOCL = ocl.getSpecification(constraint);
//					if (expressionInOCL != null) {
//						String name = constraint.getName();
//						if (name != null) {
//							constraintMap.put(name, expressionInOCL);
//							debugPrintf("%s: %s%n\n", name, expressionInOCL.getOwnedBody());
//						}
//					}
//				} catch (ParserException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}
		logger.debug(MessageFormat.format("Loading {0} returns constraints mapped to {1} contexts", uri.toPlatformString(false),constraintMap.entrySet().size()));
		return constraintMap;
	}

}

