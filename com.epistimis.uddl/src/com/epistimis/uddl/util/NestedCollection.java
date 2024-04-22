package com.epistimis.uddl.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Get a collection contained in an object
 * @param <T> The type of the object containing the collection
 * @param <U> The type of the objects in the collection.
 */
@FunctionalInterface
public interface NestedCollection<T,U> {

	/**
	 * The method that extracts the nested collection
	 * @param t The root containing the collection
	 * @return The collection contained within
	 */
	 Collection<U> apply(T t);
	 
	 /**
	  * If an object has a nested collection that contains elements of its own type, then
	  * it is possible to recurse down through the hierarchy and created a flattened
	  * collection of everything 
	  * @param <T> The input type
	  * @param root The starting point 
	  * @param drill The function that drills down to get the contained collection of
	  * the same type
	  * @return A flat collection (in this case a Set) containing all the contents from
	  * the entire Composite hierarchy.
	  */
	 static <T> Collection<T> flatten(T root, NestedCollection<T,T> drill) {
		 Set<T> result = new HashSet<>();
		 Collection<T> coln = drill.apply(root);
		 for( T obj: coln) {
			 /**
			  * Don't drill down if we've already got this object (and thus all of its
			  * content). This prevents us from getting into recursive loops which could
			  * happen if these collections manage cross references rather than containment.
			  */
			 if (!result.contains(obj)) {
				 Collection<T> subResult = flatten(obj,drill);
				 result.addAll(subResult);				 
			 }
		 }
		 // Lastly, add the element we started with. 
		 // NOTE: For recursive calls, we add the root of each recursive call in 
		 // the recursive call.
		 result.add(root);
		 return result;
	 }
}
