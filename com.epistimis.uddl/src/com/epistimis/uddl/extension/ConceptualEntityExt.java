/**
 * 
 */
package com.epistimis.uddl.extension;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.epistimis.uddl.ConceptualEntityProcessor;
import com.epistimis.uddl.uddl.ConceptualAssociation;
import com.epistimis.uddl.uddl.ConceptualComposableElement;
import com.epistimis.uddl.uddl.ConceptualComposition;
import com.epistimis.uddl.uddl.ConceptualEntity;
import com.google.inject.Inject;

/**
 * 
 */
public class ConceptualEntityExt extends ConceptualEntityProcessor {

	@Inject
	ConceptualComposableElementExt ccee;

	@Inject
	ConceptualEntityExt cee;
	
	@Inject
	ConceptualAssociationExt cae;
	
	/**
	 * This returns a set of 'paths' (each as a sequence of strings) that list the
	 * relative qualified rolenames to get from self to a ConceptualObservable of
	 * the specified type. Since same observable may be in different
	 * ConceptualEntities contained within this one, we can have multiple paths - so
	 * we return a Set of Sequences
	 */
//	def: pathsToObservable(obs: ConceptualObservable):Set(Sequence(String)) =
//		self.composition->iterate(comp: ConceptualComposition; result:Set(Sequence(String)) = Set{Sequence{}} |
//			let tempResults = 
//				if (comp.type = obs) then 
//					Set{Sequence{comp.calculatedRoleName()}}  
//				else 
//					if (comp.type.oclIsKindOf(ConceptualEntity)) then 
//						let lowerResult = comp.type.oclAsType(ConceptualEntity).pathsToObservable(obs) in
//						let prepResult = lowerResult->collectNested(prepend(comp.rolename)) in
//						prepResult->asSet()
//					else
//						null --Set{null}
//					endif
//				endif
//			in
//			tempResults->reject(null)->asSet()
//		)

	/**
	 * Return a map where the key is the ConceptualObservable and the value is the
	 * set of RQN / paths to observables of that type from self
	 */
//	def: pathsToObservables(obs: Set(ConceptualObservable)):Map(ConceptualObservable,Set(Sequence(String))) =
//		obs->iterate(o: ConceptualObservable; result: Map(ConceptualObservable,Set(Sequence(String))) = Map{} |
//			result->including(o,self.pathsToObservable(o))
//		) 


	/**
	 * NOTE: You probably want to use referncedModelTyeps instead of this method
	 * 
	 * Return a set of all the model types referenced by this element. This is a
	 * 'raw' or 'base' method that doesn't include 'self' because it it called by
	 * other defs that add 'self'. This is an optimization issue - we want to track
	 * what has been processed - and we want to know if a type is self referential
	 * because we need to terminate any recursion immediately. This method won't
	 * recurse since it only walks the specialization hierarchy, and we have
	 * invariants that already check for recursion in specialization.
	 * 
	 */
//	def: typeReferences(): Set(ConceptualComposableElement) =
//		let myComps = self.composition->collect(type.referencedModelTypes())->flatten()->asSet() in
//		let parentTypes = if (self.specializes.oclIsUndefined()) then  Set {} 
//		else 
//			if self.specializes.oclIsKindOf(ConceptualAssociation) then
//				self.specializes.oclAsType(ConceptualAssociation).referencedModelTypes() 
//			else 
//				self.specializes.referencedModelTypes() 
//			endif
//		endif in
//		myComps->union(parentTypes)->asSet()
	public Set<ConceptualComposableElement> typeReferences(ConceptualEntity self) {
		Set<ConceptualComposableElement> result = self.getComposition().stream()
				.map(ConceptualComposition::getType)
				.map(ccee::referencedModelTypes)
				.flatMap(list -> list.stream())
				.collect(Collectors.toSet());

		Set<ConceptualComposableElement> parentResults = new HashSet<ConceptualComposableElement>();
		ConceptualEntity spec = self.getSpecializes();
		if (spec != null) {
			if (spec instanceof ConceptualAssociation) {
				parentResults.addAll(cae.referencedModelTypes((ConceptualAssociation)spec));
			}
			else {
				parentResults.addAll(cee.referencedModelTypes(spec));
			}
		}
		
		result.addAll(parentResults);
		return result;
	}

