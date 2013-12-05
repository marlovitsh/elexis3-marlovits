package ch.marlovits_auf;

// +++++ hmm

import java.sql.ResultSet;
import java.sql.SQLException;

import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.data.Fall;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

public class tester {

	/*
	 * saved Brief_AUF_Grund***************************** import
	 * java.sql.ResultSet; import java.sql.SQLException; import
	 * ch.elexis.actions.ElexisEventDispatcher; import ch.elexis.data.Fall;
	 * import ch.elexis.data.Patient; import ch.elexis.data.PersistentObject;
	 * import ch.rgw.tools.JdbcLink; import ch.rgw.tools.JdbcLink.Stm; import
	 * ch.elexis.data.AUF;
	 * 
	 * String becauseOf = " wegen ";
	 * 
	 * Fall actFall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
	 * Patient actPat = (Patient) ElexisEventDispatcher
	 * .getSelected(Patient.class); if (actFall == null) return ""; else { if
	 * (!actFall.getPatient().getId().equalsIgnoreCase(actPat.getId())) { return
	 * "";
	 * 
	 * } } String fallID = actFall.getId();
	 * 
	 * String conditionAUF = ""; AUF actAuf = (AUF)
	 * ElexisEventDispatcher.getSelected(AUF.class); if (actAuf == null) { //
	 * *** display all AUFs } else { // *** display the selected AUF String
	 * aufID = actAuf.getId(); conditionAUF = " and id = " +
	 * JdbcLink.wrap(aufID) + " "; }
	 * 
	 * JdbcLink j = PersistentObject.getConnection(); Stm stm =
	 * j.getStatement();
	 * 
	 * String sql =
	 * "select count(*) as cnt from (select grund from auf where fallid = " +
	 * JdbcLink.wrap(fallID) + conditionAUF + " and deleted = " +
	 * JdbcLink.wrap("0") + " group by grund) as tmp";
	 * 
	 * ResultSet rs = stm.query(sql); try { rs.next(); int count =
	 * rs.getInt("cnt"); rs.close(); j.releaseStatement(stm); if (count == 1) {
	 * Stm stm2 = j.getStatement(); String sql2 =
	 * "select grund from auf where fallid = " + JdbcLink.wrap(fallID) +
	 * conditionAUF + " and deleted = " + JdbcLink.wrap("0") +
	 * " group by grund"; ResultSet rs2 = stm.query(sql2); rs2.next(); String
	 * result = rs2.getString("grund");
	 * 
	 * j.releaseStatement(stm2); return becauseOf + result;
	 * 
	 * } else { return ""; } } catch (SQLException e) { return ""; }
	 */
}
