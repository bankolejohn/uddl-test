/**
 * 
 */
package com.epistimis.uddl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.Scopes;
import org.eclipse.xtext.scoping.impl.FilteringScope;
//import org.eclipse.xtext.util.SimpleAttributeResolver;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.IteratorExtensions;

//import com.epistimis.face.face.UopPortableComponent;
import com.epistimis.uddl.exceptions.CharacteristicNotFoundException;
import com.epistimis.uddl.util.IndexUtilities;
import com.epistimis.uddl.uddl.UddlElement;
import com.google.inject.Inject;

/**
 * C/L/P Entity and Association processing
 */
public abstract class EntityProcessor<ComposableElement extends UddlElement, 
										Characteristic extends EObject, 
										Entity extends ComposableElement, 
										Association extends Entity, 
										Composition extends Characteristic, 
										Participant extends Characteristic, 
										ElementalComposable extends ComposableElement,
										Container extends UddlElement> {
	// @Inject
//		private Provider<ResourceSet> resourceSetProvider;
	//
	//
//		@Inject
//		IResourceServiceProvider.Registry reg;
	//
//		IResourceServiceProvider queryRSP;
//		IResourceFactory queryResFactory;

//		@Inject
//		ParseHelper<QuerySpecification> parseHelper;

	@Inject
	IndexUtilities ndxUtil;

	@Inject
	IQualifiedNameProvider qnp;

	@Inject
	IQualifiedNameConverter qnc;

	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

	static MessageFormat CharacteristicNotFoundMsgFmt = new MessageFormat(
			"Entity {0} does not have a characteristic with rolename {1}");

	/**
	 * Get the Characteristic's rolename
	 * 
	 * @param obj
	 * @return
	 */
	abstract public String getCharacteristicRolename(Characteristic obj);// abstract protected Characteristic
																			// getCharacteristicByRolename(Entity ent,
																			// String roleName) throws
																			// CharacteristicNotFoundException;

	abstract public EClass getEntityEClass();
	abstract public EClass getCompositionEClass();
	abstract public EClass getParticipantEClass();

	abstract public Entity getSpecializes(Entity ent);

	abstract public boolean isAssociation(Entity ent);
	abstract public Association conv2Association(Entity ent);

	abstract public EList<? extends Composition> getComposition(Entity obj);

	abstract public EList<? extends Participant> getParticipant(Association obj);

	abstract public Composition conv2Composition(Characteristic characteristic);
	abstract public Participant conv2Participant(Characteristic characteristic);
	abstract public ComposableElement getCompositionType(Composition comp);
	abstract public Entity 			  getParticipantType(Participant part);
	
	abstract public String			  getCharacteristicDescription(Characteristic characteristic);

	abstract public boolean isContainer(UddlElement obj);
	abstract public Container conv2Container(UddlElement obj);
	abstract public EList<? extends UddlElement> getElement(Container obj);
	
	/**
	 * Get the type parameters for this generic class See also
	 * https://stackoverflow.com/questions/4213972/java-generics-get-class-of-generic-methods-return-type
	 * 
	 * @param ndx the index into the list of type parameters
	 * @return
	 */
	public Class<?> returnedTypeParameter(int ndx) {
		ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
		return (Class<?>) parameterizedType.getActualTypeArguments()[ndx];
	}

	/**
	 * Methods to return each of the parameter types - these warnings must remain
	 * because the alternative is a compile error when these values get used.
	 * 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Class getComposableElementType() {
		return returnedTypeParameter(0);
	}

	@SuppressWarnings("rawtypes")
	public Class getCharacteristicType() {
		return returnedTypeParameter(1);
	}

	@SuppressWarnings("rawtypes")
	public Class getEntityType() {
		return returnedTypeParameter(2);
	}

	@SuppressWarnings("rawtypes")
	public Class getAssociationType() {
		return returnedTypeParameter(3);
	}

	@SuppressWarnings("rawtypes")
	public Class getCompositionType() {
		return returnedTypeParameter(4);
	}

	@SuppressWarnings("rawtypes")
	public Class getParticipantType() {
		return returnedTypeParameter(5);
	}

	@SuppressWarnings("rawtypes")
	public Class getElementalComposableType() {
		return returnedTypeParameter(6);
	}

	@SuppressWarnings("rawtypes")
	public Class getContainerType() {
		return returnedTypeParameter(7);
	}
	
	/**
	 * Taken from the book, SmallJavaLib.getSmallJavaObjectClass - and converted
	 * from XTend to Java
	 * 
	 * Additionally, this checks for RQNs instead of just leaf names, or FQNs
	 * 
	 * Also note that case doesn't matter
	 * 
	 * @param context - Check visibility to this object
	 * @param type    - Filter for only for instances of this type
	 * @param name    - Filter for only instances that match this RQN
	 * @return A list of matching objects
	 * 
	 *         TODO:
	 */
	protected List<IEObjectDescription> searchAllVisibleObjects(EObject context, EClass type, String name) {
		List<IEObjectDescription> lod = ndxUtil.searchAllVisibleEObjectDescriptions(context, type, name);
		return lod;
	}

	/**
	 * Find all the Entities in this hierarchy.  
	 * This looks externally once the hierarchy in this Resource has been exhausted.
	 * 
	 * @param q
	 * @param context
	 * @return
	 */
	public IScope entityScope(EObject context) {
		/*
		 * 
		 * containers will always be a (C/L/P)DM or a DataModel
		 */
		@SuppressWarnings("unchecked") // The expression of type Class needs unchecked conversion to conform to
										// Class<Entity>
		final Iterable<Entity> entities = IterableExtensions
				.<Entity>filter(IteratorExtensions.<EObject>toIterable(context.eAllContents()), getEntityType());
		EObject container = context.eContainer();
		if (container != null) {
			return Scopes.scopeFor(entities, entityScope(container));
		} else {
			// When we reach the top, we can include externally visible entities
			// TODO: Will this create a problem for RQNs?
			return Scopes.scopeFor(entities,
									Scopes.scopeFor(ndxUtil.getVisibleExternalEObjectsByType(context, getEntityEClass()),IScope.NULLSCOPE)
							);
		}
	}


//		/**
//		 * Return the named characteristic - which could be a composition or a
//		 * participant
//		 * 
//		 * @param ent      The Entity containing the characteristic
//		 * @param roleName The rolename of the characteristic to find
//		 * @return The found characteristic
//		 */
//		protected Characteristic getCharacteristicByRolename(Entity ent, String roleName)
//				/*throws CharacteristicNotFoundException*/ {
//			return clp.getCharacteristicByRolename(ent, roleName);
//		}

	/**
	 * Because we can have duplicate leaf names even when the FQNs are distinct
	 * we return the full set. The caller will need to filter these in more
	 * detail. Note further: We do not have a generic QualifiedNameProvider
	 * defined in OCL because that duplicates what we have in Java. So we don't
	 * parse QNs here.
	 */
//	static def: findByName(n: String): Set(ConceptualEntity) =
//		let ents = uddl::ConceptualEntity.allInstances() in
//		ents->select(o|o.name = n)->asSet()
	@SuppressWarnings("unchecked") // call to getEntityEClass() ensures cast will work
	public Set<Entity> findByName(EObject context, String name) {
		List<EObject> objs = ndxUtil.searchAllVisibleObjects(context, getEntityEClass(), name);

		// Convert this to a Set of the appropriate type - this does the cast and conversion from List to Set
		Set<Entity> result = new HashSet<Entity>();
		for (Object o : objs) {
			result.add((Entity)o);
		}
		return result;
	}

//	/**
//	 * Return a set of all the model types referenced by this element
//	 */
////	    def: referencedModelTypes(): Set(ConceptualComposableElement) =
////	 		if (self.oclIsKindOf(ConceptualObservable)) then
////				self.oclAsType(ConceptualObservable).referencedModelTypes()
////			else
////				self.oclAsType(ConceptualEntity).referencedModelTypes()
////			endif
//
//	
//	public Set<ComposableElement> referencedModelTypes(ComposableElement self) {
//		if (getElementalComposableType().isInstance(self)) {
//			return referencedElementalModelTypes((ElementalComposable) self);
//		} else {
//			return referencedEntityModelTypes((Entity) self);
//		}
//	
//	public Set<ComposableElement> referencedElementalModelTypes(ElementalComposable self) {
//		Set<ComposableElement> result = new HashSet<ComposableElement>();
//		return result;
//	}
//		
//	public Set<ComposableElement> referencedEntityModelTypes(Entity self) {
//		Set<ComposableElement> result = new HashSet<ComposableElement>();
//		return result;
//	}
//	
//	/**
//	 * NOTE: You probably want to use referncedModelTyeps instead of this method
//	 * 
//	 * Return a set of all the model types referenced by this element. This is a
//	 * 'raw' or 'base' method that doesn't include 'self' because it it called by
//	 * other defs that add 'self'. This is an optimization issue - we want to track
//	 * what has been processed - and we want to know if a type is self referential
//	 * because we need to terminate any recursion immediately. This method won't
//	 * recurse since it only walks the specialization hierarchy, and we have
//	 * invariants that already check for recursion in specialization.
//	 * 
//	 */
////	def: typeReferences(): Set(ConceptualComposableElement) =
////		let myComps = self.composition->collect(type.referencedModelTypes())->flatten()->asSet() in
////		let parentTypes = if (self.specializes.oclIsUndefined()) then  Set {} 
////		else 
////			if self.specializes.oclIsKindOf(ConceptualAssociation) then
////				self.specializes.oclAsType(ConceptualAssociation).referencedModelTypes() 
////			else 
////				self.specializes.referencedModelTypes() 
////			endif
////		endif in
////		myComps->union(parentTypes)->asSet()
//	public Set<ComposableElement> typeReferences(Entity self) {
//		Set<ComposableElement> result = getComposition(self).stream()
//				.map(Composition::getType)
//				.map(referencedModelTypes)
//				.flatMap(list -> list.stream())
//				.collect(Collectors.toSet());
//
//		if (self.getSpecializes() != null) {
//			
//		}
//		
//		return result;
//	}

	
	/**
	 * Get all the characteristics
	 * 
	 * @param obj
	 * @return the list of characteristics.
	 */
	public Map<String, Characteristic> getCharacteristics(Entity obj) {
		Map<String, Characteristic> characteristics = new HashMap<String, Characteristic>();
		getCharacteristicsAndRecurse(obj, characteristics);
		return characteristics;
	}

	/**
	 * Get all the participants
	 * 
	 * @param obj
	 * @return the list of participants.
	 */
	public Map<String, Participant> getParticipants(Association assoc) {
		Map<String, Participant> characteristics = new HashMap<String, Participant>();
		getParticipantsAndRecurse(assoc, characteristics);
		return characteristics;
	}
	/**
	 * Get the characteristics from this Entity, without following the
	 * specialization hierarchy
	 * 
	 * @param obj
	 * @param characteristics
	 */
	public void getLocalCharacteristics(Entity obj, Map<String, Characteristic> characteristics) {
		for (Composition pc : getComposition(obj)) {
			characteristics.putIfAbsent(getCharacteristicRolename(pc), pc);
		}
		if (isAssociation(obj)) {
			@SuppressWarnings("unchecked") // isAssociation ensures cast will work
			Association assoc = (Association) obj;
			for (Participant pp : getParticipant(assoc)) {
				characteristics.putIfAbsent(getCharacteristicRolename(pp), (Characteristic) pp);
			}
		}

	}

	/**
	 * Get the participants from this Association, without following the
	 * specialization hierarchy
	 * 
	 * @param obj
	 * @param participants
	 */
	public void getLocalParticipants(Association assoc, Map<String, Participant> participants) {
		for (Participant pp : getParticipant(assoc)) {
			participants.putIfAbsent(getCharacteristicRolename(pp), pp);
		}
	}

	/**
	 * Get the set of all characteristics from this entity - across the entire
	 * specialization hierarchy. Note that, because we start from the bottom, any
	 * specializing characteristics will override same named elements higher in the
	 * hierarchy
	 * 
	 * This actually implements collecting the characteristics. It handles the
	 * recursion
	 * 
	 * @param obj
	 * @param the map of characteristics. Starts empty and gets filled.
	 */
	protected void getCharacteristicsAndRecurse(Entity obj, Map<String, Characteristic> characteristics) {

		getLocalCharacteristics(obj, characteristics);

		// Now check for specialization
		Entity specializes = getSpecializes(obj);
		if (specializes != null) {
			getCharacteristicsAndRecurse(specializes, characteristics);
		}

	}

	/**
	 * Get the set of all participants from this association - across the entire
	 * specialization hierarchy. Note that, because we start from the bottom, any
	 * specializing participants will override same named elements higher in the
	 * hierarchy
	 * 
	 * This actually implements collecting the participants. It handles the
	 * recursion
	 * 
	 * @param obj
	 * @param the map of participants. Starts empty and gets filled.
	 */
	@SuppressWarnings("unchecked") // isAssociation check ensures cast will work
	protected void getParticipantsAndRecurse(Association assoc, Map<String, Participant> participants) {

		getLocalParticipants(assoc, participants);

		// Now check for specialization
		Entity specializes = getSpecializes(assoc);
		if (specializes != null && isAssociation(specializes)) {
			getParticipantsAndRecurse((Association)specializes, participants);
		}

	}

	/**
	 * Return the named characteristic - which could be a composition or a
	 * participant
	 * 
	 * @param ent      The Entity containing the characteristic
	 * @param roleName The rolename of the characteristic to find
	 * @return The found characteristic
	 */
	public Characteristic getCharacteristicByRolename(Entity ent, String roleName)
	/* throws CharacteristicNotFoundException */ {
		// Look for the characteristic in this Entity and, if not found, go up the
		// specializes chain until we find it
		for (Composition comp : getComposition(ent)) {
			if (getCharacteristicRolename(comp).equals(roleName))
				return comp;
		}
		if (isAssociation(ent)) {
			@SuppressWarnings("unchecked") // isAssociation ensures cast will work
			Association assoc = (Association) ent;
			for (Characteristic part : getParticipant(assoc)) {
				if (getCharacteristicRolename(part).equals(roleName))
					return part;

			}
		}
		// If we get here, we haven't found it yet - check for specializes
		if (getSpecializes(ent) != null) {
			return getCharacteristicByRolename(getSpecializes(ent), roleName);
		}
		// If we get here, it wasn't found
		Object[] args = { ent, roleName };
		throw new CharacteristicNotFoundException(CharacteristicNotFoundMsgFmt.format(args));
	}

	/**
	 * Given a string containing a (possibly qualified) rolename, return a map of
	 * all Characteristics that contain that rolename in their FQN somewhere. In
	 * some ways this is the inverse of Scopes.scopeFor. A Scope determines what can
	 * be found from the current context point using the specified name and
	 * reference type. That means that RQNs are relative to this point.
	 * 
	 * What we want here is anything where the specified RQN is a part of the name
	 * of something contained in or referenced by the context object - and the name
	 * may not give a complete enough path to be reachable just using the RQN from
	 * this context point.
	 * 
	 * Net result: We can't just use Scopes.scopeFor to find what we want.
	 */
	public Map<QualifiedName, Characteristic> getFQRoleName(Entity ent, String roleName) {
		logger.error("This method has not been finished!");
		// The simple approach just looks at what is contained in the specified entity.
		// It does not follow references to other entities.
		Map<QualifiedName, Characteristic> result = new HashMap<QualifiedName, Characteristic>();
		try {
			Characteristic c = getCharacteristicByRolename(ent, roleName);
			result.put(qnp.getFullyQualifiedName(c), c);

		} catch (CharacteristicNotFoundException excp) {
			// do nothing
		}
		// TODO: We also need to scan all the Compositions that have Entity types and
		// drill down into those
		for (Composition comp : getComposition(ent)) {
			// if (comp.type instanceof Entity)
			// TODO: THIS IS NOT FINISHED !!!!
		}
		throw new RuntimeException("Method not yet implemented");
//		return result;

		// TODO: follow participants
	}

	/**
	 * Return the in order list of specializations starting with this object and
	 * walking up the specialization hierarchy
	 * 
	 * @param start - the first entity
	 * @return - a list of entities that are the specialization ancestry of this
	 *         starting entity
	 */
	public List<Entity> specializationAncestry(Entity start) {
		Entity spec = getSpecializes(start);
		List<Entity> result = new ArrayList<Entity>();
		result.add(start);
		if (spec != null) {
			result.addAll(specializationAncestry(spec));
		}
		return result;
	}

	/**
	 * oclIsKindOf uses the metamodel. We want to follow the UDDL specialization
	 * hierarchy
	 */
	public boolean isTypeOrSpecializationOf(Entity obj, Entity targetType) {
		if (obj == targetType) {
			return true;
		}
		Entity spec = getSpecializes(obj);
		if (spec != null) {
			return isTypeOrSpecializationOf(spec, targetType);
		}
		// else
		return false;
	}

	/**
	 * Get all the Entities that specialize the root. (this is traversing
	 * specialization in the inverse direction. If we cannot follow the inverse
	 * directly, then we have to use allInstances)
	 */
	public Set<Entity> specializationHierarchy(Entity root) {
		Set<Entity> result = new HashSet<Entity>();
		EClass clz = getEntityEClass();
		Iterable<EObject> vobjs = ndxUtil.getVisibleObjects(root, clz);
		for (EObject obj : vobjs) {
			@SuppressWarnings("unchecked") // getVisibleObjects has already filtered by type
			Entity entity = (Entity) obj;
			if (isTypeOrSpecializationOf(entity, root)) {
				result.add(entity);
			}
		}
		return result;
	}

	/**
	 * Recurse up the Entity specializaton hierarchy to collect the list of Composition elements
	 * already specified. By walking up the hierarchy and creating a Map, we enable shadowing
	 * if a specialization wants to override the Entity it specializes
	 * @param entity
	 * @return
	 */
	public Map<String, Composition> allCompositions(Entity entity) {
		if (entity == null) {
			return new HashMap<String,Composition>();
		}
		else {
			Map<String,Composition> prevElements = allCompositions(getSpecializes(entity));
			for (Composition c: getComposition(entity)) {
				// By overwriting prior values, we allow specializations to shadow the entities they
				// specialize
				prevElements.put(getCharacteristicRolename(c), c);
			}
			return prevElements;
		}
	}

	/**
	 * Recurse up the Association specializaton hierarchy to collect all the Participants. This 
	 * automatically enables shadowing
	 * @param entity
	 * @return
	 */
	public Map<String,Participant> allParticipants(Association entity) {
		if (entity == null) {
			return new HashMap<String,Participant>();
		}
		else {
			Map<String,Participant> prevElements = new HashMap<String,Participant>();
			Entity spec = getSpecializes(entity);
			// Associations can specialize an Entity but not the otherway around (because once
			// you have participants, you can't get rid of them). So, once we find a specialization
			// that is *not* an Association, we can stop.
			if (isAssociation(spec)) {
				prevElements = allParticipants(conv2Association(spec));
			}
			for (Participant p: getParticipant(entity) ) {
				// By overwriting prior values, we allow specializations to shadow the entities they
				// specialize
				prevElements.put(getCharacteristicRolename(p), p);				
			}
			return prevElements;
		}
	}


	/**
	 * Recurse up the Entity specialization hierarchy to generate the Composition appropriate scope. This 
	 * automatically enables shadowing
	 * @param entity
	 * @return
	 */
	public IScope scopeForCompositionSelection(Entity entity) {
		if (entity == null) {
			return IScope.NULLSCOPE;
		}
		else {
			EList<? extends Composition> comps = getComposition(entity);
			// Can't use the default scopeFor because Compositions have a 'rolename' instead of a 'name'. 
			// See the implementation of the default Scopes.scopeFor method.
			// So, do this instead:
//			return Scopes.scopeFor(comps, QualifiedName.wrapper(SimpleAttributeResolver.newResolver(String.class,"rolename")),
//					scopeForCompositionSelection(getSpecializes(entity)));
			
            IScope existingScope = Scopes.scopeFor(ndxUtil.getVisibleObjects(entity, getCompositionEClass()),
            		scopeForCompositionSelection(getSpecializes(entity)));
                       
            // Scope that filters to select only the parts that are relevant
			return new FilteringScope(existingScope, (e) -> comps.contains(e.getEObjectOrProxy()));
		
		}
	}

	/**
	 * Recurse up the Association specializaton hierarchy to generate the Participant appropriate scope. This 
	 * automatically enables shadowing
	 * @param entity
	 * @return
	 */
	public IScope scopeForParticipantSelection(Association entity) {
		if (entity == null) {
			return IScope.NULLSCOPE;
		}
		else {
			Entity spec = getSpecializes(entity);
			EList<? extends Participant> parts =  getParticipant(entity);
			// Associations can specialize an Entity but not the otherway around (because once
			// you have participants, you can't get rid of them). So, once we find a specialization
			// that is *not* an Association, we can stop.
			if (isAssociation(spec)) {
				// Can't use the default scopeFor because Compositions have a 'rolename' instead of a 'name'. 
				// See the implementation of the default Scopes.scopeFor method.
				// So, do this instead:
//				return Scopes.scopeFor(parts,QualifiedName.wrapper(SimpleAttributeResolver.newResolver(String.class,"rolename")),
//						scopeForParticipantSelection(conv2Association(spec)));

	            IScope existingScope = Scopes.scopeFor(ndxUtil.getVisibleObjects(entity, getCompositionEClass()),
	            		scopeForParticipantSelection(conv2Association(spec)));
	                       
	            // Scope that filters to select only the parts that are relevant
				return new FilteringScope(existingScope, (e) -> parts.contains(e.getEObjectOrProxy()));

			
			} else
			{
				// Can't use the default scopeFor because Compositions have a 'rolename' instead of a 'name'. 
				// See the implementation of the default Scopes.scopeFor method.
				// So, do this instead:
//				return Scopes.scopeFor(parts,QualifiedName.wrapper(SimpleAttributeResolver.newResolver(String.class,"rolename")),
//						IScope.NULLSCOPE);			

	            IScope existingScope = Scopes.scopeFor(ndxUtil.getVisibleObjects(entity, getCompositionEClass()),
	            		scopeForParticipantSelection(conv2Association(spec)));
	                       
	            // Scope that filters to select only the parts that are relevant
				return new FilteringScope(existingScope, (e) -> parts.contains(e.getEObjectOrProxy()));

			
			}
		}
	}
	
}
