/**
 * 
 */
package com.epistimis.uddl.ui.tests;

import org.eclipse.xtext.testing.InjectWith;
import org.eclipse.xtext.testing.extensions.InjectionExtension;
import org.eclipse.xtext.testing.util.ParseHelper;
import org.eclipse.xtext.ui.testing.AbstractHoverTest;
//import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import com.epistimis.uddl.uddl.ConceptualDataModel;
import com.epistimis.uddl.uddl.DataModel;
//import com.epistimis.uddl.uddl.LogicalDataModel;
//import com.epistimis.uddl.uddl.PlatformDataModel;
import com.epistimis.uddl.uddl.UddlElement;
//import com.epistimis.uddl.ui.hover.UddlKeywordHoverText;
import com.google.inject.Inject;

/**
 * @author HA PHUONG
 *
 */
@ExtendWith(InjectionExtension.class)
@InjectWith(UddlUiInjectorProvider.class)
public class UddlHoverTest extends AbstractHoverTest {
	@Inject
	ParseHelper<DataModel> parseHelper;

	String dataModel = """
			dm TestNameDm  "Test description for DataModel" {
				cdm TestNameCdm "Test description for ConceptualDataModel" {

				}
				ldm TestNameLdm "Test description for LogicalDataModel" {

				}
			    pdm TestNamePdm "Test description for PlatformDataModel" {

			    }
			}
			""";

//	@Test
//	public void hoverOverDataModel() throws Exception {
//		DataModel parseDm = parseHelper.parse(dataModel);
//		assertHoverOverKeyword(dataModel, "dm", UddlKeywordHoverText.dmKeywordHover);
//		assertHoverOverUddlElementName(dataModel, parseDm);
//	}
//
//	@Test
//	public void hoverOverConceptualDataModel() throws Exception {
//		ConceptualDataModel parseCdm = parseHelper.parse(dataModel).getCdm().get(0);
//		assertHoverOverKeyword(dataModel, "cdm", UddlKeywordHoverText.cdmKeywordHover);
//		assertHoverOverUddlElementName(dataModel, parseCdm);
//	}
//
//	@Test
//	public void hoverOverLogicDataModel() throws Exception {
//		LogicalDataModel parseLdm = parseHelper.parse(dataModel).getLdm().get(0);
//		assertHoverOverKeyword(dataModel, "ldm", UddlKeywordHoverText.ldmKeywordHover);
//		assertHoverOverUddlElementName(dataModel, parseLdm);
//	}
//
//	@Test
//	public void hoverOverPlatformDataModel() throws Exception {
//		PlatformDataModel parseLdm = parseHelper.parse(dataModel).getPdm().get(0);
//		assertHoverOverKeyword(dataModel, "pdm", UddlKeywordHoverText.pdmKeywordHover);
//		assertHoverOverUddlElementName(dataModel, parseLdm);
//	}

	/**
	 * Assert hover over a UddlElement name
	 */
	public void assertHoverOverUddlElementName(String dataModel, UddlElement element) throws Exception {
		hasHoverOver(dataModel, element.getName(), element.getName());
		hasHoverOver(dataModel, element.getName(), element.getDescription());
	}

	/**
	 * Assert hover over keyword
	 */
	public void assertHoverOverKeyword(String dataModel, String keyword, String expectedHoverText) throws Exception {
		hasHoverOver(dataModel, keyword, expectedHoverText);
	}

}
