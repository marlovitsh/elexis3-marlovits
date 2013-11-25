package ch.marlovits.plans.data;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

public class InputBox_2 extends Dialog {
	Text text = null;
	
	String[][] theSpecs;
	LinkedList<String[]> resultList = new LinkedList<String[]>();
	
	private Shell shell;
	
	List list = null;
	DateTime dateFrom;
	
	Boolean createDateField = false;
	Boolean createListBoxField = false;
	Boolean createTextField = false;
	
	LinkedList<Widget> widgets = new LinkedList<Widget>();
	
	// ++ Text:..Label:..numOfLines:..text
	// ++ List:..Label:..defSelItem:..multiSelect:..asString:..items
	// ++ Menu:..Label:..defSelItem:..multiSelect:..asString:items
	// ++ radio:.Label:..defSelItem:..asString:items
	// ++ check:.Label:..defSelItem:..asString:items
	// ++ date:..Label:..date
	
	@SuppressWarnings("deprecation")
	public InputBox_2(Shell parent, int style, String windowTitle, String[] buttonLabels,
		int defaultButton, String[][] lSpec){
		super(parent, checkStyle(style));
		
		int numOfCols = 1;
		
		theSpecs = lSpec;
		
		shell = new Shell(parent, SWT.DIALOG_TRIM | checkStyle(style));
		shell.setText(windowTitle);
		shell.setLayout(new GridLayout(2, false));
		
		Composite body = new Composite(shell, SWT.NONE);
		
		GridData data0 = new GridData();
		data0.grabExcessHorizontalSpace = true;
		data0.grabExcessVerticalSpace = true;
		data0.horizontalAlignment = SWT.FILL;
		data0.verticalAlignment = SWT.FILL;
		body.setLayoutData(data0);
		
		GridLayout twoColsLayout = new GridLayout();
		twoColsLayout.numColumns = numOfCols;
		body.setLayout(twoColsLayout);
		
		for (int i = 0; i < lSpec.length; i++) {
			String[] parts = lSpec[i];
			String firstPart = parts[0];
			String[] dbFieldName_displayArgs = firstPart.split("\\|");
			String dbFieldName = "";
			if (dbFieldName_displayArgs.length > 1) {
				firstPart = dbFieldName_displayArgs[1];
				dbFieldName = dbFieldName_displayArgs[0];
				// dbFieldName = dbFieldName.split("\\[")[0];
			}
			// ********* Text
			if (firstPart.equalsIgnoreCase("Text")) {
				Label label = new Label(body, SWT.NONE);
				label.setText(parts[1]);
				
				String numOfLinesStr = parts[2];
				int multi = SWT.MULTI;
				int numOfLines = Integer.parseInt(numOfLinesStr);
				switch (numOfLines) {
				case 0:
				case 1:
					multi = 0;
					break;
				}
				text = new Text(body, SWT.BORDER + multi);
				
				// *** trick for display of multiple lines...
				String placeholder = "";
				for (int ii = 1; ii < numOfLines; ii++) {
					placeholder = placeholder + "\n";
				}
				text.setText(parts[3] + placeholder);
				
				GridData textGridData = new GridData();
				textGridData.grabExcessHorizontalSpace = true;
				textGridData.grabExcessVerticalSpace = true;
				textGridData.horizontalAlignment = SWT.FILL;
				textGridData.verticalAlignment = SWT.FILL;
				text.setLayoutData(textGridData);
				text.setData("dbFieldName", dbFieldName);
				
				widgets.add(text);
			}
			// ********* List
			// List:..Label:..defSelItem:..multiSelect:..items
			// "List", "Label for ListField", "0", "1", "Item 1", "Item 2", "Item 3", "Item 4"
			else if (firstPart.equalsIgnoreCase("List")) {
				Label label = new Label(body, SWT.NONE);
				label.setText(parts[1]);
				
				String selItemStr = parts[2];
				int selItem = -1;
				try {
					selItem = Integer.parseInt(selItemStr);
				} catch (Exception e) {}
				String multiSelectStr = parts[3];
				int multiSelect = Integer.parseInt(multiSelectStr);
				String asString = parts[4];
				List list = new List(body, SWT.BORDER + (multiSelect == 1 ? SWT.MULTI : 0));
				list.setData("asString", asString);
				list.setData("dbFieldName", dbFieldName);
				for (int ii = 5; ii < parts.length; ii++) {
					list.add(parts[ii]);
					if (selItem == -1) {
						if (parts[ii].equalsIgnoreCase(selItemStr))
							selItem = ii - 5;
					}
				}
				
				list.setSelection(selItem);
				widgets.add(list);
			}
			// ********* Combo
			else if (firstPart.equalsIgnoreCase("Combo")) {
				Label label = new Label(body, SWT.NONE);
				label.setText(parts[1]);
				
				String selItemStr = parts[2];
				int selItem = Integer.parseInt(selItemStr);
				String multiSelectStr = parts[3];
				int multiSelect = Integer.parseInt(multiSelectStr);
				String asString = parts[4];
				CCombo combo = new CCombo(body, SWT.BORDER + (multiSelect == 1 ? SWT.MULTI : 0));
				combo.setData("asString", asString);
				combo.setData("dbFieldName", dbFieldName);
				for (int ii = 5; ii < parts.length; ii++) {
					combo.add(parts[ii]);
					if ((ii - 5) == selItem)
						combo.setText(parts[ii]);
				}
				combo.setSelection(new Point(0, 32000));
				widgets.add(combo);
			}
			// ********* Radio
			else if (firstPart.equalsIgnoreCase("Radio")) {
				Label label = new Label(body, SWT.NONE);
				label.setText(parts[1]);
				
				Composite comp = new Composite(body, SWT.NONE);
				GridLayout gl = new GridLayout(1, false);
				gl.marginWidth = 0;
				gl.marginHeight = 0;
				comp.setLayout(gl);
				String selItemStr = parts[2];
				int selItem = Integer.parseInt(selItemStr);
				String asString = parts[3];
				for (int ii = 4; ii < parts.length; ii++) {
					Button radio = new Button(comp, SWT.RADIO);
					radio.setText(parts[ii]);
					radio.setSelection((ii - 4) == selItem ? true : false);
					radio.setData("asString", asString);
					radio.setData("dbFieldName", dbFieldName);
				}
				widgets.add(comp);
			}
			// ********* Check
			else if (firstPart.equalsIgnoreCase("Check")) {
				Label label = new Label(body, SWT.NONE);
				label.setText(parts[1]);
				
				Composite comp = new Composite(body, SWT.NONE);
				GridLayout gl = new GridLayout(1, false);
				gl.marginWidth = 0;
				gl.marginHeight = 0;
				comp.setLayout(gl);
				String selItemStr = parts[2];
				String asString = parts[3];
				for (int ii = 4; ii < parts.length; ii++) {
					Button check = new Button(comp, SWT.CHECK);
					check.setText(parts[ii]);
					String selItem = selItemStr.substring(ii - 4, ii - 3);
					check.setSelection(selItem.equalsIgnoreCase("1") ? true : false);
					check.setData("asString", asString);
					check.setData("dbFieldName", dbFieldName);
				}
				widgets.add(comp);
			}
			// ********* Date
			else if (firstPart.equalsIgnoreCase("Date")) {
				try {
					Label label = new Label(body, SWT.NONE);
					label.setText(parts[1]);
					
					String theDate = parts[2];
					SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					dateFrom = new DateTime(body, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
					Date date = (Date) formatter.parse(theDate);
					dateFrom.setDate(1900 + date.getYear(), date.getMonth(), date.getDate());
					dateFrom.setData("dbFieldName", dbFieldName);
					widgets.add(dateFrom);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} else {
				// *** don't do anything...
			}
		}
		// ********************************* footer
		Composite footer = new Composite(shell, SWT.NONE);
		
		GridData data3 = new GridData();
		data3.grabExcessHorizontalSpace = true;
		data3.horizontalAlignment = SWT.FILL;
		data3.horizontalSpan = 2;
		footer.setLayoutData(data3);
		
		RowLayout layout = new RowLayout();
		layout.justify = true;
		layout.fill = true;
		footer.setLayout(layout);
		
		Button[] buttons = new Button[buttonLabels.length];
		
		for (int i = 0; i < buttonLabels.length; i++) {
			Button tmp = new Button(footer, SWT.PUSH);
			buttons[i] = tmp;
			tmp.setText(buttonLabels[i]);
			final int finalI = i;
			tmp.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e){
					if (finalI == 0) {
						for (int i = 0; i < widgets.size(); i++) {
							Widget tmpWidget = widgets.get(i);
							String asStringStr = (String) tmpWidget.getData("asString");
							boolean asString = false;
							if (asStringStr != null)
								asString = asStringStr.equalsIgnoreCase("0") ? false : true;
							String dbFieldName = (String) tmpWidget.getData("dbFieldName");
							
							// ********* Text
							if (tmpWidget instanceof Text) {
								String currText = ((Text) tmpWidget).getText();
								if (dbFieldName.isEmpty()) {
									String[] tmpRes = {
										currText
									};
									resultList.add(tmpRes);
								} else {
									String[] tmpRes = {
										dbFieldName, currText
									};
									resultList.add(tmpRes);
								}
							}
							// ********* List
							else if (tmpWidget instanceof List) {
								if (asString) {
									String[] currText = ((List) tmpWidget).getSelection();
									if (!dbFieldName.isEmpty())
										currText = (String[]) arrayPrepend(currText, dbFieldName);
									resultList.add(currText);
								} else {
									int[] intSels = ((List) tmpWidget).getSelectionIndices();
									int listLen = intSels.length;
									int startCounter = 0;
									String[] tmpStrArr = new String[listLen];
									if (!dbFieldName.isEmpty()) {
										listLen++;
										startCounter++;
										tmpStrArr[0] = dbFieldName;
									}
									for (int ii = startCounter; ii < listLen; ii++) {
										tmpStrArr[ii] = "" + intSels[ii];
									}
									resultList.add(tmpStrArr);
								}
							}
							// ********* Combo
							else if (tmpWidget instanceof CCombo) {
								String currText = "";
								if (asString) {
									currText = ((CCombo) tmpWidget).getText();
								} else {
									currText = "" + ((CCombo) tmpWidget).getSelectionIndex();
								}
								if (dbFieldName.isEmpty()) {
									String[] tmpRes = {
										currText
									};
									resultList.add(tmpRes);
								} else {
									String[] tmpRes = {
										dbFieldName, currText
									};
									resultList.add(tmpRes);
								}
							}
							// ********* Date
							// MUST test BEFORE Composite since this is actually a composite
							else if (tmpWidget instanceof DateTime) {
								DateTime dateTime = ((DateTime) tmpWidget);
// String tmpDate =
// dateTime.getDay() + "." + dateTime.getMonth() + "."
// + dateTime.getYear();
								int theMonth = dateTime.getMonth() + 1;
								String month = (theMonth > 9) ? "" + theMonth : "0" + theMonth;
								String day =
									(dateTime.getDay() > 9) ? "" + dateTime.getDay() : "0"
										+ dateTime.getDay();
								String tmpDate = "" + day + "." + month + "." + dateTime.getYear();
								if (dbFieldName.isEmpty()) {
									String[] tmpRes = {
										tmpDate
									};
									resultList.add(tmpRes);
								} else {
									String[] tmpRes = {
										dbFieldName, tmpDate
									};
									resultList.add(tmpRes);
								}
							}
							// ********* Radio/Check
							else if (tmpWidget instanceof Composite) {
								Composite comp = ((Composite) tmpWidget);
								Control[] children = comp.getChildren();
								LinkedList<String> checkBoxResult = new LinkedList<String>();
								if (!dbFieldName.isEmpty()) {
									checkBoxResult.add(dbFieldName);
								}
								boolean isCheckboxSeries = false;
								for (int ii = 0; ii < children.length; ii++) {
									Control ctrl = children[ii];
									Button btn = (Button) ctrl;
									// ****** Radio
									int masked = btn.getStyle() & SWT.RADIO;
									if (masked != 0) {
										if (btn.getSelection()) {
											String theText = btn.getText();
											if (!asString) {
												theText = "" + ii;
											}
											if (dbFieldName.isEmpty()) {
												String[] tmpRes = {
													theText
												};
												resultList.add(tmpRes);
											} else {
												String[] tmpRes = {
													dbFieldName, theText
												};
												resultList.add(tmpRes);
											}
											break;
										}
									}
									// ****** Check
									masked = btn.getStyle() & SWT.CHECK;
									if (masked != 0) {
										isCheckboxSeries = true;
										if (asString) {
											checkBoxResult.add(btn.getSelection() ? btn.getText()
													: "");
										} else {
											checkBoxResult.add(btn.getSelection() ? "1" : "0");
										}
									}
								}
								if (isCheckboxSeries) {
									String[] strArr = checkBoxResult.toArray(new String[0]);
									resultList.add(strArr);
								}
							}
						}
					} else
						resultList = null;
					shell.dispose();
				}
			});
		}
		
