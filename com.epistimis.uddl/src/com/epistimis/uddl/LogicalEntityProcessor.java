/**
 * 
 */
package com.epistimis.uddl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.epistimis.uddl.uddl.LogicalAssociation;
import com.epistimis.uddl.uddl.LogicalCharacteristic;
import com.epistimis.uddl.uddl.LogicalComposableElement;
import com.epistimis.uddl.uddl.LogicalComposition;
import com.epistimis.uddl.uddl.LogicalDataModel;
import com.epistimis.uddl.uddl.LogicalEntity;
import com.epistimis.uddl.uddl.LogicalMeasurement;
import com.epistimis.uddl.uddl.LogicalParticipant;
import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.uddl.UddlPackage;

/**
 * 
 */
public class LogicalEntityProcessor extends
		EntityProcessor<LogicalComposableElement, LogicalCharacteristic, LogicalEntity, LogicalAssociation, LogicalComposition, LogicalParticipant, LogicalMeasurement, LogicalDataModel> {

	@Override
	public EClass getEntityEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getLogicalEntity();

	}
	
	@Override
	public EClass getCompositionEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getLogicalComposition();
	}

	@Override
	public EClass getParticipantEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getLogicalParticipant();
	}

	public String getCharacteristicRolename(LogicalCharacteristic obj) {
		return obj.getRolename();
	}

	@Override
	public LogicalEntity getSpecializes(LogicalEntity ent) {
		// TODO Auto-generated method stub
		return ent.getSpecializes();
	}

	@Override
	public boolean isAssociation(LogicalEntity ent) {
		// TODO Auto-generated method stub
		return (ent instanceof LogicalAssociation);
	}

	@Override
	public LogicalAssociation conv2Association(LogicalEntity ent) {
		// TODO Auto-generated method stub
		return (LogicalAssociation)ent;
	}

	@Override
	public EList<LogicalComposition> getComposition(LogicalEntity obj) {
		// TODO Auto-generated method stub
		return obj.getComposition();
	}

	@Override
	public EList<LogicalParticipant> getParticipant(LogicalAssociation obj) {
		// TODO Auto-generated method stub
		return obj.getParticipant();
	}

	@Override
	public LogicalComposition conv2Composition(LogicalCharacteristic characteristic) {
		// TODO Auto-generated method stub
		return (LogicalComposition)characteristic;
	}

	@Override
	public LogicalParticipant conv2Participant(LogicalCharacteristic characteristic) {
		// TODO Auto-generated method stub
		return (LogicalParticipant)characteristic;
	}

	@Override
	public LogicalComposableElement getCompositionType(LogicalComposition comp) {
		// TODO Auto-generated method stub
		return comp.getType();
	}

	@Override
	public LogicalEntity getParticipantType(LogicalParticipant part) {
		// TODO Auto-generated method stub
		return part.getType();
	}

	@Override
	public String getCharacteristicDescription(LogicalCharacteristic characteristic)
	{
		return characteristic.getDescription();
	}

	@Override
	public boolean isContainer(UddlElement obj) {
		// TODO Auto-generated method stub
		return (obj instanceof LogicalDataModel);
	}

	@Override
	public LogicalDataModel conv2Container(UddlElement obj) {
		// TODO Auto-generated method stub
		return (LogicalDataModel)obj;
	}

	@Override
	public EList<? extends UddlElement> getElement(LogicalDataModel obj) {
		// TODO Auto-generated method stub
		return obj.getElement();
	}

}
