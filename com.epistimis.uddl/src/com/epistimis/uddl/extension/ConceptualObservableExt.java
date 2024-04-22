/**
 * 
 */
package com.epistimis.uddl.extension;

import java.util.HashSet;
import java.util.Set;

import com.epistimis.uddl.uddl.ConceptualComposableElement;
import com.epistimis.uddl.uddl.ConceptualObservable;

/**
 * 
 */
public class ConceptualObservableExt {

//	static def: findByName(n: String): Set(ConceptualObservable) =
//			ConceptualObservable.allInstances()->select(o|o.name = n)->asSet()
		/**
		 * Return a set of all the model types referenced by this element
		 */
//	    def: referencedModelTypes(): Set(ConceptualComposableElement) =
//			self->asSet()

		Set<ConceptualComposableElement> referencedModelTypes(ConceptualObservable self) {
			Set<ConceptualComposableElement> result = new HashSet<ConceptualComposableElement>();
			result.add(self);
			return result;
		}
}
