/**
 * 
 */
package com.epistimis.uddl.scoping;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EPackage;

/**
 * Provides the same EPackage access that Validators do  - except this is public instead of protected
 */
public class UddlPackageProvider implements IPackageProvider {

	@Override
	public List<EPackage> getEPackages() {
		List<EPackage> result = new ArrayList<EPackage>();
		result.add(com.epistimis.uddl.uddl.UddlPackage.eINSTANCE);	
		return result;
	}

}

