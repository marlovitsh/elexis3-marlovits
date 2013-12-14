package ch.elexis.exchange;

public class TwainCapability implements Comparable {

	// The capability Code, stored as an int value. The ICAPs, had hexa values
	// e.g. ICAP_PIXELTYPE = 0x0101
	private int capability;

	// In each container structure ItemType can be TWTY_INT8, TWTY_INT16, etc.
	private int capabilityType;

	// The desired value for the capability
	private String capabilityValue;

	public TwainCapability(int capability, int capabilityType,
			String capabilityValue) {
		this.capability = capability;
		this.capabilityType = capabilityType;
		this.capabilityValue = capabilityValue;
	}

	/**
	 * @return the capability
	 */
	public int getCapability() {
		return capability;
	}

	/**
	 * @return the capabilityType
	 */
	public int getCapabilityType() {
		return capabilityType;
	}

	/**
	 * @return the capabilityValue
	 */
	public String getCapabilityValue() {
		return capabilityValue;
	}

	public int compareTo(Object o) {
		TwainCapability temp = (TwainCapability) o;
		return capability - temp.getCapability();
	}
}
