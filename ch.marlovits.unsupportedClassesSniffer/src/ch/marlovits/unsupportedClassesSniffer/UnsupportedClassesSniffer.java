package ch.marlovits.unsupportedClassesSniffer;

import java.io.ByteArrayInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipInputStream;

import ch.elexis.core.data.PersistentObject;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;
import ch.rgw.tools.JdbcLinkException;

/**
 * 
 * 
 * @author marlovitsh
 * 
 */

public class UnsupportedClassesSniffer {
	// *** chunk of records to read from tables
	static int chunk = 100;

	/**
	 * contains a list of all binary fields to be searched for unresolved
	 * classes. Format: [tableName]:[binaryFieldName]
	 */
	static String[] snifferList = { "Artikel:extinfo", "Behandlungen:eintrag",
			"ch_elexis_eigendiagnosen:extinfo",
			"ch_elexis_icpc_encounter:extinfo",
			"ch_elexis_icpc_episodes:extinfo",
			"ch_elexis_privatrechnung:extinfo",
			"ch_marlovits_aeskulap_codes:extinfo", "elexisbefunde:befunde",
			"faelle:extinfo", "heap:inhalt", "heap2:contents", "icd10:extinfo",
			"Kontakt:extinfo", "leistungen:detail",
			"leistungsblock:leistungen", "logs:extinfo", "output_log:extinfo",
			"patient_artikel_joint:extinfo", "rechnungen:extinfo",
			"tarmed_extension:limits", "" };

