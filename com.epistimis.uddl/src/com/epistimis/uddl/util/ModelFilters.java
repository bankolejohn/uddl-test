package com.epistimis.uddl.util;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.naming.QualifiedName;

import com.epistimis.uddl.exceptions.WrongTypeException;

//import org.eclipse.emf.common.util.EList;

import com.epistimis.uddl.uddl.ConceptualBasisEntity;
import com.epistimis.uddl.uddl.ConceptualEntity;
import com.epistimis.uddl.uddl.LogicalAbstractMeasurement;
import com.epistimis.uddl.uddl.LogicalAbstractMeasurementSystem;
import com.epistimis.uddl.uddl.LogicalEntity;
import com.epistimis.uddl.uddl.LogicalLandmark;
import com.epistimis.uddl.uddl.LogicalMeasurement;
import com.epistimis.uddl.uddl.LogicalMeasurementAxis;
import com.epistimis.uddl.uddl.LogicalMeasurementSystem;
import com.epistimis.uddl.uddl.LogicalReferencePoint;
import com.epistimis.uddl.uddl.LogicalValueTypeUnit;
import com.epistimis.uddl.uddl.PlatformDataType;
import com.epistimis.uddl.uddl.PlatformEntity;
import com.google.inject.Inject;

/**
 * Containts methods used to filter model elements that are generally useful - and specific to UDDL.
 * Generalized methods not specific to UDDL can be found in IndexUtilities and NavigationUtilities.
 * 
 * @author stevehickman
 *
 */
public class ModelFilters {

	@Inject IQualifiedNameProvider qnp;
	@Inject IQualifiedNameConverter qnc;

	/**
	 * Determine if the specified ConceptualEntity has a reference to the
	 * BasisEntity.
	 * 
	 * TODO: Do we need to have a qualified name that includes Domain? Domains don't
	 * contain BasisEntities, so it is technically possible for the same BasisEntity
	 * to be in multiple Domains. For now, ignore Domain.
	 * 
	 * @param ce the ConceptualEntity to check
	 * @param be the BasisEntity to check for
	 * @return
	 */
	public static boolean hasBasisInAncestry(ConceptualEntity ce, ConceptualBasisEntity be) {
		if (ce.getBasisEntity().contains(be))
			return true;
		if (ce.getSpecializes() != null) {
			return hasBasisInAncestry(ce.getSpecializes(), be);
		}
		// If we get here, it doesn't have this basis.
		return false;
	}

	/**
	 * Determine if the specified ConceptualEntity has a reference to the
	 * BasisEntity.
	 * 
	 * @param ce        the ConceptualEntity to check
	 * @param basisName the name of the BasisEntity to check for
	 * @return
	 */
	public  boolean hasBasisInAncestry(ConceptualEntity ce, String basisName) {
		QualifiedName qn = qnc.toQualifiedName(basisName);

		return hasBasisInAncestry(ce, qn);
	}

	/**
	 * Check to see if this ConceptualEntity references the specified BasisEntity
	 * 
	 * @param ce
	 * @param basisName
	 * @return
	 */
	public  boolean hasBasisInAncestry(ConceptualEntity ce, QualifiedName basisName) {
		// Find all visible BasisEntities with this QN - pick the best match
		// When matching names, we may have a RQN, not a FQN - so compare segment counts
		// and skip the difference
		int basisCount = basisName.getSegmentCount();
		for (ConceptualBasisEntity be : ce.getBasisEntity()) {
			QualifiedName beQN = qnp.getFullyQualifiedName(be);
			int beCnt = beQN.getSegmentCount();
			int skipCount = Integer.max(beCnt - basisCount, 0);
			if (beQN.skipFirst(skipCount).equals(basisName)) {
				return true;
			}
		}
//		if (ce.getBasisEntity().stream().anyMatch(be -> be.getName().equalsIgnoreCase(basisName)))
//		return true;

		// If we get here, no match yet - walk up the specialization hierarchy
		if (ce.getSpecializes() != null) {
			return hasBasisInAncestry(ce.getSpecializes(), basisName);
		}
		// If we get here, it doesn't have this basis.
		return false;
	}

	public  boolean hasBasisInAncestry(LogicalEntity le, String basisName) {

		QualifiedName qn = qnc.toQualifiedName(basisName);
		return hasBasisInAncestry(le.getRealizes(), qn);
	}

