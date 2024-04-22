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

import com.epistimis.uddl.LPRealizationProcessor;
import com.epistimis.uddl.LogicalEntityProcessor;
import com.epistimis.uddl.PlatformEntityProcessor;
import com.epistimis.uddl.uddl.LogicalAbstractMeasurement;
import com.epistimis.uddl.uddl.LogicalAssociation;
import com.epistimis.uddl.uddl.LogicalCharacteristic;
import com.epistimis.uddl.uddl.LogicalComposableElement;
import com.epistimis.uddl.uddl.LogicalComposition;
import com.epistimis.uddl.uddl.LogicalEntity;
import com.epistimis.uddl.uddl.LogicalParticipant;
import com.epistimis.uddl.uddl.PlatformAssociation;
import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformComposableElement;
import com.epistimis.uddl.uddl.PlatformComposition;
import com.epistimis.uddl.uddl.PlatformDataType;
import com.epistimis.uddl.uddl.PlatformEntity;
import com.epistimis.uddl.uddl.PlatformParticipant;
import com.epistimis.uddl.uddl.UddlPackage;

/**
 * 
 */
public class LPRealizationProposalProcessor extends
		EntityRealizationProposalProcessor<LogicalComposableElement, PlatformComposableElement, 
			LogicalEntity, PlatformEntity, 
			LogicalCharacteristic, PlatformCharacteristic, 
			LogicalComposition, PlatformComposition, 
			LogicalParticipant, PlatformParticipant, 
			LogicalAssociation, PlatformAssociation, 
			LPRealizationProcessor, LogicalEntityProcessor, PlatformEntityProcessor> {

	final public static String ABS_MEAS_REALIZATION_ERR 	= "AbstractMeasurement {0} is not realized by any PlatformDataType";
	final public static String ABS_MEAS_REALIZATION_MANY 	= "AbstractMeasurement {0} is realized by multiple PlatformDataTypes - picking one";
	final public static String ENTITY_REALIZATION_ERR 		= "LogicalEntity {0} is not realized by any PlatformEntity";
	final public static String ENTITY_REALIZATION_MANY 		= "LogicalEntity {0} is realized by multiple PlatformEntity - picking one";


	@Override
	protected void completeSuperRealizingComposition(UddlProposalProvider pp, EObject obj, RuleCall ruleCall, ContentAssistContext context,
			ICompletionProposalAcceptor acceptor) {
		pp.superComplete_PlatformComposition(obj, ruleCall, context, acceptor);	
	}

	@Override
	protected void completeSuperRealizingComposition_Rolename(UddlProposalProvider pp, EObject obj,
			Assignment assignment,ContentAssistContext context, ICompletionProposalAcceptor acceptor) {
		pp.superCompletePlatformComposition_Rolename(obj, assignment, context, acceptor);
		
	}

	@Override
	protected String proposalDisplayString(LogicalCharacteristic bc) {

		return PROPOSAL_PREFIX + bc.getRolename() + PROPOSAL_SUFFIX;
	}

	@Override
	protected String compositionInsertionString(LogicalComposition bc, String indent) {
		String typeName = DUMMY_TYPE;
		PlatformComposableElement ce = null;
		if (bc.getType() instanceof LogicalEntity) {
			ce = (PlatformComposableElement) rezProc.getRealizingType(bc.getType(),ENTITY_REALIZATION_ERR,ENTITY_REALIZATION_MANY);
		} else if (bc.getType() instanceof LogicalAbstractMeasurement) {
			ce = (PlatformComposableElement) rezProc.getRealizingType(bc.getType(),ABS_MEAS_REALIZATION_ERR,ABS_MEAS_REALIZATION_MANY);
			
		}
		if (ce != null) {
			typeName = qnp.minimalReferenceString(ce,  bc);

		}
		return String.format(COMPOSITION_FMT_STRING,indent, typeName, bc.getRolename(),
				bc.getLowerBound(), bc.getUpperBound(), bc.getDescription(),
				qnp.getFullyQualifiedName(bc).toString());
	}

	@Override
	protected String participantInsertionString(LogicalParticipant bc, String  indent) {
		String typeName = DUMMY_TYPE;
		PlatformEntity ce = (PlatformEntity) rezProc.getRealizingType(bc.getType(),ENTITY_REALIZATION_ERR,ENTITY_REALIZATION_MANY);
		if (ce != null) {
			typeName = qnp.minimalReferenceString(ce,  bc);
		}
		return String.format(PARTICIPANT_FMT_STRING,indent, typeName, bc.getRolename(),
				bc.getLowerBound(), bc.getUpperBound(), bc.getDescription(),
				qnp.getFullyQualifiedName(bc).toString(), bc.getSourceLowerBound(), bc.getSourceUpperBound());
	}

	@Override
	protected String getRealizingTypeName(EObject realizingType) {
		// TODO Auto-generated method stub
		return ((PlatformDataType)realizingType).getName();
	}


	@Override
	protected EReference getCompositionTypeReference() {
		return UddlPackage.Literals.PLATFORM_COMPOSITION__TYPE;
	}

	@Override
	protected EReference getCompositionRealizesReference() {
		return UddlPackage.Literals.PLATFORM_COMPOSITION__REALIZES;
	}
}
