/**
 * 
 */
package com.epistimis.uddl.ui;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;

/**
 * 
 */
public class UITools {

	// See https://stackoverflow.com/questions/9348767/how-to-get-active-editor-in-eclipse-plugin
	public static IEditorPart getActiveEditor() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();		
	}
	
	// See https://stackoverflow.com/questions/9348767/how-to-get-active-editor-in-eclipse-plugin
	public static IEditorReference[] allOpenEditors() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();		
	}
}
