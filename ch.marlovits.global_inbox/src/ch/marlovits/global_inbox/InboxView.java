/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.marlovits.global_inbox;

import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.hwpf.model.PAPBinTable;
import org.apache.poi.hwpf.model.PAPX;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/*import ag.ion.bion.workbench.office.editor.core.EditorCorePlugin;*/
import ag.ion.noa4e.ui.NOAUIPlugin;
import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.contacts.dialogs.PatientErfassenDialog;
import ch.elexis.core.ui.dialogs.ChoiceDialog;
import ch.elexis.core.ui.dialogs.KontaktSelektor;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.preferences.TextTemplatePreferences;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.views.TextView;
import ch.elexis.data.Brief;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Person;
import ch.elexis.data.Query;
import ch.marlovits.global_inbox.Activator;
import ch.marlovits.global_inbox.DocHandle;
import ch.marlovits.global_inbox.FileImportDialogMarlovits;
import ch.marlovits.global_inbox.InboxContentProvider;
import ch.marlovits.global_inbox.InboxLabelProvider;
import ch.marlovits.global_inbox.Messages;
import ch.marlovits.global_inbox.ReturnDocHandleAndCreateAufgebot;
import ch.marlovits.importDocsFromFaxEmail.ImportDocumentsFromFaxEmail;
import ch.marlovits.plans.data.DocHandle_Mv;
/*import ch.elexis.noa.NOAText_jsl.closeListener;*/
import ch.rgw.io.FileTool;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import com.lowagie.text.Anchor;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

import ch.marlovits.plans.views.PlansView;
import ch.marlovits.plans.views.PlansView.itemIxs;

public class InboxView extends ViewPart {
	static public boolean showTrash = false;
	
	public static String[] dontShowTheseFiles = {
		".file_info", "Thumbs.db"
	};
	public static String[] dontShowTheseFilesStartings = {
		"ImportFromEmail"
	};
	
	static Font catFont = new Font(Font.TIMES_ROMAN, 18, Font.BOLD);
	Font redFont = new Font(Font.TIMES_ROMAN, 12, Font.NORMAL, new Color(255, 0, 0));
	static Font subFont = new Font(Font.TIMES_ROMAN, 16, Font.BOLD);
	Font smallBold = new Font(Font.TIMES_ROMAN, 12, Font.BOLD);
	
	OpenOfficeConnection OOconnection = null;
	
	private static TableViewer tv;
	private IAction addAction, deleteAction, execAction, reloadAction,
			createPatientAddDocumentCreateAufgebot, rotate180Action, rotate90Action, splitAction,
			variableAction, testerAction, actionHistologieImporter, actionAddAdvanced;
	Composite parent2;
	static String IMAGEMAGICK_CONVERT_PATH =
		"C:\\Program Files\\ImageMagick-6.7.3-Q16\\convert.exe";
	static String GHOSTSCRIPT_PATH = "C:\\Program Files\\Bullzip\\PDF Printer\\gs\\gswin32c.exe";
	
	// static String TRASHPATH = "Y:/Eingangsfach/Papierkorb";
	static String TRASHPATH = "Z:/Praxis/PatientenDaten/ElexisDaten/ElexisEingangsfach/Papierkorb";
	
	String[] columnHeaders = new String[] {
		Messages.InboxView_category, Messages.InboxView_title, "Datum", "Patient", "geb."
	};
	TableColumn[] tc;
	private Browser browserViewer;
	Table table;
	static ChoiceDialog synchCD = null;
	static KontaktSelektor synchKS = null;
	// savedMatches contains user matches
	static HashMap<String, Patient> savedMatches = new HashMap<String, Patient>();
	
	HashMap<String, String> histoTextParts = new HashMap<String, String>();
	
	public InboxView(){
		// TODO Auto-generated constructor stub
	}
	
	String currSelFilePath = "";
	
	@Override
	public void createPartControl(Composite parent){
		if (Activator.getDefault().getInboxDir() == null) {
			Label l = new Label(parent, SWT.NONE);
			l.setText("Es ist kein Eingangsverzeichnis definiert");
		} else {
			SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
			sash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
			
			table = new Table(sash/* +++++parent */, SWT.FULL_SELECTION);
			tv = new TableViewer(table);
			tc = new TableColumn[columnHeaders.length];
			for (int i = 0; i < tc.length; i++) {
				tc[i] = new TableColumn(table, SWT.NONE);
				tc[i].setText(columnHeaders[i]);
				tc[i].setWidth(100);
			}
			table.setHeaderVisible(true);
			table.setLinesVisible(true);
			InboxContentProvider cp = Activator.getDefault().getContentProvider();
			makeActions();
			tv.setContentProvider(cp);
			tv.setLabelProvider(new InboxLabelProvider());
			tv.setSorter(new ViewerSorter() {
				@Override
				public int compare(Viewer viewer, Object e1, Object e2){
					File f1 = (File) e1;
					File f2 = (File) e2;
					return f1.getAbsolutePath().compareTo(f2.getAbsolutePath());
				}
			});
			tv.addSelectionChangedListener(new ISelectionChangedListener() {
				
				@Override
				public void selectionChanged(SelectionChangedEvent event){
					IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
					// *** enable/disable items
					addAction.setEnabled(!sel.isEmpty());
					// +++++createPatientAddDocumentCreateAufgebot.setEnabled(!sel.isEmpty());
					rotate90Action.setEnabled(!sel.isEmpty());
					rotate180Action.setEnabled(!sel.isEmpty());
					splitAction.setEnabled(!sel.isEmpty());
					deleteAction.setEnabled(!sel.isEmpty());
					// *** display pdf in panel
					if (!sel.isEmpty()) {
						File selFile = (File) (sel.getFirstElement());
						if (selFile.exists()) {
							String selFilePath = selFile.getPath();
							if (!currSelFilePath.equals(selFilePath)) {
								String selFileName = selFile.getName();
								int suffixPos = selFileName.lastIndexOf(".");
								String selFileNameBase = selFileName.substring(0, suffixPos);
								String selFileNameSuffix = selFileName.substring(suffixPos);
								File tempDir = CoreHub.getTempDir();
								if ((selFileNameSuffix.equalsIgnoreCase(".doc"))
									|| (selFileNameSuffix.equalsIgnoreCase(".docx"))) {
									String destPath =
										tempDir + File.separator
											+ selFile.getName().replace(selFileNameSuffix, ".pdf");
									destPath = convertToPdf(selFile.getPath(), destPath);
									// +++++++++++++ overlay histo START
									if (1 == 0) {
										String overlayImage =
											"Z:\\_Daten\\ElexisDaten\\Eingangsfach\\Histologiebefunde\\image.pdf";
										String overlayCommand =
											"\""
												+ IMAGEMAGICK_CONVERT_PATH
												+ "\" \""
												+ destPath
												+ "\" \""
												+ overlayImage
												+ "\" -gravity center -composite -format pdf -quality 90 \""
												+ destPath + "\"";
										
										String line2 = null;
										Process pr2;
										try {
											Runtime rt = Runtime.getRuntime();
											pr2 = rt.exec(overlayCommand);
// BufferedReader input1 =
// new BufferedReader(new InputStreamReader(pr2
// .getInputStream()));
// line2 = input1.readLine();
// while (line2 != null) {
// System.out.println(line2);
// line2 = input1.readLine();
// }
										} catch (IOException e) {
											e.printStackTrace();
										}
									}
									// +++++++++++++ overlay histo END
									
									browserViewer.setUrl(destPath);
								} else if (selFileNameSuffix.equalsIgnoreCase(".pdf")) {
									String newUrl = tempDir + File.separator + selFileName;
									File f = new File(newUrl);
									int i = 0;
									while (f.exists()) {
										newUrl =
											tempDir + File.separator + selFileNameBase + i
												+ selFileNameSuffix;
										f = new File(newUrl);
										i++;
									}
									
									copyfile(((File) (sel.getFirstElement())).getPath(), newUrl);
									browserViewer.setUrl(newUrl);
								} else {
									String newUrl = tempDir + File.separator + selFileName;
									File f = new File(newUrl);
									int i = 0;
									while (f.exists()) {
										newUrl =
											tempDir + File.separator + selFileNameBase + i
												+ selFileNameSuffix;
										f = new File(newUrl);
										i++;
									}
									
									copyfile(((File) (sel.getFirstElement())).getPath(), newUrl);
									browserViewer.setUrl(newUrl);
								}
								
								tv.getTable().setFocus();
								currSelFilePath = selFilePath;
							}
						} else {
							SWTHelper
								.alert("Datei gelöscht",
									"Die Datei kann nicht geöffnet werden, da sie nicht mehr im Eingangsordner ist.");
						}
					}
				}
			});
			cp.setView(this);
			tv.setInput(this);
			final MenuManager mgr = new MenuManager();
			mgr.addMenuListener(new IMenuListener() {
				
				@Override
				public void menuAboutToShow(IMenuManager manager){
					IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
					Patient pat = ElexisEventDispatcher.getSelectedPatient();
					
					String patStr = "Zuordnen an:";
					if (pat != null)
						patStr = "Zuordnen an: " + pat.getPersonalia();
					addAction.setToolTipText(patStr);
					addAction.setText(patStr);
					// *** enable/disable items
					addAction.setEnabled((pat != null) && (!sel.isEmpty()));
					mgr.add(addAction);
					
					TableItem selTableItem = table.getSelection()[0];
					String patient = selTableItem.getText(3) + ", " + selTableItem.getText(4);
					if (!selTableItem.getText(3).isEmpty()) {
						actionAddAdvanced.setToolTipText("Zuordnen an " + patient);
						actionAddAdvanced.setText("Zuordnen an " + patient);
						mgr.add(actionAddAdvanced);
					}
					
					// +++++mgr.add(createPatientAddDocumentCreateAufgebot);
					// +++++createPatientAddDocumentCreateAufgebot.setEnabled(!sel.isEmpty());
					
					mgr.add(new Separator());
					
					for (int menuIx = 0; menuIx < PlansView.tabAndColNames.length; menuIx++) {
						final int tmpIx = menuIx;
						final String catName =
							PlansView.tabAndColNames[menuIx][itemIxs.CATNAME.ordinal()];
						final String actionName = "Verschieben zu " + catName + "...";
						
						variableAction = new Action(actionName) {
							{
								setToolTipText(actionName);
								setImageDescriptor(Images.IMG_NEXT.getImageDescriptor());
							}
							
							@Override
							public void run(){
								Patient pat = Patient.load("x5bb881673cc7d9330120"); // 8422, ZZZ
// OPPlan
// SWTHelper.alert("", "asdf");
// if (pat == null) {
//
// }
								String docID = "";
								File file = null;
								IDocumentManager dm =
									(IDocumentManager) Extensions
										.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
								try {
									file = getSelection();
									GenericDocument fd =
										new GenericDocument(pat, file.getName(), null, file,
											new TimeTool().toString(TimeTool.DATE_GER), "", null);
									docID = dm.addDocument(fd);
									if ((docID != null) && (!docID.isEmpty())) {}
								} catch (Exception ex) {
									ExHandler.handle(ex);
									SWTHelper.alert(Messages.InboxView_error, ex.getMessage());
								}
								DocHandle_Mv dh = DocHandle_Mv.load(docID);
								boolean successful =
									PlansView.moveDocument(parent2.getShell(), dh, tmpIx);
								if (successful) {
									if (file.delete()) {
										Activator.getDefault().getContentProvider().reload();
										// dm.removeDocument(docID);
									}
								} else {}
							}
						};
						mgr.add(variableAction);
						variableAction.setEnabled(!sel.isEmpty());
					}
					
					mgr.add(new Separator());
					
					mgr.add(rotate180Action);
					rotate180Action.setEnabled(!sel.isEmpty());
					mgr.add(rotate90Action);
					rotate90Action.setEnabled(!sel.isEmpty());
					mgr.add(splitAction);
					splitAction.setEnabled(!sel.isEmpty());
					
					mgr.add(new Separator());
					
					mgr.add(deleteAction);
					deleteAction.setEnabled(!sel.isEmpty());
					
					mgr.add(new Separator());
					mgr.add(actionHistologieImporter);
				}
			});
			mgr.setRemoveAllWhenShown(true);
			table.setMenu(mgr.createContextMenu(table));
			ViewMenus menus = new ViewMenus(getViewSite());
			menus.createToolbar(addAction, execAction, reloadAction, null, deleteAction,
				testerAction);
			addAction.setEnabled(false);
			deleteAction.setEnabled(false);
			execAction.setEnabled(false);
			// +++++ ADD START
			browserViewer = new Browser(sash/* +++++parent */, SWT.NONE);
			parent2 = parent;
			// +++++ ADD END
		}
		
		// set the column order and widths to the saved values from the prefs
		// +++++ new TableSavingSupport(tv, true, true, false, true);
	}
	
