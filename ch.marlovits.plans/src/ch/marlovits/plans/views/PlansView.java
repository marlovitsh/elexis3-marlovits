package ch.marlovits.plans.views;

import java.io.BufferedReader;
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
import java.lang.reflect.Array;
import java.text.DateFormatSymbols;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.*;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

import ch.elexis.core.constants.StringConstants;
import ch.elexis.core.data.Anwender;
import ch.elexis.core.data.Patient;
import ch.elexis.core.data.Query;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.exceptions.ElexisException;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.marlovits.plans.data.Activator;
import ch.marlovits.plans.data.DocHandle_Mv;
import ch.marlovits.plans.data.InputBox_2;
import ch.rgw.tools.TimeTool;
import ch.marlovits.plans.data.ReturnDocHandleAndCreateAufgebot;

/**
 * 
 * <p>
 */

public class PlansView extends ViewPart implements IActivationListener {
	public static final String ID = "ch.marlovits.plans.views.plansView"; //$NON-NLS-1$
	private static final String ICON = "opplan_view"; //$NON-NLS-1$
	private Action rotate180Action, deleteAction, doubleClickAction, editAction, reloadAction,
			variableAction;
	PlansView myself;
	CTabFolder folderx;
	
	static int menuIx;
	
	private Browser browserViewer;
	static String IMAGEMAGICK_CONVERT_PATH =
		"F:\\Program Files\\ImageMagick-6.7.3-Q16\\convert.exe";
	
	private static final String BLANKPAGE = "about:blank";
	
	// *** characteritics trees/tables
	static LinkedList<TreeViewer> treeViewers = new LinkedList<TreeViewer>();
	
	static String[] operateureItems = {
		"Erban", "Riess", "Schweizer", "Steinke", "Stelz", "Marlovits", "Fehr", "Lorenz", "Kast",
		"Payer", "Reilly", "Rühli"
	};
	
	enum treeNames {
		OPPLAN, OPANMELDUNG, ANDERES
	}
	
	int[][] colWidths = {
// {
// 20, 75, 75, 120, 0
// },
		{
			19, 65, 70, 115, 240, 240
		}, {
			19, 65, 70, 115, 70, 85, 240
		}, {
			19, 65, 70, 115, 70, 85, 240
		}, {
			19, 65, 70, 115, 120, 120
		}, {
			19, 65, 70, 115, 120, 120
		}
	};
	
	// *** index 0: tabLabel
	// *** index 1: catName in omnivore
	// *** index 2: title omnivore
	public enum itemIxs {
		TABNAME, GROUPBYDATE, CATNAME, DOCTITLE, STARTCOLNAMES
	}
	
	// TabTitel-GruppierungNachDatum-CatName-DokTitel-Aufklapp*-DokDatum*-Wochentag*-DatumFax*-Fakultativ1/2/3
	// GruppierungNachDatum: 1 oder 0
	// ........... bei Gruppierung wird nach Datum gruppiert und kann aufgeklappt werden
	// CatName:... Anzuzeigende Kategorie, IMMER benötigt
	// DokTitel:.. Nur Dokumente mit diesem Titel anzeigen
	// Es folgen die Definition der Spalten:
	// Aufklapp:.. Spalte mit dem Aufklappzeichen - leer lassen
	// ........... IMMER benötigt
	// DokDatum:.. Erstellungsdatum des Dokumentes
	// ........... IMMER benötigt
	// Wochentag:. Wochentag des Erstellungsdatums des Dokumentes
	// ........... IMMER benötigt
	// ........... hier wird nur die Bezeichnung der Spalte benötigt, der Wert wird berechnet
	// DatumFax:.. Datum der Ankunft des Faxes
	// ........... IMMER benötigt
	// ........... hier wird nur die Bezeichnung der Spalte benötigt, der Wert wird berechnet
	// FakultativeSpalten n*
	
	// ****** Die Definition der Datenherkunft/Darstellung für die verschiedenen Datentypen
	// Text:.. Label:..numOfLines:..text
	// List:.. Label:..defSelItem:..multiSelect:..asString:..items
	// Menu:.. Label:..defSelItem:..multiSelect:..asString:items
	// radio:. Label:..defSelItem:..asString:items
	// check:. Label:..defSelItem:..asString:items
	// date:.. Label:..date
	// ----:..
	//
	// Parameter 0: <FelddefinitionDatenbank>|<DatentypDialogAbfrage>
	// ............. FelddefinitionDatenbank: <Feldname> oder <Feldname[n],>
	// ............... n = index im String Array, das Trennzeichen ist hier das Komma
	// ............ DatentypDialogAbfrage: Typ, der für die Abfrage im Dialog benötigt wird
	// ............... kann sein: Text/List/Menu(Combo)Radio/Check/Date/----
	// ............... bei "----" erfolgt keine Abfrage im Dialog
	// Parameter 1: das Label, das im Dialog angezeigt wird
	// Parameter 2: bei Text: Anzahl der angezeigten Zeilen für das Textfeld
	// ............ bei List/Menu/Radio/Check: selektiertes Item
	// ............ bei Date: das anzuzeigende Datum
	// Parameter 3: bei Text: der anzuzeigende Text
	// ............ bei List/Menu: multiSelect - kann mehr als ein Item ausgewählt werden?
	// ............ bei Radio/Check: asString: 1: der ausgewählte Text wird zurückgegeben,
	// ....................................... 0: der ItemIx (0-basiert) wird zurückgegeben
	// Parameter 4: bei List/Menu: asString: 1: der ausgewählte Text wird zurückgegeben,
	// ..................................... 0: der ItemIx (0-basiert) wird zurückgegeben
	// Parameter 5-n: items für List/Menu
	//
	// statt eines festen Wertes kann auch *db* angegeben werden - hier wird der Wert
	// aus der Datenbank ausgelesen und angezeigt
	
