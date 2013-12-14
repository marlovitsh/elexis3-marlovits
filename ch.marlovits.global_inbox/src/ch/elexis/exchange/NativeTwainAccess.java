package ch.elexis.exchange;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;

import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

public class NativeTwainAccess {
	/**
	 * Name of the C/C++ JNI DLL which communicates with the C TWAIN DLL. The file name is case
	 * sensative and must NOT contain the .DLL extension
	 */
	protected final String DLL_NAME = "EZTW32";
	
	/**
	 * Staic instance used by singleton
	 */
	private static final NativeTwainAccess nativeTwainAccess = new NativeTwainAccess();
	
	/**
	 * Creates a new instance of NativeTwainAccess Once the source manager has been loaded, it is
	 * not normally unloaded until this object is destroyed. This makes normal TWAIN operations
	 * quite a bit faster. Since the Java appliation will not release the JNI DLL the TWAIN DLL is
	 * not released until the end of the JMM's lifetime even though the scanning is complete Calling
	 * TwainAvailable the first time loads the source manager.
	 */
	private NativeTwainAccess(){
		initLib();
	}
	
	/**
	 * Loads the C/JNI Libray
	 * 
	 */
	private void initLib(){
		try {
			URL url =
				FileLocator.toFileURL(new URL(FileTool.getClassPath(NativeTwainAccess.class)));
			File base = new File(url.getPath());
			File libdir = base.getParentFile();
			do {
				String[] files = libdir.list();
				if (StringTool.getIndex(files, "lib") != -1) {
					libdir = new File(libdir, "lib/" + DLL_NAME);
					break;
				}
				libdir = libdir.getParentFile();
			} while (libdir != null);
			Runtime.getRuntime().load(libdir.getAbsolutePath() + ".dll");
		} catch (Exception ex) {
			ExHandler.handle(ex);
		} finally {
			// Send to your logging subsystem
			System.out.println("Loading : " + DLL_NAME + ".dll");
		}
	}
	
	/**
	 * Singleton interface point Using the single pattern in order to prevent unessary DLL
	 * initialization calls. As only one device typically is active at a time this design pattern is
	 * appropriate.
	 * 
	 * @return Static instance of class
	 */
	public static NativeTwainAccess getInstance(){
		return nativeTwainAccess;
	}
	
	/**
	 * <CODE>true</CODE> if the TWAIN Datasource Manager is available and can be loaded. Does not
	 * actually load the devices's datasources. But normally, the presence of the DSM means that at
	 * least one datasource has been installed. IsAvailable is fast after the first call It can be
	 * used to enable or disable menu items for example.
	 * 
	 * @return Determines if the underlying WIN32 OS has a TWAIN32 implementation installed. Either
	 *         a devive or the TWAIN Groups Sample SDK must be installed for this method to retuen
	 *         <CODE>true</CODE>
	 */
	public native boolean isTwainAvailble();
	
	/**
	 * Returns the names and descriptions of all the available TAWAIN devices. These names are
	 * vendor specific and perhaps not localized. The names are not guaranteed to be unique, and
	 * sometimes the names are not very descriptive in some cases.
	 * 
	 * @return String array of all the device names found in the OS.
	 */
	public native String[] getAvailableSources();
	
	/**
	 * Opens and enables the default source. The string is not empty if successful, empty if
	 * something goes wrong. If successful the filename is returned. Note if acquire is called again
	 * the previous image is lost forever. Clients are encouraged to rename the file or otherwise
	 * make a local copy if the actual image must persist.
	 * 
	 * @return The fully qualified path and filename for the BMP file created
	 */
	public native String acquire();
	
	/**
	 * Opens and enables the named source. The string is not empty if successful, empty if something
	 * goes wrong. If successful the filename is returned. Note if acquire is called again the
	 * previous image is lost forever. Clients are encouraged to rename the file or otherwise make a
	 * local copy if the actual image must persist.
	 * 
	 * @return The fully qualified path and filename for the BMP file created
	 * @param sourceName
	 *            The name of the source. Preferably this name is a value derived from the
	 *            <CODE>getAvailbleSources</CODE> method
	 */
	public native String acquire(String sourceName);
	
