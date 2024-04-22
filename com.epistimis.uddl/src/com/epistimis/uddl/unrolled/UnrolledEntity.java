package com.epistimis.uddl.unrolled;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import com.epistimis.uddl.uddl.UddlElement;
//import com.google.common.base.Optional;
import com.epistimis.uddl.util.IndexUtilities;


public abstract class UnrolledEntity<ComposableElement extends UddlElement, 
										Entity extends ComposableElement, 
										Characteristic  extends EObject,  
										Composition extends Characteristic ,
										UComposableElement extends  UnrolledComposableElement<ComposableElement >,
										UComposition extends UnrolledComposition<ComposableElement, Characteristic, Composition, UComposableElement>> 
	extends UnrolledComposableElement<ComposableElement >
{

	/**
	 * NOTE: (P/L/C) Composition element specialization is interpreted as follows:
	 * If a (P/L/C) Composition element specializes, it must specialize a Composition element from an Entity at the same (P/L/C)
	 * level.  
	 * 
	 * (Only realization can bring Compositions from C->L  or L->P - those are not addressed here).
	 * 
	 * NOTE: Specialization/ realization should be self consistent - a realizes-specializes 
	 * path should arrive at the same place as a specializes-realizes path (e.g. Given Entity PE2 that 
	 * specializes Entity PE1 and realizes LogicalEntity LE2, then LE2 must specialize LE1, and PE1 must 
	 * realize LE1. ) If they are not self consistent, this is an error 
	 * (these errors should be caught by the OCL - see logical.ocl and platform.ocl)
	 *   
	 *   
	 * Specialization also means that the specialized element
	 * is not inherited 'as is' - rather, the specialization overrides whatever would have been inherited.
	 * It should be that specialization only adds compositions/ participants - that nothing is overridden.
	 * However, we handle the possibility that something is overridden just to be safe.
	 *
	 * That means that the UnrolledEntity must account for this when working through the hierarchy. To do that:
	 *
	 * 1) recursively walk up the specializes chains. 
	 *
	 * 2) Once reaching the root Entity (the one that does not specialize anything), then unroll all of its Composition elements.
	 *
	 * 3) Then recursively pop out of /work down the Entity specializes hierarchy. At each new level, unroll any Composition elements
	 * we already have if they are specialized at this level. Then add any new Composition elements found at this level that do not
	 * specialize.
	 *
	 * Once all the recursion is done, the Entity should be fully unrolled - except for the type.
	 *
	 * 4) Once all the Entities are unrolled, we can go back and link the type for each. This requires that we keep track of the
	 * original relationship between Entity and UnrolledEntity.
	 */

	abstract public String 			getRolename			(Characteristic c);
	abstract public Characteristic 	getSpecializes		(Characteristic c);
	abstract public Entity 			getSpecializes		(Entity e);
	abstract EList<Composition> 	getComposition		(Entity entity);
	abstract UComposition 			createComposition	(Composition c);
	
	/**
	 * The key will be the rolename of the composition. If a specialization results in a change in rolename, then the entry will be
	 * removed from the old rolename and reinserted using the new rolename.
	 */
	private Map<String, UComposition> composition;

	// NOTE: package private. Factories have public creation methods so they can force cache updates
	UnrolledEntity(Entity pe) {
		super(pe);
		composition = processSpecializationForCompositions(pe);
	}

	protected Map<String, UComposition> processSpecializationForCompositions(Entity pe) {
		Map<String, UComposition> compositionSoFar;
		/**
		 * First recurse if this is also specialized, the process locally. That allows locals to override anything
		 * inherited via specialization
		 */
		Entity specializedEntity = IndexUtilities.unProxiedEObject(getSpecializes(pe),pe);
		if (specializedEntity != null) {
			compositionSoFar = processSpecializationForCompositions(specializedEntity);
		} else {
			compositionSoFar = new LinkedHashMap<String, UComposition>();
		}
		setDescription(pe);
		return processLocalCompositions(pe,compositionSoFar);
	}

	protected Map<String, UComposition> processLocalCompositions(Entity pe, Map<String, UComposition> compositionSoFar) {
		/**
		 * NOTE: We do not merge compositionSoFar into results deliberately. Why? Because we might have multiple
		 * compositions that rename on specialization. In fact, we might have several that swap names. If we 
		 * were to merge these maps, we would not be able to tell if we could remove something from 'results'
		 * or not, because we wouldn't know if the 'results' map entry was an updated version reusing a name. 
		 * By keeping the maps separate, we we can do that safely. Then, at the very end, we merge what is left 
		 * of compositionSoFar into results - everything we want to 'override' has already been removed from it.
		 * 
		 * NOTE: Using LinkedHashMap to get predictable ordering (ordered by insertion)
		 */
		Map<String,UComposition> results = new LinkedHashMap<>();
		for (Composition pc: getComposition(pe)) {
			UComposition rc = null;			
			@SuppressWarnings("unchecked") // OCL invariants say that compositions can only specialize compositions
			Composition specializedComp = (Composition) IndexUtilities.unProxiedEObject( getSpecializes(pc),pe);
			if (specializedComp != null) {
				/** this is already in the map, find it by the rolename */
				 rc = compositionSoFar.remove(getRolename(specializedComp));
				/**
				 * By removing from the first list under the original rolename and inserting in the
				 * new results by the new rolename (line 107) , we also address any change to the rolename 
				 * that might occur as part of specialization.
				 */
				rc.update(pc, null);
			}
			else {
				/**
				 * It wasn't specializing anything, so create a new one
				 */
				 rc = createComposition(pc);
  			}
			results.put(getRolename(pc), rc);

		}
		// Merge in whatever is left of compositionsSoFar
		results.putAll(compositionSoFar);
		
		return results;
	}

	/**
	 * Get all the composition elements. Key is the rolename
	 * @return
	 */
	public Map<String, UComposition> getComposition() {
		return composition;
	}


}