	public static String[][] tabAndColNames = {
		{
			"OP-Pläne", "1", "OP-Pläne",
			
			"", "", "Datum|Date:OP-Tag:*db*", "", "Datum Fax", "Keywords|Text:Bemerkungen:3:*db*"
		},
		{
			"OP-Anmeldungen", "1", "OP-Anmeldungen", "", "", "Datum|Date:OP-Tag:*db*", "",
			"Datum Fax",
			"Keywords[2]¦|List:Operateur:*db*:0:1:" + strArr_makeString(operateureItems, ":"),
			"Keywords[0]¦|Text:Patient:1:*db*", "Keywords[1]¦|Text:Bemerkungen:1:*db*"
		},
		{
			"OP-Berichte", "0", "OP-Berichte",
			
			"", "", "Datum|Date:OP-Datum:*db*", "", "Datum Fax",
			"Keywords[2]¦|List:Operateur:*db*:0:1:" + strArr_makeString(operateureItems, ":"),
			"Keywords[0]¦|Text:Patient:1:*db*", "Keywords[1]¦|Text:Bemerkungen:3:*db*"
		},
		{
			"Anderes", "0", "Anderes", "", "", "Datum|Date:Dokument-Datum:*db*", "", "Datum Fax",
			"Titel|Text:Bezeichnung:1:*db*", "Keywords|Text:Stichwörter:3:*db*"
		},
// {
// "Anderes 2", "0", "Anderes", "DokTitel", "", "Datum|Date:Dokument-Datum:*db*", "",
// "Datum Fax", "Titel|Text:Bezeichnung:1:*db*", "Keywords|Text:Stichwörter:3:*db*"
// }
		};
	
	static private TreeViewer currViewer;
	static final int SORTMODE_DATE = 0;
	static final int SORTMODE_TITLE = 1;
	private boolean bReverse = true;
	
	Composite parent2;
	
	/*****************************************************/
	/***** String Array Procs ********************/
	/*****************************************************/
	static String strArr_setString(String strArr, String delimiter, int index, String newString){
		String[] lArr = strArr.split(delimiter);
		int currLen = lArr.length;
		if (index >= (currLen - 1)) {
			String[] newArray = new String[index + 1];
			System.arraycopy(lArr, 0, newArray, 0, currLen);
			for (int i = currLen; i < index; i++) {
				newArray[i] = "";
			}
			newArray[index] = newString;
			return strArr_makeString(newArray, delimiter);
		} else {
			lArr[index] = newString;
			return strArr_makeString(lArr, delimiter);
		}
	}
	
	static String strArr_getString(String strArr, String delimiter, int index){
		String[] lArr = strArr.split(delimiter);
		int currLen = lArr.length;
		if (index >= currLen) {
			return "";
		} else {
			return lArr[index].trim();
		}
	}
	
	static String strArr_makeString(String[] arrayIn, String delimiter){
		String result = "";
		String currDelim = "";
		for (int i = 0; i < arrayIn.length; i++) {
			result = result + currDelim + arrayIn[i];
			currDelim = delimiter;
		}
		return result;
	}
	
	/*****************************************************/
	/***** String Array Procs END ********************/
	/*****************************************************/
	
