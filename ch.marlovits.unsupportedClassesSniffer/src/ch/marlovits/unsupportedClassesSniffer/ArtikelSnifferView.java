package ch.marlovits.unsupportedClassesSniffer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.data.PersistentObject;
import ch.elexis.data.PersistentObjectFactory;
import ch.elexis.core.data.constants.ExtensionPointConstantsData;
import ch.elexis.core.data.util.Extensions;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.JdbcLink.Stm;

public class ArtikelSnifferView extends ViewPart {
	public static final String ID = "ch.marlovits.unsupportedClassesSniffer.ArtikelSnifferView";

	public static Text snifferList;
	public static Text unresolvedClasses;
	public static Text snifferErrorsList;

	public ArtikelSnifferView() {
	}

	static String getSearchQuery() {
		JdbcLink j = PersistentObject.getConnection();
		String dbFlavor = j.DBFlavor;

		// *** base query
		String sql = "";
		// *** read chunk# of rows
		if (dbFlavor.equalsIgnoreCase("postgresql")) {
			sql = "select substring(artikel, 0, position('::' in artikel))  from PATIENT_ARTIKEL_JOINT group by substring(artikel, 0, position('::' in artikel))";
		} else if (dbFlavor.equalsIgnoreCase("mysql")) {
			sql = "select substring(artikel, 0, position('::' in artikel))  from PATIENT_ARTIKEL_JOINT group by substring(artikel, 0, position('::' in artikel))";
		} else if (dbFlavor.equalsIgnoreCase("h2")) {
			sql = "select substring(artikel, 0, LOCATE('::', artikel))  from PATIENT_ARTIKEL_JOINT group by substring(artikel, 0, LOCATE('::', artikel))";
		}
		return sql;
	}

	// working for both postgres AND MYSQL
	// select substring(artikel, 0, position('::' in artikel)) from
	// PATIENT_ARTIKEL_JOINT
	// group by substring(artikel, 0, position('::' in artikel))
	// for h2 ???
	// select substring(artikel, 0, LOCATE('::', artikel)) from
	// PATIENT_ARTIKEL_JOINT
	// group by substring(artikel, 0, LOCATE('::', artikel))

	public void createPartControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		comp.setLayout(gl);
		LayoutData layoutData = new LayoutData();
		layoutData.verticalAlignment = SWT.TOP;
		comp.setLayoutData(layoutData);

		GridData gridData = new GridData();
		gridData.widthHint = 500;
		gridData.heightHint = 150;
		gridData.grabExcessHorizontalSpace = true;
		// ***********
		Button btnSearchAllArtikelClasses = new Button(comp, SWT.PUSH);
		btnSearchAllArtikelClasses
				.setText("Search for all artikel classes now");
		btnSearchAllArtikelClasses
				.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						Stm stm = PersistentObject.getConnection()
								.getStatement();
						ResultSet res = stm.query(getSearchQuery());
						String result = "";
						if (res != null) {
							try {
								while (res.next()) {
									String resultPart = res.getString(1);
									if (resultPart != null) {
										result = result + resultPart + "\n";
										if (!classExists(resultPart)) {
											result = result
													+ "   *** not found ***\n";
										}
									}
								}
							} catch (SQLException e1) {
								e1.printStackTrace();
							}
						}
						snifferList.setText(result);
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

		snifferList = new Text(comp, SWT.BORDER + SWT.MULTI);
		snifferList.setLayoutData(gridData);

		// ***********
		Button btnSearchForUnresolvedClasses = new Button(comp, SWT.PUSH);
		btnSearchForUnresolvedClasses
				.setText("Search for unresolved classes now");
		btnSearchForUnresolvedClasses
				.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						unresolvedClasses.setText("");
						snifferErrorsList.setText("");
						HashSet<String> foundClasses = ExtInfoSniffer
								.searchAllTablesForUnresolvedClasses(snifferList
										.getText());
						String result = "";
						Iterator<String> iterator = foundClasses.iterator();
						while (iterator.hasNext())
							result = result + iterator.next() + "\n";
						if (result.isEmpty())
							unresolvedClasses
									.setText("no unresolved classes found");
						else
							unresolvedClasses
									.setText("*** unresolved classes found ***\n"
											+ result);
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

		unresolvedClasses = new Text(comp, SWT.BORDER + SWT.MULTI);
		unresolvedClasses.setLayoutData(gridData);

		// ***********
		Label lbl2 = new Label(comp, SWT.None);
		lbl2.setText("Errors on undef classes sniffing");

		snifferErrorsList = new Text(comp, SWT.BORDER + SWT.MULTI);
		snifferErrorsList.setLayoutData(gridData);
	}

	// *****************************************************************
	// *** my class tester
	// *****************************************************************
	@SuppressWarnings("rawtypes")
	public static boolean classExists(String className) {
		Class clazz = createTemplate(className);
		return (clazz != null);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Class createTemplate(String className) {
		try {
			return Class.forName(className, false, null);
		} catch (ClassNotFoundException ex) {
			List<PersistentObjectFactory> exts = Extensions.getClasses(
					ExtensionPointConstantsData.PERSISTENT_REFERENCE, "Class");
			for (PersistentObjectFactory po : exts) {
				// Object template = CoreHub.poFactory
				// .createTemplate(Class.className);
				PersistentObject tmop = po.createFromString(className + "::1234");
				Class clazz = po.getClassforName(className);
				if (clazz != null) {
					return clazz;
				}
			}
		}
		return null;
	}

	@Override
	public void setFocus() {
	}

}