/**
 * 
 */
package com.epistimis.uddl.scoping;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;

/**
 * Provides the same EPackage access that Validators do  - except this is public instead of protected
 */
public interface IPackageProvider {


	List<EPackage> getEPackages() ;

}
