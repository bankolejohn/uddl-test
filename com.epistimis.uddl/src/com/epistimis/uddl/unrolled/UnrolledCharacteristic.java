package com.epistimis.uddl.unrolled;

//import java.lang.invoke.MethodHandles;
import java.text.MessageFormat;

import org.eclipse.emf.ecore.EObject;
//import org.apache.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.UddlElement;

public abstract class UnrolledCharacteristic<ComposableElement extends UddlElement,Characteristic  extends EObject, 
												UComposableElement extends UnrolledComposableElement<ComposableElement>> {

	//private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());
	/**
	 * NOTE: (P/L/C) Composition element specialization is interpreted as follows:
	 * If a (P/L/C) Composition element specializes, it must specialize a Composition element from an Entity at the same (P/L/C)
	 * level.  Only realization can bring Compositions from C->L  or L->P.  Specialization also means that the specialized element
	 * is not inherited 'as is' - rather, the specialization overrides whatever would have been inherited.
	 * 
	 * The FACE spec itself does not support overriding. Specialization just adds composition elements. So the override capability here
	 * is overkill for now.
	 */
	/**
	 * NOTE: We do not have a 'specializes' attribute for the 'Realized' classes *because* we unroll the specialization when
	 * creating these. In fact, that's a major reason these 'Realized' classes exist. A characteristic specializes if its containing entity
	 * also specializes and has an characteristic that 'overrides' a characteristic from the specialized Entity
	 */
	
	abstract String getRolename(Characteristic c);
	abstract String getDescription(Characteristic c);
	abstract int    getLowerBound(Characteristic c);
	abstract int 	getUpperBound(Characteristic c);
	
	protected Characteristic referencedCharacteristic;

	/**
	 * Use the lowest level rolename (Platform)
	 */
	private String rolename;

	/**
	 * The description
	 */
	private String description;
	/**
	 * The lower bound cardinality
	 */
	private int lowerBound;
	/**
	 * The upper bound cardinality. -1 means unbounded.
	 */
	private int upperBound;

	/**
	 * The unrolledType of this composition Element
	 * NOTE: This does not use UComposableElement because that type is not a base class of UEntity or UAssociation.
	 * UnrolledComposableElement<ComposableElement> is the base class.
	 */
	private UnrolledComposableElement<ComposableElement> unrolledType;

	public UnrolledCharacteristic(@NonNull String rolename) {
		this.rolename = rolename;
		this.lowerBound = 1;
		this.upperBound = 1;
	}

	public UnrolledCharacteristic(@NonNull Characteristic pc, @NonNull UComposableElement rce) {
		this.rolename = getRolename(pc);
		this.description= getDescription(pc);
		this.lowerBound = getLowerBound(pc);
		this.upperBound = getUpperBound(pc);
		this.referencedCharacteristic = pc;
		this.unrolledType = rce;
	}

	/**
	 * Call this method to update this instance based on information from the passed in
	 * 
	 * @param pc
	 */
	public void updateChar(Characteristic pc, UComposableElement rce) {
		/**
		 * Allowed updates:
		 * 1) rolename can change - but can't become empty
		 * 2) description: can change - but can't be emptied if it was previously nonempty
		 * 3) bounds can be no looser - but they can be more restricted
		 * 4) precision cannot be made better?
		 */
		if (getRolename(pc).trim().length() > 0) {
			this.rolename = getRolename(pc);
		}
		if (getDescription(pc).trim().length() > 0) {
			this.description = getDescription(pc);
		}
		if (getLowerBound(pc) > this.lowerBound) {
			this.lowerBound = getLowerBound(pc);
		}
		/**
		 * If the updating upper bound is unbounded, then it can't narrow the
		 * existing definition, so go no further
		 */
		if (getUpperBound(pc) != -1) {
			if ((this.upperBound == -1) || (getUpperBound(pc) < this.upperBound)) {
				this.upperBound = getUpperBound(pc);
			}
		}

		if (rce != null) {
			this.unrolledType = rce;
		}

	}

	public Characteristic getCharacteristic() {
		return this.referencedCharacteristic;
	}
	public UnrolledComposableElement<ComposableElement> getUnrolledType() {
		return this.unrolledType;
	}

	public void setUnrolledType(UnrolledComposableElement<ComposableElement> rce) {
		this.unrolledType = rce;
	}

	public String getDescription() {
		return description;
	}

	public String getRolename() {
		return rolename;
	}
	
	public String toString() {
		return MessageFormat.format("{0} {1} [{2}:{3}] \'{4}\' from {5}", unrolledType.toString(), rolename, lowerBound, upperBound, description,
				referencedCharacteristic.toString());
	}
}
/*
 * ConceptualComposition:
	unrolledType=[ConceptualComposableElement|QN]  rolename=ID '[' (lowerBound=INT)? ':' (upperBound=INT)? ']' (description=STRING)? ':' (specializes=[ConceptualCharacteristic|QN])? ';'
;
 */
/*
 *
LogicalComposition:
	unrolledType=[LogicalComposableElement|QN]  rolename=ID '[' lowerBound=INT ':' upperBound=INT ']' (description=STRING)? (':' specializes=[LogicalCharacteristic|QN])?  '->' realizes=[ConceptualComposition|QN]
;
 */
/*
PlatformComposition:
unrolledType=[PlatformComposableElement|QN]  rolename=ID '[' lowerBound=INT ':' upperBound=INT ']' (description=STRING)? (':' specializes=[PlatformCharacteristic|QN])?  '->' realizes=[LogicalComposition|QN]
	'{'
		'prec:' precision=FLOAT
	'}'';'
;
*/