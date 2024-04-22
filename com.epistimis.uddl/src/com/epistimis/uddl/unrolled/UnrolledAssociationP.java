/**
 * 
 */
package com.epistimis.uddl.unrolled;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.PlatformAssociation;
import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformComposableElement;
import com.epistimis.uddl.uddl.PlatformComposition;
import com.epistimis.uddl.uddl.PlatformEntity;
import com.epistimis.uddl.uddl.PlatformParticipant;

/**
 * 
 */
public class UnrolledAssociationP extends
		UnrolledAssociation<PlatformComposableElement, PlatformEntity, PlatformAssociation, PlatformCharacteristic, PlatformComposition, PlatformParticipant, UnrolledComposableElementP, UnrolledCompositionP, UnrolledParticipantP> {

	/**
	 * @param pa
	 */
	public UnrolledAssociationP(@NonNull PlatformAssociation pa) {
		super(pa);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PlatformEntity getSpecializes(PlatformEntity e) {
		// TODO Auto-generated method stub
		return e.getSpecializes();
	}

	@Override
	public EList<PlatformParticipant> getParticipant(PlatformAssociation assoc) {
		// TODO Auto-generated method stub
		return assoc.getParticipant();
	}

	@Override
	boolean isAssociation(PlatformEntity e) {
		// TODO Auto-generated method stub
		return (e instanceof PlatformAssociation);
	}

	@Override
	UnrolledParticipantP createParticipant(PlatformParticipant c) {
		// TODO Auto-generated method stub
		return new UnrolledParticipantP(c,null);
	}

	@Override
	public String getRolename(PlatformCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getRolename();
	}

	@Override
	public PlatformCharacteristic getSpecializes(PlatformCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getSpecializes();
	}

	@Override
	EList<PlatformComposition> getComposition(PlatformEntity entity) {
		// TODO Auto-generated method stub
		return entity.getComposition();
	}

	@Override
	UnrolledCompositionP createComposition(PlatformComposition c) {
		// TODO Auto-generated method stub
		return new UnrolledCompositionP(c,null);
	}

	/**
	 * NOTE: Because this type is not considered as derived from ``UnrolledComposableElementP`, that version of 
	 * updateMaps isn't inherited - so reimplement it here.
	 * TODO: Fix this
	 */
	@Override
	void updateMaps(PlatformComposableElement ce) {
		UnrolledComposableElementP.allComposable2Unrolled.put(ce, this);
		UnrolledComposableElementP.allUnrolled2Composable.put(this,ce);		
		
	}

}
