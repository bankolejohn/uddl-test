/**
 * 
 */
package com.epistimis.uddl.extension;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.epistimis.uddl.util.IndexUtilities;
import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.uddl.UddlPackage;
import com.google.inject.Inject;

/**
 * 
 */
public class UddlElementExt {

	@Inject
	IndexUtilities ndxUtil;
	
	/**
	 * Find the object visible from the context of the specified type and name.
	 * This just renames an existing function so the name matches what we're using
	 * in OCL. Note that getUniqueObjectForName (which this calls) can process RQNs,
	 * not just leaf names. In that sense it is more powerful than the OCL equivalent
	 * @param context
	 * @param type
	 * @param name
	 * @return
	 */
	public UddlElement findByName(EObject context, String name ) {

		return (UddlElement) ndxUtil.getUniqueObjectForName(context,UddlPackage.eINSTANCE.getUddlElement(),name);
		
	}

	/**
	 * Find specific instances. Note that this looks through all of them. Use it sparingly.
	 * Further, it does that for each name. Need to make getUniqueObjectForName more efficient. 
	 */	
	public Set<UddlElement> findByNames(EObject context,  Set<String> names) {
		Set<UddlElement> result = new HashSet<UddlElement>();
		for (String name: names) {
			result.add(findByName(context,name));
		}
		return result;
	}

}
