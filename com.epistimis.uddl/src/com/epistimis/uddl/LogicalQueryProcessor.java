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
import com.epistimis.uddl.uddl.LogicalAssociation;
import com.epistimis.uddl.uddl.LogicalCharacteristic;
import com.epistimis.uddl.uddl.LogicalComposableElement;
import com.epistimis.uddl.uddl.LogicalCompositeQuery;
import com.epistimis.uddl.uddl.LogicalComposition;
import com.epistimis.uddl.uddl.LogicalDataModel;
import com.epistimis.uddl.uddl.LogicalEntity;
import com.epistimis.uddl.uddl.LogicalMeasurement;
import com.epistimis.uddl.uddl.LogicalParticipant;
import com.epistimis.uddl.uddl.LogicalQuery;
import com.epistimis.uddl.uddl.LogicalQueryComposition;
import com.epistimis.uddl.uddl.LogicalView;
import com.epistimis.uddl.uddl.UddlPackage;

public class LogicalQueryProcessor extends
		QueryProcessor<LogicalComposableElement,LogicalCharacteristic, LogicalEntity, LogicalAssociation, LogicalComposition, LogicalParticipant, 
		LogicalView, LogicalQuery, LogicalCompositeQuery, LogicalQueryComposition,LogicalMeasurement, LogicalDataModel,
		LogicalEntityProcessor> {

	protected static Map<String,Map<String, QuerySelectedComposition<LogicalCharacteristic>>> cardinalityCache = new HashMap<>();

	protected EList<LogicalQueryComposition> getComposition(LogicalCompositeQuery ent) {
		return ent.getComposition();
	}

	protected LogicalView getType(LogicalQueryComposition obj) {
		return obj.getType();
	}

	
	protected boolean isCompositeQuery(LogicalView obj) {
		return (obj instanceof LogicalCompositeQuery);
	}

	protected EClass getRelatedPackageEntityInstance(LogicalQuery obj) {
		return 	UddlPackage.eINSTANCE.getLogicalEntity();
	}


	protected List<LogicalCharacteristic> getCharacteristics(LogicalEntity obj) {
		
		List<LogicalCharacteristic> characteristics = new ArrayList<>();
		for (LogicalComposition pc: obj.getComposition()) {
			characteristics.add(pc);
		}
		if (obj instanceof LogicalAssociation) {
			for (LogicalParticipant pp: ((LogicalAssociation)obj).getParticipant()) {
				characteristics.add(pp);
			}
		}
		return characteristics;
	}
	protected String getCharacteristicRolename(LogicalCharacteristic obj) { return obj.getRolename(); }

	@Override
	public LogicalComposableElement getCharacteristicType(LogicalCharacteristic obj) {
		// TODO Auto-generated method stub
		if (obj instanceof LogicalComposition) {
			return ((LogicalComposition)obj).getType();
		};
		if (obj instanceof LogicalParticipant) {
			return ((LogicalParticipant)obj).getType();
		}
		
		String msg = MessageFormat.format(WRONG_TYPE_FMT, qnp.getFullyQualifiedName(obj).toString(), "LogicalComposition or LogicalParticipant",
				obj.getClass().toString());
		throw new WrongTypeException(msg);

	}

	@Override
	protected int getCharacteristicLowerBound(LogicalCharacteristic obj) {
		// TODO Auto-generated method stub
		return obj.getLowerBound();
	}

	@Override
	protected int getCharacteristicUpperBound(LogicalCharacteristic obj) {
		// TODO Auto-generated method stub
		return obj.getUpperBound();
	}

	@Override
	public void updateCardinalityCache(LogicalQuery q, Map<String, QuerySelectedComposition<LogicalCharacteristic>> map) {
		Map<String, QuerySelectedComposition<LogicalCharacteristic>> foundMap = getCardinalties(q);
		assert(foundMap == null); // We should not be updating existing content
		String key = qnp.getFullyQualifiedName(q).toString();
		cardinalityCache.put(key, map);
		
	}

	@Override
	public Map<String, QuerySelectedComposition<LogicalCharacteristic>> getCardinalties(LogicalQuery q) {
		String key = qnp.getFullyQualifiedName(q).toString();
		Map<String, QuerySelectedComposition<LogicalCharacteristic>> foundMap = cardinalityCache.get(key);
		return foundMap;
	}

	@Override
	public void flushCardinalityCache() {
		cardinalityCache.clear();
		
	}

	@Override
	public Map<String, LogicalCharacteristic> selectCharacteristicsFromCache(LogicalQuery q) {
		Map<String, QuerySelectedComposition<LogicalCharacteristic>> foundMap = getCardinalties(q);
		
		Map<String, LogicalCharacteristic> result = new LinkedHashMap<String, LogicalCharacteristic>();
		for (Map.Entry<String, QuerySelectedComposition<LogicalCharacteristic>> entry: foundMap.entrySet()) {
			result.put(entry.getKey(), entry.getValue().referencedCharacteristic);
		}
		return result;
	}

}
