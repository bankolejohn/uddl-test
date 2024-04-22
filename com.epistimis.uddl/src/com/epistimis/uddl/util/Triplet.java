/**
 * 
 */
package com.epistimis.uddl.util;



public class Triplet<T, U, V> {

	private final T first;
	private final U second;
	private final V third;

	public Triplet(final T firstElement, final U secondElement, final V thirdElement) {
		this.first = firstElement;
		this.second = secondElement;
		this.third = thirdElement;
	}

	public T getFirst() {
		return first;
	}

	public U getSecond() {
		return second;
	}

	public V getThird() {
		return third;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (this == other)
			return true;
		if (this.getClass().equals(other.getClass())) {
			Triplet<?, ?, ?> otherTriplet = (Triplet<?, ?,?>) other;
			boolean isEqual = (first == null) ? otherTriplet.getFirst() == null : first.equals(otherTriplet.getFirst());

			if (!isEqual)
				return false;

			isEqual = (second == null) ? otherTriplet.getSecond() == null : second.equals(otherTriplet.getSecond());

			if (!isEqual)
				return false;

			return (third == null) ? otherTriplet.getThird() == null : third.equals(otherTriplet.getThird());
		
		}
		return false;
	}

	@Override
	public int hashCode() {
		return first == null ? 0 : first.hashCode() + 17 * (second == null ? 0 : second.hashCode()) + 347 * (third == null ? 0 : third.hashCode());
	}

	@Override
	public String toString() {
		return "Triplet(" + first + ", " + second + ", " + third + ")";
	}

}
