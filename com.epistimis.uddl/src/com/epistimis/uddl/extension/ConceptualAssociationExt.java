/**
 * 
 */
package com.epistimis.uddl.extension;

import java.util.Set;
import java.util.stream.Collectors;

import com.epistimis.uddl.uddl.ConceptualAssociation;
import com.epistimis.uddl.uddl.ConceptualComposableElement;
import com.epistimis.uddl.uddl.ConceptualEntity;
import com.epistimis.uddl.uddl.ConceptualParticipant;

/**
 * 
 */
public class ConceptualAssociationExt extends ConceptualEntityExt {
	/**
	 * Return a set of all the model types referenced by this element. Note that it includes 'self' and,
	 * because the specialization processing is recursive, it also includes the entire 'specialization'
	 * hierarchy for this entity (done in the ConceptualEntity::referencedModelTypes() call)
	 */
//	def: referencedModelTypes(): Set(ConceptualComposableElement) =
//		-- compTypes call will also handle the specialization 
//		let compTypes = self.oclAsType(ConceptualEntity).referencedModelTypes() in
//		let assocTypes = self.participant->collect(type.referencedModelTypes())->flatten()->asSet() in
//		compTypes->union(assocTypes)

	public Set<ConceptualComposableElement> referencedModelTypes(ConceptualAssociation self) {
		Set<ConceptualComposableElement> result = super.referencedModelTypes((ConceptualEntity)self);
		Set<ConceptualComposableElement> assocTypes = self.getParticipant().stream()
																			.map(ConceptualParticipant::getType)
																			.map(ccee::referencedModelTypes)
																			.flatMap(list -> list.stream())
																			.collect(Collectors.toSet());
		result.addAll(assocTypes);
		return result;
	}

}
