package com.epistimis.uddl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.epistimis.uddl.exceptions.WrongTypeException;
import com.epistimis.uddl.uddl.PlatformAssociation;
import com.epistimis.uddl.uddl.PlatformCharacteristic;
import com.epistimis.uddl.uddl.PlatformComposableElement;
import com.epistimis.uddl.uddl.PlatformCompositeQuery;
import com.epistimis.uddl.uddl.PlatformComposition;
import com.epistimis.uddl.uddl.PlatformDataModel;
import com.epistimis.uddl.uddl.PlatformDataType;
import com.epistimis.uddl.uddl.PlatformEntity;
import com.epistimis.uddl.uddl.PlatformParticipant;
import com.epistimis.uddl.uddl.PlatformQuery;
import com.epistimis.uddl.uddl.PlatformQueryComposition;
import com.epistimis.uddl.uddl.PlatformView;
import com.epistimis.uddl.uddl.UddlPackage;

public class PlatformQueryProcessor extends
		QueryProcessor<PlatformComposableElement, PlatformCharacteristic, PlatformEntity, PlatformAssociation, PlatformComposition, PlatformParticipant, 
		PlatformView, PlatformQuery, PlatformCompositeQuery, PlatformQueryComposition, PlatformDataType, PlatformDataModel,
		PlatformEntityProcessor> {

	protected static Map<String,Map<String, QuerySelectedComposition<PlatformCharacteristic>>> cardinalityCache = new HashMap<>();

	protected EList<PlatformQueryComposition> getComposition(PlatformCompositeQuery ent) {
		return ent.getComposition();
	}

	protected PlatformView getType(PlatformQueryComposition obj) {
		return obj.getType();
	}

	protected boolean isCompositeQuery(PlatformView obj) {
		return (obj instanceof PlatformCompositeQuery);
	}

	protected EClass getRelatedPackageEntityInstance(PlatformQuery obj) {
		return UddlPackage.eINSTANCE.getPlatformEntity();
	}

	protected String getCharacteristicRolename(PlatformCharacteristic obj) { return obj.getRolename(); }

	@Override
	public PlatformComposableElement getCharacteristicType(PlatformCharacteristic obj) {
		// TODO Auto-generated method stub
		if (obj instanceof PlatformComposition) {
			return ((PlatformComposition)obj).getType();
		};
		if (obj instanceof PlatformParticipant) {
			return ((PlatformParticipant)obj).getType();
		}
		
		String msg = MessageFormat.format(WRONG_TYPE_FMT, qnp.getFullyQualifiedName(obj).toString(), "PlatformComposition or PlatformParticipant",
				obj.getClass().toString());
		throw new WrongTypeException(msg);

	}

	@Override
	protected int getCharacteristicLowerBound(PlatformCharacteristic obj) {
		// TODO Auto-generated method stub
		return obj.getLowerBound();
	}

	@Override
	protected int getCharacteristicUpperBound(PlatformCharacteristic obj) {
		// TODO Auto-generated method stub
		return obj.getUpperBound();
	}

	@Override
	public void updateCardinalityCache(PlatformQuery q, Map<String, QuerySelectedComposition<PlatformCharacteristic>> map) {
		Map<String, QuerySelectedComposition<PlatformCharacteristic>> foundMap = getCardinalties(q);
		assert(foundMap == null); // We should not be updating existing content
		String key = qnp.getFullyQualifiedName(q).toString();
		cardinalityCache.put(key, map);		
	}

	@Override
	public Map<String, QuerySelectedComposition<PlatformCharacteristic>> getCardinalties(PlatformQuery q) {
		String key = qnp.getFullyQualifiedName(q).toString();
		Map<String, QuerySelectedComposition<PlatformCharacteristic>> foundMap = cardinalityCache.get(key);
		return foundMap;
	}

	@Override
	public void flushCardinalityCache() {
		cardinalityCache.clear();
		
	}

	@Override
	public Map<String, PlatformCharacteristic> selectCharacteristicsFromCache(PlatformQuery q) {
		Map<String, QuerySelectedComposition<PlatformCharacteristic>> foundMap = getCardinalties(q);
		
		Map<String, PlatformCharacteristic> result = new LinkedHashMap<String, PlatformCharacteristic>();
		for (Map.Entry<String, QuerySelectedComposition<PlatformCharacteristic>> entry: foundMap.entrySet()) {
			result.put(entry.getKey(), entry.getValue().referencedCharacteristic);
		}
		return result;
	}

}