		shell.setDefaultButton(buttons[defaultButton]);
	}
	
	protected static int checkStyle(int style){
		if ((style & SWT.SYSTEM_MODAL) == SWT.SYSTEM_MODAL) {
			return SWT.SYSTEM_MODAL;
		} else if ((style & SWT.PRIMARY_MODAL) == SWT.PRIMARY_MODAL) {
			return SWT.PRIMARY_MODAL;
		} else if ((style & SWT.APPLICATION_MODAL) == SWT.APPLICATION_MODAL) {
			return SWT.APPLICATION_MODAL;
		}
		
		return SWT.APPLICATION_MODAL;
	}
	
	protected static int checkImageStyle(int style){
		if ((style & SWT.ICON_ERROR) == SWT.ICON_ERROR) {
			return SWT.ICON_ERROR;
		} else if ((style & SWT.ICON_INFORMATION) == SWT.ICON_INFORMATION) {
			return SWT.ICON_INFORMATION;
		} else if ((style & SWT.ICON_QUESTION) == SWT.ICON_QUESTION) {
			return SWT.ICON_QUESTION;
		} else if ((style & SWT.ICON_WARNING) == SWT.ICON_WARNING) {
			return SWT.ICON_WARNING;
		} else if ((style & SWT.ICON_WORKING) == SWT.ICON_WORKING) {
			return SWT.ICON_WORKING;
		}
		
		return SWT.NONE;
	}
	
	public LinkedList<String[]> open(){
		shell.pack();
		shell.open();
		shell.layout();
		
		// *** set text controls to the real text
		for (int i = 0; i < theSpecs.length; i++) {
			String[] parts = theSpecs[i];
			// ********* Text
			String firstPart = parts[0];
			String[] parts2 = firstPart.split("\\|");
			String type = firstPart;
			if (parts2.length > 1)
				type = parts2[1];
			if (type.equalsIgnoreCase("Text")) {
				((Text) widgets.get(i)).setText(parts[3]);
			}
		}
		
		while (!shell.isDisposed())
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
		
		return resultList;
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
