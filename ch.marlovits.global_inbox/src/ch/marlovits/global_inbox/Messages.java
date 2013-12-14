/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.marlovits.global_inbox;

import org.eclipse.osgi.util.NLS;

import ch.marlovits.global_inbox.Messages;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.elexis.global_inbox.messages"; //$NON-NLS-1$
	public static String Activator_noInbox;
	public static String InboxContentProvider_noInboxDefined;
	public static String InboxContentProvider_thereIsNoDocumentManagerHere;
	public static String InboxView_assign;
	public static String InboxView_assignThisDocument;
	public static String InboxView_assignxtoy;
	public static String InboxView_category;
	public static String InboxView_couldNotStart;
	public static String InboxView_delete;
	public static String InboxView_error;
	public static String InboxView_inbox;
	public static String InboxView_reallydelete;
	public static String InboxView_reload;
	public static String InboxView_reloadNow;
	public static String InboxView_thisreallydelete;
	public static String InboxView_title;
	public static String InboxView_view;
	public static String InboxView_viewThisDocument;
	public static String Preferences_directory;
	
	public static String DocHandle_73;
	public static String DocHandle_cantReadCaption;
	public static String DocHandle_cantReadText;
	public static String DocHandle_configErrorCaption;
	public static String DocHandle_configErrorText;
	public static String DocHandle_couldNotLoadError;
	public static String DocHandle_fileNameTooLong;
	public static String DocHandle_importError2;
	public static String DocHandle_importErrorText;
	public static String DocHandle_importErrorText2;
	public static String DocHandle_loadErrorText;
	public static String DocHandle_noPatientSelected;
	public static String DocHandle_pleaseSelectPatient;
	public static String DocHandle_readError;
	public static String DocHandle_readErrorCaption;
	public static String DocHandle_readErrorCaption2;
	public static String DocHandle_readErrorCaption3;
	public static String DocHandle_readErrorHeading;
	public static String DocHandle_readErrorText;
	public static String DocHandle_readErrorText2;
	public static String DocHandle_runErrorHeading;
	public static String DocHandle_scannedImageDialogCaption;
	public static String DocHandle_writeErrorCaption;
	public static String DocHandle_writeErrorCaption2;
	public static String DocHandle_writeErrorHeading;
	public static String DocHandle_writeErrorText;
	public static String DocHandle_writeErrorText2;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
	
	private Messages(){}
}
