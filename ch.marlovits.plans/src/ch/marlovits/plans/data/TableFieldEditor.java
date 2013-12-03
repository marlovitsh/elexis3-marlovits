package ch.marlovits.plans.data;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.internal.win32.OS;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

public abstract class TableFieldEditor extends FieldEditor {
	
	/**
	 * The table widget; <code>null</code> if none (before creation or after disposal).
	 */
	protected Table table;
	
	/**
	 * The button box containing the Add, Remove, Up, and Down buttons; <code>null</code> if none
	 * (before creation or after disposal).
	 */
	private Composite buttonBox;
	
	/**
	 * The Add button.
	 */
	private Button addButton;
	
	/**
	 * The Edit button.
	 */
	private Button editButton;
	
	/**
	 * The Remove button.
	 */
	private Button removeButton;
	
	/**
	 * The selection listener.
	 */
	private SelectionListener selectionListener;
	
	private final String[] columnNames;
	
	private final int[] columnWidths;
	
	// +++++ START
	
	/**
	 * the default delimiter used for separating records in the string saved to the prefs
	 */
	protected static final String DEFAULTRECORDDELIMITER = "\n";
	/**
	 * the default delimiter used for separating items withing a tableItem in the string saved to
	 * the prefs
	 */
	protected static final String DEFAULTITEMDELIMITER = "\t";
	
	protected static String defaultRecordDelimiter = DEFAULTRECORDDELIMITER;
	protected static String defaultItemDelimiter = DEFAULTITEMDELIMITER;
	
	/**
	 * this sets the record delimiter used to save the table to the prefs. The default record
	 * delimiter is DEFAULTRECORDDELIMITER, "\n". Used this if you need to save items with "\n*
	 * inside the cell contents.
	 * 
	 * @param delimiter
	 *            the delimiter to be used to separate records in the prefs
	 */
	public void setRecordDelimiter(String delimiter){
		defaultRecordDelimiter = delimiter;
	}
	
	/**
	 * this sets the item delimiter used to save the table to the prefs. The default item delimiter
	 * is DEFAULTITEMDELIMITER, "\t". Used this if you need to save items with "\t" inside the cell
	 * contents.
	 * 
	 * @param delimiter
	 *            the delimiter to be used to separate items in the prefs
	 */
	public void setItemDelimiter(String delimiter){
		defaultItemDelimiter = delimiter;
	}
	
	// setTitles
	// set widths
	// set editors
	// different positions/arrangements for button box
	// hide/show button box
	// hide/show buttons of button box
	
	// *** this contains the editors
	// *** if no editor is specified, then the cell is not editable
	// *** if the Object is of type String then it contains the type
	// *** it may contain an actual editor
	private final Object[] editors = new Object[1];
	
	// *** true if editing starts directly with a click
	// *** if false then a doubleclick starts the editing
	private boolean directEditing = true;
	// *** true if the combo-menu/date-selector, etc should directly
	// *** pop down/up when the cell is selected
	private boolean directPop = true;
	
	// ***
	Point selectedCell = new Point(-1, -1);
	
	// +++++ END
	
	/**
	 * Creates a new table field editor
	 */
	protected TableFieldEditor(){
		columnNames = new String[0];
		columnWidths = new int[0];
	}
	
	/**
	 * Creates a table field editor.
	 * 
	 * @param name
	 *            the name of the preference this field editor works on
	 * @param labelText
	 *            the label text of the field editor
	 * @param columnTitles
	 *            the titles of columns
	 * @param columnWidths
	 *            the widths of columns
	 * @param parent
	 *            the parent of the field editor's control
	 * 
	 */
	protected TableFieldEditor(String name, String labelText, String[] columnTitles,
		int[] columnWidths, Composite parent){
		init(name, labelText);
		this.columnNames = columnTitles;
		this.columnWidths = columnWidths;
		parent.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		createControl(parent);
	}
	
	public void showPopup(Combo combo, boolean show){
		if (!combo.isDisposed()) {
			OS.SendMessage(combo.handle, OS.CB_SHOWDROPDOWN, show ? 1 : 0, 0);
		}
	}
	
	public boolean isPopupShowing(Combo combo){
		boolean result = false;
		if (!combo.isDisposed()) {
			result = (OS.SendMessage(combo.handle, OS.CB_GETDROPPEDSTATE, 0, 0) != 0);
		}
		return result;
	}
	
