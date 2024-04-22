/**
 * 
 */
package com.epistimis.uddl.ui.contentassist;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.naming.IQualifiedNameConverter;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.ui.editor.contentassist.ConfigurableCompletionProposal;
import org.eclipse.xtext.ui.editor.contentassist.ReplacementTextApplier;

/**
 * Cloned from org.eclipse.xtext.common.types.xtext.ui.JdtTypesProposalProvider.FQNShortener
 */

public class FQNShortener extends ReplacementTextApplier {
	protected final IScope scope;
	protected final Resource context;
	protected final IQualifiedNameConverter qualifiedNameConverter;
	protected final IValueConverter<String> valueConverter;
	
	public FQNShortener(Resource context, IScope scope, IQualifiedNameConverter qualifiedNameConverter, IValueConverter<String> valueConverter) {
		this.context = context;
		this.scope = scope;
		this.qualifiedNameConverter = qualifiedNameConverter;
		this.valueConverter = valueConverter;
	}
	
	protected String applyValueConverter(QualifiedName qualifiedName) {
		String result = qualifiedNameConverter.toString(qualifiedName);
		if (valueConverter != null)
			result = valueConverter.toString(result);
		return result;
	}
	
	@Override
	public String getActualReplacementString(ConfigurableCompletionProposal proposal) {
		String replacementString = proposal.getReplacementString();
		if (scope != null) {
			String qualifiedNameAsString = replacementString;
			if (valueConverter != null) {
				qualifiedNameAsString = valueConverter.toValue(qualifiedNameAsString, null);
			}
			IEObjectDescription element = scope.getSingleElement(qualifiedNameConverter.toQualifiedName(qualifiedNameAsString));
			if (element != null) {
				EObject resolved = EcoreUtil.resolve(element.getEObjectOrProxy(), context);
				if (!resolved.eIsProxy()) {
					IEObjectDescription shortendElement = scope.getSingleElement(resolved);
					if (shortendElement != null)
						replacementString = applyValueConverter(shortendElement.getName());
				}
			}
		}
		return replacementString;
	}
}