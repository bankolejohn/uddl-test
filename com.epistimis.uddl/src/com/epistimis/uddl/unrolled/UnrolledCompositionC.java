/**
 * 
 */
package com.epistimis.uddl.unrolled;

import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.ConceptualCharacteristic;
import com.epistimis.uddl.uddl.ConceptualComposableElement;
import com.epistimis.uddl.uddl.ConceptualComposition;

/**
 * 
 */
public class UnrolledCompositionC extends UnrolledComposition<ConceptualComposableElement, ConceptualCharacteristic, ConceptualComposition,
							UnrolledComposableElement<ConceptualComposableElement>> {

	/**
	 * @param pc
	 * @param rce
	 */
	public UnrolledCompositionC(@NonNull ConceptualComposition pc,
			UnrolledComposableElement<ConceptualComposableElement> rce) {
		super(pc, rce);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getPrecision(ConceptualComposition c) {
		// precision not applicable
		return 0;
	}

	@Override
	public ConceptualComposableElement getType(ConceptualComposition c) {
		// TODO Auto-generated method stub
		return c.getType();
	}


	@Override
	String getRolename(ConceptualCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getRolename();
	}

	@Override
	String getDescription(ConceptualCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getDescription();
	}

	@Override
	int getLowerBound(ConceptualCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getLowerBound();
	}

	@Override
	int getUpperBound(ConceptualCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getUpperBound();
	}

}
