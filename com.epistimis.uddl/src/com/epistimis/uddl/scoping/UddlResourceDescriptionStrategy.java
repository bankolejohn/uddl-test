package com.epistimis.uddl.scoping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.EObjectDescription;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.impl.DefaultResourceDescriptionStrategy;
import org.eclipse.xtext.scoping.impl.ImportUriResolver;
import org.eclipse.xtext.util.IAcceptor;
import org.eclipse.xtext.xbase.lib.IterableExtensions;

import com.epistimis.uddl.uddl.ConceptualPathNode;
import com.epistimis.uddl.uddl.ConceptualQueryComposition;
import com.epistimis.uddl.uddl.LogicalConstraint;
import com.epistimis.uddl.uddl.LogicalPathNode;
import com.epistimis.uddl.uddl.LogicalQueryComposition;
import com.epistimis.uddl.uddl.LogicalReferencePoint;
import com.epistimis.uddl.uddl.LogicalReferencePointPart;
import com.epistimis.uddl.uddl.ModelFile;
import com.epistimis.uddl.uddl.PlatformPathNode;
import com.epistimis.uddl.uddl.PlatformQueryComposition;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class UddlResourceDescriptionStrategy extends DefaultResourceDescriptionStrategy {
	public static final String INCLUDES = "includes";
	@Inject
	ImportUriResolver uriResolver;

	public UddlResourceDescriptionStrategy() {
		// TODO Auto-generated constructor stub
	}

	@Override 
	public boolean createEObjectDescriptions(EObject eObject, IAcceptor<IEObjectDescription> acceptor) {
//		if(eObject instanceof ModelFile) {
//			createEObjectDescriptionForModel((ModelFile)eObject, acceptor);
//			return true;
//		} else 
		if ((eObject instanceof ConceptualPathNode) 
				|| (eObject instanceof LogicalPathNode)
				|| (eObject instanceof PlatformPathNode) 
				|| (eObject instanceof LogicalConstraint)  // ??
				|| (eObject instanceof ConceptualQueryComposition)
				|| (eObject instanceof LogicalQueryComposition)
				|| (eObject instanceof PlatformQueryComposition)
				|| (eObject instanceof LogicalReferencePointPart)
				|| (eObject instanceof LogicalReferencePoint)
				
				)
		{
			// don't index these items for external visibility
			return false;
		} else
			return super.createEObjectDescriptions(eObject, acceptor);
	}
	protected void createEObjectDescriptionForModel(ModelFile model, IAcceptor<IEObjectDescription> acceptor) {
		List<String> uris = new ArrayList<>();
		model.getIncludes().stream().forEach( it -> uris.add(uriResolver.apply(it)));
		Map<String,String> userData = new HashMap<>();
	    userData.put(INCLUDES, IterableExtensions.join(uris, ","));
		acceptor.accept(EObjectDescription.create(QualifiedName.create(model.eResource().getURI().toString()), model, userData));
	}
}
