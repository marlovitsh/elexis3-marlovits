package ch.marlovits.plans.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InputBox extends Dialog {
	private Shell shell;
	private Label label;
	
	private String message;
	private String result;
	Text text = null;
	List list = null;
	DateTime dateFrom;
	
	Boolean createDateField = false;
	Boolean createListBoxField = false;
	Boolean createTextField = false;
	
	public InputBox(Shell parent, int style, String[] buttonLabels, int defaultButton){
		super(parent, checkStyle(style));
		createDateField = false;
		createListBoxField = false;
		createTextField = true;
		_InputBoxInternal(parent, style, buttonLabels, defaultButton, null, null);
	}
	
	public InputBox(Shell parent, int style, String[] buttonLabels, int defaultButton,
		String[] listSelectionItems){
		super(parent, checkStyle(style));
		createDateField = false;
		createListBoxField = false;
		createTextField = true;
		_InputBoxInternal(parent, style, buttonLabels, defaultButton, listSelectionItems, null);
	}
	
	public InputBox(Shell parent, int style, String[] buttonLabels, int defaultButton,
		String[] listSelectionItems, String dateString){
		super(parent, checkStyle(style));
		createDateField = true;
		createListBoxField = false;
		createTextField = true;
		_InputBoxInternal(parent, style, buttonLabels, defaultButton, listSelectionItems,
			dateString);
	}
	
	public InputBox(Shell parent, int style, String[] buttonLabels, int defaultButton,
		String dateString){
		super(parent, checkStyle(style));
		createDateField = true;
		createListBoxField = false;
		createTextField = false;
		_InputBoxInternal(parent, style, buttonLabels, defaultButton, null, dateString);
	}
	
	//
	public void _InputBoxInternal(Shell parent, int style, String[] buttonLabels,
		int defaultButton, String[] listSelectionItems, String dateString){
		shell = new Shell(parent, SWT.DIALOG_TRIM | checkStyle(style));
		shell.setText(getText());
		shell.setLayout(new GridLayout(2, false));
		
		new Label(shell, SWT.CENTER).setImage(shell.getDisplay().getSystemImage(
			checkImageStyle(style)));
		
		Composite body = new Composite(shell, SWT.NONE);
		
		GridData data0 = new GridData();
		data0.grabExcessHorizontalSpace = true;
		data0.grabExcessVerticalSpace = true;
		data0.horizontalAlignment = SWT.FILL;
		data0.verticalAlignment = SWT.FILL;
		body.setLayoutData(data0);
		
		body.setLayout(new GridLayout());
		
		label = new Label(body, SWT.LEFT);
		
		GridData data1 = new GridData();
		data1.grabExcessHorizontalSpace = true;
		data1.grabExcessVerticalSpace = true;
		data1.horizontalAlignment = SWT.FILL;
		data1.verticalAlignment = SWT.FILL;
		label.setLayoutData(data1);
		
		GridData data2 = new GridData();
		data2.grabExcessHorizontalSpace = true;
		data2.horizontalAlignment = SWT.FILL;
		
		if (((dateString != null) && (!dateString.isEmpty())) || (createDateField)) {
			dateFrom = new DateTime(body, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
			try {
				if ((dateString != null) && (!dateString.isEmpty())) {
					SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
					Date date = (Date) formatter.parse(dateString);
					dateFrom.setDate(1900 + date.getYear(), date.getMonth(), date.getDate());
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		if (listSelectionItems != null) {
			list = new List(body, SWT.SINGLE | SWT.BORDER);
			list.setItems(listSelectionItems);
			list.setSelection(0);
			list.setLayoutData(data2);
		} else {
			if (createTextField) {
				text = new Text(body, SWT.SINGLE | SWT.BORDER);
				text.setLayoutData(data2);
			}
		}
		
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
					if (finalI == 0)
						if ((text == null) && (list != null))
							if (createDateField)	{
								result =
									"" + dateFrom.getDay() + "." + (dateFrom.getMonth() + 1) + "."
										+ dateFrom.getYear();
								result = result + ";" + list.getSelection()[0];
							}
							else
								result = list.getSelection()[0];
						else {
							if (createTextField)
								result = text.getText();
							else {
								result =
									"" + dateFrom.getDay() + "." + (dateFrom.getMonth() + 1) + "."
										+ dateFrom.getYear();
							}
						}
					else
						result = null;
					shell.dispose();
				}
			});
		}
		
		shell.setDefaultButton(buttons[defaultButton]);
	}
	
	public void setDefaultText(String defaultText){
		if (text != null)
			text.setText(defaultText);
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
	
	@Override
	public void setText(String string){
		super.setText(string);
		
		shell.setText(string);
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String message){
		if (message == null)
			SWT.error(SWT.ERROR_NULL_ARGUMENT);
		
		this.message = message;
		label.setText(message);
	}
	
	public String open(){
		shell.pack();
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed())
			if (!shell.getDisplay().readAndDispatch())
				shell.getDisplay().sleep();
		
		return result;
	}
	
	public String getInputText(){
		if (text != null)
			return text.getText();
		else
			return "";
	}
	
	public String getInputDate(){
		if (dateFrom != null)
			return "" + (1900 + dateFrom.getYear()) + "." + dateFrom.getMonth()
				+ (dateFrom.getDay() + 1);
		else
			return "";
	}
}

/*
 * ***********************************
 * Copyright 2005 Completely Random Solutions * * DISCLAMER: * We are not responsible for any damage
 * * directly or indirectly caused by the usage * of this or any other class in association * with
 * this class. Use at your own risk. * This or any other class by CRS is not * certified for use in
 * life support systems, by * Lockheed Martin engineers, in development * or use of nuclear
 * reactors, weapons of mass * destruction, or in inter-planetary conflict. * (Unless otherwise
 * specified) ************************************
 */