	/**
	 * Combines the given list of items into a single string. This method is the converse of
	 * <code>parseString</code>.
	 * 
	 * @param items
	 *            the list of items
	 * @return the combined string
	 * @see #parseString
	 */
	protected String createList(String[][] items){
		String result = "";
		String recDelim = "";
		for (String[] strArr : items) {
			String itemDelim = "";
			String lineResult = "";
			for (int i = 0; i < strArr.length; i++) {
				lineResult = lineResult + itemDelim + strArr[i];
				itemDelim = defaultItemDelimiter;
			}
			result = result + recDelim + lineResult;
			recDelim = defaultRecordDelimiter;
		}
		return result;
	}
	
	/**
	 * Splits the given string into a array of array of value. This method is the converse of
	 * <code>createList</code>.
	 * 
	 * @param string
	 *            the string
	 * @return an array of array of <code>string</code>
	 * @see #createList
	 */
	protected String[][] parseString(String string){
		String[][] result = null;
		String[] tableItems = string.split(defaultRecordDelimiter);
		for (int i = 0; i < tableItems.length; i++) {
			String tableItem = tableItems[i];
			String[] items = tableItem.split(defaultItemDelimiter);
			if (result == null)
				result = new String[tableItems.length][items.length];
			for (int j = 0; j < items.length; j++)
				result[i][j] = items[j];
		}
		return result;
	}
	
	/**
	 * Creates and returns a new value row for the table.
	 * <p>
	 * Subclasses must implement this method.
	 * </p>
	 * 
	 * @return a new item
	 */
	protected abstract String[] getNewInputObject();
	
	protected abstract String[] getChangedInputObject(TableItem tableItem);
	
	/**
	 * Creates the Add, Remove, Up, and Down button in the given button box.
	 * 
	 * @param box
	 *            the box for the buttons
	 */
	private void createButtons(Composite box){
		box.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		addButton = createPushButton(box, "New");
		editButton = createPushButton(box, "Edit");
		removeButton = createPushButton(box, "Remove");
	}
	
	/**
	 * Return the Add button.
	 * 
	 * @return the button
	 */
	protected Button getAddButton(){
		return addButton;
	}
	
	/**
	 * Return the Edit button.
	 * 
	 * @return the button
	 */
	protected Button getEditButton(){
		return editButton;
	}
	
	/**
	 * Return the Remove button.
	 * 
	 * @return the button
	 */
	protected Button getRemoveButton(){
		return removeButton;
	}
	
