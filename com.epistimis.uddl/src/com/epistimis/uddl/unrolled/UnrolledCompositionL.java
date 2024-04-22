/**
 * 
 */
package com.epistimis.uddl.unrolled;

import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.LogicalCharacteristic;
import com.epistimis.uddl.uddl.LogicalComposableElement;
import com.epistimis.uddl.uddl.LogicalComposition;

/**
 * 
 */
public class UnrolledCompositionL extends UnrolledComposition<LogicalComposableElement, LogicalCharacteristic, LogicalComposition,
															UnrolledComposableElement<LogicalComposableElement>> {

	/**
	 * @param pc
	 * @param rce
	 */
	public UnrolledCompositionL(@NonNull LogicalComposition pc,
			UnrolledComposableElement<LogicalComposableElement> rce) {
		super(pc, rce);
		// TODO Auto-generated constructor stub
	}

	@Override
	public float getPrecision(LogicalComposition c) {
		// Not applicable
		return 0;
	}

	@Override
	public LogicalComposableElement getType(LogicalComposition c) {
		// TODO Auto-generated method stub
		return c.getType();
	}


	@Override
	String getRolename(LogicalCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getRolename();
	}

	@Override
	String getDescription(LogicalCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getDescription();
	}

	@Override
	int getLowerBound(LogicalCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getLowerBound();
	}

	@Override
	int getUpperBound(LogicalCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getUpperBound();
	}

}
