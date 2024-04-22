package com.epistimis.uddl

import com.epistimis.uddl.exceptions.CharacteristicNotFoundException
import com.epistimis.uddl.uddl.ConceptualAssociation
import com.epistimis.uddl.uddl.ConceptualCharacteristic
import com.epistimis.uddl.uddl.ConceptualComposableElement
import com.epistimis.uddl.uddl.ConceptualCompositeQuery
import com.epistimis.uddl.uddl.ConceptualComposition
import com.epistimis.uddl.uddl.ConceptualEntity
import com.epistimis.uddl.uddl.ConceptualParticipant
import com.epistimis.uddl.uddl.ConceptualQuery
import com.epistimis.uddl.uddl.ConceptualQueryComposition
import com.epistimis.uddl.uddl.ConceptualView
import com.epistimis.uddl.uddl.LogicalAssociation
import com.epistimis.uddl.uddl.LogicalCharacteristic
import com.epistimis.uddl.uddl.LogicalComposableElement
import com.epistimis.uddl.uddl.LogicalCompositeQuery
import com.epistimis.uddl.uddl.LogicalComposition
import com.epistimis.uddl.uddl.LogicalEntity
import com.epistimis.uddl.uddl.LogicalParticipant
import com.epistimis.uddl.uddl.LogicalQuery
import com.epistimis.uddl.uddl.LogicalQueryComposition
import com.epistimis.uddl.uddl.LogicalView
import com.epistimis.uddl.uddl.PlatformAssociation
import com.epistimis.uddl.uddl.PlatformCharacteristic
import com.epistimis.uddl.uddl.PlatformComposableElement
import com.epistimis.uddl.uddl.PlatformCompositeQuery
import com.epistimis.uddl.uddl.PlatformComposition
import com.epistimis.uddl.uddl.PlatformEntity
import com.epistimis.uddl.uddl.PlatformParticipant
import com.epistimis.uddl.uddl.PlatformQuery
import com.epistimis.uddl.uddl.PlatformQueryComposition
import com.epistimis.uddl.uddl.PlatformView
import com.epistimis.uddl.uddl.UddlElement
import com.epistimis.uddl.uddl.UddlPackage
import com.google.inject.Inject
import java.text.MessageFormat
import java.util.HashMap
import java.util.Map
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EObject
import org.eclipse.xtext.naming.IQualifiedNameProvider
import org.eclipse.xtext.naming.QualifiedName

/**
 * This is a set of methods that extract values from instances for use with templated methods
 * @author stevehickman
 *
 */
class CLPExtractors {

	@Inject IQualifiedNameProvider qnp; // = new UddlQNP();

	def dispatch static ConceptualEntity getSpecializes(EObject ent) {
		return null;
	}	
	def dispatch static ConceptualEntity getSpecializes(ConceptualEntity ent) {
		return  ent.getSpecializes();
	}
	def dispatch static LogicalEntity getSpecializes(LogicalEntity ent) {
		return ent.getSpecializes();
	}
	def dispatch static PlatformEntity getSpecializes(PlatformEntity ent) {
		return ent.getSpecializes();
	}
	

	/**
	 * Type checking defaults to false
	 */
	def dispatch static boolean isAssociation(EObject ent) {
		return false;
	}
	def dispatch static boolean isAssociation(ConceptualEntity ent) {
		return  (ent instanceof ConceptualAssociation);
	}
	def dispatch static boolean isAssociation(LogicalEntity ent) {
		return  (ent instanceof LogicalAssociation);
	}
	def dispatch static boolean isAssociation(PlatformEntity ent) {
		return  (ent instanceof PlatformAssociation);
	}

	def dispatch static ConceptualAssociation conv2Association(ConceptualEntity ent) {
		return ent as ConceptualAssociation;
	}	
	def dispatch static LogicalAssociation conv2Association(LogicalEntity ent) {
		return ent as LogicalAssociation;
	}
	def dispatch static PlatformAssociation conv2Association(PlatformEntity ent) {
		return  ent as PlatformAssociation;
	}

	def dispatch static EList<ConceptualComposition> getComposition(ConceptualEntity obj) {
		return obj.getComposition();
	}
	def dispatch static EList<LogicalComposition> getComposition(LogicalEntity obj) {
		return obj.getComposition();
	}
	def dispatch static EList<PlatformComposition> getComposition(PlatformEntity obj) {
		return obj.getComposition();
	}

	
	def dispatch static EList<ConceptualParticipant> getParticipant(ConceptualAssociation obj) {
		return obj.getParticipant();
	}
	def dispatch static EList<LogicalParticipant> getParticipant(LogicalAssociation obj) {
		return obj.getParticipant();
	}
	def dispatch static EList<PlatformParticipant> getParticipant(PlatformAssociation obj) {
		return obj.getParticipant();
	}

