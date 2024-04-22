/**
 * 
 */
package com.epistimis.uddl.validation;

import java.lang.invoke.MethodHandles;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.jdt.annotation.NonNull;

/**
 * Tools to load files dynamically
 */
public class FileLoadingSupport {
	private static Logger logger = Logger.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Get the URI to file from the specified plugin
	 * 
	 * @param localFileName The filename (relative to/ inside of) the specified
	 *                      plugin
	 * @param pluginId      The ID of the plugin containing the file
	 * @return URI to the desired file
	 * 
	 * Copied from org.eclipse.ocl.examples.pivot.tests.PivotTestCase.java:
	 * getModelURI See
	 * https://eclipse.googlesource.com/ocl/org.eclipse.ocl/+/refs/heads/master/tests/org.eclipse.ocl.examples.xtext.tests/src/org/eclipse/ocl/examples/pivot/tests/PivotTestCase.java
	 * and
	 * https://eclipse.googlesource.com/ocl/org.eclipse.ocl/+/refs/heads/master/tests/org.eclipse.ocl.examples.xtext.tests/src/org/eclipse/ocl/examples/test/xtext/PivotDocumentationExamples.java
	 * and
	 * https://eclipse.googlesource.com/ocl/org.eclipse.ocl/+/refs/heads/master/tests/org.eclipse.ocl.examples.xtext.tests/models/documentation/parsingDocumentsExample.ocl?autodive=0%2F%2F
	 * 
	 * @param localFileName - relative to the plugin root directory (not the Maven
	 *                      parent directory) - see examples
	 * @return a properly constructed URI
	 */
	public static @NonNull URI getInputURI(@NonNull String localFileName, @NonNull String pluginId) {
		String plugInPrefix = pluginId + "/";
		URI plugURI = EcorePlugin.IS_ECLIPSE_RUNNING ? URI.createPlatformPluginURI(plugInPrefix, true)
				: URI.createPlatformResourceURI(plugInPrefix, true);
		URI localURI = URI.createURI(localFileName.startsWith("/") ? localFileName.substring(1) : localFileName);
		URI result = localURI.resolve(plugURI);
		logger.debug(result.toPlatformString(false));
		return result;
		// NOTE: See alternative implementation at
		// https://stackoverflow.com/questions/40086759/how-to-access-the-member-files-and-folders-of-a-plug-in-in-eclipse

	}

}
