package com.epistimis.uddl;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;

import com.epistimis.uddl.uddl.Taxonomy;
import com.epistimis.uddl.uddl.UddlPackage;

public class TaxonomyBaseProcessor extends TaxonomyProcessor<Taxonomy> {

	static Map<String,Taxonomy> cache = new HashMap<String,Taxonomy>();
	

	@Override
	public EClass getBaseMetaClass() {
		return UddlPackage.eINSTANCE.getTaxonomy();
	}


	@Override
	public Map<String,Taxonomy> getCache() {
		return cache;
	}

}
