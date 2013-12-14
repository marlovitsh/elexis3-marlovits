package ch.elexis.exchange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public class TwainSourceConfiguration {

	// The HashSet object is used to avoid duplicate configuration
	private TreeSet<TwainCapability> config;

	// Attribute to show or hide the source configuration dialog
	private boolean showSourceUI;

	// Attribute to set the TW_IMAGELAYOUT properties
	private TwainImageLayout imageLayout;

	public TwainSourceConfiguration() {
		config = new TreeSet<TwainCapability>();
		// The image layout is null as it is not used as default
		imageLayout = null;
	}

	/**
	 * Class constructor that receives a configuration String with the same
	 * characteristics as the ones used in the native methods (<code>setTwainCapabilities</code>
	 * and <code>getLatestConfiguration</code>).
	 * 
	 * @param configurationString
	 *            a configuration String
	 */
	public TwainSourceConfiguration(String configurationString) {
		config = new TreeSet<TwainCapability>();
		// The image layout is null as it is not used as default
		imageLayout = null;

		String configItems[] = configurationString.split("\\|");
		for (int i = 0; i < configItems.length; i++) {
			String configItem = configItems[i];
			String singleConfiguration[] = configItem.split("&");
			// Simple verification
			if (singleConfiguration.length > 0) {
				if ("cap".equalsIgnoreCase(singleConfiguration[0])) {
					parseCapability(singleConfiguration);
				} else if ("imgLayout".equalsIgnoreCase(singleConfiguration[0])) {
					parseImageLayout(singleConfiguration);
				}
			}
		}

	}

	/**
	 * Method that parses and sets a capability based in a configuration
	 * 
	 * @param singleConfiguration
	 *            configuration
	 */
	private void parseImageLayout(String[] singleConfiguration) {
		// Here are an assignation with default values to avoid
		// parse errors
		double left = 0, right = 8.5, top = 0, bottom = 11;
		int docNum = 1, pageNum = 1, frameNum = 1;

		String imgLayoutParts[] = singleConfiguration[3].split(";");
		if (imgLayoutParts.length > 0) {
			int index = 0;
			left = Double.parseDouble(imgLayoutParts[index++]);
			right = Double.parseDouble(imgLayoutParts[index++]);
			top = Double.parseDouble(imgLayoutParts[index++]);
			bottom = Double.parseDouble(imgLayoutParts[index++]);
			docNum = Integer.parseInt(imgLayoutParts[index++]);
			pageNum = Integer.parseInt(imgLayoutParts[index++]);
			frameNum = Integer.parseInt(imgLayoutParts[index++]);
		}
		this.imageLayout = new TwainImageLayout(left, right, top, bottom,
				docNum, pageNum, frameNum);
		// TODO This print is useful for debugging, and reading configuration in
		// a human way
		System.out.println(this.imageLayout);
	}

	/**
	 * Method that parses and sets the image layout based in a configuration
	 * 
	 * @param singleConfiguration
	 *            configuration
	 */
	private void parseCapability(String[] singleConfiguration) {
		int cap, capType;
		String capValue;

		cap = Integer.parseInt(singleConfiguration[1]);
		capType = Integer.parseInt(singleConfiguration[2]);
		capValue = singleConfiguration[3];

		this.setCapability(cap, capType, capValue);
		// TODO This print is useful for debugging
		System.out.println(TwainConstants.getInstance().getIcapName(cap)
				+ ", Setted in: " + capValue);
	}

	/**
	 * This method is to set a capability for a TWAIN source. The supported
	 * capabilities are those declared in the class TwainConstants as ICAP...<br>
	 * Examples of invocation this method:<br>
	 * <br>
	 * <code>twSrcConfigInstance.setCapability(TwainConstats.ICAP_BITDEPTH, TwainConstants.TWTY_INT16, "24");</code><br>
	 * <code>twSrcConfigInstance.setCapability(TwainConstats.ICAP_FRAMES, TwainConstants.TWTY_FRAME, "0.0;8.5;0.0;11.0");</code><br>
	 * 
	 * @param capability
	 *            The capability to set (negotiate) in the Twain Source
	 * @param capabilityType
	 *            The corresponding capability Type
	 * @param value
	 *            The desired value of the source capability
	 */
	public void setCapability(int capability, int capabilityType, String value) {
		TwainCapability cap = new TwainCapability(capability, capabilityType,
				value);
		// It allows to overwrite the old valie
		if (config.contains(cap))
			config.remove(cap);
		config.add(cap);
	}

	/**
	 * Method that returns the set of stablished capabilities values for the
	 * source. <br>
	 * This method is an utility for handling the native methods in class
	 * <code><b>NativeTwainAccess</b></code>
	 * 
	 * @return The set of stablished capabilities for the source
	 */
	public List<String> getSettedCapabilities() {
		List<String> settedCaps = new ArrayList<String>();

		for (Iterator iter = config.iterator(); iter.hasNext();) {
			TwainCapability temp = (TwainCapability) iter.next();
			settedCaps.add("" + temp.getCapability());
		}
		return settedCaps;
	}

	/**
	 * @return the config Set
	 */
	public TreeSet<TwainCapability> getConfig() {
		return config;
	}

	/**
	 * @return the showSourceUI
	 */
	public boolean isShowSourceUI() {
		return showSourceUI;
	}

	/**
	 * @param showSourceUI
	 *            the showSourceUI to set
	 */
	public void setShowSourceUI(boolean showSourceUI) {
		this.showSourceUI = showSourceUI;
	}

	/**
	 * @return the imageLayout
	 */
	public TwainImageLayout getImageLayout() {
		return imageLayout;
	}

	/**
	 * @param imageLayout
	 *            the imageLayout to set
	 */
	public void setImageLayout(TwainImageLayout imageLayout) {
		this.imageLayout = imageLayout;
	}

}