	/**
	 * searches all fields defined in snifferList for unresolved classes
	 * 
	 * @param snifferList
	 *            String, a \n-delimited list of fields to be searched for.
	 *            Format for each entry [tableName]:[binaryFieldName]
	 * @return a list of all unresolved classes
	 */
	public static HashSet<String> searchAllTablesForUnresolvedClasses(
			String snifferList) {
		ObjectInputStreamSniffer.UNRESOLVEDCLASSES.clear();
		String[] snifferListArr = snifferList.split("\n");
		for (String sniffer : snifferListArr) {
			sniffer = sniffer.replace("\r", "").replace("\n", "");
			String[] splitted = sniffer.split(":");
			if (splitted.length == 2) {
				// *** extract tableName/fieldName
				String tableName = sniffer.split(":")[0];
				String fieldName = sniffer.split(":")[1];
				// *** "debug msg"
				System.out.println("*** processing " + sniffer + " ***");
				String[] unresolved = ObjectInputStreamSniffer.UNRESOLVEDCLASSES
						.toArray(new String[ObjectInputStreamSniffer.UNRESOLVEDCLASSES
								.size()]);
				String unresolvedStr = "";
				for (String tmp : unresolved)
					unresolvedStr = unresolvedStr + tmp + "\n";
				UnsupportedClassesSnifferView.unresolvedClasses
						.setText(((unresolvedStr.isEmpty()) ? ""
								: "*** unresolved classes found ***\n")
								+ unresolvedStr
								+ "*** processing "
								+ sniffer
								+ " ***");
				UnsupportedClassesSnifferView.unresolvedClasses.getParent()
						.getDisplay().update();
				// *** search for unresolvables, put into
				// *** ObjectInputStreamSniffer.UNRESOLVEDCLASSES
				searchTableForUnresolvedClasses(tableName, fieldName, false);
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return ObjectInputStreamSniffer.UNRESOLVEDCLASSES;
	}

	/**
	 * searches the field defined in the params for unresolved classes
	 * 
	 * @param table
	 *            String, the table to be searched for
	 * @param field
	 *            String, the field to be searched for
	 * @param clear
	 *            boolean, true if the result should be cleared before
	 * 
	 * @return a list of all unresolved classes as HashSet
	 */
	private static HashSet<String> searchTableForUnresolvedClasses(
			final String table, final String field, boolean clear) {
		if (clear)
			ObjectInputStreamSniffer.UNRESOLVEDCLASSES.clear();

		// *** find number of records in the table
		int numOfRecords = 0;
		String counterSql = "select count(*) as cnt from " + table;
		ResultSet counterRes = executeSqlQuery(counterSql);
		if (counterRes == null) {
			String asdfa = UnsupportedClassesSnifferView.snifferErrorsList.getText();
			System.out.println("ResultSet from query '" + counterSql
					+ "' == null.\r\n    May be there is no such table?");
			String before = "\r\n";
			if (UnsupportedClassesSnifferView.snifferErrorsList.getText()
					.isEmpty())
				before = "";
			UnsupportedClassesSnifferView.snifferErrorsList
					.setText(UnsupportedClassesSnifferView.snifferErrorsList
							.getText()
							+ before
							+ "ResultSet from query '"
							+ counterSql
							+ "' == null.\r\n    May be there is no such table?");
		} else {
			try {
				counterRes.next();
				numOfRecords = counterRes.getInt("cnt");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		// *** split into chunks to avoid memory errs...
		JdbcLink j = PersistentObject.getConnection();
		String dbFlavor = j.DBFlavor;

		int offset = 0;
		int limit = offset + chunk;
		while (offset < numOfRecords) {
			// *** base query
			String sql = "select " + field + " from " + table;
			// *** read chunk# of rows
			if (dbFlavor.equalsIgnoreCase("postgresql")) {
				sql = sql + " offset " + offset + " limit " + limit;
			} else if (dbFlavor.equalsIgnoreCase("mysql")) {
				sql = sql + " limit " + offset + ", " + limit;
			} else if (dbFlavor.equalsIgnoreCase("h2")) {
				sql = sql + " offset " + offset + " limit " + limit;
			}
			// *** calc next offset/chunk
			offset = offset + chunk;
			limit = chunk;

			// *** query...
			ResultSet res = executeSqlQuery(sql);
			if (res == null) {
				// *** happens if table/field not present
				System.out
						.println("ResultSet from query '"
								+ sql
								+ "' == null.\r\n    May be there is no such table/field?");
				String before = "\r\n";
				String asdfa = UnsupportedClassesSnifferView.snifferErrorsList.getText();
				if (UnsupportedClassesSnifferView.snifferErrorsList.getText()
						.isEmpty())
					before = "";
				UnsupportedClassesSnifferView.snifferErrorsList
						.setText(UnsupportedClassesSnifferView.snifferErrorsList
								.getText()
								+ before
								+ "ResultSet from query '"
								+ sql
								+ "' == null.\r\n    May be there is no such table/field?");
				break;
			} else {
				try {
					// *** loop through records
					while (res.next()) {
						byte[] blob = res.getBytes(field);
						if (blob != null) {
							// *** this calls the
							// *** ObjectInputStreamSniffer.resolveClass() which
							// *** lists unresolved classes
							@SuppressWarnings("unused")
							Hashtable<Object, Object> extinfoHashTable = fold(blob);
						}
					}
				} catch (SQLException e) {
					e.printStackTrace();
					return null;
				}
			}
			String[] unresolved = ObjectInputStreamSniffer.UNRESOLVEDCLASSES
					.toArray(new String[ObjectInputStreamSniffer.UNRESOLVEDCLASSES
							.size()]);
			String unresolvedStr = "";
			for (String tmp : unresolved)
				unresolvedStr = unresolvedStr + tmp + "\r\n";
			UnsupportedClassesSnifferView.unresolvedClasses
					.setText(((unresolvedStr.isEmpty()) ? ""
							: "*** unresolved classes found ***\r\n")
							+ unresolvedStr
							+ "*** processing "
							+ table
							+ ":"
							+ field + " ***");
			UnsupportedClassesSnifferView.unresolvedClasses.getDisplay()
					.update();
		}
		return ObjectInputStreamSniffer.UNRESOLVEDCLASSES;
	}

	// *************************************************************************
	// *** persistent object extractions/adaptions to be able to use my own
	// *** sniffer class ObjectInputStreamSniffer
	// *************************************************************************
	/**
	 * Eine Hashtable auslesen
	 * 
	 * @param field
	 *            Feldname der Hashtable
	 * @return eine Hashtable (ggf. leer). Nie null.
	 */
	@SuppressWarnings({ "rawtypes" })
	public static Map getMap(final String table, final String id,
			final String field) {
		byte[] blob = getBinaryRaw(table, id, field);
		if (blob == null) {
			return new Hashtable();
		}
		Hashtable<Object, Object> ret = fold(blob);
		if (ret == null) {
			return new Hashtable();
		}
		return ret;
	}

	private static byte[] getBinaryRaw(final String table, final String id,
			final String field) {
		StringBuilder sql = new StringBuilder();
		String mapped = (field);
		sql.append("SELECT ").append(mapped).append(" FROM ").append(table)
				.append(" WHERE ID='").append(id).append("'");

		ResultSet res = executeSqlQuery(sql.toString());
		try {
			if ((res != null) && (res.next() == true)) {
				return res.getBytes(mapped);
			}
		} catch (Exception ex) {
			ExHandler.handle(ex);
		}
		return null;
	}

	static ResultSet executeSqlQuery(String sql) {
		Stm stm = null;
		ResultSet res = null;
		try {
			stm = PersistentObject.getConnection().getStatement();
			res = stm.query(sql);
		} catch (JdbcLinkException je) {
			je.printStackTrace();
		} finally {
			PersistentObject.getConnection().releaseStatement(stm);
		}
		return res;
	}

	/**
	 * Recreate a Hashtable from a byte array as created by flatten()
	 * 
	 * @param flat
	 *            the byte array
	 * @return the original Hashtable or null if no Hashtable could be created
	 *         from the array
	 */
	@SuppressWarnings("unchecked")
	public static Hashtable<Object, Object> fold(final byte[] flat) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(flat);
			ZipInputStream zis = new ZipInputStream(bais);
			zis.getNextEntry();
			ObjectInputStreamSniffer ois = new ObjectInputStreamSniffer(zis);
			Hashtable<Object, Object> res = (Hashtable<Object, Object>) ois
					.readObject();
			ois.close();
			bais.close();
			return res;
		} catch (Exception ex) {
			return null;
		}
	}
}