	public  boolean hasBasisInAncestry(LogicalEntity le, ConceptualBasisEntity be) {
		return hasBasisInAncestry(le.getRealizes(), be);
	}

	public  boolean hasBasisInAncestry(PlatformEntity pe, String basisName) {

		QualifiedName qn = qnc.toQualifiedName(basisName);
		return hasBasisInAncestry(pe.getRealizes().getRealizes(), qn);
	}

	public  boolean hasBasisInAncestry(PlatformEntity le, ConceptualBasisEntity be) {
		return hasBasisInAncestry(le.getRealizes(), be);
	}

	/**
	 * Does this Measurement realize this ConceptualObservable?
	 * 
	 * @param lm             The measurement in question
	 * @param observableName The observable that should be associated with this
	 *                       measurement
	 * @return true if the Measurement is associated with this Observable
	 */
	public static boolean measurementOf(LogicalMeasurement lm, String observableName) {
		return lm.getRealizes().getName().equalsIgnoreCase(observableName);
	}

	/**
	 * Get the VTU associated with this PDT realization, if there is one.
	 * 
	 * @param pdt
	 * @return
	 */
	public static List<LogicalValueTypeUnit> getValueTypeUnitIfThere(@NonNull PlatformDataType pdt) {
		List<LogicalValueTypeUnit> result = new ArrayList<>();
		LogicalAbstractMeasurement absMeasure = pdt.getRealizes();
		if (absMeasure instanceof LogicalMeasurement) {
			EList<LogicalMeasurementAxis> axes = ((LogicalMeasurement)absMeasure).getMeasurementAxis();
			for (LogicalMeasurementAxis axis: axes) {
				result.addAll(axis.getValueTypeUnit());
			}
			return result;
		}
		else if (absMeasure instanceof LogicalMeasurementAxis) {
			return ((LogicalMeasurementAxis)absMeasure).getValueTypeUnit();
		}
		else if (absMeasure instanceof LogicalValueTypeUnit) {
			result.add((LogicalValueTypeUnit)absMeasure);
			return result;
		}
		return result;
	}
	
	public List<LogicalValueTypeUnit> getValueTypeUnit(@NonNull PlatformDataType pdt) {
		LogicalAbstractMeasurement absMeasure = pdt.getRealizes();
		List<LogicalValueTypeUnit> result = getValueTypeUnitIfThere(pdt);
		if (result.isEmpty()) {
			throw new WrongTypeException(MessageFormat.format("{0} has type {0} which cannot be realized by a PlatformDataType", 
					qnp.getFullyQualifiedName(absMeasure).toString(),absMeasure.getClass().getName()));
		}
		else {
			return result;
		}
	}
	/**
	 * Get the landmarks associated with this PDT, if there are any
	 * @param pdt
	 * @return
	 */
	public static List<LogicalLandmark> getLandmarks(@NonNull PlatformDataType pdt) {
		LogicalAbstractMeasurement absMeasure = pdt.getRealizes();
		if (absMeasure instanceof LogicalMeasurement) {
			LogicalMeasurement lm = (LogicalMeasurement) absMeasure;			
			LogicalAbstractMeasurementSystem amsys = lm.getMeasurementSystem();
			if (amsys instanceof LogicalMeasurementSystem) {
				LogicalMeasurementSystem lmsys = (LogicalMeasurementSystem)amsys;
				return lmsys.getReferencePoint().stream().map(LogicalReferencePoint::getLandmark).collect(Collectors.toList());
			}
		}
		return new ArrayList<>();
	}

	/**
	 * Get the reference points associated with this PDT, if there are any
	 * @param pdt
	 * @return
	 */
	public static List<LogicalReferencePoint> getReferencePoints(@NonNull PlatformDataType pdt) {
		LogicalAbstractMeasurement absMeasure = pdt.getRealizes();
		if (absMeasure instanceof LogicalMeasurement) {
			LogicalMeasurement lm = (LogicalMeasurement) absMeasure;			
			LogicalAbstractMeasurementSystem amsys = lm.getMeasurementSystem();
			if (amsys instanceof LogicalMeasurementSystem) {
				LogicalMeasurementSystem lmsys = (LogicalMeasurementSystem)amsys;
				return lmsys.getReferencePoint();
			}
		}
		return new ArrayList<>();
	}

}
