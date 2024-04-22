/**
 * 
 */
package com.epistimis.uddl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;

import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.util.IndexUtilities;
import com.google.inject.Inject;

/**
 * 
 */
public abstract class RealizationProcessor<BaseComposableElement extends UddlElement, RealizingComposableElement extends UddlElement,
											BaseEntity extends BaseComposableElement, RealizingEntity extends RealizingComposableElement, 
											BaseCharacteristic extends EObject, RealizingCharacteristic extends EObject, 
											BaseComposition extends BaseCharacteristic, RealizingComposition extends RealizingCharacteristic, 
											BaseParticipant extends BaseCharacteristic, RealizingParticipant extends RealizingCharacteristic, 
											BaseAssociation extends BaseEntity, RealizingAssociation extends RealizingEntity,											
											BaseProcessor extends EntityProcessor<BaseComposableElement, BaseCharacteristic, BaseEntity, BaseAssociation, BaseComposition, BaseParticipant, ?, ?>, 
											RealizingProcessor extends EntityProcessor<RealizingComposableElement, RealizingCharacteristic, RealizingEntity, RealizingAssociation, RealizingComposition, RealizingParticipant, ?, ?>> {

	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

	private static final String ASSOCIATION_REALIZATION_ERR_FMT = "Association {0} must be realized by an Association but {0} is an Entity";
	// @Inject
	//private Provider<ResourceSet> resourceSetProvider;
	//
	//
	//@Inject
	//IResourceServiceProvider.Registry reg;
	//
	//IResourceServiceProvider queryRSP;
	//IResourceFactory queryResFactory;

	//@Inject
	//ParseHelper<QuerySpecification> parseHelper;

	@Inject
	IndexUtilities ndxUtil;

	@Inject
	IQualifiedNameProvider qnp;

	@Inject
	IQualifiedNameConverter qnc;

	@Inject
	BaseProcessor baseProcessor;
	
	@Inject
	RealizingProcessor realizingProcessor;


	static MessageFormat CharacteristicNotFoundMsgFmt = new MessageFormat(
			"Entity {0} does not have a characteristic with rolename {1}");

	abstract public BaseEntity getRealizedEntity(RealizingEntity rent);

	abstract public BaseComposition getRealizedComposition(RealizingComposition rcomp);

	abstract public BaseParticipant getRealizedParticipant(RealizingParticipant rpart);

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
	public Class getBaseComposableElementType() {
		return returnedTypeParameter(0);
	}

	@SuppressWarnings("rawtypes")
	public Class getRealizingComposableElementType() {
		return returnedTypeParameter(1);
	}

	@SuppressWarnings("rawtypes")
	public Class getBaseEntityType() {
		return returnedTypeParameter(2);
	}

	@SuppressWarnings("rawtypes")
	public Class getRealizingEntityType() {
		return returnedTypeParameter(3);
	}

	@SuppressWarnings("rawtypes")
	public Class getBaseCharacteristicType() {
		return returnedTypeParameter(4);
	}

	@SuppressWarnings("rawtypes")
	public Class getRealizingCharacteristicType() {
		return returnedTypeParameter(5);
	}

	@SuppressWarnings("rawtypes")
	public Class getBaseCompositionType() {
		return returnedTypeParameter(6);
	}

	@SuppressWarnings("rawtypes")
	public Class getRealizingCompositionType() {
		return returnedTypeParameter(7);
	}

	@SuppressWarnings("rawtypes")
	public Class getBaseParticipantType() {
		return returnedTypeParameter(8);
	}

	@SuppressWarnings("rawtypes")
	public Class getRealizingParticipantType() {
		return returnedTypeParameter(9);
	}

	@SuppressWarnings("rawtypes")
	public Class getBaseAssociationType() {
		return returnedTypeParameter(10);
	}

	@SuppressWarnings("rawtypes")
	public Class getRealizingAssociationType() {
		return returnedTypeParameter(11);
	}


	@SuppressWarnings("rawtypes")
	public Class getBaseProcessorType() {
		return returnedTypeParameter(12);
	}

	@SuppressWarnings("rawtypes")
	public Class getRealizingProcessorType() {
		return returnedTypeParameter(13);
	}

	public RealizingProcessor 	getRealizingEntityProcessor() 	{ return realizingProcessor;}
	public BaseProcessor 		getBaseProcessor() 				{ return baseProcessor;}
	
	/**
	 * Return the set of BaseEntity compositions that are already  realized in the RealizingEntity
	 * @param rentity The RealizingEntity
	 * @return
	 */
	public List<BaseComposition> getRealizedCompositions(RealizingEntity rentity) {
		Map<String, RealizingComposition> allCompositions = realizingProcessor.allCompositions(rentity);
		// Select the keys from allCompositions - filter out all other CCs
		List<BaseComposition> result = new ArrayList<BaseComposition>();
		for (RealizingComposition c : allCompositions.values()) {
			result.add(getRealizedComposition(c));
		}
		return result;
	}

	/**
	 * Return the set of BaseEntity compositions that are not yet realized in the RealizingEntity
	 * @param rentity The RealizingEntity
	 * @return
	 */
	public Collection<BaseComposition> getUnrealizedCompositions(RealizingEntity rentity) {
		List<BaseComposition> realized = getRealizedCompositions(rentity);

		BaseEntity baseEntity = getRealizedEntity(rentity);
		Map<String, BaseComposition> allBaseCompositions = baseProcessor.allCompositions(baseEntity);

		Collection<BaseComposition> remainingValues = allBaseCompositions.values();
		remainingValues.removeAll(realized);
		return remainingValues;
	}

	/**
	 * Returns the BaseAssociation participants already realized by the RealizingAssociation
	 * (The list if empty if this is just an Entity and not an Association)
	 * @param rentity The RealizingAssociation/ Entity
	 * @return
	 */
	public List<BaseParticipant> getRealizedParticipants(RealizingEntity rentity) {
		if (!realizingProcessor.isAssociation(rentity)) {
			// If it isn't an association then it has no participants
			BaseEntity be = getRealizedEntity(rentity);
			if (baseProcessor.isAssociation(be)) {
				// If the base is an association but the realization isn't, then that's an error
				String msg = MessageFormat.format(ASSOCIATION_REALIZATION_ERR_FMT, 
						qnp.getFullyQualifiedName(be).toString(), qnp.getFullyQualifiedName(rentity).toString());
				logger.error(msg);
				//throw new RealizationException(msg); // TODO: Throw or return empty list? See also (logical/platform)Extensions.ocl invariants - this is checked there
				return new ArrayList<BaseParticipant>();
			}
			else {
				// This is not an association - it has no participants
				return new ArrayList<BaseParticipant>();
			}
		}
		else {
			Map<String, RealizingParticipant> allParticipants = realizingProcessor.allParticipants(realizingProcessor.conv2Association(rentity));
			// Select the keys from allCompositions - filter out all other CCs
			List<BaseParticipant> result = new ArrayList<BaseParticipant>();
			for (RealizingParticipant p : allParticipants.values()) {
				result.add(getRealizedParticipant(p));
			}
			return result;
			
		}
	}

	/**
	 * Return the set of BaseAssociation participants that are not yet realized in the RealizingAssociation
	 * (The list if empty if this is just an Entity and not an Association)
	 * @param rentity The RealizingAssociation/ Entity
	 * @return
	 */
	public Collection<BaseParticipant> getUnrealizedParticipants(RealizingEntity rentity) {
		List<BaseParticipant> realized = getRealizedParticipants(rentity);

		BaseEntity baseEntity = getRealizedEntity(rentity);
		if (!baseProcessor.isAssociation(baseEntity)) {
			// Base isn't an association so there are no participants
			return new ArrayList<BaseParticipant>();
		} else {
			Map<String, BaseParticipant> allBaseParticipants = baseProcessor.allParticipants(baseProcessor.conv2Association(baseEntity));

			Collection<BaseParticipant> remainingValues = allBaseParticipants.values();
			remainingValues.removeAll(realized);
			return remainingValues;
			
		}
	}

	/**
	 * Find all types that can be used to realize the specified type
	 * NOTE: Because Observables can be realized by Measurments (LogicalComposableElements) or
	 * MeasurementAxis (not a LogicalComposableElement), we can't cast the search results to LogicalComposableElement
	 * here. End result must check and do the cast.
	 * @param pkgs All the root ePackages that must be registered for the search
	 * @param type2Realize The type we want to realize
	 * @return
	 */
	//@SuppressWarnings("unchecked") // use of 'realizes' guarantees cast to RealizingComposableElement will succeed
	public Collection<EObject> getRealizingTypes( /*List<EPackage> pkgs,*/ EObject type2Realize) {
		Map<String,Object> variables = new HashMap<String,Object>();
		variables.put("self", type2Realize);
		ResourceSet resourceSet = type2Realize.eResource().getResourceSet();
		Collection<EObject> found = ndxUtil.processAQL(resourceSet, /*pkgs,*/variables,"self.eInverse('realizes')");
		return found;

	}
	
	/**
	 * Find a single realizing type, logging if none or mone than one is found.
	 * 
	 * @param pkgs All the root ePackages that must be registered for the search
	 * @param type2Realize The type we want to realize
	 * @param emptyMsgFmt Message format string for empty collection
	 * @param manyMsgFmt Message format string for collection with many elements
	 * @return
	 */
	public EObject getRealizingType( /*List<EPackage> pkgs,*/ EObject type2Realize, String emptyMsgFmt, String manyMsgFmt) {
		Collection<EObject> found = getRealizingTypes(/*pkgs,*/ type2Realize);
		if ((found == null) || (found.size() == 0 )) {		
			String msg = MessageFormat.format(emptyMsgFmt, qnp.getFullyQualifiedName(type2Realize));
			logger.error(msg);
			return null;
		} 
		if (found.size() > 1) {
			String msg = MessageFormat.format(manyMsgFmt, qnp.getFullyQualifiedName(type2Realize));
			logger.info(msg);		
		}
		// Return the first one found
		return found.iterator().next();
	}
	

}
