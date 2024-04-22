/**
 * 
 */
package com.epistimis.uddl.unrolled;

import org.eclipse.emf.common.util.EList;

import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformComposableElement;
import com.epistimis.uddl.uddl.PlatformComposition;
import com.epistimis.uddl.uddl.PlatformEntity;

/**
 * 
 */
public class UnrolledEntityP extends UnrolledEntity<PlatformComposableElement, PlatformEntity, PlatformCharacteristic, PlatformComposition,
											UnrolledComposableElementP,
											UnrolledCompositionP
										> {

	/**
	 * @param pe
	 */
	public UnrolledEntityP(PlatformEntity pe) {
		super(pe);
		// TODO Auto-generated constructor stub
	}


	@Override
	public PlatformEntity getSpecializes(PlatformEntity e) {
		// TODO Auto-generated method stub
		return e.getSpecializes();
	}

	@Override
	public String getRolename(PlatformCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getRolename();
	}

	@Override
	EList<PlatformComposition> getComposition(PlatformEntity entity) {
		// TODO Auto-generated method stub
		return entity.getComposition();
	}

  
	@Override
	public PlatformCharacteristic getSpecializes(PlatformCharacteristic c) {
		// TODO Auto-generated method stub
		return c.getSpecializes();
	}

	@Override
	UnrolledCompositionP createComposition(
			PlatformComposition c) {
		// TODO Auto-generated method stub
		return new UnrolledCompositionP(c, null);
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
