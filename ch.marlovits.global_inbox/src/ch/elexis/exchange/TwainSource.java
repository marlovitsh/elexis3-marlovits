package ch.elexis.exchange;

import ch.elexis.core.ui.exchange.IScannerAccess;


/**
 * This class implements IScannerAccess.ISource to represent an availabe TWAIN
 * source
 * 
 */
public class TwainSource implements IScannerAccess.ISource {

	// The source name
	private String name;

	// The source description
	private String description;

	// The source status
	private boolean available;

	// The source configuration
	private TwainSourceConfiguration configuration;

	/**
	 * Class constructor. Invoked when retreiving an available source
	 * 
	 * @param name
	 *            The source name
	 * @param description
	 *            The source description
	 * @param available
	 *            The source status
	 */
	public TwainSource(String name, String description, boolean available) {
		this.name = name;
		this.description = description;
		this.available = available;
		this.configuration = new TwainSourceConfiguration();
	}

	/**
	 * Name of the source
	 * 
	 * @return a human readable name
	 */
	public String getName() {
		return name;
	}

	/**
	 * A longer description
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Is the source ready at the moment?
	 * 
	 * @return true if it is ready
	 */
	public boolean isAvailable() {
		return available;
	}

	public TwainSourceConfiguration getConfiguration() {
		return configuration;
	}

	@Override
	public String toString() {
		return name;
	}

}
