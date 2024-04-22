/**
 * 
 */
package com.epistimis.uddl.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtext.Assignment;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.contentassist.ICompletionProposalAcceptor;

import com.epistimis.uddl.CLRealizationProcessor;
import com.epistimis.uddl.ConceptualEntityProcessor;
import com.epistimis.uddl.LogicalEntityProcessor;
import com.epistimis.uddl.uddl.ConceptualAssociation;
import com.epistimis.uddl.uddl.ConceptualCharacteristic;
import com.epistimis.uddl.uddl.ConceptualComposableElement;
import com.epistimis.uddl.uddl.ConceptualComposition;
import com.epistimis.uddl.uddl.ConceptualEntity;
import com.epistimis.uddl.uddl.ConceptualParticipant;
import com.epistimis.uddl.uddl.LogicalAssociation;
import com.epistimis.uddl.uddl.LogicalCharacteristic;
import com.epistimis.uddl.uddl.LogicalComposableElement;
import com.epistimis.uddl.uddl.LogicalComposition;
import com.epistimis.uddl.uddl.LogicalEntity;
import com.epistimis.uddl.uddl.LogicalMeasurement;
import com.epistimis.uddl.uddl.LogicalMeasurementAxis;
import com.epistimis.uddl.uddl.LogicalParticipant;
import com.epistimis.uddl.uddl.UddlPackage;

/**
 * 
 */
public class CLRealizationProposalProcessor extends
		EntityRealizationProposalProcessor<ConceptualComposableElement, LogicalComposableElement, 
			ConceptualEntity, LogicalEntity, 
			ConceptualCharacteristic, LogicalCharacteristic, 
			ConceptualComposition, LogicalComposition, 
			ConceptualParticipant, LogicalParticipant, 
			ConceptualAssociation, LogicalAssociation, 
			CLRealizationProcessor, ConceptualEntityProcessor, LogicalEntityProcessor> {

	final public static String OBSERVABLE_REALIZATION_ERR = "Observable {0} is not realized by any AbstractMeasurment or LogicalEntity";
	final public static String OBSERVABLE_REALIZATION_MANY = "Observable {0} is realized by multiple AbstractMeasurments / LogicalEntities - picking one";
	final public static String ENTITY_REALIZATION_ERR = "ConceptualEntity {0} is not realized by any LogicalEntity";
	final public static String ENTITY_REALIZATION_MANY = "ConceptualEntity {0} is realized by multiple LogicalEntity - picking one";

	@Override
	protected void completeSuperRealizingComposition(UddlProposalProvider pp, EObject obj, RuleCall ruleCall,
			ContentAssistContext context, ICompletionProposalAcceptor acceptor) {

		pp.superComplete_LogicalComposition(obj, ruleCall, context, acceptor);
	}

	@Override
	protected void completeSuperRealizingComposition_Rolename(UddlProposalProvider pp, EObject obj,
			Assignment assignment, ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		pp.superCompleteLogicalComposition_Rolename(obj, assignment, context, acceptor);

	}

	@Override
	protected String proposalDisplayString(ConceptualCharacteristic bc) {

		return PROPOSAL_PREFIX + bc.getRolename() + PROPOSAL_SUFFIX;
	}

	@Override
	protected String compositionInsertionString(ConceptualComposition bc, String indent) {
		String typeName = DUMMY_TYPE;

		/**
		 * Per the Spec/RIG, Observables can be realized by either Measurements (which
		 * are LogicalComposableElements) or MeasurementAxis (which aren't
		 * LogicalComposableElements). That means we can't cast the return value to
		 * LogicalComposableElement.
		 */
		// com.epistimis.uddl.ModelFilters.getValueTypeUnit(PlatformDataType pdt)
		// handles the opposite case
		EObject ce = rezProc.getRealizingType(bc.getType(), OBSERVABLE_REALIZATION_ERR, OBSERVABLE_REALIZATION_MANY);
		if (ce != null) {
			typeName = qnp.minimalReferenceString(ce, bc); //qnp.relativeQualifiedName(ce, bc).toString();
		}

		return String.format(COMPOSITION_FMT_STRING,indent, typeName, bc.getRolename(), bc.getLowerBound(),
				bc.getUpperBound(), bc.getDescription(), qnp.getFullyQualifiedName(bc).toString());
	}

	@Override
	protected String participantInsertionString(ConceptualParticipant bc, String indent) {
		String typeName = DUMMY_TYPE;
		EObject ce = rezProc.getRealizingType(bc.getType(), ENTITY_REALIZATION_ERR, ENTITY_REALIZATION_MANY);
		if (ce != null) {
			typeName = qnp.minimalReferenceString(ce, bc); //qnp.relativeQualifiedName(ce, bc).toString();
		}
		return String.format(PARTICIPANT_FMT_STRING,indent, typeName, bc.getRolename(), bc.getLowerBound(),
				bc.getUpperBound(), bc.getDescription(), qnp.getFullyQualifiedName(bc).toString(),
				bc.getSourceLowerBound(), bc.getSourceUpperBound());
	}

	@Override
	protected String getRealizingTypeName(EObject realizingType) {
		// TODO Auto-generated method stub
		if (realizingType instanceof LogicalMeasurement) {
			return ((LogicalMeasurement)realizingType).getName();
		}
		if (realizingType instanceof LogicalMeasurementAxis) {
			return ((LogicalMeasurementAxis)realizingType).getName();
		}
		// If we get here, need more
		return "Implement 'getRealizingTypeName' for " + realizingType.eClass().getName();
	}

	@Override
	protected EReference getCompositionTypeReference() {
		return UddlPackage.Literals.LOGICAL_COMPOSITION__TYPE;
	}

	@Override
	protected EReference getCompositionRealizesReference() {
		return UddlPackage.Literals.LOGICAL_COMPOSITION__REALIZES;
	}
}
