/**
 * 
 */
package com.epistimis.uddl.unrolled;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

//import com.epistimis.uddl.RealizedEntity;
//import com.epistimis.uddl.UnrolledAssociation;
//import com.epistimis.uddl.UnrolledDataType;
import com.epistimis.uddl.util.IndexUtilities;
import com.epistimis.uddl.uddl.UddlElement;
import com.google.common.collect.Iterables;

/**
 * This class handles creation of unrolled (C/L/P) elements
 */
public abstract class UnrollingFactory<ComposableElement extends UddlElement, 
	Characteristic extends EObject, 
	Entity extends ComposableElement, 
	Association extends Entity, 
	Composition extends Characteristic, 
	Participant extends Characteristic, 
	ElementalComposable extends ComposableElement,
	Container extends UddlElement,
	UComposableElement extends UnrolledComposableElement<ComposableElement >,
	UComposition extends UnrolledComposition<ComposableElement, Characteristic, Composition, UComposableElement>,
	UEntity extends UnrolledEntity<ComposableElement, Entity, Characteristic, Composition,UComposableElement,UComposition>,
	UParticipant extends UnrolledParticipant<ComposableElement, Entity, Characteristic, Participant,UComposableElement>, 
	UAssociation extends UnrolledAssociation<ComposableElement, Entity, Association, Characteristic,  Composition,Participant,UComposableElement,UComposition,UParticipant> 
	> {

	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	static final String FMT_STRING = "Could not find realization for type [{0}] and role [{1}] when processing [{2}] with description [{3}]";

	
	//abstract void 												updateMaps(ComposableElement ce, UComposableElement uce);
	abstract Set<Map.Entry<ComposableElement, UnrolledComposableElement<ComposableElement>>> 	getC2REntrySet();
	abstract <UComp extends  UnrolledComposableElement<ComposableElement>> UComp 				getUnrolledForComposable(ComposableElement type);
	abstract ComposableElement 										getType(UComposition uc);
	abstract Map<String,UComposition> 								getComposition(UEntity entity);
	abstract boolean												isUEntity(Object uce);
	abstract void 													clearMaps();
	abstract String 												getName(ComposableElement obj);
	
	abstract boolean												isElementalComposable(ComposableElement obj);
	abstract ElementalComposable									conv2ElementalComposable(ComposableElement obj);
	abstract UComposableElement										createElementalComposable(ComposableElement obj);
	
	abstract boolean    											isEntity(ComposableElement obj);
	abstract Entity													conv2Entity(ComposableElement obj);
	abstract UEntity 												createEntity(ComposableElement obj);

	abstract boolean												isAssociation(ComposableElement obj);
	abstract Association											conv2Association(ComposableElement obj);
	abstract UAssociation 											createAssociation(ComposableElement obj);
	
	/**
	 * Get the type parameters for this generic class See also
	 * https://stackoverflow.com/questions/4213972/java-generics-get-class-of-generic-methods-return-type
	 * 
	 * @param ndx the index into the list of type parameters
	 * @return
	 */
	public Class<?> returnedTypeParameter(int ndx) {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<?>) parameterizedType.getActualTypeArguments()[ndx];
	}

	/**
	 * Methods to return each of the parameter types - these warnings must remain
	 * because the alternative is a compile error when these values get used.
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Class getComposableElementType() {
		return returnedTypeParameter(0);
	}

	@SuppressWarnings("rawtypes")
	public Class getCharacteristicType() {
		return returnedTypeParameter(1);
	}

	@SuppressWarnings("rawtypes")
	public Class getEntityType() {
		return returnedTypeParameter(2);
	}

	@SuppressWarnings("rawtypes")
	public Class getAssociationType() {
		return returnedTypeParameter(3);
	}

	@SuppressWarnings("rawtypes")
	public Class getCompositionType() {
		return returnedTypeParameter(4);
	}

	@SuppressWarnings("rawtypes")
	public Class getParticipantType() {
		return returnedTypeParameter(5);
	}

	@SuppressWarnings("rawtypes")
	public Class getElementalComposableType() {
		return returnedTypeParameter(6);
	}

	@SuppressWarnings("rawtypes")
	public Class getContainerType() {
		return returnedTypeParameter(7);
	}


	public void resolve(EObject obj) {
		EcoreUtil2.resolveAll(obj);
		ResourceSet rs = obj.eResource().getResourceSet();
		unroll(rs);
	}
	
	public void resolve(Resource resource) {
		EcoreUtil2.resolveAll(resource);

		ResourceSet rs = resource.getResourceSet();
		unroll(rs);
	}

	/**
	 * 'Realize' and resolve all the EObjects in the specified ResourceSet. The
	 * results are cached.
	 * 
	 * @param resource
	 */
	public void resolve(ResourceSet rs) {

		/**
		 * Before doing anything else, resolve all ECore cross references - because we
		 * don't know if some have been lazy linked
		 */
		EcoreUtil2.resolveAll(rs);
		unroll(rs);
	}
	
	private void unroll(ResourceSet rs) {
		// Before creating new instances, check to see if we already have an instance.
		// TODO: We need a way to determine if the AST has changed so we can invalidate the cache.
		// How do we do that? Since the cache is keyed by instance ID, it is sufficient that a change
		// will cause a new instance to be created, so the old instance won't be found? The problem
		// is that approach leaves the old instances in the cache, just unused - which creates a memory
		// leak. We need to know to remove old instances. Alternatively, we just always flush the cache
		// at this point - which is what we do here.
		clearMaps();
		for (Resource res : rs.getResources()) {
			/**
			 * This will only collect all the realization info from a PlatformEntity ->
			 * LogicalEntity -> ConceptualEntity to determine what is available. The results
			 * can then be used to create instances, generate code, or ....
			 */
			@SuppressWarnings("unchecked")
			final Iterable<ComposableElement> elements = Iterables.<ComposableElement>filter(
					IteratorExtensions.<EObject>toIterable(res.getAllContents()), getComposableElementType());
			// When we create the Unrolled objects, they are automatically cached - we don't need to do anything 
			// with them here.
			for (final ComposableElement elem : elements) {
				if (isAssociation(elem)) {
					createAssociation(elem);
				} else {
					if (isElementalComposable(elem)) {
						createElementalComposable(elem);
					} else {
						if (isEntity(elem)) {
							createEntity(elem);
						} else {
							logger.warn(MessageFormat.format("No processing available for type {0}",
									elem.getClass().toString()));
						}
					}
				}
			}
		}
		/**
		 * Now go back and link all the Entity / Association types
		 * 
		 */
		linkTypes();
	}

	/**
	 * Use the maps to match types. This uses the ComposableElement instances as keys, not FQNs. This should work but it is based on
	 * object identity.
	 */
	public void linkTypes() {
		for (Map.Entry<ComposableElement, UnrolledComposableElement<ComposableElement>> entry: getC2REntrySet()) {
			ComposableElement ce = entry.getKey();
			Object rce =  entry.getValue();
			if (isUEntity(rce)) {
				@SuppressWarnings("unchecked")
				UEntity re = (UEntity) rce;
				for (UComposition rc: re.getComposition().values()) {
					ComposableElement type = IndexUtilities.unProxiedEObject(rc.getType(),ce);
					UnrolledComposableElement<ComposableElement>  unrolledType = getUnrolledForComposable(type);
					if (unrolledType == null) {
						String typename = type != null ? getName(type) : "null";
						logger.warn(MessageFormat.format(FMT_STRING,typename,rc.getRolename(), re.getName(), re.getDescription()));
					}
					rc.setUnrolledType(unrolledType);
				}
			}
		}
	}
	
}