	/**
	 * Helper method to create a push button.
	 * 
	 * @param parent
	 *            the parent control
	 * @param key
	 *            the resource name used to supply the button's label text
	 * @return Button
	 */
	private Button createPushButton(Composite parent, String key){
		Button button = new Button(parent, SWT.PUSH);
		button.setText(key);
		button.setFont(parent.getFont());
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		button.addSelectionListener(getSelectionListener());
		return button;
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void adjustForNumColumns(int numColumns){
		Control control = getLabelControl();
		((GridData) control.getLayoutData()).horizontalSpan = numColumns;
		((GridData) table.getLayoutData()).horizontalSpan = numColumns - 1;
	}
	
	/**
	 * Creates a selection listener.
	 */
	public void createSelectionListener(){
		selectionListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event){
				Widget widget = event.widget;
				if (widget == addButton) {
					addPressed();
				} else if (widget == editButton) {
					editPressed();
				} else if (widget == removeButton) {
					removePressed();
				} else if (widget == table) {
					selectionChanged();
				}
			}
		};
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns){
		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = 2;
		gridData.widthHint = 550;
		gridData.heightHint = 200;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		
		table = getTableControl(composite);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		table.setLayoutData(gd);
		
		buttonBox = getButtonBoxControl(composite);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gd);
	}
	
	protected int getNumberOfItems(){
		int numberOfItems = 0;
		if (table != null) {
			numberOfItems = table.getItems().length;
		}
		return numberOfItems;
	}
	
	protected TableItem[] getItems(){
		TableItem[] items = null;
		if (table != null) {
			items = table.getItems();
		}
		return items;
	}
	
	protected String[][] getItemsStringArray(){
		TableItem[] items = null;
		if (table != null) {
			items = table.getItems();
		}
		int arrLength = items.length;
		int numOfItems = table.getColumnCount();
		String[][] result = new String[arrLength][numOfItems];
		for (int i = 0; i < arrLength; i++) {
			for (int i2 = 0; i2 < numOfItems; i2++) {
				result[i][i2] = items[i].getText(i2);
			}
		}
		return result;
	}
	
	protected void removeTableItems(){
		if (table != null) {
			table.removeAll();
		}
	}
	
	/**
	 * Initializes this field editor with the preference value from the preference store.
	 */
	protected void doLoad(){
		String[][] fromPrefsStore =
			parseString(getPreferenceStore().getString(getPreferenceName()));
		setItems(fromPrefsStore);
	}
	
	/**
	 * Initializes this field editor with the default preference value from the preference store.
	 */
	protected void doLoadDefault(){
		if (table != null) {
			String[][] fromPrefsStore =
				parseString(getPreferenceStore().getDefaultString(getPreferenceName()));
			setItems(fromPrefsStore);
		}
	}
	
	/**
	 * Stores the preference value from this field editor into the preference store.
	 */
	protected void doStore(){
		String[][] itemsStrArr = getItemsStringArray();
		getPreferenceStore().setValue(getPreferenceName(), createList(itemsStrArr));
	}
	
	/**
	 * Returns this field editor's button box containing the Add, Remove, Up, and Down button.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the button box
	 */
	public Composite getButtonBoxControl(Composite parent){
		if (buttonBox == null) {
			buttonBox = new Composite(parent, SWT.NULL);
			GridLayout layout = new GridLayout();
			layout.marginWidth = 0;
			buttonBox.setLayout(layout);
			createButtons(buttonBox);
			buttonBox.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event){
					addButton = null;
					editButton = null;
					removeButton = null;
					buttonBox = null;
				}
			});
			
		} else {
			checkParent(buttonBox, parent);
		}
		
		selectionChanged();
		return buttonBox;
	}
	
	/**
	 * Returns this field editor's table control.
	 * 
	 * @param parent
	 *            the parent control
	 * @return the table control
	 */
	public Table getTableControl(Composite parent){
		if (table == null) {
			table =
				new Table(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL
					| SWT.FULL_SELECTION);
			table.setFont(parent.getFont());
			table.setLinesVisible(true);
			table.setHeaderVisible(true);
			table.addSelectionListener(getSelectionListener());
			table.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent event){
					table = null;
				}
			});
			for (String columnName : columnNames) {
				TableColumn tableColumn = new TableColumn(table, SWT.LEAD);
				tableColumn.setText(columnName);
				tableColumn.setWidth(100);
				// +++++
			}
			if (columnNames.length > 0) {
				TableLayout layout = new TableLayout();
				if (columnNames.length > 1) {
					for (int i = 0; i < (columnNames.length - 1); i++) {
						layout.addColumnData(new ColumnWeightData(0, columnWidths[i], false));
						
					}
				}
				layout.addColumnData(new ColumnWeightData(100,
					columnWidths[columnNames.length - 1], true));
				table.setLayout(layout);
			}
			final TableEditor editor = new TableEditor(table);
			editor.horizontalAlignment = SWT.LEFT;
			editor.grabHorizontal = true;
			
			// +++++
			// *** this calls the editor
			table.addMouseListener(new MouseListener() {
				// *** editing on doubleclick
				@Override
				public void mouseDoubleClick(MouseEvent e){
					// *** start editing
					if (!directEditing)
						startEdit(e, editor);
				}
				
				@Override
				public void mouseDown(MouseEvent e){
					// *** detect which cell is selected
					Point pt = new Point(e.x, e.y);
					TableItem item = table.getItem(pt);
					if (item == null)
						return;
					for (int i = 0; i < table.getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							int index = table.indexOf(item);
							System.out.println("Item " + index + "-" + i);
							selectedCell.x = i;
							selectedCell.y = index;
						}
					}
					// *** start editing
					if (directEditing)
						startEdit(e, editor);
				}
				
				@Override
				public void mouseUp(MouseEvent e){}
				
			});
			table.addSelectionListener(new SelectionAdapter() {});
		} else {
			checkParent(table, parent);
		}
		return table;
	}
	
	public class CellEditor extends TableEditor {
		// ***remove any previous editor
		Control oldEditor;
		TableEditor tableEditor;
		
		public CellEditor(Table table){
			super(table);
			tableEditor = new TableEditor(table);
			tableEditor.horizontalAlignment = SWT.LEFT;
			tableEditor.grabHorizontal = true;
		}
		
		/**
		 * subclasses must implement this method if they want other than a simple string editor
		 * 
		 * @return
		 */
		public/* abstract */Control createEditorControl(){
			// *** the editor must be a child of the table
			Combo newEditor = new Combo(table, SWT.NONE);
			newEditor.setItems(new String[] {
				"1111", "222", "3333"
			});
			newEditor.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent me){
					Combo text = (Combo) tableEditor.getEditor();
					tableEditor.getItem().setText(selectedCell.x, text.getText());
				}
			});
			return newEditor;
		}
		
		public void startEditing(){
			if (oldEditor != null)
				oldEditor.dispose();
			// *** identify the selected row
			if (selectedCell.y < 0)
				return;
			TableItem item = table.getItem(selectedCell.y);
			if (item == null)
				return;
			
			// *** the editor must be a child of the table
			// +++++ allow different types of editors, not just text
			Control newEditor = createEditorControl();
			// Combo newEditor = new Combo(table, SWT.NONE);
			// newEditor.setItems(new String[] {
			// "1111", "222", "3333"
			// });
			// newEditor.setText(item.getText(selectedCell.x));
			// newEditor.addModifyListener(new ModifyListener() {
			// public void modifyText(ModifyEvent me){
			// Combo text = (Combo) tableEditor.getEditor();
			// tableEditor.getItem().setText(selectedCell.x, text.getText());
			// }
			// });
			// Text newEditor = new Text(table, SWT.NONE);
			// newEditor.setText(item.getText(selectedCell.x));
			// newEditor.addModifyListener(new ModifyListener() {
			// public void modifyText(ModifyEvent me){
			// Text text = (Text) editor.getEditor();
			// editor.getItem().setText(selectedCell.x, text.getText());
			// }
			// });
			// newEditor.selectAll();
			newEditor.setFocus();
			tableEditor.setEditor(newEditor, item, selectedCell.x);
		}
		
	}
	
	protected void startEdit(MouseEvent e, final TableEditor editor){
		// ***remove any previous editor
		System.out.println("aaaa");
		Control oldEditor = editor.getEditor();
		if (oldEditor != null)
			oldEditor.dispose();
		
		// *** identify the selected row
		if (selectedCell.y < 0)
			return;
		TableItem item = table.getItem(selectedCell.y);
		if (item == null)
			return;
		
		// *** the editor must be a child of the table
		// +++++ allow different types of editors, not just text
		Combo newEditor = new Combo(table, SWT.NONE);
		newEditor.setItems(new String[] {
			"1111", "222", "3333"
		});
		newEditor.setText(item.getText(selectedCell.x));
		newEditor.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent me){
				Combo text = (Combo) editor.getEditor();
				editor.getItem().setText(selectedCell.x, text.getText());
			}
		});
