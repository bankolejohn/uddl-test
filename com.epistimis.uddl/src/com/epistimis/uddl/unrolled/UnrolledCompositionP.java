/**
 * 
 */
package com.epistimis.uddl.unrolled;

import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformComposableElement;
import com.epistimis.uddl.uddl.PlatformComposition;

/**
 * 
 */
public class UnrolledCompositionP extends UnrolledComposition<PlatformComposableElement, PlatformCharacteristic, PlatformComposition,
																UnrolledComposableElementP> {

	/**
	 * @param pc
	 * @param rce
	 */
	public UnrolledCompositionP(@NonNull PlatformComposition pc,
			UnrolledComposableElementP  rce) {
		super(pc, rce);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getPrecision(PlatformComposition c) {
		// TODO Auto-generated method stub
		return c.getPrecision();
	}

	@Override
	public PlatformComposableElement getType(PlatformComposition c) {
		// TODO Auto-generated method stub
		return c.getType();
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