	def dispatch static String getSpecification(ConceptualQuery obj) {
		return obj.specification;
	}
	def dispatch static String getSpecification(LogicalQuery obj) {
		return obj.specification;
	}
	def dispatch static String getSpecification(PlatformQuery obj) {
		return obj.specification;
	}

	def dispatch static Class<? extends UddlElement> getClassForQuery(ConceptualQuery obj) {
		return typeof(ConceptualEntity);
	}
	def dispatch static Class<? extends UddlElement> getClassForQuery(LogicalQuery obj) {
		return typeof(LogicalEntity);
	}
	def dispatch static Class<? extends UddlElement> getClassForQuery(PlatformQuery obj) {
		return typeof(PlatformEntity);
	}

	def dispatch static EList<ConceptualQueryComposition> getQueryComposition(ConceptualCompositeQuery ent) {
		return ent.composition;
	}
	def dispatch static EList<LogicalQueryComposition> getQueryComposition(LogicalCompositeQuery ent) {
		return ent.composition;
	}
	def dispatch static EList<PlatformQueryComposition> getQueryComposition(PlatformCompositeQuery ent) {
		return ent.composition;
	}

	def dispatch static ConceptualView getType(ConceptualQueryComposition obj) {
		return obj.type;
	}
	def dispatch static ConceptualComposableElement getType(ConceptualComposition obj) {
		return obj.type;
	}
	def dispatch static ConceptualComposableElement getType(ConceptualParticipant obj) {
		return obj.type;
	}

	def dispatch static LogicalView getType(LogicalQueryComposition obj) {
		return obj.type;
	}
	def dispatch static LogicalComposableElement getType(LogicalComposition obj) {
		return obj.type;
	}
	def dispatch static LogicalComposableElement getType(LogicalParticipant obj) {
		return obj.type;
	}

	def dispatch static PlatformView getType(PlatformQueryComposition obj) {
		return obj.type;
	}
	def dispatch static PlatformComposableElement getType(PlatformComposition obj) {
		return obj.type;
	}
	def dispatch static PlatformComposableElement getType(PlatformParticipant obj) {
		return obj.type;
	}



	/**
	 * Type checking defaults to false
	 */
	def dispatch static boolean isCompositeQuery(EObject ent) {
		return false;
	}	
	def dispatch static boolean isCompositeQuery(ConceptualView obj) {
		return (obj instanceof ConceptualCompositeQuery);
	}
	def dispatch static boolean isCompositeQuery(LogicalView obj) {
		return (obj instanceof LogicalCompositeQuery);
	}
	def dispatch static boolean isCompositeQuery(PlatformView obj) {
		return (obj instanceof PlatformCompositeQuery);
	}

	def dispatch static EClass getRelatedPackageEntityInstance(ConceptualQuery obj) {
		return 	UddlPackage.eINSTANCE.getConceptualEntity()
	}
	def dispatch static EClass getRelatedPackageEntityInstance(LogicalQuery obj) {
		return 	UddlPackage.eINSTANCE.getLogicalEntity()
	}
	def dispatch static EClass getRelatedPackageEntityInstance(PlatformQuery obj) {
		return 	UddlPackage.eINSTANCE.getPlatformEntity()
	}

	static MessageFormat CharacteristicNotFoundMsgFmt = new MessageFormat(
			"Entity {0} does not have a characteristic with rolename {1}");

	def dispatch  String getCharacteristicRolename(ConceptualCharacteristic obj) { return obj.getRolename(); }
	def dispatch  String getCharacteristicRolename(LogicalCharacteristic obj) { return obj.getRolename(); }
	def dispatch  String getCharacteristicRolename(PlatformCharacteristic obj) { return obj.getRolename(); }


	/**
	 * We recurse up the specializes hierarchy to collect the 'parent'. As we do
	 * this, we create a 'specialization' tree.As we pop out of that, we bring the
	 * characteristics from the prior levels to this one. When we are finished at
	 * each level
	 */

	def <Entity extends UddlElement> boolean isA(Entity obj, QualifiedName typeName) {

		if (qnp.getFullyQualifiedName(obj).equalsIgnoreCase(typeName)) {
			return true;
		}
		val UddlElement specializes = CLPExtractors.getSpecializes(obj);
		if (specializes !== null) {
			return isA(specializes, typeName);
		}
		// If we get here, it isn't that type or a specialization of that type
		return false;
	}

