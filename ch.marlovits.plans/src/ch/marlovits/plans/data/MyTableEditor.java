package ch.marlovits.plans.data;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableItem;

public class MyTableEditor extends TableFieldEditor {
	
	private Composite parent;
	
	public MyTableEditor(String key, String[] columnNames, int[] columnWidths,
		Composite parent){
		
		super(key, "", columnNames, columnWidths, parent);
		this.parent = parent;
	}
	
	@Override
	protected String[] getNewInputObject(){
		int numOfCols = table.getColumnCount();
		String[] newOne = new String[numOfCols];
		for (int i = 0; i < numOfCols; i++)
			newOne[i] = "";
		return newOne;
// FieldExtensionDialog dialog = new FieldExtensionDialog(parent.getShell(), getItems());
// dialog.open();
// String fieldNameInput = dialog.fieldNameInput;
// String fieldValueInput = dialog.fieldValueInput;
// if(fieldNameInput != null && fieldNameInput.length() > 0 &&
// fieldValueInput != null && fieldValueInput.length() > 0) {
//
// return new String[] { fieldNameInput, fieldValueInput };
// } else {
// return null;
// }
	}
	
	@Override
	protected String[] getChangedInputObject(TableItem tableItem){
		// +++++ change this table item
		int numOfCols = table.getColumnCount();
		String[] newOne = new String[numOfCols];
		for (int i = 0; i < numOfCols; i++)
			newOne[i] = tableItem.getText(i) + "x";
		return newOne;
// FieldExtensionDialog dialog = new FieldExtensionDialog(parent.getShell(), getItems(),
// tableItem.getText(0), tableItem.getText(1));
// dialog.open();
// String fieldNameInput = dialog.fieldNameInput;
// String fieldValueInput = dialog.fieldValueInput;
// if(fieldNameInput != null && fieldNameInput.length() > 0 &&
// fieldValueInput != null && fieldValueInput.length() > 0) {
//
// if(tableItem.getText(0).equals(fieldNameInput) && tableItem.getText(1).equals(fieldValueInput)) {
// return null;
// }
//
// return new String[] { fieldNameInput, fieldValueInput };
// } else {
// return null;
// }
	}
		
	@Override
	protected void selectionChanged(){
		super.selectionChanged();
		saveFieldExtensions();
	}
	
	private void saveFieldExtensions(){
		
	}
	
}
