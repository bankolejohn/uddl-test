/**
 * 
 */
package com.epistimis.uddl.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.annotation.NonNull;

/**
 * General Utilities that can be used to navigate around the model. These replicate some functionality already in OCL
 * 
 * TODO: Can we use Acceleo / AQL for this? https://eclipse.dev/acceleo/documentation/aql.html
 * See the 'Using AQL programmatically' section at the bottom of that page
 */
public class NavigationUtilities {

	//@Inject IndexUtilities iu;

	/**
	 * Walk up the containment hierarchy until we find a container that fails the test.
	 * when ofType is true, we walk until the rootClz matches the container type.
	 * when ofType is false, we stop when the parent container does not match the rootClz (and then return the current container as the root).
	 * 
	 * @param context
	 * @param rootClz
	 * @param ofType
	 * @return
	 */
	public static EObject root(@NonNull EObject context, @NonNull EClass rootClz, boolean ofType) {
		EObject container = context.eContainer();
		if (container == null) {
			// If there is no container, then context must be the root
			return context;
		}
		if (ofType) {
			if (container.eClass().equals(rootClz))
			{
				return container;
			}
			else {
				// keep going
				return root(container,rootClz,ofType);
			}
		}
		else {
			if (!container.eClass().equals(rootClz))
			{
				return context;
			}
			else {
				// keep going
				return root(container,rootClz,ofType);
			}
		}
	}
	
	/**
	 * Walk up the containment hierarchy until we find a container that fails the test.
	 * when ofType is true, we walk until the rootClz matches the container type.
	 * when ofType is false, we stop when the parent container does not match the rootClz (and then return the current container as the root).
	 * 
	 * @param context
	 * @param rootClz
	 * @param ofType
	 * @return
	 */
	public static int nestingLevels(@NonNull EObject context, @NonNull EClass rootClz, boolean ofType) {
		EObject container = context.eContainer();
		if (container == null) {
			// If there is no container, then context must be the root
			return 0;
		}
		if (ofType) {
			if (container.eClass().equals(rootClz))
			{
				return 1;
			}
			else {
				// keep going
				return nestingLevels(container,rootClz,ofType) + 1;
			}
		}
		else {
			if (!container.eClass().equals(rootClz))
			{
				return 1;
			}
			else {
				// keep going
				return nestingLevels(container,rootClz,ofType) + 1;
			}
		}
		
	}

}