// Text newEditor = new Text(table, SWT.NONE);
// newEditor.setText(item.getText(selectedCell.x));
// newEditor.addModifyListener(new ModifyListener() {
// public void modifyText(ModifyEvent me){
// Text text = (Text) editor.getEditor();
// editor.getItem().setText(selectedCell.x, text.getText());
// }
// });
// newEditor.selectAll();
		newEditor.setFocus();
		editor.setEditor(newEditor, item, selectedCell.x);
		
// if (directPop) {
// startEdit(e, editor);
// showPopup(newEditor, true);
// }
		
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public int getNumberOfControls(){
		return 2;
	}
	
	/**
	 * Returns this field editor's selection listener. The listener is created if necessary.
	 * 
	 * @return the selection listener
	 */
	private SelectionListener getSelectionListener(){
		if (selectionListener == null) {
			createSelectionListener();
		}
		return selectionListener;
	}
	
	/**
	 * Returns this field editor's shell.
	 * <p>
	 * This method is internal to the framework; subclassers should not call this method.
	 * </p>
	 * 
	 * @return the shell
	 */
	protected Shell getShell(){
		if (addButton == null) {
			return null;
		}
		return addButton.getShell();
	}
	
	/**
	 * Notifies that the Add button has been pressed.
	 */
	private void addPressed(){
		setPresentsDefaultValue(false);
		String[] newInputObject = getNewInputObject();
		if (newInputObject != null) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(newInputObject);
			selectionChanged();
		}
	}
	
	private void editPressed(){
		setPresentsDefaultValue(false);
		int index = table.getSelectionIndex();
		TableItem tableItem = table.getItem(index);
		String[] changedInputObject = getChangedInputObject(tableItem);
		if (changedInputObject != null) {
			tableItem.setText(changedInputObject);
			selectionChanged();
		}
	}
	
	/**
	 * Notifies that the Remove button has been pressed.
	 */
	private void removePressed(){
		setPresentsDefaultValue(false);
		int index = table.getSelectionIndex();
		if (index >= 0) {
			System.out.println("table removing index " + index);
			table.remove(index);
			System.out.println("done removing now selectioncChanged " + index);
			selectionChanged();
			System.out.println("done with selectioncChanged " + index);
		}
	}
	
	/**
	 * Invoked when the selection in the list has changed.
	 * 
	 * <p>
	 * The default implementation of this method utilizes the selection index and the size of the
	 * list to toggle the enabled state of the up, down and remove buttons.
	 * </p>
	 * 
	 * <p>
	 * Subclasses may override.
	 * </p>
	 * 
	 */
	protected void selectionChanged(){
		int index = table.getSelectionIndex();
		int size = table.getItemCount();
		
		editButton.setEnabled(index >= 0);
		removeButton.setEnabled(index >= 0);
	}
	
	/*
	 * (non-Javadoc) Method declared on FieldEditor.
	 */
	public void setFocus(){
		if (table != null) {
			table.setFocus();
		}
	}
	
	/*
	 * @see FieldEditor.setEnabled(boolean,Composite).
	 */
	public void setEnabled(boolean enabled, Composite parent){
		super.setEnabled(enabled, parent);
		getTableControl(parent).setEnabled(enabled);
		addButton.setEnabled(enabled);
		editButton.setEnabled(enabled);
		removeButton.setEnabled(enabled);
	}
	
	/**
	 * set the items for this table. Any existing items are deleted before
	 * 
	 * @param itemList
	 *            List&lt;String[]&gt;, the items to be added
	 */
	public void setItems(List<String[]> itemList){
		removeTableItems();
		appendItems(itemList);
	}
	
	/**
	 * set the items for this table. Any existing items are deleted before
	 * 
	 * @param itemList
	 *            String[][], the items to be added
	 */
	public void setItems(String[][] itemList){
		removeTableItems();
		appendItems(itemList);
	}
	
	/**
	 * append items to this table. Any existing items remain in place
	 * 
	 * @param itemList
	 *            List&lt;String[]&gt;, the items to be added
	 */
	public void appendItems(List<String[]> itemList){
		if (itemList == null || itemList.size() == 0)
			return;
		for (String[] item : itemList) {
			appendTableItem(item);
		}
	}
	
	/**
	 * append items to this table. Any existing items remain in place
	 * 
	 * @param itemList
	 *            String[][], the items to be added
	 */
	public void appendItems(String[][] itemList){
		if (itemList == null || itemList.length == 0)
			return;
		for (String[] item : itemList) {
			appendTableItem(item);
		}
	}
	
	/**
	 * inserts items at index into the table. Any existing items remain in place
	 * 
	 * @param itemList
	 *            List&lt;String[]&gt;, the items to be inserted
	 */
	public void insertItems(List<String[]> itemList, int index){
		if (itemList == null || itemList.size() == 0)
			return;
		for (String[] item : itemList) {
			insertTableItem(item, index);
		}
	}
	
	/**
	 * inserts items at index into the table. Any existing items remain in place
	 * 
	 * @param itemList
	 *            String[][], the items to be inserted
	 */
	public void insertItems(String[][] itemList, int index){
		if (itemList == null || itemList.length == 0)
			return;
		for (String[] item : itemList) {
			insertTableItem(item, index);
		}
	}
	
	/**
	 * appends an item to the end of the table
	 * 
	 * @param item
	 */
	protected void appendTableItem(String[] item){
		if ((table != null) && (item != null)) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(item);
		}
	}
	
	/**
	 * inserts an item at index into the table
	 * 
	 * @param item
	 */
	protected void insertTableItem(String[] item, int index){
		if ((table != null) && (item != null)) {
			TableItem tableItem = new TableItem(table, SWT.NONE, index);
			tableItem.setText(item);
		}
	}
	
}