	/*
	 * private void createMe(){ IOfficeApplication office;
	 * 
	 * 
	 * System.out.println("NOAText_jsl: createMe");
	 * 
	 * if (office == null) { System.out.println("NOAText_jsl: Please note: createMe: office==null");
	 * office = EditorCorePlugin.getDefault().getManagedLocalOfficeApplication(); }
	 * 
	 * if (office == null)
	 * System.out.println("NOAText_jsl: createMe: WARNING: still, office==null"); else
	 * System.out.println("NOAText_jsl: createMe: office="+office.toString());
	 * 
	 * if (panel == null) System.out.println("NOAText_jsl: createMe: WARNING: panel==null"); else
	 * System.out.println("NOAText_jsl: createMe: panel="+panel.toString());
	 * 
	 * doc = (ITextDocument) panel.getDocument();
	 * 
	 * if (doc == null) System.out.println("NOAText_jsl: createMe: WARNING: doc==null"); else
	 * System.out.println("NOAText_jsl: createMe: doc="+doc.toString());
	 * 
	 * if (doc != null) {
	 * 
	 * System.out.println("NOAText_jsl: createMe: doc.addCloseListener()...");
	 * 
	 * doc.addCloseListener(new closeListener(office));
	 * 
	 * System.out.println("NOAText_jsl: createMe: noas.add(this)...");
	 * 
	 * noas.add(this); } System.out.println("NOAText_jsl: createMe ends"); }
	 */
	private static void copyfile(String srFile, String dtFile){
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);
			
			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);
			
			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);
			
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.out.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void dispose(){
		Activator.getDefault().getContentProvider().setView(null);
		super.dispose();
	}
	
	@Override
	public void setFocus(){
		// TODO Auto-generated method stub
		
	}
	
	public void reload(){
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run(){
				tv.refresh();
			}
		});
	}
	
	public static File getSelection(){
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		if (sel.isEmpty()) {
			return null;
		}
		return (File) sel.getFirstElement();
	}
	
	/**
	 * 
	 * @param inFilePath
	 *            source file path
	 * @param outFilePath_
	 *            dest file path with suffix for wanted file type
	 * @return outFilePath of dest file - may be changed from supplied outFilePath parameter
	 */
	String convertToPdf(String inFilePath, String outFilePath){
		try {
			if (inFilePath == null)
				return null;
			File inFile = new File(inFilePath);
			if (!inFile.exists())
				return null;
			
			// *** start oo as background service
			if (OOconnection == null) {
				// *** get office location
				IPreferenceStore preferenceStore = NOAUIPlugin.getDefault().getPreferenceStore();
				String ooPath = preferenceStore.getString(NOAUIPlugin.PREFERENCE_OFFICE_HOME);
				// start open/libre office as a background service
				String ooBackgroundServiceStarter =
					"\""
						+ ooPath
						+ File.separator
						+ "program"
						+ File.separator
						+ "soffice\" -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard";
				String line2 = null;
				Process pr2;
				try {
					Runtime rt = Runtime.getRuntime();
					pr2 = rt.exec(ooBackgroundServiceStarter);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// wait until we've got a connection or until timeout
				long timeoutMs = 10000;
				long timeoutExpiredMs = System.currentTimeMillis() + timeoutMs;
				while (OOconnection == null) {
					OOconnection = new SocketOpenOfficeConnection(8100);
					if (System.currentTimeMillis() >= timeoutExpiredMs)
						break;
				}
				OOconnection.connect();
			}
			
			// *** can't connect to oo
			if (OOconnection == null)
				return null;
			
			// *** test if outFile already exists - if so create a unique file
			File outFile = new File(outFilePath);
			int i = 0;
			int suffixPos = outFilePath.lastIndexOf(".");
			String leftPart = outFilePath.substring(0, suffixPos);
			String suffixPart = outFilePath.substring(suffixPos);
			while (outFile.exists()) {
				outFilePath = leftPart + "_" + i + suffixPart;
				outFile = new File(outFilePath);
				i = i + 1;
			}
			
			// do the conversion
			DocumentConverter pdfConverter = new OpenOfficeDocumentConverter(OOconnection);
			pdfConverter.convert(inFile, outFile);
			
			// *** no outFile means conversion failed
			if (!outFile.exists())
				return null;
			
			// *** keep connection
			// OOconnection.disconnect();
		} catch (ConnectException e) {
			e.printStackTrace();
			return null;
		}
		return outFilePath;
	}
	
	/**
	 * make file name unique by adding a number to the end
	 */
	static String makeUniqueFile(String destPath, String fileName){
		// extract suffix from fileName (suffix WITH the dot)
		String filenameSuffix = "";
		int dotLoc = fileName.lastIndexOf(".");
		if (dotLoc >= 0) {
			filenameSuffix = fileName.substring(dotLoc);
			fileName = fileName.substring(0, dotLoc);
		}
		
		// find unique filename
		String fullPathFileName = destPath + File.separator + fileName + filenameSuffix;
		File file = new File(fullPathFileName);
		for (int i = 0; file.exists(); i++) {
			fullPathFileName = destPath + File.separator + fileName + "_" + i + filenameSuffix;
			file = new File(fullPathFileName);
		}
		return file.getAbsolutePath();
	}
	
	private void makeActions(){
		actionAddAdvanced = new Action("Advanced Import") {
			{
				setToolTipText("Advanced Import");
				ImageDescriptor img =
					AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.global_inbox",
						"icons/multipage.gif");
				setImageDescriptor(Images.IMG_IMPORT.getImageDescriptor());
			}
			
			@Override
			public void run(){
				TableItem selTableItem = table.getSelection()[0];
				String patientSpec = selTableItem.getText(3);
				String patNr = patientSpec;
				String[] splittedPatSpec = patientSpec.split(" \\(");
				patientSpec = splittedPatSpec[0];
				patNr = "";
				if (splittedPatSpec.length > 1)
					patNr = splittedPatSpec[1].replace(")", "");
				String gebDat = selTableItem.getText(4);
				String[] patSpecs = new String[8];
				patSpecs[LASTNAME_IX] = patientSpec.split(" ")[0];
				patSpecs[FIRSTNAME_IX] = patientSpec.split(" ")[1];
				patSpecs[BIRTHDATE_IX] = gebDat;
				patSpecs[PATNR_IX] = patNr;
				Patient foundPat = matchPatient(patSpecs);
				File sel = getSelection();
				ReturnDocHandleAndCreateAufgebot retVal =
					assimilateWithPatientAndDocAndSoOn(foundPat, sel.getAbsolutePath(),
						selTableItem.getText(0), selTableItem.getText(1), selTableItem.getText(2),
						"");
				System.out.println(foundPat.getName() + " " + foundPat.getVorname());
			}
		};
		actionHistologieImporter = new Action("HistologieImporterTest") {
			{
				setToolTipText("HistologieImporterTest");
				ImageDescriptor img =
					AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.global_inbox",
						"icons/multipage.gif");
				setImageDescriptor(Images.IMG_IMPORT.getImageDescriptor());
			}
			
			@Override
			public void run(){
				// *** get the selected file
				File file = getSelection();
				
				// String pdfText = getPdfText(file.getPath());
				String startMarker_date = " vom ";
				
				POIFSFileSystem fs;
				try {
					fs = new POIFSFileSystem(new FileInputStream(file.getPath()));
					HWPFDocument doc = new HWPFDocument(fs);
					
					StyleSheet ss = doc.getStyleSheet();
					PAPBinTable paragraphTable = doc.getParagraphTable();
					ArrayList<PAPX> paragraphs = paragraphTable.getParagraphs();
					for (PAPX p : paragraphs) {
						// StyleSheet dd = new StyleSheet(null, 0);
						ParagraphProperties pProperties = p.getParagraphProperties(ss);
						System.out.println();
					}
					
					String biopsieNr = "";
					String patNr = "";
					String eingangsDatum = "";
					String ausgangsDatum = "";
					String klinik = "Klinik";
					String makroskopie = "Makroskopie";
					String mikroskopie = "Mikroskopie";
					String diagnose = "Diagnose";
					String kommentar = "Kommentar";
					String nachtragbericht = "Nachtragbericht";
					String contentsText = "";
					
					WordExtractor we = new WordExtractor(doc);
					String[] paragraphText = we.getParagraphText();
					for (int pix = 0; pix < paragraphText.length; pix++) {
						String paragraph = paragraphText[pix];
						int pos = -1;
						String searchString = "";
						
						// *** BiopsieNr einlesen
						// "	B 4606.12   	"
						if (biopsieNr.isEmpty()) {
							searchString = "	B ";
							pos = paragraph.indexOf(searchString);
							if (pos >= 0) {
								biopsieNr =
									(paragraph.substring(pos + searchString.length())).split(" ")[0];
							}
						}
						
						// *** Patient aus subject holen ist einfacher
						// "	Patient/-in	Erb Adrian, geb. 03.08.1973"
						// "	Pat.Nr.	" xxxx\r\n
						if (patNr.isEmpty()) {
							searchString = "	Pat.Nr.	";
							pos = paragraph.indexOf(searchString);
							if (pos >= 0) {
								patNr = paragraph.substring(pos + searchString.length());
								patNr =
									patNr.replace("\r\n", "\n").replace("\n", "\n").split("\n")[0];
							}
						}
						
						// *** Eingangsdatum einlesen
						// "	Eingang	" /XX/XXX\r\n (wie Ausgang)
						if (eingangsDatum.isEmpty()) {
							searchString = "	Eingang	";
							pos = paragraph.indexOf(searchString);
							if (pos >= 0) {
								eingangsDatum = paragraph.substring(pos + searchString.length());
								eingangsDatum =
									eingangsDatum.replace("\r\n", "\n").replace("\n", "\n")
										.split("\n")[0];
								eingangsDatum = eingangsDatum.split("/")[0];
								TimeTool tt = new TimeTool();
								boolean isDate = tt.setDate(eingangsDatum);
								String lastChar = eingangsDatum.substring(eingangsDatum.length());
								if (isDate && lastChar.equalsIgnoreCase("")) {} else {
									eingangsDatum = "";
								}
							}
						}
						
						// *** AusgangsDatum einlesen
						// "	Ausgang	" /XX/XXX\r\n (wie Ausgang)
						if (ausgangsDatum.isEmpty()) {
							searchString = "	Ausgang	";
							pos = paragraph.indexOf(searchString);
							if (pos >= 0) {
								ausgangsDatum = paragraph.substring(pos + searchString.length());
								ausgangsDatum =
									ausgangsDatum.replace("\r\n", "\n").replace("\n", "\n")
										.split("\n")[0];
								ausgangsDatum = ausgangsDatum.split("/")[0];
								TimeTool tt = new TimeTool();
								boolean isDate = tt.setDate(ausgangsDatum);
								String lastChar = ausgangsDatum.substring(ausgangsDatum.length());
								if (isDate && lastChar.equalsIgnoreCase("")) {} else {
									ausgangsDatum = "";
								}
							}
						}
						
						// *** Textparts einlesen
						String[] textParts =
							{
								"Klinik", "Makroskopie", "Mikroskopie", "Diagnose",
								"Kommentar", "Nachtragbericht"
							};
						for (int i = 0; i < textParts.length; i++) {
							searchString = textParts[i];
							pos = paragraph.indexOf(searchString);
							if (pos >= 0) {
								String textContent = "";
								for (int contentIx = pix + 2; contentIx < paragraphText.length; contentIx++) {
									String textParagraph = paragraphText[contentIx];
									textContent = textContent + textParagraph.replace("", "");
									String lastChar =
										textParagraph.substring(textParagraph.length() - 1);
									if (lastChar.equalsIgnoreCase("")) {
										pix = contentIx + 1;
										contentIx = paragraphText.length;
									}
								}
								histoTextParts.put(searchString, textContent);
								contentsText = contentsText + searchString + "\n";
								contentsText = contentsText + textContent + "\n";
							}
						}
					}
					
					String[] patSpecs = new String[PATNR_IX + 1];
					String patPart = file.getName().split("   ")[0];
					String[] lastName_FirstName_gebDat = patPart.split(", ");
					String lastName_FirstName = lastName_FirstName_gebDat[0];
					String gebDat = lastName_FirstName_gebDat[1];
					patSpecs[LASTNAME_IX] = "Jucker-Maria"; // lastName_FirstName.split(" ")[0];
					patSpecs[FIRSTNAME_IX] = "José"; // lastName_FirstName.split(" ")[1];
					patSpecs[BIRTHDATE_IX] = "08.02.1947"; // gebDat;
					patSpecs[PATNR_IX] = ""; // patNr;
					Patient foundPat = matchPatient(patSpecs);
					
					Patient asdf = foundPat;
					
					FileImportDialogMarlovits fid =
						new FileImportDialogMarlovits(file.getName(), false);
					fid.setCategory("Histologiebefunde");
					fid.setDatumDokument(ausgangsDatum);
					histoTextParts.get("Diagnose");
					fid.setTitel(histoTextParts.get("Diagnose"));
					fid.setKeywords("Eingang: " + eingangsDatum + "\n" + "Ausgang: "
						+ ausgangsDatum + "\n");
					fid.addTextField("Histologie", contentsText, 13);
					if (fid.open() == Dialog.OK) {
						
					}
					System.out.println();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		testerAction = new Action("Papierkorb anzeigen/verbergen") {
			{
				setToolTipText("Papierkorb anzeigen/verbergen");
				ImageDescriptor img =
					AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.global_inbox",
						"icons/multipage.gif");
				setImageDescriptor(Images.IMG_ACHTUNG.getImageDescriptor());
			}
			
			@Override
			public void run(){
				showTrash = !showTrash;
				Activator.getDefault().getContentProvider().reload();
				tv.refresh();
				if (1 == 0) {
					try {
						String destFile =
							"Z:\\_Daten\\ElexisDaten\\Eingangsfach\\Histologiebefunde\\destination.pdf";
						
						Document document = new Document();
						PdfWriter.getInstance(document, new FileOutputStream(destFile));
						// document.open();
						// ++++++++++ addMetaData(document);
						// iText allows to add metadata to the PDF which can be viewed in your Adobe
						// Reader
						// under File -> Properties
						// private static void addMetaData(Document document) {
						document.addTitle("My first PDF");
						document.addSubject("Using iText");
						document.addKeywords("Java, PDF, iText");
						document.addAuthor("Lars Vogel");
						document.addCreator("Lars Vogel");
						// }
						
						document.open();
						
						// +++++++++++++++++ addTitlePage(document);
// private static void addTitlePage(Document document)
// throws DocumentException {
						Paragraph preface = new Paragraph();
						// We add one empty line
						addEmptyLine(preface, 1);
						// Lets write a big header
						preface.add(new Paragraph("Title of the document", catFont));
						
						addEmptyLine(preface, 1);
						// Will create: Report generated by: _name, _date
						preface
							.add(new Paragraph(
								"Report generated by: " + System.getProperty("user.name") + ", " + new Date(), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								smallBold));
						addEmptyLine(preface, 3);
						preface
							.add(new Paragraph(
								"This document describes something which is very important ",
								smallBold));
						
						addEmptyLine(preface, 8);
						
						preface
							.add(new Paragraph(
								"This document is a preliminary version and not subject to your license agreement or any other agreement with vogella.com ;-).",
								redFont));
						
						document.add(preface);
						// Start a new page
						document.newPage();
						// }
						
						addContent(document);
						document.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
// String filePath =
// "Z:\\_Daten\\ElexisDaten\\Eingangsfach\\Histologiebefunde\\source.doc";
// convertToPdf(filePath, "");
					
					// PDFConverter.main_ooo(null);
// PDFConverter.traverse(new File(
// "Z:\\_Daten\\ElexisDaten\\Eingangsfach\\Histologiebefunde"));
				}
			}
		};
		
		createPatientAddDocumentCreateAufgebot = new Action("Zuordnen an neuen Patienten...") {
			{
				setToolTipText("Zuordnen an neuen Patienten...");
				ImageDescriptor img =
					AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.global_inbox",
						"icons/person_new.png");
				setImageDescriptor(img);
			}
			
			@Override
			public void run(){
				// ********* Dialog neuer Patient
				if (1 == 1)
					return;
				// access rights guard
				if (!CoreHub.acl.request(AccessControlDefaults.PATIENT_INSERT)) {
					SWTHelper.alert("Geht nicht...", "Du nix dürfe!");
// SWTHelper
// .alert(
//						Messages.getString("PatientenListeView.MissingRights"), Messages.getString("PatientenListeView.YouMayNotCreatePatient")); //$NON-NLS-1$ //$NON-NLS-2$
					return;
				}
				HashMap<String, String> ctlFields = new HashMap<String, String>();
				
				PatientErfassenDialog ped =
					new PatientErfassenDialog(getViewSite().getShell(), ctlFields);
				if (ped.open() == Dialog.OK) {
					Patient actPatient = ped.getResult();
					actPatient.createStdAnschrift();
// plcp.invalidate();
// cv.notify(CommonViewer.Message.update);
// cv.setSelection(actPatient, true);
					ElexisEventDispatcher.fireSelectionEvent(actPatient);
					
					// ********* neuen Dummy Fall erstellen
					Fall fallBefore = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
					GlobalActions.neuerFallAction.run();
					Fall fallAfter = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
					
					Konsultation k = fallAfter.neueKonsultation();
					
					if (!fallBefore.equals(fallAfter)) {
						// ********* Dialog Datei importieren wie unter addAction
						Boolean createAufgebot = assimilateDocProc(true);
						
						// ********* Aufgebot erstellen, falls gewünscht
						if (createAufgebot) {
							try {
								TextView textView =
									(TextView) getViewSite().getPage().showView(TextView.ID);
								String suffix =
									CoreHub.localCfg
										.get(TextTemplatePreferences.SUFFIX_STATION, "");
								String templatenameRaw;
								String templateName = "Terminvereinbarung";
								Query<Brief> qbe = new Query<Brief>(Brief.class);
								qbe.add(Brief.FLD_TYPE, Query.EQUALS, Brief.TEMPLATE);
								qbe.and();
								qbe.add(Brief.FLD_SUBJECT, Query.EQUALS, templateName);
								qbe.startGroup();
								qbe.add(Brief.FLD_DESTINATION_ID, Query.EQUALS,
									CoreHub.actMandant.getId());
								qbe.or();
								qbe.add(Brief.FLD_DESTINATION_ID, Query.EQUALS, StringTool.leer);
								qbe.endGroup();
								List<Brief> list = qbe.execute();
								if ((list != null) && (list.size() > 0)) {
									Brief template = list.get(0);
									if (template != null) {
										textView.createDocument(template, "Terminvereinbarung",
											actPatient);
									}
								}
							} catch (PartInitException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		};
		
		splitAction = new Action("In Einzelseiten zerlegen") {
			{
				setToolTipText("In Einzelseiten zerlegen");
				ImageDescriptor img =
					AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.global_inbox",
						"icons/multipage.gif");
				setImageDescriptor(img);
			}
			
			@Override
			public void run(){
				String[] splitSpec = {
					"1-1", "2-2"
				};
				doSplit(splitSpec);
			}
		};
		
		rotate90Action = new Action("Rotieren um 90°") {
			{
				setToolTipText("Rotieren um 90°");
				ImageDescriptor img =
					AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.global_inbox",
						"icons/shape_rotate_clockwise.png");
				setImageDescriptor(img);
			}
			
			@Override
			public void run(){
				doRotate(90);
			}
		};
		
		rotate180Action = new Action("Rotieren um 180°") {
			{
				setToolTipText("Rotieren um 180°");
				ImageDescriptor img =
					AbstractUIPlugin.imageDescriptorFromPlugin("ch.elexis.global_inbox",
						"icons/shape_rotate_180.png");
				setImageDescriptor(img);
			}
			
			@Override
			public void run(){
				doRotate(180);
			}
		};
		
		addAction = new Action(Messages.InboxView_assign) {
			{
				setToolTipText(Messages.InboxView_assignThisDocument);
				setImageDescriptor(Images.IMG_PERSON.getImageDescriptor());
			}
			
			@Override
			public void run(){
				assimilateDocProc(false);
			}
		};
		deleteAction = new Action(Messages.InboxView_delete) {
			{
				setToolTipText(Messages.InboxView_reallydelete);
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}
			
			@Override
			public void run(){
				File sel = getSelection();
				String fullPath = sel.getAbsolutePath();
				if (fullPath.contains("\\Papierkorb\\")) {
					if (SWTHelper
						.askYesNo(
							Messages.InboxView_inbox,
							"Wollen Sie "
								+ sel.getName()
								+ " wirklich DEFINITIV löschen?\nDie Datei kann nicht rekonstruiert werden.")) {
						// sel.delete();
						// Activator.getDefault().getContentProvider().reload();
						Table theTable = tv.getTable();
						int currSelIx = theTable.getSelectionIndex();
						tv.remove(sel);
						int numOfEntries = theTable.getItemCount();
						if ((currSelIx + 1) >= numOfEntries)
							theTable.setSelection(numOfEntries);
						else
							theTable.setSelection(currSelIx);
						sel.delete();
						// and now delete the info file if present
						String absPath = fullPath + ".file_info";
						File absPathFile = new File(absPath);
						if (absPathFile.exists())
							absPathFile.delete();
					}
				} else {
					if (SWTHelper.askYesNo(Messages.InboxView_inbox,
						MessageFormat.format(Messages.InboxView_thisreallydelete, sel.getName()))) {
						// sel.delete();
						// Activator.getDefault().getContentProvider().reload();
						Table theTable = tv.getTable();
						int currSelIx = theTable.getSelectionIndex();
						tv.remove(sel);
						int numOfEntries = theTable.getItemCount();
						if ((currSelIx + 1) >= numOfEntries)
							theTable.setSelection(numOfEntries);
						else
							theTable.setSelection(currSelIx);
						boolean bSucc = moveToTrash(sel, true);
						
						// //boolean bSucc = sel.delete();
						// // and now delete also our info file if present
						// String absPath = sel.getAbsolutePath() + ".file_info";
						// File absPathFile = new File(absPath);
						// if (absPathFile.exists())
						// moveToTrash(absPathFile);
						// //absPathFile.delete();
					}
				}
			}
		};
		
		execAction = new Action(Messages.InboxView_view) {
			{
				setToolTipText(Messages.InboxView_viewThisDocument);
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}
			
			@Override
			public void run(){
				try {
					File sel = getSelection();
					String ext = FileTool.getExtension(sel.getName());
					Program proggie = Program.findProgram(ext);
					String arg = sel.getAbsolutePath();
					if (proggie != null) {
						proggie.execute(arg);
					} else {
						if (Program.launch(sel.getAbsolutePath()) == false) {
							Runtime.getRuntime().exec(arg);
						}
						
					}
					
				} catch (Exception ex) {
					ExHandler.handle(ex);
					SWTHelper.showError(Messages.InboxView_couldNotStart, ex.getMessage());
				}
			}
		};
		reloadAction = new Action(Messages.InboxView_reload) {
			{
				setToolTipText(Messages.InboxView_reloadNow);
				setImageDescriptor(Images.IMG_REFRESH.getImageDescriptor());
			}
			
			@Override
			public void run(){
				try {
					ImportDocumentsFromFaxEmail.readDocuments(null);
				} finally {}
				Activator.getDefault().getContentProvider().reload();
			}
		};
	}
	
	boolean moveToTrash(String fileNameWithPath, boolean copyInfoFileToo){
		File file = new File(fileNameWithPath);
		if (!file.exists())
			return false;
		String fullPathName = file.getAbsolutePath();
		int lastSlashIx = fullPathName.lastIndexOf(File.separator);
		String fileNameWithSuffix = fullPathName.substring(lastSlashIx);
		String uniqueFileName = makeUniqueFile(TRASHPATH, fileNameWithSuffix);
		File movedFile = new File(uniqueFileName);
		if (movedFile.exists())
			return false;
		if (!file.renameTo(movedFile))
			return false;
		
		// move info file if present
		if (copyInfoFileToo) {
			String sourceInfoFileName = fullPathName + ".file_info";
			File infoFile = new File(sourceInfoFileName);
			if (!infoFile.exists())
				return true; // no matter...
			// extract last part of uniqueFileName
			int lastSlashIxInfo = uniqueFileName.lastIndexOf(File.separator);
			String infoFileNameWithSuffix = uniqueFileName.substring(lastSlashIxInfo);
			File movedInfoFile = new File(TRASHPATH + infoFileNameWithSuffix + ".file_info");
			infoFile.renameTo(movedInfoFile);
		}
		return true;
	}
	
	boolean moveToTrash(File file, boolean copyInfoFileToo){
		return moveToTrash(file.getAbsolutePath(), copyInfoFileToo);
	}
	
	/**
	 * Reads and returns the text contents of a pdf-file.
	 * 
	 * @param fullDocPath
	 * @return the pdf-file contents or "" on error
	 */
	public static String getPdfText(String fullDocPath){
		String parsedText = StringTool.leer;
		COSDocument cosDoc = null;
		PDDocument pdDoc = null;
		try {
			PDFParser parser;
			parser = new PDFParser(new FileInputStream(fullDocPath));
			PDFTextStripper pdfStripper = new PDFTextStripper();
			parser.parse();
			cosDoc = parser.getDocument();
			pdDoc = new PDDocument(cosDoc);
			@SuppressWarnings("unchecked")
			List<PDPage> list = pdDoc.getDocumentCatalog().getAllPages();
			pdfStripper.setStartPage(1); // 1-based!
			pdfStripper.setEndPage(list.size());
			parsedText = pdfStripper.getText(pdDoc);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (cosDoc != null)
					cosDoc.close();
				if (pdDoc != null)
					pdDoc.close();
			} catch (IOException e) {
				// simply ignore
			}
		}
		return parsedText;
	}
	
	private static void addEmptyLine(Paragraph paragraph, int number){
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}
	
	private static void addContent(Document document) throws DocumentException{
		Anchor anchor = new Anchor("First Chapter", catFont);
		anchor.setName("First Chapter");
		
		// Second parameter is the number of the chapter
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);
		
		Paragraph subPara = new Paragraph("Subcategory 1", subFont);
		Section subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph("Hello"));
		
		subPara = new Paragraph("Subcategory 2", subFont);
		subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph("Paragraph 1"));
		subCatPart.add(new Paragraph("Paragraph 2"));
		subCatPart.add(new Paragraph("Paragraph 3"));
		
		// Add a list
		createList(subCatPart);
		Paragraph paragraph = new Paragraph();
		addEmptyLine(paragraph, 5);
		subCatPart.add(paragraph);
		
		// Add a table
		createTable(subCatPart);
		
		// Now add all this to the document
		document.add(catPart);
		
		// Next section
		anchor = new Anchor("Second Chapter", catFont);
		anchor.setName("Second Chapter");
		
		// Second parameter is the number of the chapter
		catPart = new Chapter(new Paragraph(anchor), 1);
		
		subPara = new Paragraph("Subcategory", subFont);
		subCatPart = catPart.addSection(subPara);
		subCatPart.add(new Paragraph("This is a very important message"));
		
		// Now add all this to the document
		document.add(catPart);
		
	}
	
	private static void createList(Section subCatPart){
		com.lowagie.text.List list = new com.lowagie.text.List(true, false, 10);
		list.add(new ListItem("First point"));
		list.add(new ListItem("Second point"));
		list.add(new ListItem("Third point"));
		subCatPart.add(list);
	}
	
	private static void createTable(Section subCatPart) throws BadElementException{
		PdfPTable table = new PdfPTable(3);
		
// t.setBorderColor(BaseColor.GRAY);
// t.setPadding(4);
// t.setSpacing(4);
// t.setBorderWidth(1);
		
		PdfPCell c1 = new PdfPCell(new Phrase("Table Header 1"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Table Header 2"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		
		c1 = new PdfPCell(new Phrase("Table Header 3"));
		c1.setHorizontalAlignment(Element.ALIGN_CENTER);
		table.addCell(c1);
		table.setHeaderRows(1);
		
		table.addCell("1.0");
		table.addCell("1.1");
		table.addCell("1.2");
		table.addCell("2.1");
		table.addCell("2.2");
		table.addCell("2.3");
		
		subCatPart.add(table);
	}
	
	void doSplit(String[] splitSpecs){
		// *** get the selected file
		File file = getSelection();
		
		// *** need the temp dir
		File tempDir = CoreHub.getTempDir();
		
		// ********* now convert pdf to multipage tif with imageMagick
		String parentPath = file.getParent();
		String fileName = file.getPath();
		String pureName = file.getName();
		String baseName = pureName.substring(0, pureName.length() - 4);
		String fileNameWithoutSuffix = parentPath + File.separator + baseName;
		
		Runtime rt = Runtime.getRuntime();
		
		// find unique filename
		String tempFileName1 = findUniqueBatName(pureName);
		
		FileWriter outFile1;
		try {
			String[] lSplitSpecs = splitSpecs;
			
			PdfReader reader = new PdfReader(fileName);
			int n = reader.getNumberOfPages();
			LinkedList<String> tmpSpecs = new LinkedList<String>();
			for (int i = 1; i <= n; i++) {
				tmpSpecs.add("" + i + "-" + i);
			}
			lSplitSpecs = tmpSpecs.toArray(new String[0]);
			
			String firstSel = "";
			for (int i = 0; i < lSplitSpecs.length; i++) {
				String splitSpec = lSplitSpecs[i];
				String[] pageParts = splitSpec.split("-");
				String pageStart = pageParts[0];
				String pageEnd = pageStart;
				if (pageParts.length > 1) {
					pageEnd = pageParts[1];
				}
				int pageStartInt = Integer.parseInt(pageStart);
				int pageEndInt = Integer.parseInt(pageEnd);
				String pageSpec = pageStart + "-" + pageEndInt;
				
				String newFileName = fileNameWithoutSuffix + "_" + pageSpec + ".pdf";
				if (firstSel.isEmpty())
					firstSel = newFileName;
				
				outFile1 = new FileWriter(tempFileName1);
				PrintWriter out1 = new PrintWriter(outFile1);
				out1.print("\"" + GHOSTSCRIPT_PATH + "\"" + " ");
				// out1.print("\"F:\\Program Files\\Bullzip\\PDF Printer\\gs\\gswin32c.exe\"" +
// " ");
				out1.print("-dSAFER" + " ");
				out1.print("-dBATCH" + " ");
				out1.print("-dNOPAUSE" + " ");
				out1.print("-r300" + " ");
				out1.print("-sDEVICE=pdfwrite" + " ");
				out1.print("-dQUIET" + " ");
				out1.print("-dFirstPage=" + pageStartInt + " ");
				out1.print("-dLastPage=" + pageEndInt + " ");
				out1.print("-sOutputFile=");
				out1.print("\"" + fileNameWithoutSuffix + "_" + pageSpec + ".pdf\"" + " ");
				out1.print("\"" + fileName + "\"");
				out1.close();
				
				String[] commands2 = {
					"\"" + tempFileName1 + "\""
				};
				String line2 = null;
				Process pr2;
				
				pr2 = rt.exec(commands2);
				BufferedReader input1 =
					new BufferedReader(new InputStreamReader(pr2.getInputStream()));
				line2 = input1.readLine();
				while (line2 != null) {
					System.out.println(line2);
					line2 = input1.readLine();
				}
				
			}
			
			// *** delete original pdf from disk
			file = new File(fileName);
			boolean res = file.delete();
			System.out.println(res);
			
			// *** refresh display
			IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
			File selFile = (File) (sel.getFirstElement());
			String selFileName = selFile.getName();
			int suffixPos = selFileName.lastIndexOf(".");
			String selFileNameBase = selFileName.substring(0, suffixPos);
			String selFileNameSuffix = selFileName.substring(suffixPos);
			String newUrl = tempDir + File.separator + selFileName;
			File f = new File(newUrl);
			int i = 0;
			while (f.exists()) {
				newUrl = tempDir + File.separator + selFileNameBase + i + selFileNameSuffix;
				f = new File(newUrl);
				i++;
			}
			copyfile(firstSel, newUrl);
			int oldSelIndex = table.getSelectionIndex();
			Activator.getDefault().getContentProvider().reload();
			// tv.refresh();
			browserViewer.setUrl(newUrl);
			// browserViewer.refresh();
			table.setFocus();
			table.setSelection(oldSelIndex);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			
		}
	}
	
	// creates a unique temp file name in tempdir
	String findUniqueBatName(String baseName){
		File tempDir = CoreHub.getTempDir();
		String filenameSuffix1 = ".bat";
		String tempFileNameWithoutSuffix1 = tempDir + File.separator + baseName;
		String tempFileName1 = tempFileNameWithoutSuffix1 + filenameSuffix1;
		File tempFile1 = new File(tempFileName1);
		for (int i = 0; tempFile1.exists(); i++) {
			tempFileName1 = tempDir + File.separator + baseName + i + filenameSuffix1;
			tempFile1 = new File(tempFileName1);
		}
		return tempFileName1;
	}
	
	// 0 = rotate
	// 1 = split
	void doChange(int procedure, int degrees){
		// *** get the selected file
		File file = getSelection();
		
		// *** need the temp dir
		File tempDir = CoreHub.getTempDir();
		
		// ********* now convert pdf to multipage tif with imageMagick
		String filename = file.getPath();
		String pureName = file.getName();
		
		Runtime rt = Runtime.getRuntime();
		
		// find unique filename
		String filenameSuffix1 = ".bat";
		String tempFileNameWithoutSuffix1 = tempDir + File.separator + pureName;
		String tempFileName1 = tempFileNameWithoutSuffix1 + filenameSuffix1;
		File tempFile1 = new File(tempFileName1);
		for (int i = 0; tempFile1.exists(); i++) {
			tempFileName1 = tempDir + File.separator + pureName + i + filenameSuffix1;
			tempFile1 = new File(tempFileName1);
		}
		FileWriter outFile1;
		try {
			outFile1 = new FileWriter(tempFileName1);
			PrintWriter out1 = new PrintWriter(outFile1);
			out1.print("\"" + GHOSTSCRIPT_PATH + "\"" + " ");
			// out1.print("\"F:\\Program Files\\Bullzip\\PDF Printer\\gs\\gswin32c.exe\"" + " ");
			out1.print("-dSAFER" + " ");
			out1.print("-dBATCH" + " ");
			out1.print("-dNOPAUSE" + " ");
			out1.print("-r300" + " ");
			out1.print("-sDEVICE=tiffg3" + " ");
			out1.print("-sOutputFile=");
			out1.print("\"" + filename + ".tif\"" + " ");
			out1.print("\"" + filename + "\"");
			out1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] commands2 = {
			"\"" + tempFileName1 + "\""
		};
		String line2 = null;
		Process pr2;
		try {
			pr2 = rt.exec(commands2);
			BufferedReader input1 = new BufferedReader(new InputStreamReader(pr2.getInputStream()));
			line2 = input1.readLine();
			while (line2 != null) {
				System.out.println(line2);
				line2 = input1.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// *** now rotate and convert to pdf again
		
		// find unique filename
		String filenameSuffix = ".bat";
		String tempFileNameWithoutSuffix = tempDir + File.separator + pureName;
		String tempFileName = tempFileNameWithoutSuffix + filenameSuffix;
		File tempFile = new File(tempFileName);
		for (int i = 0; tempFile.exists(); i++) {
			tempFileName = tempDir + File.separator + pureName + i + filenameSuffix;
			tempFile = new File(tempFileName);
		}
		
		FileWriter outFile;
		try {
			outFile = new FileWriter(tempFileName);
			PrintWriter out = new PrintWriter(outFile);
			out.print("\"" + IMAGEMAGICK_CONVERT_PATH + "\"" + " ");
			out.print("-rotate " + degrees + " ");
			out.print("\"" + filename + ".tif\"" + " ");
			out.print("\"" + filename + "\"");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String[] commands = {
			"\"" + tempFileName + "\""
		};
		String line = null;
		Process pr;
		try {
			pr = rt.exec(commands);
			BufferedReader input1 = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			line = input1.readLine();
			while (line != null) {
				// System.out.println(line);
				line = input1.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// *** delete original pdf from disk
		file = new File(filename + ".tif");
		boolean res = file.delete();
		System.out.println(res);
		
		// *** refresh display
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		File selFile = (File) (sel.getFirstElement());
		String selFileName = selFile.getName();
		int suffixPos = selFileName.lastIndexOf(".");
		String selFileNameBase = selFileName.substring(0, suffixPos);
		String selFileNameSuffix = selFileName.substring(suffixPos);
		String newUrl = tempDir + File.separator + selFileName;
		File f = new File(newUrl);
		int i = 0;
		while (f.exists()) {
			newUrl = tempDir + File.separator + selFileNameBase + i + selFileNameSuffix;
			f = new File(newUrl);
			i++;
		}
		copyfile(filename, newUrl);
		browserViewer.setUrl(newUrl);
		// browserViewer.refresh();
	}
	
	void doRotate(int degrees){
		// *** get the selected file
		File file = getSelection();
		
		// *** need the temp dir
		File tempDir = CoreHub.getTempDir();
		
		// ********* now convert pdf to multipage tif with imageMagick
		String filename = file.getPath();
		String pureName = file.getName();
		
		Runtime rt = Runtime.getRuntime();
		
		// find unique filename
		String filenameSuffix1 = ".bat";
		String tempFileNameWithoutSuffix1 = tempDir + File.separator + pureName;
		String tempFileName1 = tempFileNameWithoutSuffix1 + filenameSuffix1;
		File tempFile1 = new File(tempFileName1);
		for (int i = 0; tempFile1.exists(); i++) {
			tempFileName1 = tempDir + File.separator + pureName + i + filenameSuffix1;
			tempFile1 = new File(tempFileName1);
		}
		FileWriter outFile1;
		try {
			outFile1 = new FileWriter(tempFileName1);
			PrintWriter out1 = new PrintWriter(outFile1);
			out1.print("\"" + GHOSTSCRIPT_PATH + "\"" + " ");
			// out1.print("\"F:\\Program Files\\Bullzip\\PDF Printer\\gs\\gswin32c.exe\"" + " ");
			out1.print("-dSAFER" + " ");
			out1.print("-dBATCH" + " ");
			out1.print("-dNOPAUSE" + " ");
			out1.print("-r300" + " ");
			out1.print("-sDEVICE=tiffg3" + " ");
			out1.print("-sOutputFile=");
			out1.print("\"" + filename + ".tif\"" + " ");
			out1.print("\"" + filename + "\"");
			out1.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] commands2 = {
			"\"" + tempFileName1 + "\""
		};
		String line2 = null;
		Process pr2;
		try {
			pr2 = rt.exec(commands2);
			BufferedReader input1 = new BufferedReader(new InputStreamReader(pr2.getInputStream()));
			line2 = input1.readLine();
			while (line2 != null) {
				System.out.println(line2);
				line2 = input1.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// *** now rotate and convert to pdf again
		
		// find unique filename
		String filenameSuffix = ".bat";
		String tempFileNameWithoutSuffix = tempDir + File.separator + pureName;
		String tempFileName = tempFileNameWithoutSuffix + filenameSuffix;
		File tempFile = new File(tempFileName);
		for (int i = 0; tempFile.exists(); i++) {
			tempFileName = tempDir + File.separator + pureName + i + filenameSuffix;
			tempFile = new File(tempFileName);
		}
		
		FileWriter outFile;
		try {
			outFile = new FileWriter(tempFileName);
			PrintWriter out = new PrintWriter(outFile);
			out.print("\"" + IMAGEMAGICK_CONVERT_PATH + "\"" + " ");
			out.print("-rotate " + degrees + " ");
			out.print("\"" + filename + ".tif\"" + " ");
			out.print("\"" + filename + "\"");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String[] commands = {
			"\"" + tempFileName + "\""
		};
		String line = null;
		Process pr;
		try {
			pr = rt.exec(commands);
			BufferedReader input1 = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			line = input1.readLine();
			while (line != null) {
				// System.out.println(line);
				line = input1.readLine();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		// *** delete original pdf from disk
		file = new File(filename + ".tif");
		boolean res = file.delete();
		System.out.println(res);
		
		// *** refresh display
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		File selFile = (File) (sel.getFirstElement());
		String selFileName = selFile.getName();
		int suffixPos = selFileName.lastIndexOf(".");
		String selFileNameBase = selFileName.substring(0, suffixPos);
		String selFileNameSuffix = selFileName.substring(suffixPos);
		String newUrl = tempDir + File.separator + selFileName;
		File f = new File(newUrl);
		int i = 0;
		while (f.exists()) {
			newUrl = tempDir + File.separator + selFileNameBase + i + selFileNameSuffix;
			f = new File(newUrl);
			i++;
		}
		copyfile(filename, newUrl);
		browserViewer.setUrl(newUrl);
		// browserViewer.refresh();
	}
	
	Boolean assimilateDocProc(Boolean createAufgebot){
		File sel = getSelection();
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (sel != null && pat != null) {
			// +++++ ADD START
			IDocumentManager myDM =
				(IDocumentManager) Extensions
					.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
			if (myDM == null) {
				SWTHelper.alert("", "Es ist kein Dokument-Manager (zBsp Omnivore) installiert");
			} else {
				ReturnDocHandleAndCreateAufgebot retVal =
					DocHandle.assimilate(sel.getAbsolutePath(), createAufgebot);
				DocHandle tmpDocHandle = retVal.theDocHandle;
				Boolean doCreateAufgebot = retVal.createAufgebot;
				// SWTHelper.alert("", "createAufgebot: " + doCreateAufgebot);
				if (tmpDocHandle != null) {
					Table theTable = tv.getTable();
					int currSelIx = theTable.getSelectionIndex();
					tv.remove(sel);
					int numOfEntries = theTable.getItemCount();
					if ((currSelIx + 1) >= numOfEntries)
						theTable.setSelection(numOfEntries);
					else
						theTable.setSelection(currSelIx);
					boolean bSucc = sel.delete();
					updateOmnivore((DocHandle) tmpDocHandle);
				}
				return retVal.createAufgebot;
			}
		}
		return false;
	}
	
	static void updateOmnivore(DocHandle tmpDocHandle){
		ElexisEventDispatcher.fireSelectionEvent((DocHandle) tmpDocHandle);
		ElexisEvent.createUserEvent();
		ElexisEvent.createPatientEvent();
	}
	
	// indexes in patientsSpecs string array
	public static int LASTNAME_IX = 0;
	public static int FIRSTNAME_IX = 1;
	public static int BIRTHDATE_IX = 2;
	public static int SEX_IX = 3;
	public static int STREET_IX = 4;
	public static int ZIP_IX = 5;
	public static int CITY_IX = 6;
	public static int PATNR_IX = 7;
	public static int AUFTRAGSNUMMER_IX = 8;
	public static int ENTNAHMEDATUM_IX = 9;
	public static int ENDBEFUND_IX = 10;
	
	/**
	 * Try to match the patient's specs found in patSpecs. <br>
	 * 
	 * TODO: matching of umlauts èéêëe, etc
	 * 
	 * @param patSpecs
	 * @return Patient if automatically matched or by selection by the user
	 */
	public static Patient matchPatient(String[] patSpecs){
		// fast exit..
		if (patSpecs == null)
			return null;
		
		// create a query, a patients list for the results of the query and an
		// array of all the matching patients
		Query<Patient> qbe = new Query<Patient>(Patient.class);
		List<Patient> patientsList = null;
		ArrayList<Patient> matchingPatients = new ArrayList<Patient>();
		
		// format birthdate as yyyymmdd
		String birthdate = patSpecs[BIRTHDATE_IX];
		birthdate =
			birthdate.substring(6, 10) + birthdate.substring(3, 5) + birthdate.substring(0, 2);
		
		// try to match the patNr if present
		if (matchingPatients.size() == 0) {
			if (!patSpecs[PATNR_IX].isEmpty()) {
				qbe.clear();
				qbe.add("Kuerzel", Query.EQUALS, patSpecs[PATNR_IX], true); //$NON-NLS-1$
				patientsList = qbe.execute();
				if (patientsList.size() > 0) {
					for (Patient p : patientsList)
						matchingPatients.add(p);
				}
			}
		}
		
		// try to exactly match the combination birthdate-lastname-firstname
		if (matchingPatients.size() == 0) {
			qbe.clear();
			qbe.add(Person.NAME, Query.EQUALS, patSpecs[LASTNAME_IX], true);
			qbe.add(Person.FIRSTNAME, Query.EQUALS, patSpecs[FIRSTNAME_IX], true);
			qbe.add(Person.BIRTHDATE, Query.EQUALS, birthdate);
			patientsList = qbe.execute();
			if (patientsList.size() > 0) {
				for (Patient p : patientsList)
					matchingPatients.add(p);
			}
		}
		
		// try to match the combination birthdate-lastname-firstname
		// try to strip down parts of lastname and firstname for matching
		
		// always use birthdate - is always present
		// then use full length "<lastName> <firstName>" to find the lastName in the db
		// ..strip down from the right until found
		// then try to find "best match" for the remainder "firstName-Part"
		// .. must strip from left when additional lastName-Parts are present
		// .. must strip from right when additional firstName-Parts are present
		if (matchingPatients.size() == 0) {
			// combine lastname/firstname, split on " " and "-"
			String combinationName = patSpecs[LASTNAME_IX] + " " + patSpecs[FIRSTNAME_IX];
			String[] nameParts = combinationName.split("[- ]");
			// treat combinationName as lastName and strip parts from the right side
			String currLastName = combinationName;
			for (int i = (nameParts.length - 1); i >= 0; i--) {
				System.out.println(currLastName);
				// try to match lastname
				String firstNamePart =
					combinationName
						.substring((currLastName.length() == combinationName.length()) ? currLastName
							.length() : currLastName.length() + 1);
				System.out.println(firstNamePart);
				System.out.println("****************");
				// try to match firstname starting from left to right
				// only match first part then try to match remainder
				String[] firstNameParts = firstNamePart.split("[- ]");
				for (int firstNameIx = 0; firstNameIx < firstNameParts.length; firstNameIx++) {
					String currFirstName = firstNameParts[firstNameIx];
					qbe.clear();
					qbe.add(Person.NAME, Query.LIKE, currLastName + "%", true); //$NON-NLS-1$
					qbe.add(Person.FIRSTNAME, Query.LIKE, currFirstName + "%", //$NON-NLS-1$
						true);
					qbe.add(Person.BIRTHDATE, Query.EQUALS, birthdate);
					patientsList = qbe.execute();
					if (patientsList.size() > 0) {
						// try to match firstcurrFirstNamenamePart as long as possible (strip
// right-to-left)
						String longFirstNamePart = firstNamePart;
						for (int firstNameBackCount = firstNameParts.length - 1; firstNameBackCount >= 0; firstNameBackCount--) {
							String currLastPart = firstNameParts[firstNameBackCount];
							
							longFirstNamePart =
								combinationName
									.substring((longFirstNamePart.length() == combinationName
										.length()) ? longFirstNamePart.length() : longFirstNamePart
										.length() + 1);
							
							longFirstNamePart = firstNamePart.substring(0, firstNamePart.length());
						}
						
						for (Patient p : patientsList) {
							if (!matchingPatients.contains(p))
								matchingPatients.add(p);
						}
					}
				}
				// find next sub-part
				String lastPart = nameParts[i];
				int endIx = currLastName.length() - lastPart.length() - 1;
				if (endIx >= 0)
					currLastName = currLastName.substring(0, endIx);
				// System.out.println(currLastName);
			}
		}
		
		if ((matchingPatients.size() == 0) && (1 == 1)) {
			String lastname = patSpecs[LASTNAME_IX];
			String[] lastnameParts = lastname.split("[- ]"); //$NON-NLS-1$
			for (int i = (lastnameParts.length - 1); i >= 0; i--) {
				String currLastName = StringTool.leer;
				String delim = StringTool.leer;
				for (int ii = 0; ii <= i; ii++) {
					currLastName = currLastName + delim + lastnameParts[ii];
					delim = "_"; //$NON-NLS-1$
				}
				String firstname = patSpecs[FIRSTNAME_IX];
				String[] firstnameParts = firstname.split("[- ]"); //$NON-NLS-1$
				for (int fni = (firstnameParts.length - 1); fni >= 0; fni--) {
					String currFirstName = StringTool.leer;
					String fnDelim = StringTool.leer;
					for (int fnii = 0; fnii <= fni; fnii++) {
						currFirstName = currFirstName + fnDelim + firstnameParts[fnii];
						fnDelim = "_"; //$NON-NLS-1$
					}
					qbe.clear();
					qbe.add(Person.NAME, Query.LIKE, currLastName + "%", true); //$NON-NLS-1$
					qbe.add(Person.FIRSTNAME, Query.LIKE, currFirstName + "%", //$NON-NLS-1$
						true);
					qbe.add(Person.BIRTHDATE, Query.EQUALS, birthdate);
					patientsList = qbe.execute();
					if (patientsList.size() > 0) {
						for (Patient p : patientsList) {
							if (!matchingPatients.contains(p))
								matchingPatients.add(p);
						}
					}
				}
			}
		}
		
		if (matchingPatients.size() == 1) {
			// return result
			return matchingPatients.get(0);
		} else if (matchingPatients.size() > 1) {
			// let the user select from multiple possibly matching patients
			final String[] choices = new String[matchingPatients.size()];
			for (int i1 = 0; i1 < choices.length; i1++) {
				Patient pat = matchingPatients.get(i1);
				choices[i1] =
					pat.getPersonalia()
						+ ", " //$NON-NLS-1$
						+ pat.get(Patient.FLD_STREET)
						+ ", " //$NON-NLS-1$
						+ pat.get(Patient.FLD_ZIP) + StringTool.space + pat.get(Patient.FLD_PLACE)
						+ " (" + //$NON-NLS-1$
						"Patientennummer" + pat.get(Patient.FLD_PATID) + ")"; //$NON-NLS-1$
			}
			final String patSpecString =
				patSpecs[LASTNAME_IX] + StringTool.space + patSpecs[FIRSTNAME_IX]
					+ ", " + patSpecs[BIRTHDATE_IX] //$NON-NLS-1$
					+ ", " + patSpecs[BIRTHDATE_IX] + ", " //$NON-NLS-1$ //$NON-NLS-2$
					+ patSpecs[STREET_IX] + ", " + patSpecs[ZIP_IX] //$NON-NLS-1$
					+ StringTool.space + patSpecs[CITY_IX] + ", " //$NON-NLS-1$
					+ patSpecs[PATNR_IX];
			synchCD = null;
			syncInDisplayThread(new Runnable() {
				public void run(){
					synchCD = new ChoiceDialog(null, "Patient auswählen", patSpecString, choices);
					if (synchCD.open() != Dialog.OK)
						synchCD = null;
				}
			});
			if (synchCD != null) {
				int result = synchCD.getResult();
				if (result >= 0) {
					return matchingPatients.get(result);
				}
			}
		} else {
			// +++++ START add iso copying
			final String patSpecsString = patSpecs[LASTNAME_IX] + "|" //$NON-NLS-1$
				+ patSpecs[FIRSTNAME_IX] + "|" + patSpecs[BIRTHDATE_IX]; //$NON-NLS-1$
			// +++++ END add iso copying
			if (savedMatches.containsKey(patSpecsString)) {
				// already matched once -> use this match
				return savedMatches.get(patSpecsString);
			} else {
				// not yet matched->let user select from normal selection dialog
				final String patSpecString2 =
					patSpecs[LASTNAME_IX] + StringTool.space + patSpecs[FIRSTNAME_IX] + ", " //$NON-NLS-1$
						+ patSpecs[BIRTHDATE_IX] + ", " //$NON-NLS-1$
						+ patSpecs[BIRTHDATE_IX] + ", " + patSpecs[STREET_IX] //$NON-NLS-1$
						+ ", " + patSpecs[ZIP_IX] + StringTool.space //$NON-NLS-1$
						+ patSpecs[CITY_IX] + ", " + patSpecs[PATNR_IX]; //$NON-NLS-1$
				synchKS = null;
				syncInDisplayThread(new Runnable() {
					public void run(){
						// +++++ START add iso copying
						synchKS =
							new KontaktSelektor(null, Patient.class, "Patient auswählen",
								"Patient auswählen für: " + patSpecsString.replace("|", " "), false);
						// +++++ END add iso copying
// synchKS =
// new KontaktSelektor(null, Patient.class,
// "Patient auswählen", patSpecString, false);
						if (synchKS.open() != Dialog.OK)
							synchKS = null;
					}
				});
				if (synchKS != null) {
					savedMatches.put(patSpecsString, (Patient) synchKS.getSelection());
					return (Patient) synchKS.getSelection();
				}
			}
		}
		return null;
	}
	
	public static void syncInDisplayThread(final Runnable runnable){
		syncInDisplayThread(Display.getDefault(), runnable);
	}
	
	public static void syncInDisplayThread(Display display, final Runnable runnable){
		if (Display.getCurrent() != display) {
			final AtomicBoolean done = new AtomicBoolean(false);
			display.asyncExec(new Runnable() {
				public void run(){
					try {
						runnable.run();
					} finally {
						done.set(true);
					}
				}
			});
			while (!done.get() && !Thread.interrupted()) {}
		} else {
			runnable.run();
		}
	}
	
	// +++++ ADD START
	public static ReturnDocHandleAndCreateAufgebot assimilateWithPatientAndDocAndSoOn(Patient act,
		String filePathAndName, String category, String docTitle, String docDate, String keywords){
		// public static boolean assimilate(String f){
		// +++++ ADD END
		if (act == null) {
			SWTHelper.showError(Messages.DocHandle_noPatientSelected,
				Messages.DocHandle_pleaseSelectPatient);
			return null;
		}
		File file = new File(filePathAndName);
		if (!file.canRead()) {
			SWTHelper.showError(Messages.DocHandle_cantReadCaption,
				MessageFormat.format(Messages.DocHandle_cantReadText, filePathAndName));
			return null;
		}
		FileImportDialogMarlovits fid =
			new FileImportDialogMarlovits(file.getName().replace("@#@", ", ").replace(", .", "."),
				false);
		fid.setTitel(docTitle);
		fid.setCategory(category);
		fid.setDatumDokument(docDate);
		fid.setKeywords(keywords);
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
				
				// SWTHelper.alert("", "createAufgebot: " + doCreateAufgebot);
				if (tmpDocHandle != null) {
					Table theTable = tv.getTable();
					int currSelIx = theTable.getSelectionIndex();
					File sel = getSelection();
					tv.remove(sel);
					int numOfEntries = theTable.getItemCount();
					if ((currSelIx + 1) >= numOfEntries)
						theTable.setSelection(numOfEntries);
					else
						theTable.setSelection(currSelIx);
					boolean bSucc = sel.delete();
					updateOmnivore((DocHandle) tmpDocHandle);
					// and now delete also our info file
					String absPath = sel.getAbsolutePath() + ".file_info";
					File absPathFile = new File(absPath);
					if (absPathFile.exists())
						absPathFile.delete();
				}
				
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
	// +++++ ADD END
}
