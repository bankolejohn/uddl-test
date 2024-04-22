/**
 * 
 */
package com.epistimis.uddl.ui.contentassist;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.epistimis.uddl.EntityProcessor;
import com.epistimis.uddl.RealizationProcessor;
import com.epistimis.uddl.UddlQNP;
import com.epistimis.uddl.util.IndexUtilities;
import com.epistimis.uddl.uddl.UddlElement;
import com.google.inject.Inject;

/**
 * 
 */
abstract class EntityRealizationProposalProcessor<BaseComposableElement extends UddlElement, RealizingComposableElement extends UddlElement, 
											BaseEntity extends BaseComposableElement, RealizingEntity extends RealizingComposableElement, 
											BaseCharacteristic extends EObject, RealizingCharacteristic extends EObject, 
											BaseComposition extends BaseCharacteristic, RealizingComposition extends RealizingCharacteristic, 
											BaseParticipant extends BaseCharacteristic, RealizingParticipant extends RealizingCharacteristic, 
											BaseAssociation extends BaseEntity, RealizingAssociation extends RealizingEntity, 
											RezProcessor extends RealizationProcessor<BaseComposableElement, RealizingComposableElement, BaseEntity, RealizingEntity, BaseCharacteristic, RealizingCharacteristic, BaseComposition, RealizingComposition, BaseParticipant, RealizingParticipant, BaseAssociation, RealizingAssociation, BaseProcessor, RealizingProcessor>, 
											BaseProcessor extends EntityProcessor<BaseComposableElement, BaseCharacteristic, BaseEntity, BaseAssociation, BaseComposition, BaseParticipant, ?, ?>, 
											RealizingProcessor extends EntityProcessor<RealizingComposableElement, RealizingCharacteristic, RealizingEntity, RealizingAssociation, RealizingComposition, RealizingParticipant, ?, ?>> {

	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

	protected static String COMPOSITION_FMT_STRING 	= "%s%s %s[%d:%d] \"%s\" -> %s;\n";
	protected static String PARTICIPANT_PREFIX 		= "\n%sparticipants: [\n" ;
	protected static String PARTICIPANT_SUFFIX 		= "%s]" ;
	protected static String PARTICIPANT_FMT_STRING 	= "%s\t%s %s[%d:%d] \"%s\" -> %s { source: [ %s : %d ] };\n";
	protected static String DUMMY_TYPE 				= "__ReplaceMe__";
	protected static String DEFAULT_CMT 			= "%s// Replace " + DUMMY_TYPE + " with the ComposableElement type for each composition\n";
	protected static String PROPOSAL_PREFIX 		= "(Default) ";
	protected static String PROPOSAL_SUFFIX 		= "";
	protected static String REALIZE_ALL 			= "<<Default Realize All>>";
	protected static String REALIZE_REMAINING 		= "<<Default Realize Remaining>>";

	protected static String ASSOC_REALIZATIION_ERR	= "Associtation {0} realizes {1} which is not an Association";
	
	
	@Inject	UddlQNP qnp;
	@Inject PropUtils pu;
	
	@Inject IndexUtilities ndxUtil;
	
	@Inject RezProcessor rezProc;
	@Inject BaseProcessor bProc;
	@Inject RealizingProcessor rProc; 

	/**
	 * A callback to call the appropriate 'superComplete(L/P)Composition' method which can only be done from the ProposalProvider derived class.
	 */
	abstract protected void completeSuperRealizingComposition(UddlProposalProvider pp, EObject obj, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor);

	/**
	 * A callback to call the appropriate 'superComplete(L/P)Composition_Rolename' method which can only be done from the ProposalProvider derived class.
	 */
	abstract protected void completeSuperRealizingComposition_Rolename(UddlProposalProvider pp, EObject obj,
			Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor);

	/**
	 * Create the display string for the proposal (what shows up in the Popup dialog). This works for
	 * both compositions and participants.
	 * @param bc The BaseCharacteristic for which to generate the display string
	 * @return The display string for the proposal
	 */
	abstract protected String proposalDisplayString(BaseCharacteristic bc);

	/**
	 * Create the insertion string for the entire composition (all attributes)
	 * @param bc The BaseComposition for which to generate the insertion string
	 * @param indent How many tabs to indent
	 * @return The insertion string for the entire composition
	 */
	abstract protected String compositionInsertionString(BaseComposition bc, String indent);

	/**
	 * Create the insertion string for the entire participant (all attributes)
	 * @param bp The BaseParticipant for which to generate the insertion string
	 * @param indent How many tabs to indent
	 * @return The insertion string for the entire participant
	 */
	abstract protected String participantInsertionString(BaseParticipant bc, String indent);

	/**
	 * Realizing types don't have a common ancestor other than EObject. Cast them appropriately
	 * in the implementation.
	 * @param realizingType
	 * @return
	 */
	abstract protected String getRealizingTypeName(EObject realizingType);
	
	/**
	 * Get the 'type' reference to be used when creating a scope for FQN shortening on a reference.
	 */
	abstract protected EReference getCompositionTypeReference();

	/**
	 * Get the 'realizes' reference to be used when creating a scope for FQN shortening on a reference.
	 */
	abstract protected EReference getCompositionRealizesReference();
	
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
	public Class getRezProcessorType() {
		return returnedTypeParameter(12);
	}

	@SuppressWarnings("rawtypes")
	public Class getBaseProcessorType() {
		return returnedTypeParameter(13);
	}

	@SuppressWarnings("rawtypes")
	public Class getRealizingProcessorType() {
		return returnedTypeParameter(14);
	}

	/**
	 * In general, we want to indent the content of any container based on the number of levels so far,
	 * which we can determine by looking at the number segments in the FQN
	 * @param object
	 * @return
	 */
	protected String contentIndent(EObject object) {
		return PropUtils.indent(qnp.getFullyQualifiedName(object).getSegmentCount());

	}
	/**
	 * Complates compositions - and participants too!
	 * @param pp
	 * @param rproc
	 * @param rentity
	 * @param ruleCall
	 * @param context
	 * @param acceptor
	 */
	public void complete_Composition(UddlProposalProvider pp, RezProcessor rproc, RealizingEntity rentity,
			RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		// Get all the standard stuff first
//		completeSuperRealizingComposition(pp, rentity, ruleCall, context, acceptor);

		String indent = contentIndent(rentity);
		// Now add customization here
		// When doing this, propose that all ConceptualCompositions be realized - but
		// only those that
		List<BaseComposition> realized = rproc.getRealizedCompositions(rentity);
		Collection<BaseComposition> unrealized = rproc.getUnrealizedCompositions(rentity);
		List<BaseParticipant> realizedParticipants = rproc.getRealizedParticipants(rentity);
		Collection<BaseParticipant> unrealizedParticipants = rproc.getUnrealizedParticipants(rentity);

		String result = "";
		if (!unrealized.isEmpty()) {
			result += String.format(DEFAULT_CMT, indent); // Add the comment if we will also add some unrealized compositions
		}
		for (BaseComposition cc : unrealized) {
			// If this one isn't already realized, then add it to the proposal
			String oneRealizedCC = compositionInsertionString(cc,indent);
			String displayString = proposalDisplayString(cc);
			acceptor.accept(pp.createCompletionProposal(oneRealizedCC, displayString, null, context));
			result += oneRealizedCC;
		}
		if (!unrealizedParticipants.isEmpty()) {
			result += String.format(PARTICIPANT_PREFIX, indent); //"\n participants: [";

			for (BaseParticipant cp : unrealizedParticipants) {
				// If this one isn't already realized, then add it to the proposal
				String insertionString = participantInsertionString(cp,indent);
				/** 
				 * Since we are completing compositions, we can't propose individual participants - so we don't add them 
				 * to the acceptor here. However, we can build up the result to propose adding everything that remains (including participants)
				 */
				//String displayString = proposalDisplayString(cp);
				//ICompletionProposal prop = pp.createCompletionProposal(insertionString,displayString, null, context);
				//acceptor.accept(prop);
				
				result += insertionString;
			}
			result += String.format(PARTICIPANT_SUFFIX, indent); //]";
		}
		/**
		 * Only do the "all" if nothing has been done yet
		 */
		if (realized.isEmpty() && realizedParticipants.isEmpty()) {
			acceptor.accept(pp.createCompletionProposal(result, REALIZE_ALL, null, context));
		} else if (!unrealized.isEmpty() || !unrealizedParticipants.isEmpty()) {
			acceptor.accept(pp.createCompletionProposal(result, REALIZE_REMAINING, null, context));
		}

	}

	private Collection<EObject> getRealizingCTypes(BaseComposition bcomp) {
		BaseComposableElement baseType = bProc.getCompositionType(bcomp);
		if (baseType != null) {
			return rezProc.getRealizingTypes(baseType);
		}
		return new HashSet<>();
	}
	/**
	 * The type must be a type that realizes the BaseComposition, if it has been
	 * specified. If not, then it must be a type that realizes one of the
	 * BaseCompositions from the BaseEntity realized by the containing
	 * RealizingEntity.
	 * 
	 * @param pp
	 * @param rentity
	 * @param assignment
	 * @param context
	 * @param acceptor
	 */
	public void completeComposition_Type(UddlProposalProvider pp,  RealizingEntity rentity, //RealizingComposition rcomp,
					Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
//		BaseComposition bcomp = rezProc.getRealizedComposition(rcomp);
//		BaseComposableElement baseType = bProc.getCompositionType(bcomp);
		Set<EObject> realizingTypes = new HashSet<EObject>();
//		if (baseType != null) {
//			realizingTypes = rezProc.getRealizingTypes(baseType);
//		}
//		else {
			// Get the types for all the composition elements in the realized Entity
			BaseEntity bentity = rezProc.getRealizedEntity(rentity);
			Collection<BaseComposition> bcomps = bProc.allCompositions(bentity).values();
			for (BaseComposition bc : bcomps) {
				realizingTypes.addAll(getRealizingCTypes(bc));
			}
//		}
		/*
		 * If there are **no** realizing types, then we've got a lot of work to do
		 */
		if (realizingTypes.isEmpty()) {
			String msg = MessageFormat.format("No realizing types found for any composition elements of {0}",qnp.getFullyQualifiedName(rentity));
			logger.info(msg);
		}
		for (EObject rce: realizingTypes) {
			String insertionString = qnp.minimalReferenceString( rce, rentity);
			String displayString = getRealizingTypeName(rce);
			ICompletionProposal prop = pp.createCompletionProposal(insertionString,displayString, null, context);
			prop = pu.modifyConfigurableCompletionProposal(prop, context, getCompositionTypeReference(), ((UddlElement)rce).getDescription());
			acceptor.accept(prop);
		}
	}

	public void completeComposition_Rolename(UddlProposalProvider pp, RezProcessor rproc, RealizingEntity rentity,
			Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
//		completeSuperRealizingComposition_Rolename(pp, rentity, assignment, context, acceptor);

		// Pick out the roles from the list of unrealized Compositions
		for (BaseComposition cc : rproc.getUnrealizedCompositions(rentity)) {
			// If this one isn't already realized, then add it to the proposal
			String oneRealizedCC = compositionInsertionString(cc,"");
			String displayString = proposalDisplayString(cc);
			acceptor.accept(pp.createCompletionProposal(oneRealizedCC, displayString, null, context));
		}
	}

	public void completeComposition_Realizes(UddlProposalProvider pp, RezProcessor rproc, RealizingEntity rentity,
			Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {

		for (BaseComposition cc : rproc.getUnrealizedCompositions(rentity)) {
			String displayString = proposalDisplayString(cc);
			ICompletionProposal prop = pp.createCompletionProposal(qnp.getFullyQualifiedName(cc).toString(),displayString, null, context);
			prop = pu.modifyConfigurableCompletionProposal(prop, context, getCompositionRealizesReference(),bProc.getCharacteristicDescription(cc));
			acceptor.accept(prop);
		}

	}

	public void complete_Participant(UddlProposalProvider pp, RezProcessor rproc, RealizingEntity rentity,
			RuleCall ruleCall, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {

		String indent = contentIndent(rentity);
		// Now add customization here
		// When doing this, propose that all ConceptualCompositions be realized - but
		// only those that
		List<BaseParticipant> realizedParticipants = rproc.getRealizedParticipants(rentity);
		Collection<BaseParticipant> unrealizedParticipants = rproc.getUnrealizedParticipants(rentity);

		String result = String.format(DEFAULT_CMT, indent); 
		if (!unrealizedParticipants.isEmpty()) {
			/**
			 * We don't use the participant prefix/ suffix here because those must already be in place to create the context 
			 * that triggers this method.
			 */
			//result += String.format(PARTICIPANT_PREFIX, indent); //"\n participants: [";

			for (BaseParticipant cp : unrealizedParticipants) {
				// If this one isn't already realized, then add it to the proposal
				String insertionString = participantInsertionString(cp,indent);
				String displayString = proposalDisplayString(cp);
				ICompletionProposal prop = pp.createCompletionProposal(insertionString,displayString, null, context);
				acceptor.accept(prop);
				result += insertionString;
			}
			//result += String.format(PARTICIPANT_SUFFIX, indent); //]";
		}
		/**
		 * Only do the "all" if nothing has been done yet
		 */
		if (realizedParticipants.isEmpty()) {
			acceptor.accept(pp.createCompletionProposal(result, REALIZE_ALL, null, context));
		} else if ( !unrealizedParticipants.isEmpty()) {
			acceptor.accept(pp.createCompletionProposal(result, REALIZE_REMAINING, null, context));
		}

	}

	private Collection<EObject> getRealizingPTypes(BaseParticipant bcomp) {
		BaseComposableElement baseType = bProc.getParticipantType(bcomp);
		if (baseType != null) {
			return rezProc.getRealizingTypes(baseType);
		}
		return new HashSet<>();
	}
	
	/**
	 * The type must be a type that realizes the BaseParticipant, if it has been
	 * specified. If not, then it must be a type that realizes one of the
	 * BaseParticipants from the BaseAssociation realized by the containing
	 * RealizingAssociation.
	 * 
	 * @param pp
	 * @param rassoc
	 * @param assignment
	 * @param context
	 * @param acceptor
	 */
	public void completeParticipant_Type(UddlProposalProvider pp,  RealizingAssociation rassoc, 
					Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
//		BaseComposition bcomp = rezProc.getRealizedComposition(rcomp);
//		BaseComposableElement baseType = bProc.getCompositionType(bcomp);
		Set<EObject> realizingTypes = new HashSet<EObject>();
//		if (baseType != null) {
//			realizingTypes = rezProc.getRealizingTypes(baseType);
//		}
//		else {
			// Get the types for all the composition elements in the realized Entity
			BaseEntity bentity = rezProc.getRealizedEntity(rassoc);
			// The base entity must be a base association. If not, there is an error
			if (!bProc.isAssociation(bentity)) {
				logger.error(MessageFormat.format(ASSOC_REALIZATIION_ERR,qnp.getFullyQualifiedName(rassoc).toString(),qnp.getFullyQualifiedName(bentity).toString()));
				return;
			}
			BaseAssociation bAssoc = bProc.conv2Association(bentity);
			
			Collection<BaseParticipant> parts = bProc.allParticipants(bAssoc).values();
			for (BaseParticipant bp : parts) {
				realizingTypes.addAll(getRealizingPTypes(bp));
			}
//		}
		/*
		 * If there are **no** realizing types, then we've got a lot of work to do
		 */
		if (realizingTypes.isEmpty()) {
			String msg = MessageFormat.format("No realizing types found for any participant elements of {0}",qnp.getFullyQualifiedName(rassoc));
			logger.info(msg);
		}
		for (EObject rce: realizingTypes) {
			String insertionString = qnp.minimalReferenceString( rce, rassoc);
			String displayString = getRealizingTypeName(rce);
			ICompletionProposal prop = pp.createCompletionProposal(insertionString,displayString, null, context);
			prop = pu.modifyConfigurableCompletionProposal(prop, context, getCompositionTypeReference(), ((UddlElement)rce).getDescription());
			acceptor.accept(prop);
		}
	}

	
}
