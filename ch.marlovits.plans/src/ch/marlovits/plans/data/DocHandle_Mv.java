/*******************************************************************************
 * Copyright (c) 2006-2011, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 * 
 *  $Id$
 *******************************************************************************/

package ch.marlovits.plans.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.program.Program;
import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.Patient;
import ch.elexis.core.data.PersistentObject;
import ch.elexis.core.data.Query;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.MimeTool;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

public class DocHandle_Mv extends PersistentObject implements IOpaqueDocument {
	// +++++ START Missing in new version - but why?
	public static final String MimeTool_BINARY = "application/octet-stream";
	// +++++ END Missing in new version - but why?
	
	public static final String FLD_CAT = "Cat"; //$NON-NLS-1$
	public static final String FLD_TITLE = "Titel"; //$NON-NLS-1$
	public static final String FLD_MIMETYPE = "Mimetype"; //$NON-NLS-1$
	public static final String FLD_DOC = "Doc"; //$NON-NLS-1$
	public static final String FLD_PATH = "Path"; //$NON-NLS-1$
	public static final String FLD_KEYWORDS = "Keywords"; //$NON-NLS-1$
	public static final String FLD_PATID = "PatID"; //$NON-NLS-1$
	public static final String TABLENAME = "CH_ELEXIS_OMNIVORE_DATA"; //$NON-NLS-1$
	public static final String DBVERSION = "2.0.3"; //$NON-NLS-1$
	public static final String createDB = "CREATE TABLE " + TABLENAME + " (" //$NON-NLS-1$ //$NON-NLS-2$
		+ "ID				VARCHAR(25) primary key," + "lastupdate BIGINT," //$NON-NLS-1$ //$NON-NLS-2$
		+ "deleted        CHAR(1) default '0'," + "PatID			VARCHAR(25)," //$NON-NLS-1$ //$NON-NLS-2$
		+ "Datum			CHAR(8)," + "Category		VARCHAR(80) default null," //$NON-NLS-1$ //$NON-NLS-2$
		+ "Title 			VARCHAR(255)," + "Mimetype		VARCHAR(255)," //$NON-NLS-1$ //$NON-NLS-2$
		+ "Keywords		VARCHAR(255)," + "Path			VARCHAR(255)," //$NON-NLS-1$ //$NON-NLS-2$
		+ "Doc			BLOB);" + "CREATE INDEX OMN1 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (PatID);" + "CREATE INDEX OMN2 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (Keywords);" + "CREATE INDEX OMN3 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (Category);" + "CREATE INDEX OMN4 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (Mimetype);" + "CREATE INDEX OMN5 ON " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " (deleted);" + "CREATE INDEX OMN6 ON " + TABLENAME + " (Title);" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		+ "INSERT INTO " + TABLENAME + " (ID, TITLE) VALUES ('1','" //$NON-NLS-1$ //$NON-NLS-2$
		+ DBVERSION + "');"; //$NON-NLS-1$
	
	public static final String upd120 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " MODIFY Mimetype VARCHAR(255);" + "ALTER TABLE " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " MODIFY Keywords VARCHAR(255);" + "ALTER TABLE " + TABLENAME //$NON-NLS-1$ //$NON-NLS-2$
		+ " Modify Path VARCHAR(255);"; //$NON-NLS-1$
	
	public static final String upd200 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " ADD Category VARCHAR(80) default null;" //$NON-NLS-1$
		+ "CREATE INDEX OMN3 ON " + TABLENAME + " (Category);" //$NON-NLS-1$ //$NON-NLS-2$
		+ "ALTER TABLE " + TABLENAME + " MODIFY Title VARCHAR(255);"; //$NON-NLS-1$ //$NON-NLS-2$
	
	public static final String upd201 = "ALTER TABLE " + TABLENAME //$NON-NLS-1$
		+ " ADD lastupdate BIGINT default 0;"; //$NON-NLS-1$
	
	public static final String upd202 = "CREATE INDEX OMN4 ON " + TABLENAME //$NON-NLS-1$
		+ " (Mimetype);"; //$NON-NLS-1$
	
	public static final String upd203 = "CREATE INDEX OMN5 ON " + TABLENAME //$NON-NLS-1$
		+ " (deleted);" + "CREATE INDEX OMN6 ON " + TABLENAME + " (Title);"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	
	private static final String CATEGORY_MIMETYPE = "text/category"; //$NON-NLS-1$
	private static List<DocHandle_Mv> main_categories = null;
	
