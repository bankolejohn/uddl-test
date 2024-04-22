package com.epistimis.uddl.unrolled;


//import org.apache.log4j.Logger;
import org.eclipse.jdt.annotation.NonNull;

import com.epistimis.uddl.uddl.UddlElement;
import com.google.common.base.Optional;


public abstract class UnrolledComposableElement<ComposableElement extends UddlElement> {

//	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());


	private String name;

	private String description;
	
	abstract void updateMaps(ComposableElement element);
	
	// NOTE: Package private - factories have public methods to create - so they can force cache updates
	UnrolledComposableElement(@NonNull ComposableElement ce) {
		this.name = ce.getName(); // Always has to have a name, so just do that
		// Set the description - it might not always have a value
		setDescription(ce);
		
		updateMaps(ce);
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}
	
	/**
	 * Set the description - and give it a default empty value (not null)
	 * @param ce
	 */
	public void setDescription(ComposableElement ce) {
		this.description  = Optional.fromNullable(ce.getDescription()).or("").trim();
		
	}

}