	/**
	 * Return a set of all the model types referenced by this element. Note that it
	 * includes 'self' and, because the specialization processing is recursive, it
	 * also includes the entire 'specialization' parentage hierarchy for this entity
	 * (specialization goes 'up' the hierarchy).
	 * 
	 * NOTE: Something must also include the specialization parentage of all of the
	 * composition elements found - because we need to know all possible types. To
	 * make a distinction between the specified types (where we only take the
	 * specialization of the initial type, not its compositions) and inclusion of
	 * the specializations of the composition elements, we have two separate
	 * functions.
	 * 
	 * The inclusive one calls the non inclusive one.
	 * 
	 * must iterate over types discovered - accumulate a set of what is discovered
	 * and track what has been checked. Don't recheck anything. Continue until list
	 * to check is empty use recursion - and pass in list of already checked types -
	 * 
	 */
//	def: referencedModelTypes(): Set(ConceptualComposableElement) =
//		-- Include 'self' because it is a structural type that we will sometimes want to know about
//		self->asSet()->union(self.typeReferences())->asSet()

	public Set<ConceptualComposableElement> referencedModelTypes(ConceptualEntity self) {
		Set<ConceptualComposableElement> result = new HashSet<ConceptualComposableElement>();
		result.addAll(typeReferences(self));
		result.add(self);
		return result;
	}

	/**
	 * The inclusive version of referencedModelTypes - to avoid combinatorial
	 * explosion, we track which types have already been 'expanded' and don't repeat
	 * that work.
	 */
//--		def: referencedModelTypesExt(): Set(ConceptualComposableElement) =
//--			let startingTypes = self.typeReferences() in
//--			
//--			Set{}
//
//--			ics->iterate(ic: IntegrationIntegrationContext; result:Set(IntegrationTSNodeConnection) = Set{} |
//--				result->includingAll(self.linksInIC(ic))
//--			)->asSet()

	/**
	 * Checking a type means checking all the composition elements of that type
	 * across the entire specialization chain. We can collect that entire set of
	 * types first. Getting that list is independent of checking what each
	 * references. typeReferences() does that first part.
	 * 
	 * The second involves iterating over that list and finding the type references
	 * for each thing in the list. We don't check things we've already checked (to
	 * avoid infinite recursion and as an optimization)
	 */

//static def: checkASetOfTypes(typesToCheck: Set(ConceptualComposableElement), alreadyChecked: Set(ConceptualComposableElement)):Set(ConceptualComposableElement) =
//	let iterResults = typesToCheck->iterate(elem: ConceptualComposableElement;
//											result: Set(ConceptualComposableElement) = Set{} |
//		if (alreadyChecked->includes(elem)) then result
//		else if (elem.oclIsKindOf(ConceptualEntity)) then result->includingAll(elem.oclAsType(ConceptualEntity).typeReferences())
//			 else result->including(elem)
//			 endif
//		endif						
//	) in
//	iterResults

	/**
	 * This checks a set of types and then logs that it has checked them. Although
	 * this doesn't update totalChecked as the work is done, it shouldn't matter
	 * because typesToCheck is a Set - so it has no duplicates.
	 */
//static def: checkTypeAndLogDone(typesToCheck: Set(ConceptualComposableElement), alreadyChecked: Set(ConceptualComposableElement)):Tuple(found: Set(ConceptualComposableElement),totalChecked: Set(ConceptualComposableElement)) =
//	Tuple{	found: 			Set(ConceptualComposableElement) = checkASetOfTypes (typesToCheck, alreadyChecked), 
//			totalChecked: 	Set(ConceptualComposableElement) = alreadyChecked->includingAll(typesToCheck)->asSet()
//	}

