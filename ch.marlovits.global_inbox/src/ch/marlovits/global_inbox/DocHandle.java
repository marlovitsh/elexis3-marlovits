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

package ch.marlovits.global_inbox;

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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.program.Program;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.text.IOpaqueDocument;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.SWTHelper;
//import ch.elexis.UiDesk;
//import ch.elexis.ElexisException;
//import ch.elexis.StringConstants;
//import ch.elexis.actions.ElexisEventDispatcher;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Query;
import ch.marlovits.global_inbox.DocHandle;
import ch.marlovits.global_inbox.FileImportDialogMarlovits;
import ch.marlovits.global_inbox.Preferences_2;
import ch.marlovits.global_inbox.ReturnDocHandleAndCreateAufgebot;
import ch.marlovits.plans.data.Messages;
//import ch.elexis.text.IOpaqueDocument;
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.JdbcLink;
import ch.rgw.tools.MimeTool;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import ch.rgw.tools.VersionInfo;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

public class DocHandle extends PersistentObject implements IOpaqueDocument {
	// +++++ START Missing in new version - but why?
	public static final String MimeTool_BINARY = "application/octet-stream";
	public static final int EE_CONFIGURATION_ERROR = 8;
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
	private static List<DocHandle> main_categories = null;
	
	static {
		addMapping(TABLENAME, FLD_PATID, "Cat=Category", DATE_COMPOUND, //$NON-NLS-1$
			"Titel=Title", FLD_KEYWORDS, FLD_PATH, FLD_DOC, FLD_MIMETYPE); //$NON-NLS-1$
		DocHandle start = load(StringConstants.ONE);
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
		if (force || CoreHub.localCfg.get(Preferences_2.STOREFS, false)) {
			String pathname = CoreHub.localCfg.get(Preferences_2.BASEPATH, null);
			// +++++ SHIT START
			// pathname = pathname.replace("Z:\\_Daten\\ElexisDaten\\", "Y:\\");
			// +++++ SHIT END
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
			// +++++ START Missing in new version - but why?
				EE_CONFIGURATION_ERROR);
			// +++++ END Missing in new version - but why?
		}
		return null;
	}
	
	public DocHandle(IOpaqueDocument doc) throws ElexisException{
		create(doc.getGUID());
		set(new String[] {
			FLD_CAT, FLD_PATID, FLD_DATE, FLD_TITLE, FLD_KEYWORDS, FLD_MIMETYPE
		}, doc.getCategory(), doc.getPatient().getId(), doc.getCreationDate(), doc.getTitle(),
			doc.getKeywords(), doc.getMimeType());
		store(doc.getContentsAsBytes());
		
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
	
	public DocHandle(String category, byte[] doc, Patient pat, String title, String mime,
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
		List<DocHandle> dox = getMainCategories();
		ArrayList<String> ret = new ArrayList<String>(dox.size());
		for (DocHandle doch : dox) {
			ret.add(doch.get(FLD_TITLE));
		}
		return ret;
		
	}
	
	public static List<DocHandle> getMainCategories(){
		if (main_categories == null) {
			Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class);
			qbe.add(FLD_MIMETYPE, "=", CATEGORY_MIMETYPE);
			main_categories = qbe.execute();
		}
		return main_categories;
	}
	
	public static void addMainCategory(String name){
		DocHandle dh = new DocHandle();
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
	
	public DocHandle getCategoryDH(){
		String name = getCategoryName();
		if (!StringTool.isNothing(name)) {
			List<DocHandle> ret = new Query<DocHandle>(DocHandle.class, FLD_TITLE, name).execute();
			if (ret != null && ret.size() > 0) {
				return ret.get(0);
			}
		}
		return null;
	}
	
	public List<DocHandle> getMembers(Patient pat){
		Query<DocHandle> qbe = new Query<DocHandle>(DocHandle.class, FLD_CAT, get(FLD_TITLE));
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
	
	public static DocHandle load(String id){
		DocHandle ret = new DocHandle(id);
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
					if (!CoreHub.localCfg.get(Preferences_2.STOREFS, false)) {
						try {
							setBinary(FLD_DOC, bytes);
						} catch (Exception ex1) {
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
	
	protected DocHandle(String id){
		super(id);
	}
	
	protected DocHandle(){}
	
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
	
	public static void assimilate(List<ImageData> images, Boolean createAufgebot){
		FileImportDialogMarlovits fid =
			new FileImportDialogMarlovits(Messages.DocHandle_scannedImageDialogCaption,
				createAufgebot);
		if (fid.open() == Dialog.OK) {
			try {
				
				Document pdf = new Document(PageSize.A4);
				pdf.setMargins(0, 0, 0, 0);
				ByteArrayOutputStream baos = new ByteArrayOutputStream(100000);
				PdfWriter.getInstance(pdf, baos);
				pdf.open();
				ImageLoader il = new ImageLoader();
				for (int i = 0; i < images.size(); i++) {
					ImageData[] id = new ImageData[] {
						images.get(i)
					};
					il.data = id;
					ByteArrayOutputStream bimg = new ByteArrayOutputStream();
					il.save(bimg, SWT.IMAGE_PNG);
					Image image = Image.getInstance(bimg.toByteArray());
					int width = id[0].width;
					int height = id[0].height;
					// 210mm = 8.27 In = 595 px bei 72dpi
					// 297mm = 11.69 In = 841 px
					if ((width > 595) || (height > 841)) {
						image.scaleToFit(595, 841);
					}
					pdf.add(image);
				}
				
				pdf.close();
				new DocHandle(fid.category, baos.toByteArray(),
					ElexisEventDispatcher.getSelectedPatient(), fid.title,
					"image.pdf", fid.keywords); //$NON-NLS-1$
				
				/*
				 * ImageLoader il=new ImageLoader(); ImageData[] id=new ImageData[]{images.get(0)};
				 * il.data=id; ByteArrayOutputStream bimg=new ByteArrayOutputStream(); il.save(bimg,
				 * SWT.IMAGE_PNG); new DocHandle(fid.category,bimg.toByteArray(),
				 * GlobalEvents.getSelectedPatient (),fid.title,"image.pdf",fid.keywords);
				 */
			} catch (Exception ex) {
				ExHandler.handle(ex);
				SWTHelper
					.showError(Messages.DocHandle_readError, Messages.DocHandle_readErrorText2);
			}
		}
		
	}
	
	// +++++ ADD START
	public static ReturnDocHandleAndCreateAufgebot assimilate(String f, Boolean createAufgebot){
		// public static boolean assimilate(String f){
		// +++++ ADD END
		Patient act = ElexisEventDispatcher.getSelectedPatient();
		if (act == null) {
			SWTHelper.showError(Messages.DocHandle_noPatientSelected,
				Messages.DocHandle_pleaseSelectPatient);
			return null;
		}
		File file = new File(f);
		if (!file.canRead()) {
			SWTHelper.showError(Messages.DocHandle_cantReadCaption,
				MessageFormat.format(Messages.DocHandle_cantReadText, f));
			return null;
		}
		FileImportDialogMarlovits fid =
			new FileImportDialogMarlovits(file.getName(), createAufgebot);
		if (fid.open() == Dialog.OK) {
			try {
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// Thread.sleep(1000);
				int in;
				while ((in = bis.read()) != -1) {
					baos.write(in);
				}
				bis.close();
				baos.close();
				String nam = file.getName();
				if (nam.length() > 255) {
					SWTHelper.showError(Messages.DocHandle_readErrorCaption3,
						Messages.DocHandle_fileNameTooLong);
					return null;
				}
				DocHandle tmpDocHandle =
					new DocHandle(fid.category, baos.toByteArray(), act, fid.title, file.getName(),
						fid.keywords);
				tmpDocHandle.set("Datum", fid.dateStr);
				ReturnDocHandleAndCreateAufgebot retVal = new ReturnDocHandleAndCreateAufgebot();
				retVal.createAufgebot = fid.createAufgebot;
				retVal.theDocHandle = tmpDocHandle;
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
			if (!m.equals(MimeTool_BINARY)) { // +++++
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
