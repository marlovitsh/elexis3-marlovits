package ch.marlovits.importer.aeskulap.krankenkassentarifsh.data;
//ch.marlovits.importer.aeskulap.krankenkassentarifsh.data.KrankenkassenTarifSH

import java.util.List;

import ch.elexis.data.Fall;
import ch.elexis.data.VerrechenbarAdapter;
import ch.elexis.data.Xid;
import ch.elexis.core.data.interfaces.IOptifier;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class SUVATarifUVG extends VerrechenbarAdapter {
	public static final String CODESYSTEM_CODE_SUVATARIF = "1"; //$NON-NLS-1$
	public static final String MULTIPLICATOR_NAME = "SUVATARIF"; //$NON-NLS-1$
	public static final String CODESYSTEM_NAME = "SUVA Tarif UVG (Ae)"; //$NON-NLS-1$
	public static final String XIDDOMAIN = "marlovits.ch/aeskulap/suvatarifuvg/"; //$NON-NLS-1$

	public static final String FLD_PARENT = "Parent";
	public static final String FLD_CODE = "Code";
	public static final String FLD_TITEL = "Titel";
	public static final String FLD_KURZBEZ = "Kurzbez";
	public static final String FLD_DRUCKBEZ = "Druckbez";
	public static final String FLD_KOMMENTAR = "Kommentar";
	public static final String FLD_PREISMIN = "Preismin";
	public static final String FLD_PREISMAX = "Preismax";
	public static final String FLD_DATUMVON = "Datumvon";
	public static final String FLD_DATUMBIS = "Datumbis";
	public static final String FLD_AUSSCHLUSSCODE = "Ausschlusscode";
	public static final String FLD_UMTRIEBSCODE = "Umtriebscode";

	private final static String TABLENAME = "CH_MARLOVITS_AESKULAP_SUVA_TARIF_UVG"; //$NON-NLS-1$
	public static final String VERSION = "1.0"; //$NON-NLS-1$
	private static final String createTable = "create table " + TABLENAME
			+ "(                                         " //$NON-NLS-1$
			+ "ID             VARCHAR(25) primary key,   " //$NON-NLS-1$ 
			+ "lastupdate     BIGINT,                    " //$NON-NLS-1$
			+ "deleted        CHAR(1) default '0',       " //$NON-NLS-1$ 
			+ "parent         VARCHAR(14),               " //$NON-NLS-1$
			+ "code           VARCHAR(14),               " //$NON-NLS-1$
			+ "titel          VARCHAR(90),               " //$NON-NLS-1$ 
			+ "kurzbez        VARCHAR(8),                " //$NON-NLS-1$ 
			+ "druckbez       VARCHAR(32),               " //$NON-NLS-1$ 
			+ "kommentar      VARCHAR(250),              " //$NON-NLS-1$ 
			+ "preismin       VARCHAR(4),                " //$NON-NLS-1$ 
			+ "preismax       VARCHAR(4),                " //$NON-NLS-1$ 
			+ "datumvon       VARCHAR(8),                " //$NON-NLS-1$ 
			+ "datumbis       VARCHAR(8),                " //$NON-NLS-1$ 
			+ "ausschlusscode VARCHAR(1),                " //$NON-NLS-1$ 
			+ "umtriebscode   VARCHAR(1)                 " //$NON-NLS-1$ 
			+ ");                                        " //$NON-NLS-1$
			+ "INSERT INTO " + TABLENAME + "(ID,code)    " //$NON-NLS-1$
			+ "VALUES (1,'" + VERSION + "');             "; //$NON-NLS-1$ //$NON-NLS-2$

	private static final IOptifier suvaoptifier = new SUVATarifUVGOptifier();
	
	static {
		addMapping(TABLENAME, FLD_PARENT, FLD_CODE,
				FLD_TITEL, FLD_KURZBEZ, FLD_DRUCKBEZ, FLD_KOMMENTAR,
				FLD_PREISMIN, FLD_PREISMAX, FLD_DATUMVON, FLD_DATUMBIS,
				FLD_AUSSCHLUSSCODE, FLD_UMTRIEBSCODE);

		SUVATarifUVG version = load("1"); //$NON-NLS-1$
		if (!version.exists()) {
			createOrModifyTable(createTable);
		}
		Xid.localRegisterXIDDomainIfNotExists(XIDDOMAIN,
				"SUVA Tarif UVG", Xid.ASSIGNMENT_REGIONAL); //$NON-NLS-1$
	}

	@Override
	public String getLabel() {
		String code = getCode();
		if (!StringTool.isNothing(code)) {
			return new StringBuilder(code).append(" ").append(getText()) //$NON-NLS-1$
					.append(" (").append(get(FLD_TITEL)).append(")") //$NON-NLS-1$ //$NON-NLS-2$
					.toString();
		} else {
			return "?"; //$NON-NLS-1$
		}
	}

	@Override
	public String getCode() {
		return get(FLD_CODE);
	}

	@Override
	public String getText() {
		return StringTool.getFirstLine(get(FLD_TITEL), 80);
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public static String getTableName2() {
		return TABLENAME;
	}

	public static SUVATarifUVG load(final String id) {
		return new SUVATarifUVG(id);
	}

	protected SUVATarifUVG(final String id) {
		super(id);
	}

	public SUVATarifUVG() {
	}

	public double getFactor(TimeTool date, Fall fall) {
		double ret = getVKMultiplikator(date, MULTIPLICATOR_NAME);
		return ret;
	}

	public int getTP(TimeTool date, Fall fall) {
		double tp = checkZeroDouble(get(FLD_PREISMIN));
		return (int) Math.round(tp * 100.0);
	}

	@Override
	public boolean isDragOK() {
		return true;
	}

	@Override
	public String getCodeSystemName() {
		return CODESYSTEM_NAME;
	}

	public String getCodeSystemCode() {
		return CODESYSTEM_CODE_SUVATARIF;
	}

	@Override
	public IOptifier getOptifier() {
		return suvaoptifier;
	}

	public String getXidDomain() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getActions(Object context){
		// TODO Auto-generated method stub
		return null;
	}

}