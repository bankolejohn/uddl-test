/**
 * 
 */
package com.epistimis.uddl;

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
import com.epistimis.uddl.uddl.PlatformEntity;
import com.epistimis.uddl.uddl.PlatformParticipant;

/**
 * 
 */
public class LPRealizationProcessor extends
		RealizationProcessor<LogicalComposableElement, PlatformComposableElement,
								LogicalEntity, PlatformEntity, LogicalCharacteristic, PlatformCharacteristic, 
								LogicalComposition, PlatformComposition, LogicalParticipant, PlatformParticipant, 
								LogicalAssociation, PlatformAssociation, LogicalEntityProcessor, PlatformEntityProcessor> {

	@Override
	public LogicalEntity getRealizedEntity(PlatformEntity rent) {
		// TODO Auto-generated method stub
		return rent.getRealizes();
	}

	@Override
	public LogicalComposition getRealizedComposition(PlatformComposition rcomp) {
		// TODO Auto-generated method stub
		return rcomp.getRealizes();
	}

	@Override
	public LogicalParticipant getRealizedParticipant(PlatformParticipant rpart) {
		// TODO Auto-generated method stub
		return rpart.getRealizes();
	}

}
