/*******************************************************************************
 * Copyright (c) 2007-2009, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Harald Marlovits - initial implementation
 *    
 *    $Id: MarlovitsIDD9.java 6422 2010-06-11 11:34:21Z marlovitsh $
 *******************************************************************************/
package ch.marlovits.importer.aeskulap.diagnosen.data;

//ch.marlovits.importer.aeskulap.diagnosen.data.MarlovitsCodes

import java.util.List;

import org.eclipse.jface.action.IAction;

import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Verrechnet;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.VersionInfo;

public class MarlovitsCodes extends PersistentObject implements IDiagnose {
	static final String VERSION = "1.0";
	static final String TABLENAME = "CH_MARLOVITS_AESKULAP_CODES";
	public static final String CODESYSTEM_NAME = "Aeskulap Diagnosen";
	private static final String createDB = "CREATE TABLE "
			+ TABLENAME
			+ "("
			+ "ID           VARCHAR(25) primary key," // must always be present
			+ "lastupdate   BIGINT," // must always be present
			+ "deleted      CHAR(1) default '0'," // must always be present
			+ "parent       VARCHAR(20),"
			+ "code			VARCHAR(20),"
			+ "title        TEXT,"
			+ "comment      TEXT,"
			+ "bez_druck    VARCHAR(80),"
			+ "kurzbez      VARCHAR(80),"
			+ "canprint     CHAR(1) default '1',"
			+ "ExtInfo      BLOB);"
			+ "CREATE INDEX " + TABLENAME + "_idx1 on " + TABLENAME
			+ "(parent,code);" + "INSERT INTO " + TABLENAME
			+ " (ID,title) VALUES ('VERSION','" + VERSION + "');";

	/**
	 * Here we define the mapping between internal fieldnames and database
	 * fieldnames. (@see PersistentObject) then we try to load a version
	 * element. If this does not exist, we create the table. If it exists, we
	 * check the version
	 */
	static {
		addMapping(TABLENAME, "parent", "Text=title", "Kuerzel=code",
				"Kommentar=comment", "ExtInfo");
		MarlovitsCodes check = load("VERSION");
		if (check.state() < PersistentObject.DELETED) { // Object never existed,
														// so we have to
			// create the database
			initialize();
		} else { // found existing table, check version
//			VersionInfo v = new VersionInfo(check.get("Text"));
//			if (v.isOlder("1.2")) {
//				createOrModifyTable("ALTER TABLE " + TABLENAME
//						+ " MODIFY title TEXT;");
//				check.set("Text", VERSION);
//			}
		}

	}

	public MarlovitsCodes(String parent, String code, String text, String comment) {
		create(null);
		set(new String[] { "parent", "Kuerzel", "Text", "Kommentar" },
				new String[] { parent == null ? "NIL" : parent, code, text,
						comment });
	}

	public static void initialize() {
		createOrModifyTable(createDB);
	}

	@Override
	public String getLabel() {
		return get("Kuerzel") + " " + get("Text");
	}

	@Override
	protected String getTableName() {
		return TABLENAME;
	}

	public static MarlovitsCodes load(String id) {
		return new MarlovitsCodes(id);
	}

	protected MarlovitsCodes(String id) {
		super(id);
	}

	protected MarlovitsCodes() {
	}

	public List<IAction> getActions(Verrechnet kontext) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCode() {
		return getId();
	}

	public String getCodeSystemCode() {
		return "ED";
	}

	public String getCodeSystemName() {
		return CODESYSTEM_NAME;
	}

	public String getText() {
		return get("Text");
	}

	@Override
	public boolean isDragOK() {
		return !hasChildren();
	}

	public boolean hasChildren() {
		JdbcLink link = PersistentObject.getConnection();
		String theText = get("Text");
		int numOfChildren = link.queryInt("select count(*) from " + TABLENAME
				+ " where deleted = 0 and parent = " + JdbcLink.wrap(theText));
		if (numOfChildren > 0) {
			return true;
		}
		return false;
	}

	@Override
	public List<Object> getActions(Object context){
		// TODO Auto-generated method stub
		return null;
	}
}
