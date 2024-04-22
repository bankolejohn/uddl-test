/**
 * 
 */
package com.epistimis.uddl.unrolled;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.PlatformComposableElement;

/**
 * 
 */
public class UnrolledComposableElementP extends UnrolledComposableElement<PlatformComposableElement> {

	/**
	 * This is, essentially, a compiler name table. It contains the results of processing all the composable elements in the model to get the
	 *  net of unrolling.  This can be used as is, or can be used by the QueryProcessor when selecting Entities, Associations, and Compositions.
	 *
	 * Note that this contains Platform level info only
	 */
	public static Map<PlatformComposableElement,UnrolledComposableElement<PlatformComposableElement>> allComposable2Unrolled = new HashMap<>();
	public static Map<UnrolledComposableElement<PlatformComposableElement>,PlatformComposableElement> allUnrolled2Composable = new HashMap<>();

	/**
	 * @param ce
	 */
	UnrolledComposableElementP(@NonNull PlatformComposableElement ce) {
		super(ce);
		// TODO Auto-generated constructor stub
	}


	@Override
	void updateMaps(PlatformComposableElement ce) {
		allComposable2Unrolled.put(ce, this);
		allUnrolled2Composable.put(this,ce);		
		
	}

}
