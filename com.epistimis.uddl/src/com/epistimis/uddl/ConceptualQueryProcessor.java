package com.epistimis.uddl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.epistimis.uddl.exceptions.WrongTypeException;
import com.epistimis.uddl.uddl.ConceptualAssociation;
import com.epistimis.uddl.uddl.ConceptualCharacteristic;
import com.epistimis.uddl.uddl.ConceptualComposableElement;
import com.epistimis.uddl.uddl.ConceptualCompositeQuery;
import com.epistimis.uddl.uddl.ConceptualComposition;
import com.epistimis.uddl.uddl.ConceptualDataModel;
import com.epistimis.uddl.uddl.ConceptualEntity;
import com.epistimis.uddl.uddl.ConceptualObservable;
import com.epistimis.uddl.uddl.ConceptualParticipant;
import com.epistimis.uddl.uddl.ConceptualQuery;
import com.epistimis.uddl.uddl.ConceptualQueryComposition;
import com.epistimis.uddl.uddl.ConceptualView;
import com.epistimis.uddl.uddl.UddlPackage;

public class ConceptualQueryProcessor extends
		QueryProcessor<ConceptualComposableElement,ConceptualCharacteristic, ConceptualEntity, ConceptualAssociation, ConceptualComposition, ConceptualParticipant, 
		ConceptualView, ConceptualQuery, ConceptualCompositeQuery, ConceptualQueryComposition,ConceptualObservable, ConceptualDataModel,
		ConceptualEntityProcessor> {

	protected static Map<String,Map<String, QuerySelectedComposition<ConceptualCharacteristic>>> cardinalityCache = new HashMap<>();
	
	protected EList<ConceptualQueryComposition> getComposition(ConceptualCompositeQuery ent) {
		return ent.getComposition();
	}

	protected ConceptualView getType(ConceptualQueryComposition obj) {
		return obj.getType();
	}

	
	protected boolean isCompositeQuery(ConceptualView obj) {
		return (obj instanceof ConceptualCompositeQuery);
	}

	protected EClass getRelatedPackageEntityInstance(ConceptualQuery obj) {
		return 	UddlPackage.eINSTANCE.getConceptualEntity();
	}

	protected List<ConceptualCharacteristic> getCharacteristics(ConceptualEntity obj) {
		
		List<ConceptualCharacteristic> characteristics = new ArrayList<>();
		for (ConceptualComposition pc: obj.getComposition()) {
			characteristics.add(pc);
		}
		if (obj instanceof ConceptualAssociation) {
			for (ConceptualParticipant pp: ((ConceptualAssociation)obj).getParticipant()) {
				characteristics.add(pp);
			}
		}
		return characteristics;
	}
	protected String getCharacteristicRolename(ConceptualCharacteristic obj) { return obj.getRolename(); }

	@Override
	public ConceptualComposableElement getCharacteristicType(ConceptualCharacteristic obj) {
		// TODO Auto-generated method stub
		if (obj instanceof ConceptualComposition) {
			return ((ConceptualComposition)obj).getType();
		};
		if (obj instanceof ConceptualParticipant) {
			return ((ConceptualParticipant)obj).getType();
		}
		
		String msg = MessageFormat.format(WRONG_TYPE_FMT, qnp.getFullyQualifiedName(obj).toString(), "ConceptualComposition or ConceptualParticipant",
				obj.getClass().toString());
		throw new WrongTypeException(msg);

	}

	@Override
	protected int getCharacteristicLowerBound(ConceptualCharacteristic obj) {
		// TODO Auto-generated method stub
		return obj.getLowerBound();
	}

	@Override
	protected int getCharacteristicUpperBound(ConceptualCharacteristic obj) {
		// TODO Auto-generated method stub
		return obj.getUpperBound();
	}

	@Override
	public void updateCardinalityCache(ConceptualQuery q,
			Map<String, QuerySelectedComposition<ConceptualCharacteristic>> map) {
		Map<String, QuerySelectedComposition<ConceptualCharacteristic>> foundMap = getCardinalties(q);
		assert(foundMap == null); // We should not be updating existing content
		String key = qnp.getFullyQualifiedName(q).toString();
		cardinalityCache.put(key, map);
		
	}

	@Override
	public Map<String, QuerySelectedComposition<ConceptualCharacteristic>> getCardinalties(ConceptualQuery q) {
		String key = qnp.getFullyQualifiedName(q).toString();
		Map<String, QuerySelectedComposition<ConceptualCharacteristic>> foundMap = cardinalityCache.get(key);
		return foundMap;
	}

	@Override
	public void flushCardinalityCache() {
		cardinalityCache.clear();
		
	}

	@Override
	public Map<String, ConceptualCharacteristic> selectCharacteristicsFromCache(ConceptualQuery q) {
		Map<String, QuerySelectedComposition<ConceptualCharacteristic>> foundMap = getCardinalties(q);
		
		Map<String, ConceptualCharacteristic> result = new LinkedHashMap<String, ConceptualCharacteristic>();
		for (Map.Entry<String, QuerySelectedComposition<ConceptualCharacteristic>> entry: foundMap.entrySet()) {
			result.put(entry.getKey(), entry.getValue().referencedCharacteristic);
		}
		return result;

	}


}
