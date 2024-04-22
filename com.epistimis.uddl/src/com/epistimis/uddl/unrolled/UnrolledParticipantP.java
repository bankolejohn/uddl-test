/**
 * 
 */
package com.epistimis.uddl.unrolled;

import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformComposableElement;
import com.epistimis.uddl.uddl.PlatformEntity;
import com.epistimis.uddl.uddl.PlatformParticipant;

/**
 * 
 */
public class UnrolledParticipantP
		extends UnrolledParticipant<PlatformComposableElement, PlatformEntity, PlatformCharacteristic, PlatformParticipant, UnrolledComposableElementP> {

	/**
	 * @param pp
	 * @param rce
	 */
	public UnrolledParticipantP(@NonNull PlatformParticipant pp, UnrolledComposableElementP rce) {
		super(pp, rce);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getSourceLowerBound(PlatformParticipant p) {
		// TODO Auto-generated method stub
		return p.getSourceLowerBound();
	}

	@Override
	public int getSourceUpperBound(PlatformParticipant p) {
		// TODO Auto-generated method stub
		return p.getSourceUpperBound();
	}

	@Override
	public PlatformComposableElement getType(PlatformParticipant p) {
		// TODO Auto-generated method stub
		return p.getType();
	}

	@Override
	PlatformEntity conv2Entity(PlatformComposableElement ce) {
		// TODO Auto-generated method stub
		return (PlatformEntity)ce;
	}

	@Override
	String getRolename(PlatformCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getRolename();
	}

	@Override
	String getDescription(PlatformCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getDescription();
	}

	@Override
	int getLowerBound(PlatformCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getLowerBound();
	}

	@Override
	int getUpperBound(PlatformCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getUpperBound();
	}

}