	public static final String PREFBASE = "plugins/omnivore-direct";
	public static final String STOREFS = PREFBASE + "/store_in_fs";
	public static final String BASEPATH = PREFBASE + "/basepath";
	
	static {
		addMapping(TABLENAME, FLD_PATID, "Cat=Category", DATE_COMPOUND, //$NON-NLS-1$
			"Titel=Title", FLD_KEYWORDS, FLD_PATH, FLD_DOC, FLD_MIMETYPE); //$NON-NLS-1$
		DocHandle_Mv start = load(StringConstants.ONE);
		if (start == null) {
			init();
		} else {
			VersionInfo vi = new VersionInfo(start.get(FLD_TITLE));
			if (vi.isOlder(DBVERSION)) {
				if (vi.isOlder("1.1.0")) { //$NON-NLS-1$
					getConnection().exec("ALTER TABLE " + TABLENAME //$NON-NLS-1$
						+ " ADD deleted CHAR(1) default '0';"); //$NON-NLS-1$
				}
				if (vi.isOlder("1.2.0")) { //$NON-NLS-1$
					createOrModifyTable(upd120);
				}
				if (vi.isOlder("2.0.0")) { //$NON-NLS-1$
					createOrModifyTable(upd200);
				}
				if (vi.isOlder("2.0.1")) { //$NON-NLS-1$
					createOrModifyTable(upd201);
				}
				if (vi.isOlder("2.0.2")) { //$NON-NLS-1$
					createOrModifyTable(upd202);
				}
				if (vi.isOlder("2.0.3")) { //$NON-NLS-1$
					createOrModifyTable(upd203);
				}
				start.set(FLD_TITLE, DBVERSION);
			}
		}
	}
	
	/**
	 * If force is set or the preference Preferences_2.STOREFS is true a new File object is created.
	 * Else the file is a BLOB in the db and null is returned.
	 * 
	 * The path of the new file will be: Preferences_2.BASEPATH/PatientCode/
	 * 
	 * The name of the new file will be: PersistentObjectId.FileExtension
	 * 
	 * @param force
	 *            access to the file system
	 * @return File to read from, or write to, or null
	 */
	public File getStorageFile(boolean force) throws ElexisException{
		if (force || CoreHub.localCfg.get(STOREFS, false)) {
			String pathname = CoreHub.localCfg.get(BASEPATH, null);
			// +++++ SHIT START
			pathname = pathname.replace("\\\\192.168.1.40\\Praxis\\_Daten\\ElexisDaten\\", "Y:\\");
			// +++++ SHIT START
			if (pathname != null) {
				File dir = new File(pathname);
				if (dir.isDirectory()) {
					Patient pat = Patient.load(get(FLD_PATID));
					File subdir = new File(dir, pat.getPatCode());
					if (!subdir.exists()) {
						subdir.mkdir();
					}
					File file = new File(subdir, getId() + "." //$NON-NLS-1$
						+ FileTool.getExtension(get(FLD_MIMETYPE)));
					return file;
				}
			}
			throw new ElexisException(getClass(), "Could not find file path",
				ElexisException.EE_NOT_FOUND);
		}
		return null;
	}
	
	public DocHandle_Mv(IOpaqueDocument doc) throws ElexisException{
		create(doc.getGUID());
		set(new String[] {
			FLD_CAT, FLD_PATID, FLD_DATE, FLD_TITLE, FLD_KEYWORDS, FLD_MIMETYPE
		}, doc.getCategory(), doc.getPatient().getId(), doc.getCreationDate(), doc.getTitle(),
			doc.getKeywords(), doc.getMimeType());
		store(doc.getContentsAsBytes());
		
	}
	
