package ch.marlovits.databaseConverter;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.elexis.core.data.Anwender;
import ch.elexis.core.data.PersistentObject;
import ch.elexis.core.data.Query;

/**
 * TO DO BEFORE CONVERSION: temporarily add MFUList.java to ch.rgw.tools in
 * ch.rgw.utility. After the conversion simply delete both modifications again!
 * 
 * @author marlovitsh
 * 
 */

public class DatabaseConverters {
	// *******************************************************************************************
	// *** convert extinfo entries from old style to new style:
	// *** ch.rgw.tools.MFUList -> ch.elexis.core.data.util.MFUList
	// *******************************************************************************************

	/**
	 * convert all extinfos of all users/Anwender. Calls convertKontaktMFU().
	 */
	public static void convertAllMFUEntries() {
		Query<Anwender> qbe = new Query<Anwender>(Anwender.class);
		qbe.clear(true); // *** include deleted entries, too
		List<Anwender> userList = qbe.execute();
		if ((userList == null) || (userList.size() < 1))
			return;
		for (Anwender user : userList) {
			convertKontaktMFU(user);
		}
	}

	/**
	 * converts the MFUList entry "LeistungenMFU" from old version
	 * (ch.rgw.tools.MFUList) to new version (ch.elexis.core.data.util.MFUList)
	 * for the Anwender user
	 * 
	 * @param user
	 *            Anwender, user for which the extinfo is to be corrected
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void convertKontaktMFU(Anwender user) {
		String MFUName = "LeistungenMFU";

		// *** read extinfo
		Map extInfoAsMap = user.getMap(PersistentObject.FLD_EXTINFO);

		try {
			/**
			 * try to read LeistungenMFU as a Map. read as old version
			 * (ch.rgw.tools.MFUList). If there is an old version then the entry
			 * is read correctly. If there is already the new version then the
			 * conversion fails (cast error) -> catch
			 */
			// *** read MFU from extinfo (old version) = SOURCE
			ch.rgw.tools.MFUList<String> leistungenMFU = (ch.rgw.tools.MFUList<String>) extInfoAsMap
					.get(MFUName);
			// *** create an empty MFUList (new version) = DEST
			ch.elexis.core.data.util.MFUList<String> leistungenMFU_NEW = new ch.elexis.core.data.util.MFUList(
					5, 15);
			// *** loop through the items of the source MFUList
			Iterator<String> oldIterator = leistungenMFU.iterator();
			while (oldIterator.hasNext()) {
				String value = oldIterator.next();
				// *** add item to the new version dest MFUList
				leistungenMFU_NEW.count(value);
			}
			// *** delete old MFUList version from extinfo
			extInfoAsMap.remove(MFUName);

			// *** write new MFUList version to extinfo
			extInfoAsMap.put(MFUName, leistungenMFU_NEW);

			// *** write converted extinfo
			user.setMap(PersistentObject.FLD_EXTINFO, extInfoAsMap);
		} catch (Exception e1) {
			// *** if we get here then the MFUList is already converted...
		}
	}
}
