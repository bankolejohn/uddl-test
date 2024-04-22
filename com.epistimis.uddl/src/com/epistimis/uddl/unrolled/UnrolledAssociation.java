package com.epistimis.uddl.unrolled;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.UddlElement;
import com.epistimis.uddl.util.IndexUtilities;


public abstract class UnrolledAssociation<ComposableElement extends UddlElement, 
											Entity extends ComposableElement, 
											Association extends Entity, 
											Characteristic  extends EObject,  
											Composition extends Characteristic,
											Participant extends Characteristic,
											UComposableElement extends UnrolledComposableElement<ComposableElement>,
											UComposition extends UnrolledComposition<ComposableElement, Characteristic, Composition,UComposableElement>,
											UParticipant extends UnrolledParticipant<ComposableElement, Entity, Characteristic, Participant,UComposableElement>> 
	extends UnrolledEntity<ComposableElement, Entity, Characteristic,  Composition ,UComposableElement, UComposition> 
	{
	/**
	 * The key will be the rolename of the participant. If a specialization results in a change in rolename, then the entry will be
	 * removed from the old rolename and reinserted using the new rolename.
	 */
	private Map<String, UParticipant> participant;
	
	abstract public Entity  			getSpecializes(Entity e);
	abstract public EList<Participant>	getParticipant(Association assoc);
	abstract boolean 					isAssociation(Entity e);
	abstract UParticipant 				createParticipant(Participant c);
	
	public UnrolledAssociation(@NonNull Association pa) {
		super(pa);

		participant = processSpecializationForParticipants(pa);

	}

	@SuppressWarnings("unchecked") // Cast to Association OK because of isAssociation check
	protected Map<String, UParticipant> processSpecializationForParticipants(Association pa) {
		Map<String, UParticipant> participantsSoFar;
		/**
		 * First recurse if this is also specialized, the process locally. That allows locals to override anything
		 * inherited via specialization
		 */
		Entity specializedEntity = IndexUtilities.unProxiedEObject(getSpecializes(pa),pa);
		if ((specializedEntity != null) && isAssociation(specializedEntity)) {
			/**
			 * This assumes that once we inherit from Association, everything down the specialization hierarchy
			 * must also be a Association.
			 */
			participantsSoFar = processSpecializationForParticipants((Association)specializedEntity);
		} else {
			participantsSoFar = new HashMap<String, UParticipant>();
		}
		return processLocalParticipants(pa,participantsSoFar);
	}

	protected Map<String, UParticipant> processLocalParticipants(Association pe, Map<String, UParticipant> participantsSoFar) {
		/**
		 * NOTE: We do not merge participantsSoFar into results deliberately. Why? Because we might have multiple
		 * compositions that rename on specialization. In fact, we might have several that swap names. If we 
		 * were to merge these maps, we would not be able to tell if we could remove something from 'results'
		 * or not, because we wouldn't know if the 'results' map entry was an updated version reusing a name. 
		 * By keeping the maps separate, we we can do that safely. Then, at the very end, we merge what is left 
		 * of participantsSoFar into results - everything we want to 'override' has already been removed from it.
		 */
		Map<String,UParticipant> results = new HashMap<String, UParticipant>();
		for (Participant pc: getParticipant(pe)) {
			UParticipant rp = null;
			@SuppressWarnings("unchecked") // OCL checks mean participants can only specialize participants
			Participant specializedPart = (Participant) IndexUtilities.unProxiedEObject(getSpecializes(pc),pc);
			if (specializedPart != null) {
				/** this is already in the map, find it by the rolename */
				 rp = participantsSoFar.remove(getRolename(specializedPart));
				/**
				 * By removing from the first list under the original rolename and inserting in the
				 * new results by the new rolename, we also address any change to the rolename that might
				 * occur as part of specialization.
				 */
				rp.update(pc, null);
			}
			else {
				/**
				 * It wasn't specializing anything, so create a new one
				 */
				rp = createParticipant(pc);
  			}
			results.put(getRolename(pc), rp);

		}
		// Merge remaining previous results
		results.putAll(participantsSoFar);
		return results;
	}

	public Map<String, UParticipant> getParticipant() {
		return participant;
	}

}
