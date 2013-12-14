package ch.elexis.exchange;

import java.util.HashMap;

/**
 * Utility class that maps some constants from <b>TWAIN.H</b>
 */
public class TwainConstants {
	
	private HashMap<String, String> valueNameMap;
	
	private static TwainConstants instance = null;
	
	/* There are four containers used for capabilities negotiation:
	 *    TWON_ONEVALUE, TWON_RANGE, TWON_ENUMERATION, TWON_ARRAY
	 * In each container structure ItemType can be TWTY_INT8, TWTY_INT16, etc.
	 * The kind of data stored in the container can be determined by doing
	 * DCItemSize[ItemType] where the following is defined in TWAIN glue code:
	 *          DCItemSize[]= { sizeof(TW_INT8),
	 *                          sizeof(TW_INT16),
	 *                          etc.
	 *                          sizeof(TW_UINT32) };
	 *
	 */
	public static int TWTY_INT8        = 0x0000    /* Means Item is a TW_INT8   */;
	public static int TWTY_INT16       = 0x0001    /* Means Item is a TW_INT16  */;
	public static int TWTY_INT32       = 0x0002    /* Means Item is a TW_INT32  */;

	public static int TWTY_UINT8       = 0x0003    /* Means Item is a TW_UINT8  */;
	public static int TWTY_UINT16      = 0x0004    /* Means Item is a TW_UINT16 */;
	public static int TWTY_UINT32      = 0x0005    /* Means Item is a TW_UINT32 */;

	public static int TWTY_BOOL        = 0x0006    /* Means Item is a TW_BOOL   */;

	public static int TWTY_FIX32       = 0x0007    /* Means Item is a TW_FIX32  */;

	public static int TWTY_FRAME       = 0x0008    /* Means Item is a TW_FRAME  */;

	public static int TWTY_STR32       = 0x0009    /* Means Item is a TW_STR32  */;
	public static int TWTY_STR64       = 0x000a    /* Means Item is a TW_STR64  */;
	public static int TWTY_STR128      = 0x000b    /* Means Item is a TW_STR128 */;
	public static int TWTY_STR255      = 0x000c    /* Means Item is a TW_STR255 */;
	public static int TWTY_STR1024     = 0x000d    /* Means Item is a TW_STR1024...added 1.9 */;
	public static int TWTY_UNI512      = 0x000e    /* Means Item is a TW_UNI512...added 1.9 */;

	/* image data sources are REQUIRED to support these caps */;
	public static int ICAP_COMPRESSION  = 0x0100;
	public static int ICAP_PIXELTYPE    = 0x0101;
	public static int ICAP_UNITS        = 0x0102 /* default is TWUN_INCHES */;
	public static int ICAP_XFERMECH     = 0x0103;
	
