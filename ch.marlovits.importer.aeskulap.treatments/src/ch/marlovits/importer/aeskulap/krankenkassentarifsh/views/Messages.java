package ch.marlovits.importer.aeskulap.krankenkassentarifsh.views;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "ch.marlovits.importer.aeskulap.krankenkassentarifsh.views.messages"; //$NON-NLS-1$
	public static String DetailDisplay_PARENT;
	public static String DetailDisplay_POSITION;
	public static String DetailDisplay_BEZEICHNUNG;
	public static String DetailDisplay_KURZBEZ;
	public static String DetailDisplay_DRUCKBEZ;
	public static String DetailDisplay_KOMMENTAR;
	public static String DetailDisplay_PREISMIN;
	public static String DetailDisplay_PREISMAX;
	public static String DetailDisplay_DATUMVON;
	public static String DetailDisplay_DATUMBIS;
	public static String DetailDisplay_AUSSCHLUSSCODE;
	public static String DetailDisplay_UMTRIEBSCODE;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
