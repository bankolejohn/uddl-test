/**
 * 
 */
package com.epistimis.uddl.ui.contentassist;

import java.util.ArrayList;
import java.util.List;

//import org.eclipse.emf.core.Resource;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.impl.QualifiedNameValueConverter;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ContentAssistContext;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;

import com.epistimis.uddl.UddlQNP;
import com.epistimis.uddl.util.IndexUtilities;
import com.google.inject.Inject;

/**
 * 
 */
public class PropUtils {

	@Inject
	IndexUtilities ndxUtils;

	@Inject
	UddlQNP qnp;

	@Inject
	private IScopeProvider scopeProvider;

	@Inject
	private IEObjectHover hover;

	final static String INDENT = "\t";

	@Inject
	private IQualifiedNameConverter qualifiedNameConverter;

	public IQualifiedNameConverter getQualifiedNameConverter() {
		return qualifiedNameConverter;
	}

	public QualifiedNameValueConverter getQualifiedNameValueConverter() {
		return qnp.getQualifiedNameValueConverter();
	}

	public static String indent(int cnt) {
		StringBuilder ndentBldr = new StringBuilder();
		for (int i = 0; i < cnt; i++) {
			ndentBldr.append(INDENT);
		}
		return ndentBldr.toString();
	}

	/**
	 * Generic list of candidates for a reference. Some cases can limit this based
	 * on some criteria.
	 * 
	 * TODO: Should I just use IndexUtilities.getVisibleEObjectDescriptions() ?
	 * @param model     The instance where the reference is
	 * @param reference The reference needing candidates
	 * @return
	 */
	Iterable<IEObjectDescription> getCandidateDescriptions(EObject model, EReference reference) {
		return scopeProvider.getScope(model, reference).getAllElements();
	}

	/**
	 * TODO: Should I just use IndexUtilities.getVisibleEObjects() ?
	 * @param model
	 * @param reference
	 * @return
	 */
	List<EObject> getCandidates(EObject model, EReference reference) {
		Iterable<IEObjectDescription> descriptions = getCandidateDescriptions(model, reference);
		List<EObject> result = new ArrayList<EObject>();
		for (IEObjectDescription description : descriptions) {
			result.add(IndexUtilities.objectFromDescription(model.eResource(),description));
		}
		return result;
	}


	/**
	 * Replace proposal insertion text with a minimized RQN if possible. Cloned from
	 * https://www.eclipse.org/forums/index.php/t/583114/
	 * 
	 * @param context
	 * @param typeScope
	 * @param qualifiedNameConverter
	 * @param valueConverter
	 * @return
	 */
	public ConfigurableCompletionProposal.IReplacementTextApplier createTextApplier(Resource resource,
			IScope typeScope, IQualifiedNameConverter qualifiedNameConverter, IValueConverter<String> valueConverter) {
		return new FQNShortener(resource, typeScope, qualifiedNameConverter, valueConverter);
	}

	/**
	 * Modify the proposal as needed - cloned from
	 * https://www.eclipse.org/forums/index.php/t/583114/
	 * 
	 * TODO:  It doesn't 'grey' out the FQNs of unselected alternatives. 
	 * 
	 * @param theProposal
	 * @param context
	 * @param ref
	 * @return
	 */
	public ICompletionProposal modifyConfigurableCompletionProposal(ICompletionProposal theProposal,
			ContentAssistContext context, EReference ref, String additionalInfo) {
		IScope typeScope = null;
		if (context.getCurrentModel() != null) {
			typeScope = scopeProvider.getScope(context.getCurrentModel(), ref);
		}
		if (theProposal != null && theProposal instanceof ConfigurableCompletionProposal) {
			ConfigurableCompletionProposal configurableCompletionProposal = (ConfigurableCompletionProposal) theProposal;
			// Use 'new Provider<EObject>() {}' if EObject should be found in the first
			// place
			configurableCompletionProposal.setAdditionalProposalInfo(additionalInfo);
			configurableCompletionProposal.setHover(hover);
			configurableCompletionProposal.setTextApplier(createTextApplier(context.getResource(), typeScope,
					getQualifiedNameConverter(), getQualifiedNameValueConverter()));
		}
		return theProposal;
	}


}
