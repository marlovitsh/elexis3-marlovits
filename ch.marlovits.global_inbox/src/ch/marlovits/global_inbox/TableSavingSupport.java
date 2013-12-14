package ch.marlovits.global_inbox;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.TypedListener;

import ch.elexis.core.data.activator.CoreHub;
//import ch.elexis.global_inbox.TableSavingSupport.TableSavingSupportContributionItem;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class TableSavingSupport {
// /*
// * the error handling is often done quite simple with try/catch. This way any errors are catched
// * and anyway we don't know anything better than to suspend the called method.
// */
//
// Menu clsHeaderMenu;
// Menu staticTableMenu;
// Menu clsSubMenuColumns;
// Table staticTable;
// TableViewer staticTableViewer;
// Object[] viewerComparators;
//
// boolean listenerAdded = false;
// boolean addMenuItemsBefore = true;
//
// String PREFS_NAME_BASE;
// final static String COL_ORDER_SAVING = "colorder";
// final static String COL_WIDTH_SAVING = "colwidths";
// final static String COL_SORT_SAVING = "colsorting";
//
// static SettingsPreferenceStore prefsStore = new SettingsPreferenceStore(CoreHub.localCfg);
//
// static ArrayList<TableColumn> fullList;
//
// // save the full list of the menus (used for showing/hiding colums)
// // fullList = new ArrayList<TableColumn>(Arrays.asList(table.getColumns()));
//
// /**
// * adds support for saving column order, column widhts, column sorting
// *
// * @param table
// * @param addSorting
// * @param addWidthSaving
// * @param restartWithOriginal
// * @param showHidingMenu
// */
// public TableSavingSupport(TableViewer tableViewer, final boolean addSorting,
// final boolean addWidthSaving, boolean restartWithOriginal, boolean showHidingMenu){
// staticTable = tableViewer.getTable();
// staticTableViewer = tableViewer;
// readViewerComparators();
// // calc saving prefs name = <className>
// PREFS_NAME_BASE = this.getClass().getName() + "/";
// addColOrderAndWidthListener(addSorting, addWidthSaving, restartWithOriginal, showHidingMenu);
// }
//
// public TableSavingSupport(String fullPrefsName, TableViewer tableViewer,
// final boolean addSorting, final boolean addWidthSaving, boolean restartWithOriginal,
// boolean showHidingMenu){
// staticTable = tableViewer.getTable();
// staticTableViewer = tableViewer;
// readViewerComparators();
// // staticTable.getColumn(0).get
// // calc saving prefs name = <className>
// PREFS_NAME_BASE = fullPrefsName;
// addColOrderAndWidthListener(addSorting, addWidthSaving, restartWithOriginal, showHidingMenu);
// }
//
// /**
// * Save the column order and/or the column widths to the prefs. This must be called for each
// * column of a table
// *
// * @param tableColumn
// * the tableColumn for which the necessary listeners should be added
// * @param addOrdering
// * true if sorting should be added
// * @param addWidthSaving
// * true if saving widhts should be added
// */
// void addColOrderAndWidthListener(final boolean addOrdering, final boolean addWidthSaving,
// boolean restartWithOriginal, boolean showHidingMenu){
// // create context menu for the column header and the column body, add the listener
// createHeaderMenu(staticTable);
// createBodyMenu(staticTable);
//
// // *********************************************************************
// // *** adding our menu items to a new menu OR to an existing menu
// // *********************************************************************
// // *** test if there is already some kind of menu
// // Menu currentMenu = staticTable.getMenu();
// // if (currentMenu == null) {
// // // *** there is no menu yet - simply use our menu
// // } else {
// // // *** there already IS a menu - add our items to the existing menu with a menuListener
// // final MenuManager menuMgr =
// // (MenuManager) currentMenu
// // .getData("org.eclipse.jface.action.MenuManager.managerKey");
// // clsHeaderMenu = currentMenu;
// // boolean removeAllWhenShown = menuMgr.getRemoveAllWhenShown();
// // if (removeAllWhenShown) {
// // // if removeAllWhenShown then we need to re-add our items every time again
// // if (!listenerAdded) {
// // menuMgr.addMenuListener(new IMenuListener() {
// // @Override
// // public void menuAboutToShow(IMenuManager manager){
// // addHeaderMenuItems(menuMgr, addMenuItemsBefore);
// // }
// // });
// // listenerAdded = true;
// // }
// // } else {
// // // if not removeAllWhenShown then we just need to add out items once
// // addHeaderMenuItems(menuMgr, addMenuItemsBefore);
// // }
// // }
//
// // *********************************************************************
// // *** detect whether the click is in the header or not
// // *********************************************************************
// staticTable.addListener(SWT.MenuDetect, new Listener() {
// public void handleEvent(Event event){
// Point pt =
// staticTable.getShell().getDisplay()
// .map(null, staticTable, new Point(event.x, event.y));
// Rectangle clientArea = staticTable.getClientArea();
// boolean header =
// clientArea.y <= pt.y && pt.y < (clientArea.y + staticTable.getHeaderHeight());
// // staticTable.setMenu(header ? clsHeaderMenu : staticTableMenu);
// if (header) {
// System.out.println("Click in header");
// Menu currentMenu = staticTable.getMenu();
// if (currentMenu == null) {
// // simply use our menu
// staticTable.setMenu(clsHeaderMenu);
// } else {
// // add our items to the existing menu with a menuListener
// // read the currently used menuManager
// final MenuManager menuMgr =
// (MenuManager) currentMenu
// .getData("org.eclipse.jface.action.MenuManager.managerKey");
// if (menuMgr != null) {
// menuMgr.addMenuListener(new IMenuListener() {
// @Override
// public void menuAboutToShow(IMenuManager manager){
// if (!listenerAdded) {
// // addHeaderMenuItems(menuMgr);
// listenerAdded = true;
// }
// }
// });
// }
// // menuMgr.setRemoveAllWhenShown(true);
// }
// } else {
// System.out.println("Click in body");
// }
// }
// });
//
// // important: dispose the menus (only the current menu, set with setMenu(), will be
// // automatically disposed)
// staticTable.addListener(SWT.Dispose, new Listener() {
// public void handleEvent(Event event){
// clsHeaderMenu.dispose();
// staticTableMenu.dispose();
// }
// });
//
// if (!restartWithOriginal) {
// readColOrderPrefs();
// readColWidthPrefs();
// readTableSortingPrefs();
// }
//
// // *********************************************************************
// // *** adding saving of ordering, sorting and resizing of columns
// // *********************************************************************
// for (TableColumn tableColumn : staticTable.getColumns()) {
// createSubMenuItem(clsSubMenuColumns, tableColumn);
//
// // +++++
// // tableColumn.setImage(Desk.getImage(Desk.IMG_ADDITEM));
//
// // *********************************************************************
// // *** for saving of columns sorting
// tableColumn.addListener(SWT.Selection, new Listener() {
// @Override
// public void handleEvent(Event event){
// try {
// writeTableSortingPrefs();
// System.out.println("handleEvent");
// String colTitle =
// ((TableColumn) (event.widget)).getParent().getSortColumn().getText();
// System.out.println("colTitle: " + colTitle);
// } catch (Exception ex) {
// System.out.println("*** some error in handleEvent ***");
// }
// }
//
// });
//
// tableColumn.addControlListener(new ControlListener() {
// // *********************************************************************
// // *** for saving of columnorder
// @Override
// public void controlMoved(ControlEvent e){
// try {
// if (addOrdering) {
// // write current order to prefs
// writeColOrderPrefs();
// // recreate the hiding menu to reflect the new order
// for (MenuItem item : clsSubMenuColumns.getItems())
// item.dispose();
// int[] colOrder = staticTable.getColumnOrder();
// for (int colIx : colOrder)
// createSubMenuItem(clsSubMenuColumns, staticTable.getColumn(colIx));
// }
// } catch (Exception ex) {
//
// }
// }
//
// // *********************************************************************
// // *** for saving of columnwidths
// @Override
// public void controlResized(ControlEvent e){
// try {
// if (addWidthSaving) {
// // if no original sizes have been written to the prefs then do it right
// // now
// String originalWidths =
// CoreHub.localCfg.get(PREFS_NAME_BASE + COL_WIDTH_SAVING /*
// * +++++ DEFAULT
// * !!!!
// */, "");
// if ((originalWidths.isEmpty())
// || (("," + originalWidths + ",").indexOf(",0,")) >= 0) {
// writeColWidthOriginalPrefs();
// }
// // write current col widths to prefs
// writeColWidthPrefs();
// }
// } catch (Exception ex) {
//
// }
//
// }
// });
// }
// }
//
// /**
// * create the submenu for item restore
// * @param parentMenu
// * @param parentItem
// */
// void createSubMenuRestore(Menu parentMenu, MenuItem parentItem){
// // create the submenu
// Menu subMenuRestore = new Menu(parentMenu);
// parentItem.setMenu(subMenuRestore);
// // add submenu items
// final MenuItem itemRestoreColumnAll = new MenuItem(subMenuRestore, SWT.NONE);
// itemRestoreColumnAll.setText("Alles");
// final MenuItem itemRestoreColumnOrder = new MenuItem(subMenuRestore, SWT.NONE);
// itemRestoreColumnOrder.setText("Reihenfolge");
// final MenuItem itemRestoreColumnWidths = new MenuItem(subMenuRestore, SWT.NONE);
// itemRestoreColumnWidths.setText("Spaltenbreiten");
// final MenuItem itemRestoreColumnSorting = new MenuItem(subMenuRestore, SWT.NONE);
// itemRestoreColumnSorting.setText("Sortierung");
// }
//
// void createSubMenuHideColumns(Menu parentMenu, MenuItem parentItem) {
// Menu clsSubMenuColumns = new Menu(parentMenu);
// parentItem.setMenu(clsSubMenuColumns);
// readColOrderPrefs();
// for (TableColumn tableColumn : staticTable.getColumns())
// createSubMenuItem(clsSubMenuColumns, tableColumn);
// parentItem.addListener(SWT.Selection, new Listener() {
// public void handleEvent(Event event){
// readColOrderPrefs();
// // /////ESRView.tableViewerColumnSorter.setSorter(SWT.UP);
// System.out.println("readColOrderPrefs called");
// // TableColumn tableColumn = (TableColumn) (event.widget);
// // if (tableColumn.getWidth() == 0) {
// // tableColumn.setWidth(150);
// // tableColumn.setResizable(true);
// // } else {
// // tableColumn.setWidth(0);
// // tableColumn.setResizable(false);
// // }
// }
// });
// }
//
// /**
// *
// * This adds all our menu items to the menuMgr
// *
// * @param menuMgr
// * @param prepend
// */
// void addHeaderMenuItems(MenuManager menuMgr, boolean prepend){
// LinkedList<TableSavingSupportContributionItem> itemList =
// new LinkedList<TableSavingSupportContributionItem>();
// itemList.add(new TableSavingSupportContributionItem("Spalte ausblenden") {
// @Override
// public void fill(Menu parent, int index){
// if (!added) {
// super.fill(parent, index);
// final MenuItem menuItem = parent.getItem(index);
// menuItem.addSelectionListener(new SelectionListener() {
// @Override
// public void widgetSelected(SelectionEvent e){
// System.out.println(menuItem.getText() + " selected");
// }
//
// @Override
// public void widgetDefaultSelected(SelectionEvent e){}
// });
// }
// };
// });
// itemList.add(new TableSavingSupportContributionItem("Tabellenspalten->") {
// @Override
// public void fill(Menu parent, int index){
// if (!added) {
// super.fill(parent, index);
// final MenuItem menuItem = parent.getItem(index);
// menuItem.addSelectionListener(new SelectionListener() {
// @Override
// public void widgetSelected(SelectionEvent e){
// System.out.println(menuItem.getText() + " selected");
// }
//
// @Override
// public void widgetDefaultSelected(SelectionEvent e){}
// });
// createSubMenuHideColumns(parent, menuItem);
// }
// };
// });
// itemList.add(new TableSavingSupportContributionItem("Wiederherstellen") {
// @Override
// public void fill(Menu parent, int index){
// if (!added) {
// super.fill(parent, index);
// final MenuItem menuItem = parent.getItem(index);
// menuItem.addSelectionListener(new SelectionListener() {
// @Override
// public void widgetSelected(SelectionEvent e){
// System.out.println(menuItem.getText() + " selected");
// }
//
// @Override
// public void widgetDefaultSelected(SelectionEvent e){}
// });
// createSubMenuRestore(parent, menuItem);
// }
// };
// });
//
// // *** add our items to the menu
// if (prepend) {
// menuMgr.insert(0, new TableSavingSupportContributionItem("-"));
// for (int i = itemList.size() - 1; i >= 0; i--)
// menuMgr.insert(0, itemList.get(i));
// } else {
// menuMgr.add(new TableSavingSupportContributionItem("-"));
// for (int i = 0; i < itemList.size(); i++)
// menuMgr.add(itemList.get(i));
// }
// }
//
// void createHeaderMenu(Table table){
// System.out.println("createHeaderMenu");
// // create the menu itself
// if (clsHeaderMenu == null) {
// clsHeaderMenu = new Menu(table.getShell(), SWT.POP_UP);
//
// // item hide column
// final MenuItem itemHideColumn = new MenuItem(clsHeaderMenu, SWT.NONE);
// itemHideColumn.setText("Spalte ausblenden");
// itemHideColumn.addListener(SWT.Selection, new Listener() {
// public void handleEvent(Event event){
// readColOrderPrefs();
// // /////ESRView.tableViewerColumnSorter.setSorter(SWT.UP);
// System.out.println("readColOrderPrefs called");
// // TableColumn tableColumn = (TableColumn) (event.widget);
// // if (tableColumn.getWidth() == 0) {
// // tableColumn.setWidth(150);
// // tableColumn.setResizable(true);
// // } else {
// // tableColumn.setWidth(0);
// // tableColumn.setResizable(false);
// // }
// }
// });
//
// // item for submenu for hiding/showing table columns
// final MenuItem itemShowHideColumns = new MenuItem(clsHeaderMenu, SWT.CASCADE);
// itemShowHideColumns.setText("Tabellenspalten");
// // create the empty submenu - the items are added via createSubMenuItem()
// clsSubMenuColumns = new Menu(clsHeaderMenu);
// itemShowHideColumns.setMenu(clsSubMenuColumns);
//
// // item for restoring original settings
// final MenuItem itemRestoreSettings = new MenuItem(clsHeaderMenu, SWT.CASCADE);
// itemRestoreSettings.setText("Wiederherstellen");
// // create the submenu
// Menu subMenuRestoreSettings = new Menu(clsHeaderMenu);
// itemRestoreSettings.setMenu(subMenuRestoreSettings);
// // add submenu items
// final MenuItem itemRestoreColumnAll = new MenuItem(subMenuRestoreSettings, SWT.NONE);
// itemRestoreColumnAll.setText("Alles");
// final MenuItem itemRestoreColumnOrder = new MenuItem(subMenuRestoreSettings, SWT.NONE);
// itemRestoreColumnOrder.setText("Reihenfolge");
// final MenuItem itemRestoreColumnWidths = new MenuItem(subMenuRestoreSettings, SWT.NONE);
// itemRestoreColumnWidths.setText("Spaltenbreiten");
// final MenuItem itemRestoreColumnSorting =
// new MenuItem(subMenuRestoreSettings, SWT.NONE);
// itemRestoreColumnSorting.setText("Sortierung");
// }
// }
//
// /**
// *
// * @param parentMenu
// * @param tableColumn
// */
// void createSubMenuItem(Menu parentMenu, final TableColumn tableColumn){
// // +++++final int savedSize = -1;
// // +++++final int fSavedSize = savedSize;
// final MenuItem menuItem = new MenuItem(parentMenu, SWT.CHECK);
// menuItem.setText(tableColumn.getText());
// menuItem.setSelection(tableColumn.getResizable() && tableColumn.getWidth() != 0);
// menuItem.addListener(SWT.Selection, new Listener() {
// public void handleEvent(Event event){
// if (tableColumn.getResizable() && tableColumn.getWidth() != 0) {
// // "hiding" means width of 0...
// // +++++savedSize = tableColumn.getWidth();
// tableColumn.setWidth(0);
// tableColumn.setResizable(false);
// menuItem.setSelection(false);
// } else {
// tableColumn.setWidth(150);
// tableColumn.setResizable(true);
// menuItem.setSelection(true);
// }
// }
// });
//
// // important: get rid of our handles for listeners
// menuItem.addDisposeListener(new DisposeListener() {
// @Override
// public void widgetDisposed(DisposeEvent e){
// for (Listener listener : e.widget.getListeners(SWT.Selection))
// (e.widget).removeListener(SWT.Selection, listener);
// }
//
// });
// }
//
// void createBodyMenu(Table table){
// staticTableMenu = new Menu(table.getShell(), SWT.POP_UP);
// MenuItem itemCascade = new MenuItem(staticTableMenu, SWT.CASCADE);
// itemCascade.setText("Open");
// MenuItem item = new MenuItem(staticTableMenu, SWT.PUSH);
// item.setText("Open");
// MenuItem subMenuItem = item;
// item = new MenuItem(staticTableMenu, SWT.PUSH);
// item.setText("Open With");
// new MenuItem(staticTableMenu, SWT.SEPARATOR);
// item = new MenuItem(staticTableMenu, SWT.PUSH);
// item.setText("Cut");
// item = new MenuItem(staticTableMenu, SWT.PUSH);
// item.setText("Copy");
// item = new MenuItem(staticTableMenu, SWT.PUSH);
// item.setText("Paste");
// new MenuItem(staticTableMenu, SWT.SEPARATOR);
// item = new MenuItem(staticTableMenu, SWT.PUSH);
// item.setText("Delete");
// Menu subMenu = new Menu(staticTableMenu);
// item = new MenuItem(subMenu, SWT.PUSH);
// item.setText("111");
// item = new MenuItem(subMenu, SWT.PUSH);
// item.setText("222");
// item = new MenuItem(subMenu, SWT.PUSH);
// item.setText("333");
// item = new MenuItem(subMenu, SWT.PUSH);
// item.setText("TEST PROC");
// itemCascade.setMenu(subMenu);
// item.addSelectionListener(new SelectionListener() {
// @Override
// public void widgetSelected(SelectionEvent e){
// readTableSortingPrefs();
// }
//
// @Override
// public void widgetDefaultSelected(SelectionEvent e){}
// });
// }
//
// public int[] integerToIntArray(Integer[] integerArray){
// try {
// int[] result = new int[integerArray.length];
// for (int i = 0; i < result.length; i++)
// result[i] = integerArray[i];
// return result;
// } catch (Exception ex) {
// return null;
// }
// }
//
// static public Integer[] intToIntegerArray(int[] intArray){
// try {
// Integer[] result = new Integer[intArray.length];
// for (int i = 0; i < result.length; i++)
// result[i] = intArray[i];
// return result;
// } catch (Exception ex) {
// return null;
// }
// }
//
// /**
// * read order of columns from prefs and set it in the table
// */
// void readColOrderPrefs(){
// // very simple error handling with try/catch is enough...
// try {
// Integer[] integerPrefs =
// (Integer[]) prefsStore.getArray(PREFS_NAME_BASE + COL_ORDER_SAVING, Integer.class);
// // if number of columns not equal then re-initialize array (set to empty)
// if (integerPrefs.length != staticTable.getColumnCount()) {
// prefsStore.setValue(PREFS_NAME_BASE + COL_ORDER_SAVING, "");
// return;
// }
// int[] intPrefs = integerToIntArray(integerPrefs);
// staticTable.setColumnOrder(intPrefs);
// } catch (Exception ex) {}
// }
//
// /**
// * get current colums order and write to prefs
// */
// void writeColOrderPrefs(){
// // very simple error handling with try/catch is enough...
// try {
// int[] currOrder = staticTable.getColumnOrder();
// prefsStore.setValue(PREFS_NAME_BASE + COL_ORDER_SAVING, intToIntegerArray(currOrder),
// Integer.class);
// } catch (Exception ex) {}
// }
//
// /**
// * read table sorting from prefs and set it in the table
// */
// void readTableSortingPrefs(){
// // very simple error handling with try/catch is enough...
// try {
// // Integer[] integerPrefs =
// // (Integer[]) prefsStore.getArray(PREFS_NAME_BASE + COL_SORT_SAVING, Integer.class);
// // TableColumn[] tableColums = staticTable.getColumns();
// // if (integerPrefs[0] != -1)
// // staticTable.setSortColumn(tableColums[integerPrefs[0]]);
// // staticTable.setSortDirection(integerPrefs[1]);
// // staticTableViewer.refresh();
// // int testCol = integerPrefs[0];
// // TableColumn tableColumn = tableColums[testCol /* integerPrefs[0] */];
// //
// // // //////////////////////////////////
// // ViewerComparator viewerComparator = ESRView.tableViewerColumnSorter[testCol /*
// // * integerPrefs[
// // */];
// //
// // // staticTableViewer.setSorter((ViewerSorter) viewerComparator.);
// // staticTable.setSortColumn(tableColumn);
// // staticTable.setSortDirection(SWT.DOWN);
// // ViewerComparator currComparator = staticTableViewer.getComparator();
// // // if (viewerComparator.equals(currComparator)) {
// // // staticTableViewer.refresh();
// // // } else {
// // // staticTableViewer.setComparator(viewerComparator);
// // // }
// // staticTableViewer.setComparator(viewerComparator);
// // staticTableViewer.refresh();
// // // System.out.println("");
// // // //////////////////////////////////
// // // //////////////////////////////////
// //
// // // ESRView.tableViewerColumnSorter[integerPrefs[0]].setSorter(1);
// // //
// // // Object obj = tableColumn.getData();
// // // Class[] parameterTypes = new Class[1];
// // // parameterTypes[0] = int.class;
// // // Class theClass = (obj.getClass());
// // // Method[] methods111 = theClass.getMethods();
// // // Class enclosingClass = theClass.getEnclosingClass();
// // // Method[] methods = enclosingClass.getMethods();
// // //
// // // Class[] paramTypes = new Class[] {
// // // int.class
// // // };
// // // Method method_setSorter = enclosingClass.getMethod("setSorter", paramTypes);
// // // Object[] params = new Object[] {
// // // new Integer(1)
// // // };
// // // method_setSorter.invoke(obj, params);
// // //
// // // Integer[] parameters = new Integer[1];
// // // parameters[0] = new Integer(1);
// // // method_setSorter.invoke(obj, 1);
// // //
// // // // TableViewerColumnSorter eventListener = (TableViewerColumnSorter)
// // // // tableColumn.getData();
// // // staticTableViewer.setComparator((ViewerComparator) enclosingClass.cast(obj));
// // // staticTableViewer.refresh();
// // // staticTable.redraw();
// // //
// // // // ESRView.tableViewerColumnSorter.setSorter(integerPrefs[0]);
// } catch (Exception ex) {
// ex.printStackTrace();
// System.out.println();
// }
// }
//
// /**
// * get current table sorting and write to prefs
// */
// void writeTableSortingPrefs(){
// // very simple error handling with try/catch is enough...
// try {
// TableColumn currSortingCol = staticTable.getSortColumn();
// TableColumn[] tableColums = staticTable.getColumns();
// Integer[] colSortingArray = new Integer[2];
// if (currSortingCol == null) {
// colSortingArray[0] = -1;
// } else {
// for (int i = 0; i < tableColums.length; i++) {
// if (currSortingCol.equals(tableColums[i])) {
// colSortingArray[0] = i;
// break;
// }
// }
// }
// colSortingArray[1] = staticTable.getSortDirection();
// prefsStore.setValue(PREFS_NAME_BASE + COL_SORT_SAVING, colSortingArray, Integer.class);
// } catch (Exception ex) {
// System.out.println("asdfas");
// }
// }
//
// /**
// * read column widths from prefs and set it in the table
// */
// void readColWidthPrefs(){
// // very simple error handling with try/catch is enough...
// try {
// Integer[] integerPrefs =
// (Integer[]) prefsStore.getArray(PREFS_NAME_BASE + COL_WIDTH_SAVING, Integer.class);
// // if number of columns not equal then re-initialize array (set to empty)
// if (integerPrefs.length != staticTable.getColumnCount()) {
// prefsStore.setValue(PREFS_NAME_BASE + COL_WIDTH_SAVING, "");
// return;
// }
// TableColumn[] columns = staticTable.getColumns();
// for (int i = 0; i < integerPrefs.length; i++)
// columns[i].setWidth(integerPrefs[i]);
// } catch (Exception ex) {}
// }
//
// /**
// * get current column widths and write to prefs
// */
// void writeColWidthPrefs(){
// // very simple error handling with try/catch is enough...
// try {
// TableColumn[] columns = staticTable.getColumns();
// Integer[] colWidthArray = new Integer[columns.length];
// for (int i = 0; i < columns.length; i++)
// colWidthArray[i] = columns[i].getWidth();
// prefsStore.setValue(PREFS_NAME_BASE + COL_WIDTH_SAVING, colWidthArray, Integer.class);
// } catch (Exception ex) {}
// }
//
// /**
// * read column widths from prefs and set it in the table. THIS IS ONLY FOR SAVING/RESTORING THE
// * INITIAL/DEFAULT VALUES
// */
// void readColWidthOriginalPrefs(){
// int[] currPrefs =
// readIntArrayPrefs_(PREFS_NAME_BASE + COL_WIDTH_SAVING /* +++++ DEFAULT!!!! */);
// if (currPrefs.length == 0)
// return;
// TableColumn[] columns = staticTable.getColumns();
// for (int i = 0; i < currPrefs.length; i++)
// columns[i].setWidth(currPrefs[i]);
// }
//
// /**
// * get current column widths and write to prefs. THIS IS ONLY FOR SAVING/RESTORING THE
// * INITIAL/DEFAULT VALUES
// */
// void writeColWidthOriginalPrefs(){
// // +++++
// // if (staticTable.isDisposed())
// // return;
// // TableColumn[] columns = staticTable.getColumns();
// // int[] colWidthArray = new int[columns.length];
// // for (int i = 0; i < columns.length; i++)
// // colWidthArray[i] = columns[i].getWidth();
// // writeIntArrayPrefs_(PREFS_NAME_BASE + COL_WIDTH_SAVING /* +++++ DEFAULT!!!! */,
// // colWidthArray);
// }
//
// /**
// * read int array from prefs (comma delimited string)
// *
// * @param prefsName
// * the name for the prefs
// * @return the int[] array created from the prefs
// */
// static int[] readIntArrayPrefs_(String prefsName){
// String prefsStr = CoreHub.localCfg.get(prefsName, "");
// if (!prefsStr.isEmpty()) {
// String[] prefsStrSplitted = prefsStr.split(",");
// int[] prefsIntArray = new int[prefsStrSplitted.length];
// for (int i = 0; i < prefsStrSplitted.length; i++)
// prefsIntArray[i] = Integer.parseInt(prefsStrSplitted[i]);
// return prefsIntArray;
// } else {
// return new int[0];
// }
// }
//
// /**
// * write int array to prefs (comma delimited string)
// *
// * @param prefsName
// * the name for the prefs
// * @param prefsArray
// * the int[] to be written to the prefs
// * @return true on success, false if not
// */
// static boolean writeIntArrayPrefs_(String prefsName, int[] prefsArray){
// String prefsArrayStr = "";
// for (int i = 0; i < prefsArray.length; i++)
// prefsArrayStr = prefsArrayStr + prefsArray[i] + ",";
// prefsArrayStr = prefsArrayStr.substring(0, prefsArrayStr.length() - 1);
// return CoreHub.localCfg.set(prefsName, prefsArrayStr);
// }
//
// void hideColumn(TableViewer tableViewer, TableColumn tableColumn){
// // find the index of tableColum
// int tableColumnIndex = fullList.indexOf(tableColumn);
// if (tableColumnIndex < 0)
// return;
//
// // really dispose of the tableColumn
// tableViewer.getTable().getColumn(tableColumnIndex).dispose();
//
// // refresh our display
// tableViewer.refresh();
//
// // ************************************
//
// int emailIndex = -1;
// for (int i = 0; i < tableViewer.getColumnProperties().length; i++) {
// if (tableViewer.getColumnProperties()[i].toString().equals("email")) {
// emailIndex = i;
// break;
// }
// }
//
// ArrayList list = new ArrayList(Arrays.asList(tableViewer.getCellEditors()));
// list.remove(emailIndex);
// CellEditor[] editors = new CellEditor[list.size()];
// list.toArray(editors);
// tableViewer.setCellEditors(editors);
//
// list = new ArrayList(Arrays.asList(tableViewer.getColumnProperties()));
// list.remove(emailIndex);
// String[] columnProperties = new String[list.size()];
// list.toArray(columnProperties);
// tableViewer.setColumnProperties(columnProperties);
//
// // really dispose of the tableColumn
// tableViewer.getTable().getColumn(emailIndex).dispose();
// // refresh our display
// tableViewer.refresh();
// }
//
// class TableSavingSupportContributionItem extends Object implements IContributionItem {
//
// String itemName;
// boolean added = false;
//
// public TableSavingSupportContributionItem(String itemName){
// this.itemName = itemName;
// }
//
// @Override
// public void dispose(){
// // TODO Auto-generated method stub
// // System.out.println("dispose() called");
// }
//
// @Override
// public void fill(Composite parent){
// // TODO Auto-generated method stub
// // System.out.println("fill(Composite parent) called");
// }
//
// /**
// * this just fills the menu item's text.<br>
// * there is no action yet - must be overridden!
// */
// @Override
// public void fill(Menu parent, int index){
// if (!added) {
// // System.out.println("fill(Menu parent, int index) called");
// // TODO Auto-generated method stub
// // MenuItem theItem = parent.getItem(index);
// // theItem.setText("asdfasdfa");
// MenuItem tmpItem;
// if (itemName.equalsIgnoreCase("-"))
// tmpItem = new MenuItem(parent, SWT.SEPARATOR, index);
// else
// tmpItem = new MenuItem(parent, SWT.CASCADE, index);
// final MenuItem itemShowHideColumns = tmpItem;
// itemShowHideColumns.setText(itemName);
// // ///////////////////itemShowHideColumns.setMenu(clsSubMenuColumns);
// // itemShowHideColumns.addSelectionListener(new SelectionListener() {
// // @Override
// // public void widgetSelected(SelectionEvent e){
// // System.out.println(itemShowHideColumns.getText() + " selected");
// // }
// //
// // @Override
// // public void widgetDefaultSelected(SelectionEvent e){}
// // });
// }
// }
//
// @Override
// public void fill(ToolBar parent, int index){
// // TODO Auto-generated method stub
// // System.out.println("fill(ToolBar parent, int index) called");
// }
//
// @Override
// public void fill(CoolBar parent, int index){
// // TODO Auto-generated method stub
// // System.out.println("fill(CoolBar parent, int index) called");
// }
//
// @Override
// public String getId(){
// // TODO Auto-generated method stub
// // System.out.println("getId() called");
// return null;
// }
//
// @Override
// public boolean isEnabled(){
// // TODO Auto-generated method stub
// // System.out.println("isEnabled() called");
// return true;
// }
//
// @Override
// public boolean isDirty(){
// // TODO Auto-generated method stub
// // System.out.println("isDirty() called");
// return false;
// }
//
// @Override
// public boolean isDynamic(){
// // TODO Auto-generated method stub
// // System.out.println("isDynamic() called");
// return true;
// }
//
// @Override
// public boolean isGroupMarker(){
// // TODO Auto-generated method stub
// // System.out.println("isGroupMarker() called");
// return false;
// }
//
// @Override
// public boolean isSeparator(){
// // TODO Auto-generated method stub
// // System.out.println("isSeparator() called");
// if (itemName.equalsIgnoreCase("-")) {
// // System.out.println("isSeparator() called - true");
// return true;
// } else {
// return false;
// }
// }
//
// @Override
// public boolean isVisible(){
// // TODO Auto-generated method stub
// // System.out.println("isVisible() called");
// return true;
// }
//
// @Override
// public void saveWidgetState(){
// // TODO Auto-generated method stub
// // System.out.println("saveWidgetState() called");
// }
//
// @Override
// public void setParent(IContributionManager parent){
// // TODO Auto-generated method stub
// // System.out.println("setParent(IContributionManager parent) called");
// }
//
// @Override
// public void setVisible(boolean visible){
// // TODO Auto-generated method stub
// // System.out.println("setVisible(boolean visible) called");
// }
//
// @Override
// public void update(){
// // TODO Auto-generated method stub
// // System.out.println("update() called");
// }
//
// @Override
// public void update(String id){
// // TODO Auto-generated method stub
// // System.out.println("update(String id) called");
// }
//
// }
//
// protected void readViewerComparators(){
// // public void setSorter(int direction){
// TableColumn[] tableColumns = staticTable.getColumns();
// Object[] viewerComparators = new Object[tableColumns.length];
// for (int i = 0; i < tableColumns.length; i++) {
// TableColumn tableColumn = tableColumns[i];
// Listener[] listeners = tableColumn.getListeners(SWT.Selection);
// // find selection listener for the table column which is of type ViewerComparator which
// // in turn may have been set for the column for sorting
// for (Listener listener : listeners) {
// System.out.println();
// try {
// // use the getEventListener which should actually not be used but for I haven't
// // found another reliable way
// Method method_getEventListener =
// (((listeners[0]).getClass())).getMethod("getEventListener");
// Object eventListenerObj = method_getEventListener.invoke(listeners[0]);
// String eventListenerObjClass =
// eventListenerObj.getClass().getName().split("\\$")[0];
// Class baseClass = Class.forName(eventListenerObjClass);
// Class isThisTheViewerComparatorOrNot = baseClass.getSuperclass();
// Field[] declaredFields = baseClass.getDeclaredFields();
// Class enclosingClass = baseClass.getEnclosingClass();
// String isThisTheViewerComparatorOrNotStr =
// isThisTheViewerComparatorOrNot.getSimpleName();
// if (isThisTheViewerComparatorOrNotStr.equalsIgnoreCase("ViewerComparator")) {
// tableColumn.setData(eventListenerObj);
// viewerComparators[i] = eventListenerObj;
// break;
// }
// } catch (NoSuchMethodException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (SecurityException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (IllegalAccessException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (IllegalArgumentException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (InvocationTargetException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// } catch (ClassNotFoundException e) {
// // TODO Auto-generated catch block
// e.printStackTrace();
// }
// }
// }
// }
//
// public String getPrefsNameColumnOrder(){
// return PREFS_NAME_BASE + COL_ORDER_SAVING;
// }
//
// public String getPrefsNameColumnSorting(){
// return PREFS_NAME_BASE + COL_SORT_SAVING;
// }
//
// public String getPrefsNameWidthsSaving(){
// return PREFS_NAME_BASE + COL_WIDTH_SAVING;
// }
//
}
