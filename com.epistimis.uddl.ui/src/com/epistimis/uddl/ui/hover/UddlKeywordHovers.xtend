package com.epistimis.uddl.ui.hover

import com.google.inject.Inject
import com.epistimis.uddl.services.UddlGrammarAccess
import org.eclipse.xtext.Keyword

/**
 * From blog post
 * https://blogs.itemis.com/en/xtext-usability-hovers-on-keywords
 * Alternatively, see https://ckulla.wordpress.com/2011/02/06/hover-support-in-xtext-2-0/
 * 
 * Just update this file with keyword hover text. 
 */
class UddlKeywordHovers {
	@Inject UddlGrammarAccess ga;

	def hoverText(Keyword k) {

		val result = switch (k) {
			case ga.dataModelAccess.dmKeyword_0:
				UddlKeywordHoverText.dmKeywordHover
			case ga.conceptualDataModelAccess.cdmKeyword_0:
				UddlKeywordHoverText.cdmKeywordHover
			case ga.logicalDataModelAccess.ldmKeyword_0:
				UddlKeywordHoverText.ldmKeywordHover
			case ga.platformDataModelAccess.pdmKeyword_0:
				UddlKeywordHoverText.pdmKeywordHover
			default: "<need to implement hover for :" + k.toString + " for feature " + k.eContainingFeature.toString + ">"
		}

		result.toString;
	}
}
