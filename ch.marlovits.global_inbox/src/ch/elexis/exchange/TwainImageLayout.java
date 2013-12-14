package ch.elexis.exchange;

/**
 * This class is used to manage the TW_IMAGELAYOUT structure. For setting the
 * DAT_IMAGELAYOUT operations.
 * 
 * The DAT_IMAGELAYOUT operations control information on the physical layout of
 * the image on the acquisition platform of the Source (e.g. the glass of a
 * flatbed scanner, the size of a photograph, etc.).
 */
public class TwainImageLayout {

	// Declaration of necessary attributes to be compatible with
	// TW_IMAGELAYOUT structure.

	// The following attributes Defines the Left, Top, Right, and Bottom
	// coordinates (in ICAP_UNITS) of the rectangle enclosing the original image
	// on the original �page�. If the application isn�t interested in setting
	// the origin of the image, set both Top and Left to zero. The Source will
	// fill in the actual values following the acquisition. See also TW_FRAME.
	private double frameLeft;

	private double frameRight;

	private double frameTop;

	private double frameBottom;

	// The document number, assigned by the Source, that the acquired data
	// originated on. Useful for grouping pages together. Usually a physical
	// representation, this could just as well be a logical construct. Initial
	// value is 1. Increment when a new document is placed into the document
	// feeder (usually tell this has happened when the feeder empties). Reset
	// when no longer acquiring from the feeder.
	private int documentNumber;

	// The page which the acquired data was captured from. Useful for grouping
	// Frames together that are in some way related, usually Source. Usually a
	// physical representation, this could just as well be a logical construct.
	// Initial value is 1. Increment for each page fed from a page feeder. Reset
	// when a new document is placed into the feeder.
	private int pageNumber;

	// Usually a chronological index of the acquired frame. These frames are
	// related to one another in some way; usually they were acquired from the
	// same page. The Source assigns these values. Initial value is 1. Reset
	// when a new page is acquired from.
	private int frameNumber;

	/**
	 * Class constructor from parameters
	 * 
	 * @param frameLeft
	 *            Left coordinate (in ICAP_UNITS) of the rectangle enclosing the
	 *            original image
	 * @param frameRight
	 *            Right coordinate (in ICAP_UNITS) of the rectangle enclosing
	 *            the original image
	 * @param frameTop
	 *            Top coordinate (in ICAP_UNITS) of the rectangle enclosing the
	 *            original image
	 * @param frameBottom
	 *            Bottom coordinate (in ICAP_UNITS) of the rectangle enclosing
	 *            the original image
	 * @param documentNumber
	 *            The document number
	 * @param pageNumber
	 *            The page which the acquired data was captured from
	 * @param frameNumber
	 *            Usually a chronological index of the acquired frame
	 */
	public TwainImageLayout(double frameLeft, double frameRight,
			double frameTop, double frameBottom, int documentNumber,
			int pageNumber, int frameNumber) {
		this.frameLeft = frameLeft;
		this.frameRight = frameRight;
		this.frameTop = frameTop;
		this.frameBottom = frameBottom;
		this.documentNumber = documentNumber;
		this.pageNumber = pageNumber;
		this.frameNumber = frameNumber;
	}

	/**
	 * @return the documentNumber
	 */
	public int getDocumentNumber() {
		return documentNumber;
	}

	/**
	 * @return the frameBottom
	 */
	public double getFrameBottom() {
		return frameBottom;
	}

	/**
	 * @return the frameLeft
	 */
	public double getFrameLeft() {
		return frameLeft;
	}

	/**
	 * @return the frameNumber
	 */
	public int getFrameNumber() {
		return frameNumber;
	}

	/**
	 * @return the frameRight
	 */
	public double getFrameRight() {
		return frameRight;
	}

	/**
	 * @return the frameTop
	 */
	public double getFrameTop() {
		return frameTop;
	}

	/**
	 * @return the pageNumber
	 */
	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public String toString() {
		return "Frame Left: " + frameLeft + ", Frame Right:" + frameRight
				+ ", Frame Top:" + frameTop + ", Frame Bottom:" + frameBottom
				+ ", Document Number :" + documentNumber + ", Page Number: "
				+ pageNumber + ", Frame Number:" + frameNumber;
	}
}
