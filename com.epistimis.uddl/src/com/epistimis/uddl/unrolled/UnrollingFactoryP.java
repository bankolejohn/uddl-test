/**
 * 
 */
package com.epistimis.uddl.unrolled;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.epistimis.uddl.uddl.PlatformAssociation;
import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformComposableElement;
import com.epistimis.uddl.uddl.PlatformComposition;
import com.epistimis.uddl.uddl.PlatformDataModel;
import com.epistimis.uddl.uddl.PlatformDataType;
import com.epistimis.uddl.uddl.PlatformEntity;
import com.epistimis.uddl.uddl.PlatformParticipant;

/**
 * 
 */
public class UnrollingFactoryP extends
		UnrollingFactory<PlatformComposableElement, 
							PlatformCharacteristic, 
							PlatformEntity, 
							PlatformAssociation, 
							PlatformComposition, 
							PlatformParticipant, 
							PlatformDataType, 
							PlatformDataModel,
							UnrolledComposableElementP,
							UnrolledCompositionP,
							UnrolledEntityP,
							UnrolledParticipantP,
							UnrolledAssociationP
							> {

	@Override
	public Set<Entry<PlatformComposableElement, UnrolledComposableElement<PlatformComposableElement>>> getC2REntrySet() {
		return UnrolledComposableElementP.allComposable2Unrolled.entrySet();
	}

	@SuppressWarnings("unchecked") // Return type matches type of map values
	@Override
	public UnrolledComposableElement<PlatformComposableElement> getUnrolledForComposable(PlatformComposableElement type) {
		// TODO Auto-generated method stub
		return  UnrolledComposableElementP.allComposable2Unrolled.get(type);
	}

	@Override
	PlatformComposableElement getType(UnrolledCompositionP uc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	Map<String, UnrolledCompositionP> getComposition(UnrolledEntityP entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	boolean isUEntity(Object uce) {
		// TODO Auto-generated method stub
		return (uce instanceof UnrolledEntityP);
	}


	@Override
	void clearMaps() {
		// TODO Auto-generated method stub
		UnrolledComposableElementP.allComposable2Unrolled.clear();
		UnrolledComposableElementP.allUnrolled2Composable.clear();
	}


	@Override
	String getName(PlatformComposableElement obj) {
		return obj.getName();
//		// TODO Auto-generated method stub
//		if (obj instanceof UnrolledComposableElementP) {
//			return ((UnrolledComposableElementP)obj).getName();
//		}
//		if (obj instanceof UnrolledEntityP) {
//			return ((UnrolledEntityP)obj).getName();			
//		}
//		if (obj instanceof UnrolledAssociationP) {
//			return ((UnrolledAssociationP)obj).getName();			
//		}
//
//		return "";
	}


	@Override
	boolean isAssociation(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		return obj instanceof PlatformAssociation;
	}


	@Override
	boolean isElementalComposable(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		return obj instanceof PlatformDataType;
	}


	@Override
	boolean isEntity(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		return obj instanceof PlatformEntity;
	}


	@Override
	PlatformAssociation conv2Association(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		return (PlatformAssociation)obj;
	}


	@Override
	PlatformDataType conv2ElementalComposable(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		return (PlatformDataType)obj;
	}


	@Override
	PlatformEntity conv2Entity(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		return (PlatformEntity)obj;
	}


	@Override
	UnrolledComposableElementP createElementalComposable(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		return new UnrolledDataType((PlatformDataType)obj);
	}


	@Override
	UnrolledEntityP createEntity(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		UnrolledEntityP retval = new UnrolledEntityP((PlatformEntity)obj);
//		updateMaps(entity, retval);
		return retval;
	}


	@Override
	UnrolledAssociationP createAssociation(PlatformComposableElement obj) {
		// TODO Auto-generated method stub
		UnrolledAssociationP retval = new UnrolledAssociationP((PlatformAssociation)obj);
//		updateMaps(entity, retval);
		return retval;
	}


}
