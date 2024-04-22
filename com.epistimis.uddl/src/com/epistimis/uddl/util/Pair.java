/**
 * 
 */
package com.epistimis.uddl.util;


/**
 * An immutable Pair (because values can only be set in the constructor).
 * @param <T>
 * @param <U>
 */
public class Pair<T, U> {

	protected  T first;

	protected  U second;

	public Pair(final T firstElement, final U secondElement) {
		this.first = firstElement;
		this.second = secondElement;
	}

	public T getFirst() {
		return first;
	}

	public U getSecond() {
		return second;
	}
	

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;
		if (this == other)
			return true;
		if (this.getClass().equals(other.getClass())) {
			Pair<?, ?> otherPair = (Pair<?, ?>) other;
			boolean isEqual = (first == null) ? otherPair.getFirst() == null : first.equals(otherPair.getFirst());

			if (!isEqual)
				return false;

			return (second == null) ? otherPair.getSecond() == null : second.equals(otherPair.getSecond());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return first == null ? 0 : first.hashCode() + 17 * (second == null ? 0 : second.hashCode());
	}

	@Override
	public String toString() {
		return "Pair(" + first + ", " + second + ")";
	}

	/**
	 * A mutable Pair - values can be reset
	 * @param <T>
	 * @param <U>
	 */
	public static class Mutable<T,U> extends Pair<T,U> {

		/**
		 * @param firstElement
		 * @param secondElement
		 */
		public Mutable(T firstElement, U secondElement) {
			super(firstElement, secondElement);
			// TODO Auto-generated constructor stub
		}
		public void setFirst(final T firstElement) { first = firstElement; }
		public void setSecond(final U secondElement) { second = secondElement; }
	
	}
}
