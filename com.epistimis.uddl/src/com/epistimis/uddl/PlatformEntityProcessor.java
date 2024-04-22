/**
 * 
 */
package com.epistimis.uddl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.epistimis.uddl.uddl.PlatformAssociation;
import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformComposableElement;
import com.epistimis.uddl.uddl.PlatformComposition;
import com.epistimis.uddl.uddl.PlatformDataModel;
import com.epistimis.uddl.uddl.PlatformDataType;
import com.epistimis.uddl.uddl.PlatformEntity;
import com.epistimis.uddl.uddl.PlatformParticipant;
import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.uddl.UddlPackage;

/**
 * 
 */
public class PlatformEntityProcessor extends
		EntityProcessor<PlatformComposableElement, PlatformCharacteristic, PlatformEntity, PlatformAssociation, PlatformComposition, PlatformParticipant, PlatformDataType, PlatformDataModel> {

	@Override
	public EClass getEntityEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getPlatformEntity();

	}

	@Override
	public EClass getCompositionEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getPlatformComposition();
	}

	@Override
	public EClass getParticipantEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getPlatformParticipant();
	}

	public String getCharacteristicRolename(PlatformCharacteristic obj) {
		return obj.getRolename();
	}

	@Override
	public PlatformEntity getSpecializes(PlatformEntity ent) {
		// TODO Auto-generated method stub
		return ent.getSpecializes();
	}

	@Override
	public boolean isAssociation(PlatformEntity ent) {
		// TODO Auto-generated method stub
		return (ent instanceof PlatformAssociation);
	}

	@Override
	public PlatformAssociation conv2Association(PlatformEntity ent) {
		// TODO Auto-generated method stub
		return (PlatformAssociation)ent;
	}

	@Override
	public EList<PlatformComposition> getComposition(PlatformEntity obj) {
		// TODO Auto-generated method stub
		return obj.getComposition();
	}

	@Override
	public EList<PlatformParticipant> getParticipant(PlatformAssociation obj) {
		// TODO Auto-generated method stub
		return obj.getParticipant();
	}

	@Override
	public PlatformComposition conv2Composition(PlatformCharacteristic characteristic) {
		// TODO Auto-generated method stub
		return (PlatformComposition)characteristic;
	}

	@Override
	public PlatformParticipant conv2Participant(PlatformCharacteristic characteristic) {
		// TODO Auto-generated method stub
		return (PlatformParticipant)characteristic;
	}

	@Override
	public PlatformComposableElement getCompositionType(PlatformComposition comp) {
		// TODO Auto-generated method stub
		return comp.getType();
	}

	@Override
	public PlatformEntity getParticipantType(PlatformParticipant part) {
		// TODO Auto-generated method stub
		return part.getType();
	}

	@Override
	public String getCharacteristicDescription(PlatformCharacteristic characteristic)
	{
		return characteristic.getDescription();
	}

	@Override
	public boolean isContainer(UddlElement obj) {
		// TODO Auto-generated method stub
		return (obj instanceof PlatformDataModel);
	}

	@Override
	public PlatformDataModel conv2Container(UddlElement obj) {
		// TODO Auto-generated method stub
		return (PlatformDataModel)obj;
	}

	@Override
	public EList<? extends UddlElement> getElement(PlatformDataModel obj) {
		// TODO Auto-generated method stub
		return obj.getElement();
	}


}