	/**
	 * Method that allows to hide/show the Twain Source UI. Useful to perform "Direct Scan"
	 * 
	 * @param showSource
	 *            The showSource parameter must be setted to <code>true</code> if the source UI
	 *            will be displayed, <code>false</code> otherwise
	 */
	public native void showSourceUI(boolean showSource);
	
	/**
	 * Method that allows to get the latest configuration. <br>
	 * The configItems String must be in the form:<br>
	 * <br>
	 * <code>cap&ICAP&ICAP_TYPE&VALUE|cap&ANOTHER_ICAP&ANOTHER_ICAP_TYPE&ANOTHER_VAL</code>.<br>
	 * <br>
	 * For the special case of <b>ICAP_FRAME</b>, the VALUE must be separated by ';' in the order:
	 * left, right, top, bottom. Examples:<br>
	 * <br>
	 * For <b>ICAP_BITDEPTH</b> corresponding to int number: 4395 and Type <b>TWTY_UINT16</b>
	 * corresponding to int number 4, <b>ICAP_PIXELTYPE</b> to 257 (<b>TWTY_UINT16</b> = 4) and
	 * <b>ICAP_FRAMES</b> to 4372 (<b>TWTY_FRAME</b> = 8), the configurationString String must
	 * be:<br>
	 * <br>
	 * <code>cap&257&4&2|cap&4395&4&24|cap&4372&8&0.0;8.5;0.0;11.0</code><br>
	 * <br>
	 * For Image Layout configuration, the String must complies the same contitions that ICAP_FRAME,
	 * so the form is:<br>
	 * <br>
	 * <code>imgLayout&x&x&8&0.0;8.5;0.0;11.0</code> <br>
	 * <br>
	 * <b>IMPORTANT NOTE: </b> The values sent in the configItems are needed, at least with dummy
	 * values. This is to keep the same structure with the configuration String sent in the
	 * setTwainCapabilities method
	 * 
	 * @param configItems
	 * @return The same configItems String sent, but the values are updated with the ones used by
	 *         the twain source
	 */
	public native String getLatestConfiguration(String configItems);
	
	/**
	 * Method that allows to set the configuration Parameters. <br>
	 * <br>
	 * The configurationString String must be in the form:<br>
	 * <br>
	 * <code>cap&ICAP&ICAP_TYPE&VALUE|cap&ANOTHER_ICAP&ANOTHER_ICAP_TYPE&ANOTHER_VAL</code>.<br>
	 * <br>
	 * For the special case of <b>ICAP_FRAME</b>, the VALUE must be separated by ';' in the order:
	 * left, right, top, bottom. Examples:<br>
	 * <br>
	 * For <b>ICAP_BITDEPTH</b> corresponding to int number: 4395 and Type <b>TWTY_UINT16</b>
	 * corresponding to int number 4, <b>ICAP_PIXELTYPE</b> to 257 (<b>TWTY_UINT16</b> = 4) and
	 * <b>ICAP_FRAMES</b> to 4372 (<b>TWTY_FRAME</b> = 8), the configurationString String must
	 * be:<br>
	 * <br>
	 * <code>cap&257&4&2|cap&4395&4&24|cap&4372&8&0.0;8.5;0.0;11.0</code><br>
	 * <br>
	 * For Image Layout configuration, the String must complies the same contitions that ICAP_FRAME,
	 * so the form is:<br>
	 * <br>
	 * <code>imgLayout&x&x&8&0.0;8.5;0.0;11.0</code> <br>
	 * <br>
	 * This method could be invoked in a most intutive form using the <b>getConfigurationString</b>
	 * method in TwainAccess class
	 * 
	 * @param configurationString
	 *            A String with the desired configuration
	 */
	public native void setTwainCapabilities(String configurationString);
	
}
