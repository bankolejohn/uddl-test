package com.epistimis.uddl.scoping;

import java.util.Iterator;
import java.util.LinkedHashSet;
//import java.util.function.Consumer;
import java.util.function.Consumer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.resource.IResourceDescription;
import org.eclipse.xtext.scoping.impl.ImportUriGlobalScopeProvider;
import org.eclipse.xtext.util.IResourceScopeCache;

import com.epistimis.uddl.uddl.UddlPackage;
import com.google.common.base.Splitter;
import com.google.inject.Inject;
import com.google.inject.Provider;

// NOTE: Content generated from XTend based on https://blogs.itemis.com/en/in-five-minutes-to-transitive-imports-within-a-dsl-with-xtext
// And then cleaned up a bit to make it more readabable

public class UddlGlobalScopeProvider extends ImportUriGlobalScopeProvider {

	  private static final Splitter SPLITTER = Splitter.on(",");

	  @Inject
	  private IResourceDescription.Manager descriptionManager;

	  @Inject
	  private IResourceScopeCache cache;

	  @Override
	  protected LinkedHashSet<URI> getImportedUris(final Resource resource) {
		    return cache.<LinkedHashSet<URI>>get(UddlGlobalScopeProvider.class.getSimpleName(), resource, new Provider<LinkedHashSet<URI>>() {
			      @Override
			      public LinkedHashSet<URI> get() {
			        LinkedHashSet<URI> _linkedHashSet = new LinkedHashSet<URI>(5);
			        final LinkedHashSet<URI> uniqueImportURIs = this.collectImportUris(resource, _linkedHashSet);
			        final Iterator<URI> uriIter = uniqueImportURIs.iterator();
			        while (uriIter.hasNext()) {
			          if (!EcoreUtil2.isValidUri(resource, uriIter.next())) {
			            uriIter.remove();
			          }
			        }
			        return uniqueImportURIs;
			      }

			      public LinkedHashSet<URI> collectImportUris(final Resource resource, final LinkedHashSet<URI> uniqueImportURIs) {
			        final IResourceDescription resourceDescription = UddlGlobalScopeProvider.this.descriptionManager.getResourceDescription(resource);
			        final Iterable<IEObjectDescription> models = resourceDescription.getExportedObjectsByType(UddlPackage.Literals.MODEL_FILE);
			        final Consumer<IEObjectDescription> _function = (IEObjectDescription it) -> {
			          final String userData = it.getUserData(UddlResourceDescriptionStrategy.INCLUDES);
			          if ((userData != null)) {
			            final Consumer<String> _function_1 = (String uri) -> {
			              URI includedUri = URI.createURI(uri);
			              includedUri = includedUri.resolve(resource.getURI());
			              if (uniqueImportURIs.add(includedUri)) {
			                this.collectImportUris(resource.getResourceSet().getResource(includedUri, true), uniqueImportURIs);
			              }
			            };
			            UddlGlobalScopeProvider.SPLITTER.split(userData).forEach(_function_1);
			          }
			        };
			        models.forEach(_function);
			        return uniqueImportURIs;
			      }
	    	
		    });
		    		
		  }
}