	/* image data sources MAY support these caps */
	public static int ICAP_AUTOBRIGHT                   = 0x1100;
	public static int ICAP_BRIGHTNESS                   = 0x1101;
	public static int ICAP_CONTRAST                     = 0x1103;
	public static int ICAP_CUSTHALFTONE                 = 0x1104;
	public static int ICAP_EXPOSURETIME                 = 0x1105;
	public static int ICAP_FILTER                       = 0x1106;
	public static int ICAP_FLASHUSED                    = 0x1107;
	public static int ICAP_GAMMA                        = 0x1108;
	public static int ICAP_HALFTONES                    = 0x1109;
	public static int ICAP_HIGHLIGHT                    = 0x110a;
	public static int ICAP_IMAGEFILEFORMAT              = 0x110c;
	public static int ICAP_LAMPSTATE                    = 0x110d;
	public static int ICAP_LIGHTSOURCE                  = 0x110e;
	public static int ICAP_ORIENTATION                  = 0x1110;
	public static int ICAP_PHYSICALWIDTH                = 0x1111;
	public static int ICAP_PHYSICALHEIGHT               = 0x1112;
	public static int ICAP_SHADOW                       = 0x1113;
	public static int ICAP_FRAMES                       = 0x1114;
	public static int ICAP_XNATIVERESOLUTION            = 0x1116;
	public static int ICAP_YNATIVERESOLUTION            = 0x1117;
	public static int ICAP_XRESOLUTION                  = 0x1118;
	public static int ICAP_YRESOLUTION                  = 0x1119;
	public static int ICAP_MAXFRAMES                    = 0x111a;
	public static int ICAP_TILES                        = 0x111b;
	public static int ICAP_BITORDER                     = 0x111c;
	public static int ICAP_CCITTKFACTOR                 = 0x111d;
	public static int ICAP_LIGHTPATH                    = 0x111e;
	public static int ICAP_PIXELFLAVOR                  = 0x111f;
	public static int ICAP_PLANARCHUNKY                 = 0x1120;
	public static int ICAP_ROTATION                     = 0x1121;
	public static int ICAP_SUPPORTEDSIZES               = 0x1122;
	public static int ICAP_THRESHOLD                    = 0x1123;
	public static int ICAP_XSCALING                     = 0x1124;
	public static int ICAP_YSCALING                     = 0x1125;
	public static int ICAP_BITORDERCODES                = 0x1126;
	public static int ICAP_PIXELFLAVORCODES             = 0x1127;
	public static int ICAP_JPEGPIXELTYPE                = 0x1128;
	public static int ICAP_TIMEFILL                     = 0x112a;
	public static int ICAP_BITDEPTH                     = 0x112b;
	public static int ICAP_BITDEPTHREDUCTION            = 0x112c  /* Added 1.5 */;
	public static int ICAP_UNDEFINEDIMAGESIZE           = 0x112d  /* Added 1.6 */;
	public static int ICAP_IMAGEDATASET                 = 0x112e  /* Added 1.7 */;
	public static int ICAP_EXTIMAGEINFO                 = 0x112f  /* Added 1.7 */;
	public static int ICAP_MINIMUMHEIGHT                = 0x1130  /* Added 1.7 */;
	public static int ICAP_MINIMUMWIDTH                 = 0x1131  /* Added 1.7 */;
	public static int ICAP_FLIPROTATION                 = 0x1136  /* Added 1.8 */;
	public static int ICAP_BARCODEDETECTIONENABLED      = 0x1137  /* Added 1.8 */;
	public static int ICAP_SUPPORTEDBARCODETYPES        = 0x1138  /* Added 1.8 */;
	public static int ICAP_BARCODEMAXSEARCHPRIORITIES   = 0x1139  /* Added 1.8 */;
	public static int ICAP_BARCODESEARCHPRIORITIES      = 0x113a  /* Added 1.8 */;
	public static int ICAP_BARCODESEARCHMODE            = 0x113b  /* Added 1.8 */;
	public static int ICAP_BARCODEMAXRETRIES            = 0x113c  /* Added 1.8 */;
	public static int ICAP_BARCODETIMEOUT               = 0x113d  /* Added 1.8 */;
	public static int ICAP_ZOOMFACTOR                   = 0x113e  /* Added 1.8 */;
	public static int ICAP_PATCHCODEDETECTIONENABLED    = 0x113f  /* Added 1.8 */;
	public static int ICAP_SUPPORTEDPATCHCODETYPES      = 0x1140  /* Added 1.8 */;
	public static int ICAP_PATCHCODEMAXSEARCHPRIORITIES = 0x1141  /* Added 1.8 */;
	public static int ICAP_PATCHCODESEARCHPRIORITIES    = 0x1142  /* Added 1.8 */;
	public static int ICAP_PATCHCODESEARCHMODE          = 0x1143  /* Added 1.8 */;
	public static int ICAP_PATCHCODEMAXRETRIES          = 0x1144  /* Added 1.8 */;
	public static int ICAP_PATCHCODETIMEOUT             = 0x1145  /* Added 1.8 */;
	public static int ICAP_FLASHUSED2                   = 0x1146  /* Added 1.8 */;
	public static int ICAP_IMAGEFILTER                  = 0x1147  /* Added 1.8 */;
	public static int ICAP_NOISEFILTER                  = 0x1148  /* Added 1.8 */;
	public static int ICAP_OVERSCAN                     = 0x1149  /* Added 1.8 */;
	public static int ICAP_AUTOMATICBORDERDETECTION     = 0x1150  /* Added 1.8 */;
	public static int ICAP_AUTOMATICDESKEW              = 0x1151  /* Added 1.8 */;
	public static int ICAP_AUTOMATICROTATE              = 0x1152  /* Added 1.8 */;
	public static int ICAP_JPEGQUALITY                  = 0x1153  /* Added 1.9 */;
	
	
	/* ICAP_UNITS values (UN_ means UNits) */	
	public static int TWUN_INCHES      = 0;
	public static int TWUN_CENTIMETERS = 1;
	public static int TWUN_PICAS       = 2;
	public static int TWUN_POINTS      = 3;
	public static int TWUN_TWIPS       = 4;
	public static int TWUN_PIXELS      = 5;

	
	/* ICAP_PIXELFLAVOR values (PF_ means Pixel Flavor) */
	public static int TWPF_CHOCOLATE = 0  /* zero pixel represents darkest shade */;
	public static int TWPF_VANILLA   = 1  /* zero pixel represents lightest shade */;
	
	
	/* ICAP_PIXELTYPE values (PT_ means Pixel Type) */
	public static int TWPT_BW      = 0  /* Black and White */;
	public static int TWPT_GRAY    = 1;
	public static int TWPT_RGB     = 2;
	public static int TWPT_PALETTE = 3;
	public static int TWPT_CMY     = 4;
	public static int TWPT_CMYK    = 5;
	public static int TWPT_YUV     = 6;
	public static int TWPT_YUVK    = 7;
	public static int TWPT_CIEXYZ  = 8;
	
	
	/* ICAP_SUPPORTEDSIZES values (SS_ means Supported Sizes) */
	public static int TWSS_NONE = 0;
	public static int TWSS_A4LETTER = 1;
	public static int TWSS_B5LETTER = 2;
	public static int TWSS_USLETTER = 3;
	public static int TWSS_USLEGAL = 4;
	/* Added 1.5 */;
	public static int TWSS_A5 = 5;
	public static int TWSS_B4 = 6;
	public static int TWSS_B6 = 7;
	// public static int TWSS_B = 8;
	/* Added 1.7 */;
	public static int TWSS_USLEDGER = 9;
	public static int TWSS_USEXECUTIVE = 10;
	public static int TWSS_A3 = 11;
	public static int TWSS_B3 = 12;
	public static int TWSS_A6 = 13;
	public static int TWSS_C4 = 14;
	public static int TWSS_C5 = 15;
	public static int TWSS_C6 = 16;
	/* Added 1.8 */;
	public static int TWSS_4A0 = 17;
	public static int TWSS_2A0 = 18;
	public static int TWSS_A0 = 19;
	public static int TWSS_A1 = 20;
	public static int TWSS_A2 = 21;
	public static int TWSS_A4 = TWSS_A4LETTER;
	public static int TWSS_A7 = 22;
	public static int TWSS_A8 = 23;
	public static int TWSS_A9 = 24;
	public static int TWSS_A10 = 25;
	public static int TWSS_ISOB0 = 26;
	public static int TWSS_ISOB1 = 27;
	public static int TWSS_ISOB2 = 28;
	public static int TWSS_ISOB3 = TWSS_B3;
	public static int TWSS_ISOB4 = TWSS_B4;
	public static int TWSS_ISOB5 = 29;
	public static int TWSS_ISOB6 = TWSS_B6;
	public static int TWSS_ISOB7 = 30;
	public static int TWSS_ISOB8 = 31;
	public static int TWSS_ISOB9 = 32;
	public static int TWSS_ISOB10 = 33;
	public static int TWSS_JISB0 = 34;
	public static int TWSS_JISB1 = 35;
	public static int TWSS_JISB2 = 36;
	public static int TWSS_JISB3 = 37;
	public static int TWSS_JISB4 = 38;
	public static int TWSS_JISB5 = TWSS_B5LETTER;
	public static int TWSS_JISB6 = 39;
	public static int TWSS_JISB7 = 40;
	public static int TWSS_JISB8 = 41;
	public static int TWSS_JISB9 = 42;
	public static int TWSS_JISB10 = 43;
	public static int TWSS_C0 = 44;
	public static int TWSS_C1 = 45;
	public static int TWSS_C2 = 46;
	public static int TWSS_C3 = 47;
	public static int TWSS_C7 = 48;
	public static int TWSS_C8 = 49;
	public static int TWSS_C9 = 50;
	public static int TWSS_C10 = 51;
	public static int TWSS_USSTATEMENT  = 52;
	public static int TWSS_BUSINESSCARD = 53;
	
	
	/* ICAP_ORIENTATION values (OR_ means ORientation) */
	public static int TWOR_ROT0 = 0;
	public static int TWOR_ROT90 = 1;
	public static int TWOR_ROT180 = 2;
	public static int TWOR_ROT270 = 3;
	public static int TWOR_PORTRAIT = TWOR_ROT0;
	public static int TWOR_LANDSCAPE = TWOR_ROT270;
	
