/**
 * 
 */
package com.epistimis.uddl;

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
import com.epistimis.uddl.uddl.LogicalParticipant;

/**
 * 
 */
public class CLRealizationProcessor extends
		RealizationProcessor<ConceptualComposableElement, LogicalComposableElement, 
								ConceptualEntity, LogicalEntity, ConceptualCharacteristic, LogicalCharacteristic, 
								ConceptualComposition, LogicalComposition, ConceptualParticipant, LogicalParticipant, 
								ConceptualAssociation, LogicalAssociation, ConceptualEntityProcessor, LogicalEntityProcessor> {

	@Override
	public ConceptualEntity getRealizedEntity(LogicalEntity rent) {
		// TODO Auto-generated method stub
		return rent.getRealizes();
	}

	@Override
	public ConceptualComposition getRealizedComposition(LogicalComposition rcomp) {
		// TODO Auto-generated method stub
		return rcomp.getRealizes();
	}

	@Override
	public ConceptualParticipant getRealizedParticipant(LogicalParticipant rpart) {
		// TODO Auto-generated method stub
		return rpart.getRealizes();
	}

}
