package ch.marlovits.plans.data;

import java.io.File;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import ch.elexis.core.data.Patient;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	// *** The plug-in ID
	public static final String PLUGIN_ID = "ch.marlovits.opplan"; //$NON-NLS-1$
	
	// *** The shared instance
	private static Activator plugin;
	private InboxContentProvider contentProvider = new InboxContentProvider();
	private static File myDir_ = null;
	
	/*
	 * The constructor
	 */
	public Activator(){}
	
	public InboxContentProvider getContentProvider(){
		return contentProvider;
	}
	
	public static Patient getOPPlanPatient(){
		return Patient.load("x5bb881673cc7d9330120");
	}
	
	public static File getOPPlanDir(){
		if (myDir_ == null)
			myDir_ = new File("Z:\\_Daten\\ElexisDaten\\ElexisExterneDokumente\\8422");
		return myDir_;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception{
		super.start(context);
		plugin = this;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
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
	
}
