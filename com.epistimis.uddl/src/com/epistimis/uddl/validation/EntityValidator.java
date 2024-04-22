/**
 * 
 */
package com.epistimis.uddl.validation;

import java.lang.reflect.ParameterizedType;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

import com.epistimis.uddl.EntityProcessor;
import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.uddl.UddlPackage;
import com.google.inject.Inject;

/**
 * Common validation methods for all (C/L/P) Entities. Keeping them in a generic class avoids copy/paste maintenance issues.
 */
public abstract class EntityValidator<ComposableElement extends UddlElement,
										Characteristic extends EObject, 
										Entity extends ComposableElement, 
										Association extends Entity, 
										Composition extends Characteristic, 
										Participant extends Characteristic,
										ElementalComposable extends ComposableElement,
//										View extends UddlElement, 
//										Query extends View, 
//										CompositeQuery extends View, 
//										QueryComposition extends EObject, 
										Container extends UddlElement,
										EProcessor extends EntityProcessor<ComposableElement,Characteristic, Entity, Association, Composition, Participant,ElementalComposable,Container>> {

	@Inject
	EProcessor eproc;

	public static String ENTITY_NEEDS_2_CHARACTERISTICS = UddlValidator.ISSUE_CODE_PREFIX + "EntityNeeds2Characteristics";
	public static String ASSOCIATION_NEEDS_2_PARTICIPANTS = UddlValidator.ISSUE_CODE_PREFIX + "AssociationNeeds2Participants";

	/**
	 * Interface used for method reference or logging messages.
	 * @param <T>
	 * @param <U>
	 * @param <V>
	 * @param <W>
	 * @param <X>
	 */
	public static interface QuintConsumer<T,U,V,W,X> {
		void accept(T t, U u, V v, W w, X x);
	}

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
	public Class getEntityProcessorType() {
		return returnedTypeParameter(7);
	}

//	public Class getViewType() {
//		return returnedTypeParameter(6);
//	}
//
//	public Class getQueryType() {
//		return returnedTypeParameter(7);
//	}
//
//	public Class getCompositeQueryType() {
//		return returnedTypeParameter(8);
//	}
//
//	public Class getQueryCompositionType() {
//		return returnedTypeParameter(9);
//	}


	
	/**
	 * Every entity must have at least 2 characteristics.
	 * @param ent
	 */
	public void checkCharacteristicCount(Entity ent, QuintConsumer<String,Entity,EAttribute,String,String> logMsg) {
		Map<String,Characteristic> chars = eproc.getCharacteristics(ent);
		if (chars.size() < 2) {
			/**
			 * Since we don't know if this ent has any composition elements declared locally, we just
			 * attach the error to the name attribute
			 */
			logMsg.accept("Entity '" + ent.getName() + "' should have at least 2 characteristics",ent,
					UddlPackage.eINSTANCE.getUddlElement_Name(), ENTITY_NEEDS_2_CHARACTERISTICS, ent.getName());
		}
	}
	
	/**
	 * Every association must have at least 2 participants.
	 * @param ent
	 */
	public void checkParticipantCount(Association ent,QuintConsumer<String,Association,EAttribute,String,String> logMsg) {
		Map<String,Participant> chars = eproc.getParticipants(ent);
		if (chars.values().size() < 2) {
			/**
			 * Since we don't know if this association has any participants declared locally, we just
			 * attach the error to the name attribute
			 */
			logMsg.accept("Association '" + ent.getName() + "' should have at least 2 participants",ent,
					UddlPackage.eINSTANCE.getUddlElement_Name(), ASSOCIATION_NEEDS_2_PARTICIPANTS, ent.getName());
		}
	}

	
}
