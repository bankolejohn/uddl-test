package com.epistimis.uddl.validation;

import org.eclipse.emf.ecore.EObject;
//import org.eclipse.emf.ecore.EAttribute;
//import org.eclipse.emf.ecore.ENamedElement;
//import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
//import org.eclipse.xtend.lib.annotations.Data;

/**
 * Use this when identifying a message to be specified in the Eclipse errors list / edit window
 */
//@Data
public class MsgAndLocation {

		public MsgAndLocation(String m, EStructuralFeature l, EObject i, int n) {
			msg = m;
			instance = i;
			location = l;
			ndx = n;
		}
		public MsgAndLocation(String m, EStructuralFeature l,  int n) {
			this(m,l,null,n);
		}

		/**
		 * The message content
		 */
		public final String msg;
		/**
		 * The model element location it is associated with
		 */
		public final EStructuralFeature location;
		
		public final EObject instance;
		/**
		 * For collections, this is the index into the collection
		 */
		public final int ndx;
 
}
