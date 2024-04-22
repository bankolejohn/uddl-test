/**
 * 
 */
package com.epistimis.uddl.extension;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.epistimis.uddl.util.IndexUtilities;
import com.epistimis.uddl.uddl.ConceptualComposableElement;
import com.epistimis.uddl.uddl.ConceptualEntity;
import com.epistimis.uddl.uddl.ConceptualObservable;
import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.uddl.UddlPackage;
import com.google.inject.Inject;

/**
 * 
 */
public class ConceptualComposableElementExt {

	@Inject
	IndexUtilities ndxUtil;

	@Inject
	ConceptualObservableExt coe;

	@Inject
	ConceptualEntityExt cee;

	/**
	 * Find the object visible from the context of the specified type and name. This
	 * just renames an existing function so the name matches what we're using in
	 * OCL. Note that getUniqueObjectForName (which this calls) can process RQNs,
	 * not just leaf names. In that sense it is more powerful than the OCL
	 * equivalent
	 * 
	 * @param context
	 * @param type
	 * @param name
	 * @return
	 */
	// static def: findByName(n: String): Set(ConceptualComposableElement) =
	// ConceptualComposableElement.allInstances()->select(o|o.name = n)->asSet()
	public UddlElement findByName(EObject context, String name) {
		return (UddlElement) ndxUtil.getUniqueObjectForName(context,
				UddlPackage.eINSTANCE.getConceptualComposableElement(), name);
	}

	/**
	 * TODO: Nothing here addresses finding a data structure pattern in a general
	 * way. That would require knowing roles and contains/containment/ reference
	 * information. Roles are defined at the privacy level - so this problem will
	 * have to be addressed there.
	 */

	/**
	 * Return a set of all the model types referenced by this element
	 */
//	    def: referencedModelTypes(): Set(ConceptualComposableElement) =
//	 		if (self.oclIsKindOf(ConceptualObservable)) then
//				self.oclAsType(ConceptualObservable).referencedModelTypes()
//			else
//				self.oclAsType(ConceptualEntity).referencedModelTypes()
//			endif
	public Set<ConceptualComposableElement> referencedModelTypes(ConceptualComposableElement self) {
		if (self instanceof ConceptualObservable) {
			return coe.referencedModelTypes((ConceptualObservable) self);
		} else {
			return cee.referencedModelTypes((ConceptualEntity) self);
		}
	}

	/**
	 * Does this element reference the specified type?
	 */
//		def: referencesModelType(rmt: ConceptualComposableElement): Boolean = 
//			self.referencedModelTypes()->includes(rmt)

	public boolean referencesModelType(ConceptualComposableElement self, ConceptualComposableElement rmt) {
		return referencedModelTypes(self).contains(rmt);
	}

	/**
	 * Does this element reference any of the specified observables?
	 */
//		def: referencesAnyModelTypes(objs:Set(ConceptualObservable)): Boolean = 
//			self.referencedModelTypes()->intersection(objs)->notEmpty()
	public boolean referencesAnyModelTypes(ConceptualComposableElement self, Set<ConceptualObservable> objs) {
		Set<ConceptualComposableElement> result = referencedModelTypes(self);
		result.retainAll(objs);
		return !result.isEmpty();
	}
	/**
	 * Does this element reference all of the specified observables?
	 */
//		def: referencesAllModelTypes(objs:Set(ConceptualObservable)): Boolean = 
//			let isect = self.referencedModelTypes()->intersection(objs) in
//			isect->size() = objs->size()
//	
//	
//--		def: addlReferencedModelTypes(checked: Set(ConceptualComposableElement)): Set(ConceptualComposableElement) =
//--	 		if (self.oclIsKindOf(ConceptualObservable)) then
//--	 			if (checked->includes(self)) then Set {} 
//--	 			else Set {self}
//--				endif
//--			else
//--				self.oclAsType(ConceptualEntity).addlReferencedModelTypes(checked)
//--			endif

}
