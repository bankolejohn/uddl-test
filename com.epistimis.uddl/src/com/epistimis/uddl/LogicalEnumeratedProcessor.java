package com.epistimis.uddl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;

import com.epistimis.uddl.uddl.LogicalEnumeratedBase;
import com.epistimis.uddl.uddl.UddlPackage;

public class LogicalEnumeratedProcessor extends TaxonomyProcessor<LogicalEnumeratedBase> {

	static Map<String,LogicalEnumeratedBase> cache = new HashMap<String,LogicalEnumeratedBase>();

	@Override
	public EClass getBaseMetaClass() {
		return UddlPackage.eINSTANCE.getLogicalEnumeratedBase();
	}

	@Override
	public Map<String,LogicalEnumeratedBase> getCache() {
		return cache;
	}


}
