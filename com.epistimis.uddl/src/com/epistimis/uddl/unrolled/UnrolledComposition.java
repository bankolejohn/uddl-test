package com.epistimis.uddl.unrolled;

import java.lang.invoke.MethodHandles;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.util.IndexUtilities;

public abstract class UnrolledComposition<ComposableElement extends UddlElement, 
											Characteristic  extends EObject,  
											Composition extends Characteristic,
											UComposableElement extends UnrolledComposableElement<ComposableElement>>  
	extends UnrolledCharacteristic<ComposableElement, Characteristic,UComposableElement> {

	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

	abstract public float 				getPrecision(Composition c);
	abstract public ComposableElement 	getType(Composition c);
	
	/**
	 * Track the original type because we need this later to do linkage
	 */
	private @NonNull ComposableElement type;

	/**
	 * The max precision - use this to define a rounding function for this composition?
	 * NOTE: This value is only valid/used at the Platform level
	 */
	private float precision;


	public UnrolledComposition(@NonNull String rolename) {
		super(rolename);
		// TODO Auto-generated constructor stub
		this.precision = 1;

	}

	public UnrolledComposition(@NonNull Composition pc, UComposableElement rce) {
		super(pc, rce);
		// TODO Auto-generated constructor stub
		this.type = IndexUtilities.unProxiedEObject(getType(pc), pc);
		this.precision = getPrecision(pc);

	}

	// Forced to change the nam here because Composition extends Characteristic - which produces an override error
	// due to type erasure.
	public void update( Composition pc, UComposableElement rce) {
		super.updateChar(pc, rce);

		// TODO: https://app.clickup.com/t/86bx15uh4
		// Characteristic type specialization could be tightened on realization. Tightened precision means smaller values.
		if (getPrecision(pc)  < this.precision) {
			this.precision = getPrecision(pc);
		}

	}

	public ComposableElement getType() {
		if (type == null) {
			logger.error("Returning null type from UnrolledComposition " + referencedCharacteristic.toString());
		}
		return this.type;
	}

	public float getPrecision() {return this.precision; }

}