	/**
	 * Private constructor of the TwainConstants class for implementing the
	 * Singleton
	 * 
	 */
	private TwainConstants() {
		valueNameMap = new HashMap<String, String>();
		valueNameMap.put("256", "ICAP_COMPRESSION");
		valueNameMap.put("257", "ICAP_PIXELTYPE");
		valueNameMap.put("258", "ICAP_UNITS");
		valueNameMap.put("259", "ICAP_XFERMECH");
		valueNameMap.put("4352", "ICAP_AUTOBRIGHT");
		valueNameMap.put("4353", "ICAP_BRIGHTNESS");
		valueNameMap.put("4355", "ICAP_CONTRAST");
		valueNameMap.put("4356", "ICAP_CUSTHALFTONE");
		valueNameMap.put("4357", "ICAP_EXPOSURETIME");
		valueNameMap.put("4358", "ICAP_FILTER");
		valueNameMap.put("4359", "ICAP_FLASHUSED");
		valueNameMap.put("4360", "ICAP_GAMMA");
		valueNameMap.put("4361", "ICAP_HALFTONES");
		valueNameMap.put("4362", "ICAP_HIGHLIGHT");
		valueNameMap.put("4364", "ICAP_IMAGEFILEFORMAT");
		valueNameMap.put("4365", "ICAP_LAMPSTATE");
		valueNameMap.put("4366", "ICAP_LIGHTSOURCE");
		valueNameMap.put("4368", "ICAP_ORIENTATION");
		valueNameMap.put("4369", "ICAP_PHYSICALWIDTH");
		valueNameMap.put("4370", "ICAP_PHYSICALHEIGHT");
		valueNameMap.put("4371", "ICAP_SHADOW");
		valueNameMap.put("4372", "ICAP_FRAMES");
		valueNameMap.put("4374", "ICAP_XNATIVERESOLUTION");
		valueNameMap.put("4375", "ICAP_YNATIVERESOLUTION");
		valueNameMap.put("4376", "ICAP_XRESOLUTION");
		valueNameMap.put("4377", "ICAP_YRESOLUTION");
		valueNameMap.put("4378", "ICAP_MAXFRAMES");
		valueNameMap.put("4379", "ICAP_TILES");
		valueNameMap.put("4380", "ICAP_BITORDER");
		valueNameMap.put("4381", "ICAP_CCITTKFACTOR");
		valueNameMap.put("4382", "ICAP_LIGHTPATH");
		valueNameMap.put("4383", "ICAP_PIXELFLAVOR");
		valueNameMap.put("4384", "ICAP_PLANARCHUNKY");
		valueNameMap.put("4385", "ICAP_ROTATION");
		valueNameMap.put("4386", "ICAP_SUPPORTEDSIZES");
		valueNameMap.put("4387", "ICAP_THRESHOLD");
		valueNameMap.put("4388", "ICAP_XSCALING");
		valueNameMap.put("4389", "ICAP_YSCALING");
		valueNameMap.put("4390", "ICAP_BITORDERCODES");
		valueNameMap.put("4391", "ICAP_PIXELFLAVORCODES");
		valueNameMap.put("4392", "ICAP_JPEGPIXELTYPE");
		valueNameMap.put("4394", "ICAP_TIMEFILL");
		valueNameMap.put("4395", "ICAP_BITDEPTH");
		valueNameMap.put("4396", "ICAP_BITDEPTHREDUCTION");
		valueNameMap.put("4397", "ICAP_UNDEFINEDIMAGESIZE");
		valueNameMap.put("4398", "ICAP_IMAGEDATASET");
		valueNameMap.put("4399", "ICAP_EXTIMAGEINFO");
		valueNameMap.put("4400", "ICAP_MINIMUMHEIGHT");
		valueNameMap.put("4401", "ICAP_MINIMUMWIDTH");
		valueNameMap.put("4406", "ICAP_FLIPROTATION");
		valueNameMap.put("4407", "ICAP_BARCODEDETECTIONENABLED");
		valueNameMap.put("4408", "ICAP_SUPPORTEDBARCODETYPES");
		valueNameMap.put("4409", "ICAP_BARCODEMAXSEARCHPRIORITIES");
		valueNameMap.put("4410", "ICAP_BARCODESEARCHPRIORITIES");
		valueNameMap.put("4411", "ICAP_BARCODESEARCHMODE");
		valueNameMap.put("4412", "ICAP_BARCODEMAXRETRIES");
		valueNameMap.put("4413", "ICAP_BARCODETIMEOUT");
		valueNameMap.put("4414", "ICAP_ZOOMFACTOR");
		valueNameMap.put("4415", "ICAP_PATCHCODEDETECTIONENABLED");
		valueNameMap.put("4416", "ICAP_SUPPORTEDPATCHCODETYPES");
		valueNameMap.put("4417", "ICAP_PATCHCODEMAXSEARCHPRIORITIES");
		valueNameMap.put("4418", "ICAP_PATCHCODESEARCHPRIORITIES");
		valueNameMap.put("4419", "ICAP_PATCHCODESEARCHMODE");
		valueNameMap.put("4420", "ICAP_PATCHCODEMAXRETRIES");
		valueNameMap.put("4421", "ICAP_PATCHCODETIMEOUT");
		valueNameMap.put("4422", "ICAP_FLASHUSED2");
		valueNameMap.put("4423", "ICAP_IMAGEFILTER");
		valueNameMap.put("4424", "ICAP_NOISEFILTER");
		valueNameMap.put("4425", "ICAP_OVERSCAN");
		valueNameMap.put("4432", "ICAP_AUTOMATICBORDERDETECTION");
		valueNameMap.put("4433", "ICAP_AUTOMATICDESKEW");
		valueNameMap.put("4434", "ICAP_AUTOMATICROTATE");
		valueNameMap.put("4435", "ICAP_JPEGQUALITY");
	}
	
	/**
	 * Method that implements a Singleton for the TwainConstants class
	 * 
	 * @return An unique instance of TwainConstants
	 */
	public static TwainConstants getInstance() {
		if (instance == null)
			instance = new TwainConstants();
		return instance;
	}

	/**
	 * Given the value for an ICAP constant returns the corresponding name
	 * 
	 * @param value
	 *            The numeric value of an ICAP
	 * @return The name of the corresponding ICAP
	 */
	public String getIcapName(int value) {
		String key = "" + value;
		return valueNameMap.get(key);
	}
}