	@SuppressWarnings("unused")
	public static ReturnDocHandleAndCreateAufgebot assimilate(String filename, Patient act,
		DocHandle_Mv dh){
		if (act == null) {
			SWTHelper.showError(Messages.DocHandle_noPatientSelected,
				Messages.DocHandle_pleaseSelectPatient);
			return null;
		}
		
		FileImportDialogMarlovits fid = new FileImportDialogMarlovits(filename, false);
		if (fid.open() == Dialog.OK) {
			try {
				File oldStorageFile = dh.getStorageFile(false);
				// *** if data is stored in file system then write to the right location
				if (CoreHub.localCfg.get(STOREFS, false) == true) {
					// get contents of file
					byte[] doc = dh.getContents();
					// write to (new) location
					Patient newPatient = ElexisEventDispatcher.getSelectedPatient();
					dh.set(DocHandle_Mv.FLD_PATID, newPatient.getId());
					File file = dh.getStorageFile(true);
					try {
						BufferedOutputStream bout =
							new BufferedOutputStream(new FileOutputStream(file));
						bout.write(doc);
						bout.close();
					} catch (FileNotFoundException fnf) {
						ExHandler.handle(fnf);
						SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
							Messages.DocHandle_writeErrorCaption2, fnf.getMessage());
					} catch (IOException ios) {
						ExHandler.handle(ios);
						SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
							Messages.DocHandle_writeErrorCaption2, ios.getMessage());
					}
					// delete old file from disk at the old location
					oldStorageFile.delete();
				}
				dh.set(DocHandle_Mv.FLD_CAT, fid.category);
				dh.set(DocHandle_Mv.FLD_TITLE, fid.title);
				dh.set(DocHandle_Mv.FLD_KEYWORDS, fid.keywords);
				dh.set(DocHandle_Mv.FLD_DATE, fid.dateStr);
				// tmpDocHandle.set("Datum", fid.dateStr);
				ReturnDocHandleAndCreateAufgebot retVal = new ReturnDocHandleAndCreateAufgebot();
				retVal.createAufgebot = false;
				retVal.theDocHandle = dh;
				return retVal;
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(Messages.DocHandle_readErrorCaption3,
					Messages.DocHandle_readErrorText2);
				return null;
			}
		}
		return null;
	}
	
	public void replaceFile(String fileName, Patient act){
		if (act == null) {
			SWTHelper.showError("OP-Pläne", "Die Datei " + fileName + " kann nicht gelesen werden");
			return;
		}
		File file = new File(fileName);
		if (!file.canRead()) {
			SWTHelper.showError("OP-Pläne", "Die Datei " + fileName + " kann nicht gelesen werden");
			return;
		}
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int in;
			while ((in = bis.read()) != -1)
				baos.write(in);
			bis.close();
			baos.close();
			
			store(baos.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ElexisException e) {
			e.printStackTrace();
		}
	}
	
	private void store(byte[] doc) throws ElexisException{
		File file = getStorageFile(false);
		if (file == null) {
			try {
				setBinary(FLD_DOC, doc);
			} catch (Exception ex1) {
				SWTHelper.showError(Messages.DocHandle_writeErrorCaption,
					Messages.DocHandle_writeErrorText);
				delete();
			}
		} else {
			try {
				BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
				bout.write(doc);
				bout.close();
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(
					Messages.DocHandle_73,
					Messages.DocHandle_writeErrorHeading,
					MessageFormat.format(Messages.DocHandle_writeErrorText2 + ex.getMessage(),
						file.getAbsolutePath()));
				delete();
			}
		}
		
	}
	
	private void storeInDb(byte[] doc) throws ElexisException{
		File file = getStorageFile(false);
		if (file == null) {
			try {
				setBinary(FLD_DOC, doc);
			} catch (Exception ex1) {
				SWTHelper.showError(Messages.DocHandle_writeErrorCaption,
					Messages.DocHandle_writeErrorText);
				delete();
			}
		} else {
			try {
				BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
				bout.write(doc);
				bout.close();
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper.showError(
					Messages.DocHandle_73,
					Messages.DocHandle_writeErrorHeading,
					MessageFormat.format(Messages.DocHandle_writeErrorText2 + ex.getMessage(),
						file.getAbsolutePath()));
				delete();
			}
		}
		
	}
	
	public DocHandle_Mv(String category, byte[] doc, Patient pat, String title, String mime,
		String keyw) throws ElexisException{
		if ((doc == null) || (doc.length == 0)) {
			SWTHelper.showError(Messages.DocHandle_readErrorCaption,
				Messages.DocHandle_readErrorText);
			return;
		}
		create(null);
		if (category == null || category.length() == 0) {
			set(new String[] {
				FLD_PATID, FLD_DATE, FLD_TITLE, FLD_KEYWORDS, FLD_MIMETYPE
			}, pat.getId(), new TimeTool().toString(TimeTool.DATE_GER), title, keyw, mime);
		} else {
			set(new String[] {
				FLD_CAT, FLD_PATID, FLD_DATE, FLD_TITLE, FLD_KEYWORDS, FLD_MIMETYPE
			}, category, pat.getId(), new TimeTool().toString(TimeTool.DATE_GER), title, keyw, mime);
			
		}
		store(doc);
	}
	
	public static List<String> getMainCategoryNames(){
		List<DocHandle_Mv> dox = getMainCategories();
		ArrayList<String> ret = new ArrayList<String>(dox.size());
		for (DocHandle_Mv doch : dox) {
			ret.add(doch.get(FLD_TITLE));
		}
		return ret;
		
	}
	
	public static List<DocHandle_Mv> getMainCategories(){
		if (main_categories == null) {
			Query<DocHandle_Mv> qbe = new Query<DocHandle_Mv>(DocHandle_Mv.class);
			qbe.add(FLD_MIMETYPE, "=", CATEGORY_MIMETYPE);
			main_categories = qbe.execute();
		}
		return main_categories;
	}
	
	public static void addMainCategory(String name){
		DocHandle_Mv dh = new DocHandle_Mv();
		dh.create(null);
		dh.set(new String[] {
			FLD_TITLE, FLD_MIMETYPE
		}, name, CATEGORY_MIMETYPE);
		main_categories = null;
	}
	
	public static void renameCategory(String old, String newn){
		String oldname = old.trim();
		String newName = newn.trim();
		getConnection().exec(
			"update CH_ELEXIS_OMNIVORE_DATA set Category=" + JdbcLink.wrap(newName)
				+ " where Category= " + JdbcLink.wrap(oldname));
		getConnection().exec(
			"update CH_ELEXIS_OMNIVORE_DATA set Title=" + JdbcLink.wrap(newName) + " where Title="
				+ JdbcLink.wrap(oldname) + " and mimetype=" + JdbcLink.wrap("text/category"));
		main_categories = null;
	}
	
	public static void removeCategory(String name, String destName){
		getConnection().exec(
			"update CH_ELEXIS_OMNIVORE_DATA set Category=" + JdbcLink.wrap(destName)
				+ " where Category= " + JdbcLink.wrap(name));
		getConnection().exec(
			"update CH_ELEXIS_OMNIVORE_DATA set deleted='1' where Title=" + JdbcLink.wrap(name)
				+ " AND mimetype=" + JdbcLink.wrap("text/category"));
		main_categories = null;
	}
	
	public String getCategoryName(){
		return checkNull(get(FLD_CAT));
	}
	
	public boolean isCategory(){
		return get(FLD_MIMETYPE).equals(CATEGORY_MIMETYPE);
	}
	
	public DocHandle_Mv getCategoryDH(){
		String name = getCategoryName();
		if (!StringTool.isNothing(name)) {
			List<DocHandle_Mv> ret =
				new Query<DocHandle_Mv>(DocHandle_Mv.class, FLD_TITLE, name).execute();
			if (ret != null && ret.size() > 0) {
				return ret.get(0);
			}
		}
		return null;
	}
	
	public List<DocHandle_Mv> getMembers(Patient pat){
		Query<DocHandle_Mv> qbe =
			new Query<DocHandle_Mv>(DocHandle_Mv.class, FLD_CAT, get(FLD_TITLE));
		if (pat != null) {
			qbe.add(FLD_PATID, Query.EQUALS, pat.getId());
		}
		return qbe.execute();
	}
	
	/**
	 * Tabelle neu erstellen
	 */
	public static void init(){
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run(){
				createOrModifyTable(createDB);
			}
		});
	}
	
	public static DocHandle_Mv load(String id){
		DocHandle_Mv ret = new DocHandle_Mv(id);
		if (ret.exists()) {
			return ret;
		}
		return null;
	}
	
	@Override
	public String getLabel(){
		StringBuilder sb = new StringBuilder();
		sb.append(get(FLD_DATE)).append(" ").append(get(FLD_TITLE)); //$NON-NLS-2$
		return sb.toString();
	}
	
	public String getTitle(){
		return get(FLD_TITLE);
	}
	
	public String getKeywords(){
		return get(FLD_KEYWORDS);
	}
	
	public String getDate(){
		return get(FLD_DATE);
	}
	
	@Override
	public boolean delete(){
		return super.delete();
	}
	
	public byte[] getContents() throws ElexisException{
		byte[] ret = getBinary(FLD_DOC);
		if (ret == null) {
			File file = getStorageFile(true);
			if (file != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					FileInputStream fis = new FileInputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fis);
					int b;
					while ((b = bis.read()) != -1) {
						baos.write(b);
					}
					bis.close();
					baos.close();
					// if we stored the file in the file system but decided
					// later to store it in the
					// database: copy the file from the file system to the
					// database
					byte[] bytes = baos.toByteArray();
					if (!CoreHub.localCfg.get(STOREFS, false)) {
						try	{
							setBinary(FLD_DOC, bytes);
						} catch (Exception ex1)	{
							SWTHelper.showError(Messages.DocHandle_readErrorCaption,
								Messages.DocHandle_importErrorText);
						}
					}
					
					return bytes;
				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.showError(Messages.DocHandle_readErrorHeading,
						Messages.DocHandle_importError2, MessageFormat.format(
							Messages.DocHandle_importErrorText2 + ex.getMessage(),
							file.getAbsolutePath()));
				}
			}
		}
		return ret;
	}
	
	public void execute(){
		try {
			String ext = ""; //$NON-NLS-1$
			String typname = get(FLD_MIMETYPE);
			int r = typname.lastIndexOf('.');
			if (r == -1) {
				typname = get(FLD_TITLE);
				r = typname.lastIndexOf('.');
			}
			
			if (r != -1) {
				ext = typname.substring(r + 1);
			}
			File temp = File.createTempFile("omni_", "_vore." + ext); //$NON-NLS-1$ //$NON-NLS-2$
			temp.deleteOnExit();
			byte[] b = getContents();
			if (b == null) {
				SWTHelper.showError(Messages.DocHandle_readErrorCaption2,
					Messages.DocHandle_loadErrorText);
				return;
			}
			FileOutputStream fos = new FileOutputStream(temp);
			fos.write(b);
			fos.close();
			Program proggie = Program.findProgram(ext);
			if (proggie != null) {
				proggie.execute(temp.getAbsolutePath());
			} else {
				if (Program.launch(temp.getAbsolutePath()) == false) {
					Runtime.getRuntime().exec(temp.getAbsolutePath());
				}
				
			}
			
		} catch (Exception ex) {
			ExHandler.handle(ex);
			SWTHelper.showError(Messages.DocHandle_runErrorHeading, ex.getMessage());
		}
	}
	
	public String getMimetype(){
		return get(FLD_MIMETYPE);
	}
	
	@Override
	protected String getTableName(){
		return TABLENAME;
	}
	
	protected DocHandle_Mv(String id){
		super(id);
	}
	
	protected DocHandle_Mv(){}
	
	public boolean storeExternal(String filename) throws ElexisException{
		byte[] b = getContents();
		if (b == null) {
			SWTHelper.showError(Messages.DocHandle_readErrorCaption2,
				Messages.DocHandle_couldNotLoadError);
			return false;
		}
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(b);
			fos.close();
			return true;
		} catch (IOException ios) {
			ExHandler.handle(ios);
			SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
				Messages.DocHandle_writeErrorCaption2, ios.getMessage());
			return false;
		}
	}
	
	// +++++ ADD START
	
	private void configError(){
		SWTHelper.showError("config error", Messages.DocHandle_configErrorCaption, //$NON-NLS-1$
			Messages.DocHandle_configErrorText);
	}
	
	// IDocument
	@Override
	public String getCategory(){
		return getCategoryName();
	}
	
	@Override
	public String getMimeType(){
		String mime = checkNull(get(FLD_MIMETYPE));
		if (MimeTool.getExtension(mime).length() == 0) {
			String ext = FileTool.getExtension(mime);
			String m = MimeTool.getMimeType(ext);
			if (!m.equals(MimeTool_BINARY)) {
				return m;
			}
		}
		return mime;
	}
	
	@Override
	public String getCreationDate(){
		return get("Datum");
	}
	
	public Patient getPatient(){
		return Patient.load(get(FLD_PATID));
	}
	
