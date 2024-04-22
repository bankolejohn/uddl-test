package com.epistimis.uddl.unrolled;

import java.lang.invoke.MethodHandles;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.util.IndexUtilities;
import com.epistimis.uddl.uddl.UddlElement;

public abstract class UnrolledParticipant<ComposableElement extends UddlElement, 
											Entity extends ComposableElement, 
											Characteristic  extends EObject,  
											Participant extends Characteristic,
											UComposableElement extends UnrolledComposableElement<ComposableElement>>  
		extends UnrolledCharacteristic<ComposableElement, Characteristic,UComposableElement> {

	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	abstract public int 				getSourceLowerBound(Participant p);
	abstract public int 				getSourceUpperBound(Participant p);
	abstract public ComposableElement 	getType(Participant p);
	abstract 		Entity 				conv2Entity(ComposableElement ce);
	
	/**
	 * Track the original type because we need this later to do linkage
	 */
	private @NonNull Entity type;

	private int sourceLowerBound;

	private int sourceUpperBound;

	public UnrolledParticipant(@NonNull String rolename) {
		super(rolename);
		// TODO Auto-generated constructor stub
		sourceLowerBound = 1;
		sourceUpperBound = 1;
	}
	public UnrolledParticipant(@NonNull Participant pp, UComposableElement rce) {
		super(pp,rce);
		this.type = conv2Entity(IndexUtilities.unProxiedEObject(getType(pp),pp));
		sourceLowerBound = getSourceLowerBound(pp);
		sourceUpperBound = getSourceUpperBound(pp);
	}

	public void update(@NonNull Participant pc, UComposableElement rce) {
		super.updateChar(pc, rce);

		// TODO: https://app.clickup.com/t/86bx15uh4
		// Characteristic type specialization could be tightened on realization
		if (getSourceLowerBound(pc)  > this.sourceLowerBound) {
			this.sourceLowerBound = getSourceLowerBound(pc);
		}
		if (getSourceUpperBound(pc)  < this.sourceUpperBound) {
			this.sourceUpperBound = getSourceUpperBound(pc);
		}

	}

	public Entity getType() {
		if (type == null) {
			logger.error("Returning null type from UnrolledParticipant " + referencedCharacteristic.toString());
		}
		return this.type;
	}
	
	public int getSourceLowerBound() {
		return sourceLowerBound;
	}
	public int getSourceUpperBound() {
		return sourceUpperBound;
	}

}