	/**
	 * Check a single type - return what that type references and a list of what has
	 * been checked so far, updated with this type.
	 */
//def: checkAType4Refs(checked: Set(ConceptualComposableElement)): Tuple(found: Set(ConceptualComposableElement),totalChecked: Set(ConceptualComposableElement)) =
//		let refdTypes = self.typeReferences() in
//		let specializationsOfRefdTypes = refdTypes->selectByKind(ConceptualEntity)->collect(specializationAncestry())->asSet()
//					->union(refdTypes->selectByType(ConceptualObservable))->asSet() in
//		checkTypeAndLogDone(specializationsOfRefdTypes, checked) 
//		
//--			->iterate(checked:Set(ConceptualComposableElement); 
//--													result: Tuple(found: Set(ConceptualComposableElement),totalChecked: Set(ConceptualComposableElement)) = 
//--																Tuple{ 	found:Set(ConceptualComposableElement) = Set{},
//--														   				totalChecked:Set(ConceptualComposableElement) = checked} |
//--				if (not result.totalChecked->includes(self)) then 
//--					let tempResult = 	self.addlReferencedModelTypes(result.totalChecked) in
//--					let result = 
//--								-- Append any new stuff we've found to the list of everything found
//--						Tuple{ found:Set(ConceptualComposableElement) = result.found->includingAll(self.addlReferencedModelTypes(result.totalChecked)),
//--								-- We have now checked the current 'self', so add it to the 'totalChecked' list
//--								totalChecked:Set(ConceptualComposableElement) = result.totalChecked->including(self)} in
//--					result
//--				else result 
//--				endif
//--
//--		def: addlReferencedModelTypes(checked: Set(ConceptualComposableElement)): Set(ConceptualComposableElement) =
//--			let iterResults = self.composition->iterate(elem: ConceptualComposition; 
//--													result: Tuple(found: Set(ConceptualComposableElement),totalChecked: Set(ConceptualComposableElement)) = 
//--													Tuple{ found:Set(ConceptualComposableElement) = Set{},
//--														   totalChecked:Set(ConceptualComposableElement) = checked} |
//--		--								result 
//--				if (not result.totalChecked->includes(elem)) then 
//--					let result = 
//--								-- Append any new stuff we've found to the list of everything found
//--						Tuple{ found:Set(ConceptualComposableElement) = result.found->includingAll(self.addlReferencedModelTypes(result.totalChecked)),
//--								-- We have now checked the current 'elem', so add it to the 'totalChecked' list
//--								totalChecked:Set(ConceptualComposableElement) = result.totalChecked->including(elem)} in
//--					result
//--				else result endif
//--			) in 
//--			let myComps = iterResults.found in
//--			let parentTypes = if (self.specializes.oclIsUndefined()) then  Bag {} 
//--			else 
//--				if self.specializes.oclIsKindOf(ConceptualAssociation) then
//--					self.specializes.oclAsType(ConceptualAssociation).addlReferencedModelTypes(iterResults.totalChecked) 
//--				else 
//--					self.specializes.addlReferencedModelTypes(iterResults.totalChecked) 
//--				endif
//--			endif in
//--			myComps->union(parentTypes)->flatten()->asSet()

//	def: matchingObservables(checklist: Set(uddl::ConceptualObservable) ): Set(uddl::ConceptualObservable) = 
//		self.referencedModelTypes()->selectByType(uddl::ConceptualObservable)->select(t|checklist->includes(t)) 

	/**
	 * oclIsKindOf uses the metamodel. We want to follow the UDDL specialization
	 * hierarchy
	 */
//	def: isTypeOrSpecializationOf(t: ConceptualEntity): Boolean =
//		self = t or if (self.specializes->notEmpty()) then self.specializes.isTypeOrSpecializationOf(t) else false endif

