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

import java.io.File;
import java.text.MessageFormat;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.util.Log;
import ch.marlovits.global_inbox.Activator;
import ch.marlovits.global_inbox.InboxContentProvider;
import ch.marlovits.global_inbox.Messages;
import ch.marlovits.global_inbox.Preferences;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements IStartup {
	
	// The plug-in ID
	public static final String PLUGIN_ID = "ch.elexis.global_inbox"; //$NON-NLS-1$
	
	// The shared instance
	private static Activator plugin;
	private InboxContentProvider contentProvider = new InboxContentProvider();
	private File inboxDir;
	public Log log = Log.get("GlobalInbox");
	
	/**
	 * The constructor
	 */
	public Activator(){}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext )
	 */
	public void start(BundleContext context) throws Exception{
		super.start(context);
		plugin = this;
		String filepath = CoreHub.localCfg.get(Preferences.PREF_DIR, null);
		if (filepath == null) {
			log.log("Es ist in den Einstellungen kein Eingangsverzeichnis definiert", Log.ERRORS);
			
		} else {
			inboxDir = new File(filepath);
			if (!inboxDir.exists()) {
				if (!inboxDir.mkdirs()) {
					log.log(
						MessageFormat.format("could not create inbox directory {0}.", inboxDir),
						Log.ERRORS);
					inboxDir = null;
				}
			}
		}
	}
	
	public File getInboxDir(){
		return inboxDir;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext )
	 */
	public void stop(BundleContext context) throws Exception{
		plugin = null;
		super.stop(context);
	}
	
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault(){
		return plugin;
	}
	
	public InboxContentProvider getContentProvider(){
		return contentProvider;
	}
	
	@Override
	public void earlyStartup(){}
	
	public String getCategory(File file){
		String dir = CoreHub.localCfg.get(Preferences.PREF_DIR, ""); //$NON-NLS-1$
		File parent = file.getParentFile();
		if (parent == null) {
			return Messages.Activator_noInbox;
		} else {
			String fname = parent.getAbsolutePath();
			if (fname.startsWith(dir)) {
				if (fname.length() > dir.length()) {
					return fname.substring(dir.length() + 1);
				} else {
					return "-"; //$NON-NLS-1$
				}
				
			} else {
				return "??"; //$NON-NLS-1$
			}
		}
		
	}
}