	/**
	 * Get all the characteristics
	 * @param obj
	 * @return the list of characteristics. 
	 */
	def  <Entity extends UddlElement,Characteristic extends EObject>  Map<String,Characteristic> getCharacteristics(Entity obj ) {
		var Map<String, Characteristic> characteristics = new HashMap();
		getCharacteristicsAndRecurse(obj,characteristics);
		return characteristics;
	}


	/**
	 * Get the set of all characteristics from this entity - across the entire
	 * specialization hierarchy. Note that, because we start from the bottom, any
	 * specializing characteristics will override same named elements higher in the
	 * hierarchy
	 * 
	 * This actually implements collecting the characteristics. It handles the recursion
	 * @param obj
	 * @param the map of characteristics. Starts empty and gets filled.
	 */
	def <Entity extends UddlElement,Characteristic extends EObject> void getCharacteristicsAndRecurse(Entity obj, Map<String, Characteristic> characteristics) {

		for (EObject pc : CLPExtractors.getComposition(obj)) {
			characteristics.putIfAbsent(getCharacteristicRolename(pc), pc as Characteristic);
		}
		if (CLPExtractors.isAssociation(obj)) {
			for (EObject pp : CLPExtractors.getParticipant(obj)) {
				characteristics.putIfAbsent(getCharacteristicRolename(pp), pp as Characteristic);
			}
		}
		// Now check for specialization
		val UddlElement specializes = CLPExtractors.getSpecializes(obj);
		if (specializes !== null) {
			getCharacteristicsAndRecurse( specializes as Entity, characteristics);
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
	def  <Entity extends UddlElement,Characteristic extends EObject> Characteristic getCharacteristicByRolename(Entity ent, String roleName)
			/*throws CharacteristicNotFoundException*/ {
		// Look for the characteristic in this Entity and, if not found, go up the
		// specializes chain until we find it
		for (EObject obj : CLPExtractors.getComposition(ent)) {
			val Characteristic comp =  obj as Characteristic;
			if (getCharacteristicRolename(comp).equals(roleName))
				return comp;
		}
		if (CLPExtractors.isAssociation(ent)) {
			for (EObject obj : CLPExtractors.getParticipant(ent)) {
				val Characteristic part = obj as Characteristic;
				if (getCharacteristicRolename(part).equals(roleName))
					return part;

			}
		}
		// If we get here, we haven't found it yet - check for specializes
		if (CLPExtractors.getSpecializes(ent) !== null) {
			return getCharacteristicByRolename( CLPExtractors.getSpecializes(ent) as Entity, roleName);
		}
		// If we get here, it wasn't found
		val Object[] args = #[ ent, roleName ];
		throw new CharacteristicNotFoundException(CharacteristicNotFoundMsgFmt.format(args));
	}
	
	/**
	 * Given a string containing a (possibly qualified) rolename, return a map of all Characteristics that contain that rolename in their
	 * FQN somewhere. In some ways this is the inverse of Scopes.scopeFor. A Scope determines what can be found from the current context 
	 * point using the specified name and reference type.  That means that RQNs are relative to this point. 
	 * 
	 * What we want here is anything where the specified RQN is a part of the name of something contained in or referenced by the context object -
	 * and the name may not give a complete enough path to be reachable just using the RQN from this context point.
	 * 
	 * Net result: We can't just use Scopes.scopeFor to find what we want.
	 */
	def  <Entity extends UddlElement,Characteristic extends EObject, Composition extends Characteristic> Map<QualifiedName,Characteristic> getFQRoleName(Entity ent,String roleName) {
		// The simple approach just looks at what is contained in the specified entity. It does not follow references to other entities.
		var Map<QualifiedName, Characteristic> result = new HashMap();
		try {
			val Characteristic c = getCharacteristicByRolename(ent,roleName);
			result.put(qnp.getFullyQualifiedName(c),c);
		
		} catch (CharacteristicNotFoundException excp) {
			// do nothing
		}
		// TODO: We also need to scan all the Compositions that have Entity types and drill down into those
		val EList<? extends EObject> comps = CLPExtractors.getComposition(ent);
		for (EObject obj: comps) {
			//val Composition comp = obj as Composition;
			//if (CLPExtractors.iscomp.type is Entity)
			 
		}
		
		return result;
		
		// TODO: follow participants
	}
	
	/**
	 * Navigating via associations is what ParticipantPathNode and CharacteristicPathNode are all about. Any attempt to navigate should use the
	 * same code that is used for them.
	 */
	
//	def static <Entity extends UddlElement,Characteristic extends EObject> Map<QualifiedName,Characteristic> navigateRQN(Entity ent,QualifiedName rolePath) {
	

	

}