	public void createPartControl(Composite parent){
		parent2 = parent;
		myself = this;
		
		// +++++ START PREFS
		String opplanpatid = CoreHub.localCfg.get("marlovits/opplan/opplanpat", null); //$NON-NLS-1$
		// +++++ END PREFS
		
		// *** display contents in a sash for resizing
		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		sash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		
		// *** display left part in tabs: OP-Pläne and OP-Anmeldungen
		folderx = new CTabFolder(sash, SWT.BORDER);
		int startColNames = itemIxs.STARTCOLNAMES.ordinal();
		int numOfTabs = tabAndColNames.length;
		
		for (int tabIx = 0; tabIx < numOfTabs; tabIx++) {
			// *** set tab label
			CTabItem itemx = new CTabItem(folderx, SWT.NONE);
			itemx.setText(tabAndColNames[tabIx][itemIxs.TABNAME.ordinal()]);
			itemx.setToolTipText(tabAndColNames[tabIx][itemIxs.TABNAME.ordinal()]);
			// *** create OP Plan Table
			Tree currPlanTree =
				new Tree(folderx, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
			TreeColumn[] cols = new TreeColumn[tabAndColNames[tabIx].length - startColNames];
			for (int i1 = 0; i1 < tabAndColNames[tabIx].length - startColNames; i1++) {
				cols[i1] = new TreeColumn(currPlanTree, SWT.NONE);
				cols[i1].setWidth(colWidths[tabIx][i1]);
				String[] parts = tabAndColNames[tabIx][i1 + startColNames].split(":");
				String theLabel = "";
				if (parts.length > 2) {
					theLabel = parts[1];
				} else {
					theLabel = parts[0];
				}
				cols[i1].setText(theLabel);
				cols[i1].setData(new Integer(i1));
			}
			currPlanTree.setHeaderVisible(true);
			currPlanTree.setLinesVisible(true);
			
			// *** create viewer
			TreeViewer tmpTreeViewer = new TreeViewer(currPlanTree);
			tmpTreeViewer.setContentProvider(new ViewContentProvider());
			tmpTreeViewer.setLabelProvider(new ViewLabelProvider());
			tmpTreeViewer.setSorter(new Sorter());
			tmpTreeViewer.setUseHashlookup(true);
			
			// *** fill controls for tabs
			itemx.setControl(currPlanTree);
			itemx.setShowClose(false);
			
			// changing selection
			tmpTreeViewer.addSelectionChangedListener(new opPlanSelectionChangedListener());
			
			// *** fill with data...
			tmpTreeViewer.setInput(currPlanTree);
			
			// *** add to list
			treeViewers.add(tabIx, tmpTreeViewer);
		}
		// *** need to know which tab is active / need currViewer
		folderx.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e){
				//System.out.println("selected: " + folderx.getSelectionIndex());
				currViewer = treeViewers.get(folderx.getSelectionIndex());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e){}
		});
		
		// *** actions/menus/toolbars
		makeActions();
		hookContextMenus();
		hookDoubleClickActions();
		contributeToActionBars();
		
		GlobalEventDispatcher.addActivationListener(this, this);
		// eeli_user.catchElexisEvent(ElexisEvent.createUserEvent());
		
		// *** sorting is always reverse
		bReverse = true;
		
		for (int i = 0; i < treeViewers.size(); i++) {
			treeViewers.get(i).refresh();
		}
		
		// *** browser on the right side of the sash for displaying contents
		browserViewer = new Browser(sash, SWT.NONE);
		sash.setWeights(new int[] {
			1, 2
		});
		
		// *** start with first tab
		folderx.setSelection(0);
		currViewer = treeViewers.get(0);
		
		Tree lTree = currViewer.getTree();
		TreeItem lTopItem = lTree.getItem(new Point(0, 0));
		currViewer.getTree().setSelection(lTopItem);
	}
	
	class opPlanSelectionChangedListener implements ISelectionChangedListener {
		
		@Override
		public void selectionChanged(SelectionChangedEvent event){
			currViewer = treeViewers.get(folderx.getSelectionIndex());
			IStructuredSelection sel = (IStructuredSelection) currViewer.getSelection();
			rotate180Action.setEnabled(!sel.isEmpty());
			editAction.setEnabled(!sel.isEmpty());
			deleteAction.setEnabled(!sel.isEmpty());
			if (!sel.isEmpty()) {
				Object firstEl = sel.getFirstElement();
				if (!(firstEl instanceof String)) {
					DocHandle_Mv dh = (DocHandle_Mv) (sel.getFirstElement());
					File selFile;
					try {
						selFile = dh.getStorageFile(true);
						String selFileName = selFile.getName();
						int suffixPos = selFileName.lastIndexOf(".");
						String selFileNameBase = selFileName.substring(0, suffixPos);
						String selFileNameSuffix = selFileName.substring(suffixPos);
						// *** only update if not yet shown
						String currUrl = browserViewer.getUrl();
						Path p = new Path(currUrl);
						String lastSeg = p.lastSegment();
						String[] pathParts3 = lastSeg.split("_");
						String urlBase = pathParts3[0];
						if (!(urlBase.equalsIgnoreCase(selFileNameBase))) {
							File tempDir = CoreHub.getTempDir();
							int i = 0;
							String newUrl =
								tempDir + File.separator + selFileNameBase + "_" + i
									+ selFileNameSuffix;
							File f = new File(newUrl);
							while (f.exists()) {
								newUrl =
									tempDir + File.separator + selFileNameBase + "_" + i
										+ selFileNameSuffix;
								f = new File(newUrl);
								i++;
							}
							if (copyfile(((File) selFile).getPath(), newUrl))
								browserViewer.setUrl(newUrl);
							else
								browserViewer.setUrl(BLANKPAGE);
						}
					} catch (ElexisException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public PlansView(){
		DocHandle_Mv.load(StringConstants.ONE); // make sure the table is created
		setTitleImage(UiDesk.getImage(ICON));
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
	}
	
	@Override
	public void setFocus(){
		
	}
	
	public void activation(boolean mode){ /* egal */
	}
	
	public void visible(boolean mode){
		if (mode) {} else {}
	}
	
	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput){}
		
		public void dispose(){}
		
		public Object[] getElements(Object parent){
			List<Object> ret = new LinkedList<Object>();
			String catName = "";
			String docName = "";
			boolean groupByDate = false;
			for (int i = 0; i < treeViewers.size(); i++) {
				if (treeViewers.get(i).getTree().equals(parent)) {
					catName = tabAndColNames[i][itemIxs.CATNAME.ordinal()];
					docName = tabAndColNames[i][itemIxs.DOCTITLE.ordinal()];
					groupByDate =
						(tabAndColNames[i][itemIxs.GROUPBYDATE.ordinal()]).equalsIgnoreCase("1") ? true
								: false;
					break;
				}
			}
			if (catName.equalsIgnoreCase(""))
				return ret.toArray();
			
			Patient pat = Activator.getOPPlanPatient();
			if (pat != null) {
				// *** create Months in descending order up to now, starting from first found OPPlan
				// *** find date of first/last OPPlan
				Query<DocHandle_Mv> qbe = new Query<DocHandle_Mv>(DocHandle_Mv.class);
				qbe.add(DocHandle_Mv.FLD_PATID, Query.EQUALS, pat.getId());
				qbe.add(DocHandle_Mv.FLD_CAT, Query.EQUALS, catName); //$NON-NLS-1$
				qbe.add(DocHandle_Mv.FLD_DELETED, Query.EQUALS, "0"); //$NON-NLS-1$
				if (!docName.isEmpty())
					qbe.add(DocHandle_Mv.FLD_TITLE, Query.EQUALS, docName); //$NON-NLS-1$
				qbe.orderBy(false, DocHandle_Mv.FLD_DATE);
				List<DocHandle_Mv> root = qbe.execute();
				String firstDate = "";
				String lastDate = "";
				if (root.size() > 0) {
					DocHandle_Mv firstOPPlan = root.get(0);
					firstDate = firstOPPlan.getDate();
					DocHandle_Mv lastOPPlan = root.get(root.size() - 1);
					lastDate = lastOPPlan.getDate();
				}
				
				// ***
				// *** extract year and month from first date
				TimeTool dateLow = new TimeTool(firstDate);
				int monthLow = dateLow.get(TimeTool.MONTH);
				int yearLow = dateLow.get(TimeTool.YEAR);
				TimeTool dateHigh = new TimeTool(lastDate);
				int monthHigh = dateHigh.get(TimeTool.MONTH);
				int yearHigh = dateHigh.get(TimeTool.YEAR);
				for (int iY = yearHigh; iY >= yearLow; iY--) {
					for (int iM = monthHigh; iM >= monthLow; iM--) {
						// //ret.add(monthNames[iM] + " " + yearHigh);
					}
				}
				
				qbe.clear();
				root.clear();
				
				qbe.add(DocHandle_Mv.FLD_PATID, Query.EQUALS, pat.getId());
				qbe.add(DocHandle_Mv.FLD_CAT, Query.EQUALS, catName); //$NON-NLS-1$
				if (!docName.isEmpty())
					qbe.add(DocHandle_Mv.FLD_TITLE, Query.EQUALS, docName); //$NON-NLS-1$
				qbe.orderBy(true, DocHandle_Mv.FLD_DATE, DocHandle_Mv.FLD_MIMETYPE); //$NON-NLS-1$
				root = qbe.execute();
				
				// *** if group by date remove sub-dates
				LinkedList<DocHandle_Mv> reducedList = new LinkedList<DocHandle_Mv>();
				DocHandle_Mv tmpDocHandle;
				String oldDate = "";
				String newDate = "";
				for (int i = 0; i < root.size(); i++) {
					tmpDocHandle = root.get(i);
					newDate = tmpDocHandle.getDate();
					if ((!newDate.equalsIgnoreCase(oldDate)) || (!groupByDate)) {
						reducedList.add(tmpDocHandle);
					} else {}
					oldDate = newDate;
				}
				ret.addAll(reducedList);
			}
			return ret.toArray();
		}
		
		public Object[] getChildren(Object parentElement){
			Patient pat = Activator.getOPPlanPatient();
			if (pat == null)
				return new Object[0];
			if (parentElement instanceof DocHandle_Mv) {
				DocHandle_Mv dh = (DocHandle_Mv) parentElement;
				String catName = dh.getCategory();
				String planDate = dh.getDate();
				
				// *** if group by date then there are never children
				boolean groupByDate = false;
				for (int i = 0; i < tabAndColNames.length; i++) {
					if (tabAndColNames[i][itemIxs.CATNAME.ordinal()].equalsIgnoreCase(catName)) {
						if (tabAndColNames[i][itemIxs.GROUPBYDATE.ordinal()].equalsIgnoreCase("1")) {
							groupByDate = true;
						}
						break;
					}
				}
				if (!groupByDate)
					return new Object[0];
				
				Query<DocHandle_Mv> qbe = new Query<DocHandle_Mv>(DocHandle_Mv.class);
				qbe.add(DocHandle_Mv.FLD_PATID, Query.EQUALS, pat.getId());
				qbe.add(DocHandle_Mv.FLD_CAT, Query.EQUALS, catName); //$NON-NLS-1$
				qbe.add(DocHandle_Mv.FLD_DATE, Query.EQUALS, planDate); //$NON-NLS-1$
				qbe.orderBy(true, DocHandle_Mv.FLD_DATE, DocHandle_Mv.FLD_MIMETYPE);
				List<DocHandle_Mv> root = qbe.execute();
				DocHandle_Mv firstOPPlan = root.get(0);
				String firstFaxDateFromList = calcFaxDate(firstOPPlan.getDate());
				
				if (firstFaxDateFromList.equalsIgnoreCase(planDate)) {
					// *** this is the parent - get children
					root.remove(0);
					return root.toArray();
				} else {
					// *** this is a child - has no children
					return new Object[0];
				}
			} else {
				return new Object[0];
			}
		}
		
		public Object getParent(Object element){
			if (element instanceof DocHandle_Mv) {
				DocHandle_Mv dh = (DocHandle_Mv) element;
				String catName = dh.getCategory();
				
				// *** if group by date then there are never children
				boolean groupByDate = false;
				for (int i = 0; i < tabAndColNames.length; i++) {
					if (tabAndColNames[i][itemIxs.CATNAME.ordinal()].equalsIgnoreCase(catName)) {
						if (tabAndColNames[i][itemIxs.GROUPBYDATE.ordinal()].equalsIgnoreCase("1")) {
							groupByDate = true;
						}
						break;
					}
				}
				if (!groupByDate)
					return null;
				
				String planDate = calcFaxDate(dh.getDate());
				String faxDate = calcFaxDate(dh.getMimetype());
				
				Patient pat = Activator.getOPPlanPatient();
				Query<DocHandle_Mv> qbe = new Query<DocHandle_Mv>(DocHandle_Mv.class);
				qbe.add(DocHandle_Mv.FLD_PATID, Query.EQUALS, pat.getId());
				qbe.add(DocHandle_Mv.FLD_CAT, Query.EQUALS, catName); //$NON-NLS-1$
				qbe.add(DocHandle_Mv.FLD_DATE, Query.EQUALS, planDate); //$NON-NLS-1$
				qbe.orderBy(true, DocHandle_Mv.FLD_MIMETYPE);
				List<DocHandle_Mv> root = qbe.execute();
				DocHandle_Mv firstOPPlan = root.get(0);
				String firstFaxDateFromList = calcFaxDate(firstOPPlan.getMimetype());
				
				if (faxDate.equalsIgnoreCase(firstFaxDateFromList)) {
					TimeTool tt = new TimeTool(dh.getDate());
					return tt.get(TimeTool.MONTH) + " " + tt.get(TimeTool.YEAR);
				} else {
					return firstOPPlan;
				}
			}
			return null;
			
		}
		
		public boolean hasChildren(Object element){
			if (element instanceof DocHandle_Mv) {
				DocHandle_Mv dh = (DocHandle_Mv) element;
				String catName = dh.getCategory();
				
				// *** if group by date then there are never children
				boolean groupByDate = false;
				for (int i = 0; i < tabAndColNames.length; i++) {
					if (tabAndColNames[i][itemIxs.CATNAME.ordinal()].equalsIgnoreCase(catName)) {
						if (tabAndColNames[i][itemIxs.GROUPBYDATE.ordinal()].equalsIgnoreCase("1")) {
							if (tabAndColNames[i][itemIxs.GROUPBYDATE.ordinal()]
								.equalsIgnoreCase("1")) {
								groupByDate = true;
							}
						}
						break;
					}
				}
				if (!groupByDate)
					return false;
				
				String planDate = dh.getDate();
				Patient pat = Activator.getOPPlanPatient();
				Query<DocHandle_Mv> qbe = new Query<DocHandle_Mv>(DocHandle_Mv.class);
				qbe.add(DocHandle_Mv.FLD_PATID, Query.EQUALS, pat.getId());
				qbe.add(DocHandle_Mv.FLD_CAT, Query.EQUALS, catName);
				qbe.add(DocHandle_Mv.FLD_DATE, Query.EQUALS, planDate);
				qbe.orderBy(true, DocHandle_Mv.FLD_MIMETYPE);
				List<DocHandle_Mv> root = qbe.execute();
				if (root.size() > 1) {
					DocHandle_Mv firstDH = root.get(0);
					if (firstDH.equals(dh))
						return true;
					else
						return false;
				} else
					return false;
			}
			if (element instanceof String) {
				return false;
			}
			return false;
		}
	}
	
	public String calcFaxDate(String faxDateTime){
		if (faxDateTime.length() < "xxxx.xx.xx xx'xx'xx.pdf".length())
			return faxDateTime;
		//System.out.println(faxDateTime);
		faxDateTime = faxDateTime.replace("'", ":");
		faxDateTime = faxDateTime.replace(".pdf", "");
		faxDateTime = faxDateTime.split("\\_")[0]; // _1 und _2, etc wegstrippen
		if (faxDateTime.length() < "xxxx.xx.xx xx'xx'xx".length())
			return faxDateTime;
		return faxDateTime.substring(8, 10) + "." + faxDateTime.substring(5, 7) + "."
			+ faxDateTime.substring(0, 4) + "   " + faxDateTime.substring(11);
		
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider,
			ITableColorProvider {
		public String getColumnText(Object obj, int index){
			boolean groupByDate = false;
			String colSpec = "";
			if (obj instanceof DocHandle_Mv) {
				DocHandle_Mv dh = (DocHandle_Mv) obj;
				String catName = dh.getCategory();
				
				// *** if group by date then there are never children
				for (int i = 0; i < tabAndColNames.length; i++) {
					if (tabAndColNames[i][itemIxs.CATNAME.ordinal()].equalsIgnoreCase(catName)) {
						colSpec = "";
						if (tabAndColNames[i][itemIxs.GROUPBYDATE.ordinal()].equalsIgnoreCase("1")) {
							if (tabAndColNames[i][itemIxs.GROUPBYDATE.ordinal()]
								.equalsIgnoreCase("1")) {
								groupByDate = true;
							}
						}
						colSpec = tabAndColNames[i][itemIxs.STARTCOLNAMES.ordinal() + index];
						break;
					}
				}
			}
			
			switch (index) {
			case 0:
				// this is the arrow
				return ""; //$NON-NLS-1$
			case 1:
				// this is the month or the first date
				if (obj instanceof String)
					return (String) obj;
				else {
					DocHandle_Mv dh = (DocHandle_Mv) obj;
					String planDate = dh.getDate();
					String faxDate = calcFaxDate(dh.getMimetype());
					
					if (groupByDate) {
						String faxDateComp = getFirstFaxInDate(planDate, dh);
						if (faxDate.equalsIgnoreCase(faxDateComp))
							return dh.getDate();
						else
							return "";
					} else {
						return dh.getDate();
					}
				}
			case 2:
				if (obj instanceof String)
					return "";
				else {
					DocHandle_Mv dh = (DocHandle_Mv) obj;
					String planDate = dh.getDate();
					String faxDate = calcFaxDate(dh.getMimetype());
					
					String faxDateComp = getFirstFaxInDate(planDate, dh);
					TimeTool tt = new TimeTool(dh.get(DocHandle_Mv.FLD_DATE));
					String[] weekDayNames = new DateFormatSymbols().getWeekdays();
					int weekDay = tt.get(TimeTool.DAY_OF_WEEK);
					if (groupByDate) {
						if (faxDate.equalsIgnoreCase(faxDateComp)) {
							return weekDayNames[weekDay];
						} else
							return "";
					} else {
						return weekDayNames[weekDay];
					}
					
				}
			case 3:
				// this is the first date
				if (obj instanceof String)
					return "";
				else {
					DocHandle_Mv dh = (DocHandle_Mv) obj;
					String faxDateTime = calcFaxDate(dh.getMimetype());
					return faxDateTime;
				}
			default:
				//
				if (index >= 4) {
					if (obj instanceof String)
						return "";
					else {
						DocHandle_Mv dh = (DocHandle_Mv) obj;
						
						// Operateur:FLD_KEYWORDS[3],
						// "Keywords|Text:Stichwörter:3:VorgabeText sehr individuell"
						// "Keywords[3],|----:Operateur:3:,"
						String[] parts = colSpec.split(":");
						
						String firstPart = parts[0];
						String dbFieldName = firstPart.split("\\|")[0];
						String[] split2 = dbFieldName.split("\\[");
						dbFieldName = split2[0];
						String requestedField = dh.get(dbFieldName);
						if (split2.length >= 2) {
							String indexSpecRightPart = split2[1];
							String[] indexSpecParts = indexSpecRightPart.split("\\]");
							if (indexSpecParts.length >= 2) {
								String indexStr = indexSpecParts[0];
								int indexInt = Integer.parseInt(indexStr);
								String delim = indexSpecParts[1];
								
								requestedField = strArr_getString(requestedField, delim, indexInt);
							}
						}
						return requestedField.trim();
					}
				} else {
					return "";
				}
			}
		}
		
		public Image getColumnImage(Object obj, int index){
			return null; // getImage(obj);
		}
		
		public Image getImage(Object obj){
			return PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		
		@Override
		public Color getForeground(Object element, int columnIndex){
			return UiDesk.getDisplay().getSystemColor(SWT.COLOR_BLACK);
		}
		
		@Override
		public Color getBackground(Object element, int columnIndex){
			if (element instanceof String) {
				return UiDesk.getDisplay().getSystemColor(SWT.COLOR_GRAY);
			} else
				return null;
		}
	}
	
	String getFirstFaxInDate(String planDate, DocHandle_Mv dh){
		Patient pat = Activator.getOPPlanPatient();
		String currCat = dh.getCategory();
		Query<DocHandle_Mv> qbe = new Query<DocHandle_Mv>(DocHandle_Mv.class);
		qbe.add(DocHandle_Mv.FLD_PATID, Query.EQUALS, pat.getId());
		qbe.add(DocHandle_Mv.FLD_CAT, Query.EQUALS, currCat);
		qbe.add(DocHandle_Mv.FLD_DATE, Query.EQUALS, planDate);
		qbe.orderBy(true, DocHandle_Mv.FLD_MIMETYPE);
		List<DocHandle_Mv> root = qbe.execute();
		if (root.size() == 0)
			return "";
		DocHandle_Mv firstDH = root.get(0);
		return calcFaxDate(firstDH.getMimetype());
	}
	
	class Sorter extends ViewerSorter {
		@Override
		public int compare(Viewer viewer, Object e1, Object e2){
			if ((e1 instanceof String) && (e2 instanceof String)) {
				// *** nothing - leave sorting as is!
			} else if ((e1 instanceof String) || (e2 instanceof String)) {
				// compare String with DocHandle_Mv
				
				// *** extract month/year from string
				String theString = (e1 instanceof String) ? (String) e1 : (String) e2;
				String[] monthYear = theString.split(" ");
				String[] monthNames = new DateFormatSymbols().getMonths();
				String month = monthYear[0];
				int strMonth = 0;
				for (int i = 0; i < 12; i++) {
					if (month.equalsIgnoreCase(monthNames[i])) {
						strMonth = i + 1;
						if (i > 9)
							month = "" + strMonth;
						else
							month = "0" + strMonth;
						break;
					}
				}
				String strCombi = monthYear[1] + month;
				
				// *** extract month/year from docHandle
				DocHandle_Mv docHandle =
					(e1 instanceof DocHandle_Mv) ? (DocHandle_Mv) e1 : (DocHandle_Mv) e2;
				TimeTool tt = new TimeTool(docHandle.get(DocHandle_Mv.FLD_DATE));
				int dhMonth = tt.get(TimeTool.MONTH);
				int dhYear = tt.get(TimeTool.YEAR);
				String dhCombi = "" + dhYear + ((dhMonth > 9) ? dhMonth : "0" + dhMonth);
				
				// *** now compare them
				if (e1 instanceof String)
					return dhCombi.compareTo(strCombi);
				else
					return strCombi.compareTo(dhCombi);
			} else {
				// compare DocHandle_Mv with DocHandle_Mv
				if ((e1 instanceof DocHandle_Mv) && (e2 instanceof DocHandle_Mv)) {
					DocHandle_Mv d1 = (DocHandle_Mv) e1;
					DocHandle_Mv d2 = (DocHandle_Mv) e2;
					String c1, c2;
					c1 =
						new TimeTool(d1.get(DocHandle_Mv.FLD_DATE)).toString(TimeTool.DATE_COMPACT)
							+ calcFaxDate(d1.getMimetype());
					c2 =
						new TimeTool(d2.get(DocHandle_Mv.FLD_DATE)).toString(TimeTool.DATE_COMPACT)
							+ calcFaxDate(d2.getMimetype());
					if (bReverse) {
						return c2.compareTo(c1);
					} else {
						return c1.compareTo(c2);
					}
				}
			}
			return 0;
		}
	}
	
	private void hookContextMenus(){
		// *** for OP-Pläne
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager){
				PlansView.this.fillContextMenu(manager);
			}
		});
		for (int i = 0; i < treeViewers.size(); i++) {
			Menu menu = menuMgr.createContextMenu(treeViewers.get(i).getControl());
			currViewer = treeViewers.get(i);
			currViewer.getControl().setMenu(menu);
			getSite().registerContextMenu(menuMgr, currViewer);
		}
	}
	
	private void hookDoubleClickActions(){
		for (int i = 0; i < treeViewers.size(); i++) {
			currViewer = treeViewers.get(i);
			currViewer.addDoubleClickListener(new IDoubleClickListener() {
				public void doubleClick(DoubleClickEvent event){
					doubleClickAction.run();
				}
			});
		}
	}
	
	private void contributeToActionBars(){
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}
	
	private final ElexisEventListenerImpl eeli_user = new ElexisEventListenerImpl(Anwender.class,
		ElexisEvent.EVENT_USER_CHANGED) {
		@Override
		public void run(ElexisEvent ev){
			for (int i = 0; i < treeViewers.size(); i++) {
				currViewer = treeViewers.get(i);
				currViewer.refresh();
			}
		}
	};
	
	DocHandle_Mv getSelectedDH(){
		IDocumentManager myDM =
			(IDocumentManager) Extensions
				.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		if (myDM == null) {
			SWTHelper.alert("", "Es ist kein Dokument-Manager (zBsp Omnivore) installiert");
		} else {
			currViewer = treeViewers.get(folderx.getSelectionIndex());
			ISelection currSelection = currViewer.getSelection();
			return (DocHandle_Mv) ((IStructuredSelection) currSelection).getFirstElement();
		}
		return null;
		
	}
	
	private void fillContextMenu(IMenuManager manager){
		
		Patient pat = ElexisEventDispatcher.getSelectedPatient();
		if (pat != null) {
			editAction.setToolTipText("Zuordnen an Patienten: " + pat.getPersonalia());
			editAction.setText("Zuordnen an Patienten: " + pat.getPersonalia());
			editAction.setEnabled(true);
		} else {
			editAction.setEnabled(false);
		}
		manager.add(editAction);
		manager.add(new Separator());
		for (int menuIx = 0; menuIx < treeViewers.size(); menuIx++) {
			currViewer = treeViewers.get(menuIx);
			final int tmpIx = menuIx;
			final String catName = tabAndColNames[menuIx][itemIxs.CATNAME.ordinal()];
			String actionName = "";
			if (tmpIx == folderx.getSelectionIndex())
				actionName = "Bearbeiten...";
			else
				actionName = "Verschieben zu " + catName + "...";
			final String fActionName = actionName;
			variableAction = new Action(actionName) {
				{
					setToolTipText(fActionName);
					setImageDescriptor(Images.IMG_NEXT.getImageDescriptor());
				}
				
				@Override
				public void run(){
					DocHandle_Mv dh111 = getSelectedDH();
					moveDocument(parent2.getShell(), dh111, tmpIx);
				}
			};
			manager.add(variableAction);
		}
		manager.add(new Separator());
		manager.add(rotate180Action);
		manager.add(deleteAction);
		manager.add(new Separator());
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		
		// manager.add(action2);
		// Other plug-ins can contribute there actions here
		// manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	static public boolean moveDocument(Shell parentShell, DocHandle_Mv dh, int tmpIx){
		// *** extract what is to be asked for
		String[] currStrings = tabAndColNames[tmpIx];
		LinkedList<String[]> dialogParams = new LinkedList<String[]>();
		
		// *** create the sequence indexes for the display
		LinkedList<Integer> indexSequence = new LinkedList<Integer>();
		indexSequence.add(5); // date of doc
		indexSequence.add(3); // title for doc
		for (int i = 8; i < currStrings.length; i++)
			indexSequence.add(i); // optional colums
		
		// Text:..Label:..numOfLines:..text
		// ++ List:..Label:..defSelItem:..multiSelect:..asString:..items
		// ++ Menu:..Label:..defSelItem:..multiSelect:..asString:items
		// ++ radio:.Label:..defSelItem:..asString:items
		// ++ check:.Label:..defSelItem:..asString:items
		// ++ date:..Label:..date
		
		// >= 8
		for (int i = 0; i < indexSequence.size(); i++) {
			int currIx = indexSequence.get(i);
			String parmString = currStrings[currIx];
			String[] parmsI = (parmString).split(":");
			if (parmString.indexOf("*db*") >= 0) {
				String[] first = parmsI[0].split("\\|");
				String dbFieldName = first[0];
				// dbFieldName = dbFieldName.split("\\[")[0];
				
				String[] split2 = dbFieldName.split("\\[");
				dbFieldName = split2[0];
				String theValue = dh.get(dbFieldName);
				if (split2.length >= 2) {
					String indexSpecRightPart = split2[1];
					String[] indexSpecParts = indexSpecRightPart.split("\\]");
					if (indexSpecParts.length >= 2) {
						String indexStr = indexSpecParts[0];
						int indexInt = Integer.parseInt(indexStr);
						String delim = indexSpecParts[1];
						theValue = strArr_getString(theValue, delim, indexInt);
					}
				}
				
				for (int ii = 0; ii < parmsI.length; ii++) {
					if (parmsI[ii].equalsIgnoreCase("*db*")) {
						parmsI[ii] = theValue;
					}
				}
			}
			if (parmsI.length > 1)
				dialogParams.add(parmsI);
		}
		
		String[][] dialogParamsArray = dialogParams.toArray(new String[0][0]);
		
		String[] buttonLabels = {
			"    OK    ", " Abbrechen "
		};
		InputBox_2 testInput =
			new InputBox_2(parentShell, SWT.NONE, "Verschieben zu " + currStrings[2], buttonLabels,
				0, dialogParamsArray);
		LinkedList<String[]> resultList = testInput.open();
		
		if ((resultList != null) && (resultList.size() > 0)) {
			IDocumentManager myDM =
				(IDocumentManager) Extensions
					.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
			if (myDM == null) {
				SWTHelper.alert("", "Es ist kein Dokument-Manager (zBsp Omnivore) installiert");
			} else {
				// *** set the catName
				String newCatName = currStrings[2];
				dh.set(DocHandle_Mv.FLD_CAT, newCatName);
				
				// *** move through the returned params
				for (int i = 0; i < resultList.size(); i++) {
					String[] tmpResultArray = resultList.get(i);
					String dbName = tmpResultArray[0];
					String[] dbSpecParts = dbName.split("\\[");
					if (dbSpecParts.length > 1) {
						dbName = dbSpecParts[0];
						String[] dbSpecParts_2 = dbSpecParts[1].split("\\]");
						String index = dbSpecParts_2[0];
						int indexInt = Integer.parseInt(index);
						String delimiter = dbSpecParts_2[1];
						String currValue = dh.get(dbName);
						String newValue =
							strArr_setString(currValue, delimiter, indexInt, tmpResultArray[1]);
						dh.set(dbName, newValue);
					} else {
						dh.set(dbName, tmpResultArray[1]);
					}
				}
				
				for (int i = 0; i < treeViewers.size(); i++) {
					currViewer = treeViewers.get(i);
					currViewer.refresh();
				}
			}
			return true;
		} else {
			return false;
		}
	}
	
	private void fillLocalPullDown(IMenuManager manager){
		MenuManager mnSources = new MenuManager("Messages.OmnivoreView_dataSources");
		// +++++manager.add(importAction);
	}
	
	private void fillLocalToolBar(IToolBarManager manager){
		// +++++manager.add(importAction);
		manager.add(reloadAction);
	}
	
	public void reload(){
		UiDesk.asyncExec(new Runnable() {
			@Override
			public void run(){
				for (int i = 0; i < treeViewers.size(); i++) {
					currViewer = treeViewers.get(i);
					currViewer.refresh();
				}
			}
		});
	}
	
	private void makeActions(){
		rotate180Action = new Action("Rotieren um 180°") {
			{
				setToolTipText("Rotieren um 180°");
				setImageDescriptor(Images.IMG_OK.getImageDescriptor());
			}
			
			@Override
			public void run(){
				doRotate(180);
			}
		};
		editAction = new Action("Einem Patienten zuordnen") {
			{
				setToolTipText("Einem Patienten zuordnen");
				setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
			}
			
			public void run(){
				currViewer = treeViewers.get(folderx.getSelectionIndex());
				ISelection selection = currViewer.getSelection();
				Patient pat = Activator.getOPPlanPatient();
				DocHandle_Mv dh =
					(DocHandle_Mv) ((IStructuredSelection) selection).getFirstElement();
				ReturnDocHandleAndCreateAufgebot result =
					DocHandle_Mv.assimilate(dh.getMimetype(), pat, dh);
				if (result != null)
					currViewer.remove(dh);
			}
		};
		reloadAction = new Action("OP-Plan jetzt neu einlesen") {
			{
				setToolTipText("OP-Plan jetzt neu einlesen");
				setImageDescriptor(Images.IMG_REFRESH.getImageDescriptor());
			}
			
			@Override
			public void run(){
				for (int i = 0; i < treeViewers.size(); i++) {
					currViewer = treeViewers.get(i);
					currViewer.refresh();
				}
			}
		};
		doubleClickAction = new Action() {
			public void run(){
				currViewer = treeViewers.get(folderx.getSelectionIndex());
				ISelection selection = currViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				DocHandle_Mv dh = (DocHandle_Mv) obj;
				if (dh.isCategory()) {
					if (currViewer.getExpandedState(dh)) {
						currViewer.collapseToLevel(dh, TreeViewer.ALL_LEVELS);
					} else {
						currViewer.expandToLevel(dh, TreeViewer.ALL_LEVELS);
					}
				} else {
					// don't want to open here...
					// dh.execute();
				}
				
			}
		};
		deleteAction = new Action("Löschen") {
			{
				setToolTipText("Löschen");
				setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
			}
			
			@Override
			public void run(){
				int ix = folderx.getSelectionIndex();
				currViewer = treeViewers.get(folderx.getSelectionIndex());
				ISelection selection = currViewer.getSelection();
				Object obj = ((IStructuredSelection) selection).getFirstElement();
				DocHandle_Mv dh = (DocHandle_Mv) obj;
				String faxDate = calcFaxDate(dh.getMimetype());
				String planDate = dh.getDate();
				String dokName = "";
				switch (ix) {
				case 0:
					dokName = "OP-Plan";
					break;
				case 1:
					dokName = "OP-Anmeldung";
					break;
				case 2:
					dokName = "OP-Bericht";
					break;
				default:
					dokName = "\"" + dh.getTitle() + "\"";
					break;
				}
				if (SWTHelper.askYesNo("Dokument löschen",
					"Wollen Sie dieses Dokument wirklich löschen?\n" + dokName + " vom " + planDate
						+ ", Fax vom " + faxDate)) {
					dh.delete();
					currViewer.remove(dh);
					currViewer.refresh();
				}
			}
		};
	}
	
	private final ElexisEventListenerImpl eeli_dochandle = new ElexisEventListenerImpl(
		DocHandle_Mv.class, ElexisEvent.EVENT_CREATE | ElexisEvent.EVENT_DELETE) {
		@Override
		public void run(ElexisEvent ev){
			for (int i = 0; i < treeViewers.size(); i++) {
				currViewer = treeViewers.get(i);
				currViewer.refresh();
			}
		}
	};
	
	private static boolean copyfile(String srFile, String dtFile){
		File f1 = new File(srFile);
		File f2 = new File(dtFile);
		try {
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
			SWTHelper.alert("", "Die Datei " + f1.getPath() + "konnte nicht gelesen werden.");
			System.out.println(ex.getMessage() + " in the specified directory.");
			return false;
		} catch (IOException e) {
			SWTHelper.alert("", "Die Datei " + f1.getPath() + "konnte nicht gelesen werden.");
			System.out.println(e.getMessage());
			return false;
		}
		return true;
	}
	
	void doRotate(int degrees){
		// *** get the selected file
		// +++++File file = getSelection();
		currViewer = treeViewers.get(folderx.getSelectionIndex());
		ISelection selection = currViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		DocHandle_Mv dh = (DocHandle_Mv) obj;
		
		// *** need the temp dir
		File tempDir = CoreHub.getTempDir();
		
		// *** now write to temp dir, get the selected file
		String filename = tempDir.getAbsolutePath() + File.pathSeparator + dh.getMimetype();
		try {
			dh.storeExternal(filename);
		} catch (ElexisException e2) {
			e2.printStackTrace();
			return;
		}
		File file = new File(filename);
		
		// ********* now convert pdf to multipage tif with imageMagick
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
			out1.print("\"F:\\Program Files\\Bullzip\\PDF Printer\\gs\\gswin32c.exe\"" + " ");
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
// String line2 = null;
// Process pr2;
		try {
			rt.exec(commands2);
// pr2 = rt.exec(commands2);
// BufferedReader input1 =
// new BufferedReader(new InputStreamReader(pr2.getInputStream()));
// line2 = input1.readLine();
// while (line2 != null) {
// System.out.println(line2);
// line2 = input1.readLine();
// }
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
		// System.out.println(res);
		
		// *** replace file in db with the rotated one
		Patient pat = Activator.getOPPlanPatient();
		dh.replaceFile(filename, pat);
		
		// *** refresh display
		IStructuredSelection sel = (IStructuredSelection) currViewer.getSelection();
		File selFile;
		try {
			selFile = dh.getStorageFile(true);
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
			if (copyfile(filename, newUrl))
				browserViewer.setUrl(newUrl);
			else
				browserViewer.setUrl(BLANKPAGE);
		} catch (ElexisException e) {
			e.printStackTrace();
		}
	}
	
	public File getSelection(){
		currViewer = treeViewers.get(folderx.getSelectionIndex());
		IStructuredSelection sel = (IStructuredSelection) currViewer.getSelection();
		DocHandle_Mv dh = (DocHandle_Mv) (sel.getFirstElement());
		if (sel.isEmpty()) {
			return null;
		}
		File selFile = null;
		try {
			selFile = dh.getStorageFile(true);
		} catch (ElexisException e) {
			e.printStackTrace();
		}
		return selFile;
	}
	
	public static Object[] arrayPrepend(Object[] oldArray, Object o){
		Object[] newArray =
			(Object[]) Array.newInstance(oldArray.getClass().getComponentType(),
				oldArray.length + 1);
		System.arraycopy(oldArray, 0, newArray, 1, oldArray.length);
		newArray[0] = o;
		return newArray;
	}
	
}