	/**
	 * Get all the Entities that specialize the root. (this is traversing
	 * specialization in the inverse direction. If we cannot follow the inverse
	 * directly, then we have to use allInstances)
	 */
//	static def: specializationHierarchy(root: ConceptualEntity): Set(ConceptualEntity) =
//		let ents = uddl::ConceptualEntity.allInstances() in 
//		ents->select(isTypeOrSpecializationOf(root))

	/**
	 * Get all the Entities that specialize this entity (this is traversing
	 * specialization in the inverse direction. If we cannot follow the inverse
	 * directly, then we have to use allInstances)
	 */
//	def: specializationHierarchy(): Set(ConceptualEntity) =
//		specializationHierarchy(self)

	/**
	 * Get the specialization ancestry of this entity as a Set (including self)
	 */
//	def: specializationAncestry():Set(ConceptualEntity) = 
//		let start = self.specializes in 
//		-- Note that we use closure at the first specializes to avoid infinite looping (because closure
//		-- starts with 'self') and then append self to the result
//		if (start <> null) then start->closure(specializes)->including(self)->asSet()
//		else  Set{self} 
//		endif

	/**
	 * check to see if the specified role is used anywhere. That means first finding
	 * a composition element with the specified role name and then checking from
	 * that type down to see if the checkType is used
	 */
//	 def: roleUsesType(role: String, checkType: ConceptualComposableElement): Boolean =
//	 	let comps =  self.composition->select(rolename.equalsIgnoreCase(role)) in
//	 	let usesType = comps->select(referencesModelType(checkType)) in
//	 	not usesType->isEmpty()

	/**
	 * Is this Entity of the specified type?
	 */
//	def: isTypeOf(type:String): Boolean = 
//		let type = uddl::ConceptualEntity::findByName(type) in
//		type->exists(o|self.isTypeOrSpecializationOf(o))	

	/**
	 * Retrieve the entire type hierarchy rooted in the Entity with the specified
	 * name. NOTE: This is a leaf name - it is not a FQN
	 */
//	static def: typeHierarchyRoot(type: String): ConceptualEntity =
//		let roots = uddl::ConceptualEntity::findByName(type)->asSequence() in
//		if (roots->size() = 1) then
//			roots->first()
//		else
//			null -- error either because we got zero or we got too many
//		endif

	/**
	 * We should have exactly 1 ConceptualEntity that uses this leaf name. That
	 * avoids confusion.
	 */
//	def: exactlyOneRoot(type:String): Boolean =
//		findByName(type)->size() = 1 

	/**
	 * Does this entity contain data anywhere from the specified type?
	 */
//	def: containsDataFromType(type: String): Boolean =
//		let root = typeHierarchyRoot(type) in
//		let typeHier = 	specializationHierarchy(root) in
//		not self.referencedModelTypes()->intersection(typeHier)->isEmpty()

	/**
	 * TODO: This should really use a QNP, which we don't have in OCL.
	 */
//	def: hasBasisInAncestry(basisName: String): Boolean =
//		self.basisEntity->collect(name)->includes(basisName) or
//		(self.specializes?.hasBasisInAncestry(basisName))

	/**
	 * Is there a 'user' type (possibly this one, but maybe an embedded one) that
	 * has embedded within it the 'used' type? Note that 'user' and 'used' types
	 * must include both root and specializations of those NOTE: The 'user' type
	 * must be a ConceptualEntity - a ConceptualObservable can't use another type.
	 * TODO: Unfinished
	 */
//--		def: typeUsesType(user: ConceptualEntity, used: ConceptualComposableElement): Boolean =
//--			let userTypes = specializationHierarchy(user) in
//--			let usedTypes = if (used.oclIsTypeOf(ConceptualEntity)) then 
//--				specializationHierarchy(used.oclAsType(ConceptualEntity)) else 
//--				Set {used} endif in			 			
//--			true
//

}