// @Override
// public String getPatientID(){
// return get(FLD_PATID);
// }
//
	@Override
	public InputStream getContentsAsStream() throws ElexisException{
		return new ByteArrayInputStream(getContents());
	}
	
	@Override
	public byte[] getContentsAsBytes() throws ElexisException{
		return getContents();
	}
	
	@Override
	public String getGUID(){
		return getId();
	}
	
	/**
	 * Move the DocHandle from the db to the file system and delete the BLOB afterwards.
	 * 
	 * @throws ElexisException
	 */
	public boolean exportToFileSystem() throws ElexisException{
		byte[] doc = getBinary(FLD_DOC);
		// return true if doc is already on file system
		if (doc == null)
			return true;
		File file = getStorageFile(true);
		try {
			BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
			
			bout.write(doc);
			bout.close();
			setBinary(FLD_DOC, null);
		} catch (FileNotFoundException fnf) {
			ExHandler.handle(fnf);
			SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
				Messages.DocHandle_writeErrorCaption2, fnf.getMessage());
			return false;
		} catch (IOException ios) {
			ExHandler.handle(ios);
			SWTHelper.showError(Messages.DocHandle_writeErrorCaption2,
				Messages.DocHandle_writeErrorCaption2, ios.getMessage());
			return false;
		}
		return true;
	}
	
}
