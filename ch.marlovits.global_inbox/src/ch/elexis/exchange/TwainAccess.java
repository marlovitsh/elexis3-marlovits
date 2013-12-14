package ch.elexis.exchange;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.swt.graphics.ImageData;

import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.exchange.IScannerAccess;

public class TwainAccess implements IScannerAccess, IExecutableExtension {
	public static final String DEFAULTSOURCE = "services/ScannerService/defaultScanner";
	
	public ISource getDefaultSource(){
		String sourcedef = CoreHub.localCfg.get(DEFAULTSOURCE, null);
		if (sourcedef != null) {
			for (ISource src : getSources()) {
				if (src.getName().equalsIgnoreCase(sourcedef)) {
					return src;
				}
			}
		}
		return null;
	}
	
	public void setDefaultSource(ISource src){
		CoreHub.localCfg.set(DEFAULTSOURCE, src.getName());
	}
	
	public ImageData aquire(ISource src) throws Exception{
		
		// System.out.println("Call acquire");
		ImageData imageData = null;
		File toDelete;
		if (NativeTwainAccess.getInstance().isTwainAvailble()) {
			if (src != null) {
				TwainSourceConfiguration sourceConf =
					(TwainSourceConfiguration) src.getConfiguration();
				boolean showUI = sourceConf.isShowSourceUI();
				NativeTwainAccess.getInstance().showSourceUI(showUI);
				
				String configParams = null;
				// Setting configuration params
				sourceConf.setCapability(TwainConstants.ICAP_PIXELTYPE, TwainConstants.TWTY_UINT16,
					TwainConstants.TWPT_RGB + "");
				sourceConf.setCapability(TwainConstants.ICAP_BITDEPTH, TwainConstants.TWTY_UINT16,
					"16");
				sourceConf.setCapability(TwainConstants.ICAP_XRESOLUTION,
					TwainConstants.TWTY_FIX32, "72");
				sourceConf.setCapability(TwainConstants.ICAP_YRESOLUTION,
					TwainConstants.TWTY_FIX32, "72");
				sourceConf.setCapability(TwainConstants.ICAP_AUTOMATICBORDERDETECTION,
					TwainConstants.TWTY_BOOL, "0");
				sourceConf.setCapability(TwainConstants.ICAP_XSCALING, TwainConstants.TWTY_FIX32,
					"100");
				sourceConf.setCapability(TwainConstants.ICAP_YSCALING, TwainConstants.TWTY_FIX32,
					"100");
				sourceConf.setCapability(TwainConstants.ICAP_UNITS, TwainConstants.TWTY_UINT16,
					TwainConstants.TWUN_CENTIMETERS + "");
				sourceConf.setCapability(TwainConstants.ICAP_ORIENTATION,
					TwainConstants.TWTY_UINT16, TwainConstants.TWOR_LANDSCAPE + "");
				sourceConf.setImageLayout(new TwainImageLayout(0, 21, 0, 29, 1, 1, 1));
				
				configParams = getConfigurationString(src.getConfiguration());
				// System.out.println("Sent config:\n" + configParams);
				
				// TODO if you want to send a fixed configuration
				// NativeTwainAccess.getInstance().setTwainCapabilities(configParams);
				
				String test = NativeTwainAccess.getInstance().getLatestConfiguration(configParams);
				// System.out.println(test);
				
				// TODO It is just for printing in a more human way the latest
				// config parameters.
				TwainSourceConfiguration pika = new TwainSourceConfiguration(test);
				
				NativeTwainAccess.getInstance().setTwainCapabilities(test);
				
				String fileName = NativeTwainAccess.getInstance().acquire(src.getName());
				
				if (fileName != null && fileName.length() > 0) {
					imageData = new ImageData(fileName);
					if (imageData != null) {
						toDelete = new File(fileName);
						toDelete.delete();
					}
				} else {
					System.out.println("Severe Error File Did not Acquire");
				}
			}
		}
		return imageData;
	}
	
	/**
	 * Method that allows to get the source configuration as an ordered String separated by
	 * <b>|</b>.<br>
	 * <br>
	 * This method returns the string needed by the <b><code>setTwainCapabilities</code></b> method
	 * of the <b><code>NativeTwainAccess</code></b> class
	 * 
	 * @param configuration
	 *            The configuraiton object (instance of <code>TwainSourceConfiguration</code>
	 * @return The configuratin String
	 */
	private String getConfigurationString(Object configuration){
		StringBuffer sb = new StringBuffer();
		if (configuration instanceof TwainSourceConfiguration) {
			TwainSourceConfiguration conf = (TwainSourceConfiguration) configuration;
			TreeSet<TwainCapability> capsConfig = conf.getConfig();
			
			// Process the capabilities
			for (Iterator iter = capsConfig.iterator(); iter.hasNext();) {
				TwainCapability cap = (TwainCapability) iter.next();
				sb.append("cap&");
				sb.append(cap.getCapability());
				sb.append("&");
				sb.append(cap.getCapabilityType());
				sb.append("&");
				sb.append(cap.getCapabilityValue());
				sb.append("|");
			}
			
			if (conf.getImageLayout() != null) {
				sb.append("imgLayout&x&x&");
				sb.append(conf.getImageLayout().getFrameLeft());
				sb.append(";");
				sb.append(conf.getImageLayout().getFrameRight());
				sb.append(";");
				sb.append(conf.getImageLayout().getFrameTop());
				sb.append(";");
				sb.append(conf.getImageLayout().getFrameBottom());
				sb.append("|");
			}
			
			// Removes the last '|'
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
		}
		return sb.toString();
	}
	
	/**
	 * Checks if Source UI will show or hide
	 * 
	 * @param configuration
	 *            The source configuration
	 * @return <code>true</code> If the source UI must be hidden, <code>false</code> otherwise
	 */
	public boolean showSourceUI(Object configuration){
		@SuppressWarnings("unchecked")
		HashMap<String, String> configMap = (HashMap<String, String>) configuration;
		
		boolean showUI = ("true".equals(configMap.get("showSourceUI"))) ? true : false;
		NativeTwainAccess.getInstance().showSourceUI(showUI);
		
		return showUI;
	}
	
	public void configureSource(ISource src, Object configuration){
		if (configuration instanceof TwainSourceConfiguration) {
			TwainSourceConfiguration srcConfig = (TwainSourceConfiguration) src.getConfiguration();
			TwainSourceConfiguration newConf = (TwainSourceConfiguration) configuration;
			TreeSet<TwainCapability> configSet = newConf.getConfig();
			for (Iterator iter = configSet.iterator(); iter.hasNext();) {
				TwainCapability cap = (TwainCapability) iter.next();
				srcConfig.setCapability(cap.getCapability(), cap.getCapabilityType(),
					cap.getCapabilityValue());
			}
		}
	}
	
	public ISource[] getSources(){
		ISource sources[] = null;
		
		if (NativeTwainAccess.getInstance().isTwainAvailble()) {
			// The twainSources array has 2*n size because first position is the
			// source name and next position is the source description
			String[] twainSources = NativeTwainAccess.getInstance().getAvailableSources();
			if (twainSources != null) {
				sources = new ISource[twainSources.length / 2];
				int j = 0;
				for (int i = 0; i < twainSources.length; i++) {
					TwainSource twainSource =
						new TwainSource(twainSources[i], twainSources[i + 1], true);
					sources[j] = twainSource;
					i++;
					j++;
				}
			}
		}
		return sources;
	}
	
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
		throws CoreException{
		// TODO Auto-generated method stub
		
	}
}
