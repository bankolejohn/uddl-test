/**
 * 
 */
package com.epistimis.uddl;

/**
 * Contains all the info we need on a single attribute from a Query Select. 
 * 
 * Each query creates its own 'Selection' which is just the collection of attributes selected.
 * Each of those attributes has a roleName and an alias (which may just be the roleName), and a cardinality.
 * The alias and cardinality are local to this query only. 
 * The cardinality is based on both initial cardinality and any joins. Joins affect cardinality.
 * Joins can be via participants in Associations or if an Entity is composed into another entity.
 * 
 * The referencedCharacteristic will be contained in an Entity/Association. From that we can determine whatever we need to know.
 * 
 * 
 */
public class QuerySelectedComposition<Characteristic> {
	
	public String roleName;
	public String alias;
	public int    lowerBound;
	public int 	  upperBound;
	public Characteristic referencedCharacteristic;

	public void updateBounds(int lb, int ub) {
		if (lowerBound > 0) {
			lowerBound = lowerBound * lb; // this could make lowerBound zero
		}
		if (upperBound == -1) {
			return; // upperBound is already unlimited - won't get any bigger
		}
		if (ub == -1) {
			upperBound = ub;
			return; // upperBound is now unlimited - won't get any bigger
		}
		// Else, 
		upperBound = upperBound * ub;
	}
}
