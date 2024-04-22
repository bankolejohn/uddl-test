/**
 * 
 */
package com.epistimis.uddl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.naming.IQualifiedNameProvider;

import com.epistimis.uddl.uddl.ConceptualAssociation;
import com.epistimis.uddl.uddl.ConceptualCharacteristic;
import com.epistimis.uddl.uddl.ConceptualComposableElement;
import com.epistimis.uddl.uddl.ConceptualComposition;
import com.epistimis.uddl.uddl.ConceptualDataModel;
import com.epistimis.uddl.uddl.ConceptualEntity;
import com.epistimis.uddl.uddl.ConceptualObservable;
import com.epistimis.uddl.uddl.ConceptualParticipant;
import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.uddl.UddlPackage;
import com.google.inject.Inject;

/**
 * 
 */
public class ConceptualEntityProcessor extends
		EntityProcessor<ConceptualComposableElement, ConceptualCharacteristic, ConceptualEntity, 
		ConceptualAssociation, ConceptualComposition, ConceptualParticipant, 
		ConceptualObservable, ConceptualDataModel> {

	@Inject
	IQualifiedNameProvider qnp; // = new UddlQNP();

	@Override
	public EClass getEntityEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getConceptualEntity();

	}
	@Override
	public EClass getCompositionEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getConceptualComposition();
	}

	@Override
	public EClass getParticipantEClass() {
		// TODO Auto-generated method stub
		return UddlPackage.eINSTANCE.getConceptualParticipant();
	}

	public String getCharacteristicRolename(ConceptualCharacteristic obj) {
		return obj.getRolename();
	}

	@Override
	public ConceptualEntity getSpecializes(ConceptualEntity ent) {
		// TODO Auto-generated method stub
		return ent.getSpecializes();
	}

	@Override
	public boolean isAssociation(ConceptualEntity ent) {
		// TODO Auto-generated method stub
		return (ent instanceof ConceptualAssociation);
	}

	@Override
	public ConceptualAssociation conv2Association(ConceptualEntity ent) {
		// TODO Auto-generated method stub
		return (ConceptualAssociation) ent;
	}

	@Override
	public EList<ConceptualComposition> getComposition(ConceptualEntity obj) {
		// TODO Auto-generated method stub
		return obj.getComposition();
	}

	@Override
	public EList<ConceptualParticipant> getParticipant(ConceptualAssociation obj) {
		// TODO Auto-generated method stub
		return obj.getParticipant();
	}

	@Override
	public ConceptualComposition conv2Composition(ConceptualCharacteristic characteristic) {
		// TODO Auto-generated method stub
		return (ConceptualComposition) characteristic;
	}
	@Override
	public ConceptualParticipant conv2Participant(ConceptualCharacteristic characteristic) {
		// TODO Auto-generated method stub
		return (ConceptualParticipant)characteristic;
	}

	@Override
	public ConceptualComposableElement getCompositionType(ConceptualComposition comp) {
		// TODO Auto-generated method stub
		return comp.getType();
	}
	@Override
	public ConceptualEntity getParticipantType(ConceptualParticipant part) {
		// TODO Auto-generated method stub
		return part.getType();
	}

	@Override
	public String getCharacteristicDescription(ConceptualCharacteristic characteristic)
	{
		return characteristic.getDescription();
	}

	@Override
	public boolean isContainer(UddlElement obj) {
		// TODO Auto-generated method stub
		return (obj instanceof ConceptualDataModel);
	}

	@Override
	public ConceptualDataModel conv2Container(UddlElement obj) {
		// TODO Auto-generated method stub
		return (ConceptualDataModel) obj;
	}

	@Override
	public EList<? extends UddlElement> getElement(ConceptualDataModel obj) {
		// TODO Auto-generated method stub
		return obj.getElement();
	}

}
