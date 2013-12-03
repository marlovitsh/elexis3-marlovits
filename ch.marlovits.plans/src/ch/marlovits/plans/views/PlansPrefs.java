/*******************************************************************************
 * Copyright (c) 2013, H. Marlovits and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    H. Marlovits - initial implementation
 *    
 *******************************************************************************/

package ch.marlovits.plans.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.core.constants.Preferences;
import ch.elexis.core.data.activator.CoreHub;
//import ch.elexis.core.ui.preferences.Messages;
//import ch.elexis.core.ui.preferences.ScannerPref.TextScannerListener;
import ch.elexis.core.ui.preferences.Messages;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;
import ch.elexis.core.ui.preferences.inputs.FileSelectorField;
import ch.elexis.core.ui.preferences.inputs.MultilineFieldEditor;
import ch.elexis.core.ui.preferences.inputs.StringListFieldEditor;
import ch.marlovits.plans.data.MyTableEditor;
import ch.marlovits.registry.Registry;
import ch.marlovits.registry.WriterDoc;

public class PlansPrefs extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	// *** my id
	public static final String ID = "ch.marlovits.plans.prefs"; //$NON-NLS-1$
	
	// *** prefs paths
	final static String PREFS_IMAGEMAGICK_CONVERT_PATH = "marlovits/plans/imagemagickconvertpath";
	final static String PREFS_GHOSTSCRIPT_PATH = "marlovits/plans/ghostscriptpath";
	final static String PREFS_OPERATEURE = "marlovits/plans/operateure";
	final static String PREFS_DISPLAYSPECS = "marlovits/plans/displayspecs";
	
	// *** these prefs are global to all users
	// *** may be it should be defined for a machine since the installation place could be different
	// *** on different machines
	SettingsPreferenceStore prefs = new SettingsPreferenceStore(CoreHub.globalCfg);
	
	public PlansPrefs(){
		super(GRID);
		
		prefs.setDefault(PREFS_IMAGEMAGICK_CONVERT_PATH, ""); //$NON-NLS-1$
		prefs.setDefault("Preferences.SCANNER_PREFIX_CODE", 0); //$NON-NLS-1$
		prefs.setDefault("Preferences.SCANNER_POSTFIX_CODE", 123456789); //$NON-NLS-1$
		prefs.setDefault("Preferences.BARCODE_LENGTH", 13); //$NON-NLS-1$
		
		setPreferenceStore(prefs);
		setDescription("Einstellungen für die Ansicht Pläne");
	}
	
	@Override
	protected Control createContents(final Composite parent){
		super.createContents(parent);
		
// GridLayout noMarginLayout = new GridLayout(3, false);
// noMarginLayout.marginLeft = 0;
//
// Composite comp = new Composite(parent, SWT.NONE);
// comp.setLayout(noMarginLayout);
// comp.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//
// Label lblTitle = new Label(comp, SWT.NONE);
// lblTitle.setText("Einstellungen für die Ansicht Pläne");
//
//
// lblTitle.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
// final Text txtTest = new Text(comp, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP);
// txtTest.setLayoutData(new GridData(GridData.FILL_BOTH));
//
// Button btnClear = new Button(comp, SWT.PUSH);
// btnClear.setText("Messages.ScannerPref_clear");
// btnClear.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
// btnClear.addSelectionListener(new SelectionAdapter() {
// @Override
// public void widgetSelected(SelectionEvent e){
//				txtTest.setText(""); //$NON-NLS-1$
// txtTest.setFocus();
// }
//
// });
//
// Button btnSteuerblatt = new Button(parent, SWT.PUSH);
// btnSteuerblatt.setText("Messages.ScannerPref_printSheet");
// btnSteuerblatt.addSelectionListener(new SelectionAdapter() {
// @Override
// public void widgetSelected(SelectionEvent e){
// MessageDialog.openInformation(getShell(),
// "Messages.ScannerPref_printSettingsSheet",
// "Messages.ScannerPref_NotImplemented");
// }
// });
//
		return parent;
		
// // +++++ Auswahl des Patienten, der für das Speichern der Dokumente verwendet wird
// // +++++ nö - doch besser automatisch einen Patienten erstellen der dann verwendet wird
//
// // +++++ IMAGEMAGICK_CONVERT_PATH = "F:\\Program Files\\ImageMagick-6.7.3-Q16\\convert.exe";
//
// // +++++ F:\\Program Files\\Bullzip\\PDF Printer\\gs\\gswin32c.exe
// // +++++
// String[] operateureItems =
// {
// "Erban", "Riess", "Schweizer", "Steinke", "Stelz", "Marlovits", "Fehr", "Lorenz",
// "Kast", "Payer", "Reilly", "Rühli"
// };
//
// // +++++
// int[][] colWidths = {
// // {
// // 20, 75, 75, 120, 0
// // },
// {
// 19, 65, 70, 115, 240, 240
// }, {
// 19, 65, 70, 115, 70, 85, 240
// }, {
// 19, 65, 70, 115, 70, 85, 240
// }, {
// 19, 65, 70, 115, 120, 120
// }, {
// 19, 65, 70, 115, 120, 120
// }
// };
//
//		return new MultiplikatorEditor(parent, "EAL"); //$NON-NLS-1$
	}
	
	public void init(final IWorkbench workbench){
		
	}
	
	@Override
	protected void createFieldEditors(){
		// +++++ windows
		String programFolder = System.getenv("ProgramFiles");
		
		String[] allowedExtensions = {
			"*convert.exe"
		};
		
		// *** image magick convert exe path
		FileFieldEditor ffeImageMagicExe =
			new FileFieldEditor(PREFS_IMAGEMAGICK_CONVERT_PATH, "Exe for Image Magic Convert",
				getFieldEditorParent());
		ffeImageMagicExe.setFileExtensions(allowedExtensions);
		ffeImageMagicExe.setFilterPath(new File(programFolder));
		addField(ffeImageMagicExe);
		// +++++ call /version -> test if it returns something with ImageMagick -> then it is an
// imageMagick exe
		
		// *** ghost script exe path
		FileFieldEditor ffeGhostscriptExe =
			new FileFieldEditor(PREFS_GHOSTSCRIPT_PATH, "Exe for Ghostscript",
				getFieldEditorParent());
		ffeGhostscriptExe.setFileExtensions(allowedExtensions);
		ffeGhostscriptExe.setFilterPath(new File(programFolder));
		addField(ffeGhostscriptExe);
		
		// *** operateure
		MultilineFieldEditor sfe =
			new MultilineFieldEditor(PREFS_OPERATEURE, "Operateure, 1 pro Zeile",
				getFieldEditorParent());
		Text text = sfe.getTextControl(getFieldEditorParent());
		addField(sfe);
		
		Button sortOperateure = new Button(getFieldEditorParent(), SWT.PUSH);
		sortOperateure.setText("Sort Operateure");
		new Label(getFieldEditorParent(), SWT.NONE);
		new Label(getFieldEditorParent(), SWT.NONE);
		
		// *** Defs
		MyTableEditor fee = new MyTableEditor(PREFS_DISPLAYSPECS, new String[] {
			"erste", "zweite", "dritte"
		}, new int[] {
			200, 200, 300
		}, getFieldEditorParent());
		List<String[]> fieldList = new ArrayList<String[]>();
		fieldList.add(new String[] {
			"1.1", "1.2", "1.3"
		});
		fieldList.add(new String[] {
			"2.1", "2.2", "1.3"
		});
		fieldList.add(new String[] {
			"3.1", "3.2", "1.3"
		});
		fieldList.add(new String[] {
			"4.1", "4.2", "1.3"
		});
		fieldList.add(new String[] {
			"5.1", "5.2", "1.3"
		});
		//fee.setItems(fieldList);
		addField(fee);
		
		// *** some space
		new Label(getFieldEditorParent(), SWT.NONE);
		new Label(getFieldEditorParent(), SWT.NONE);
		new Label(getFieldEditorParent(), SWT.NONE);
		
		new Label(getFieldEditorParent(), SWT.NONE);
		new Label(getFieldEditorParent(), SWT.NONE);
		new Label(getFieldEditorParent(), SWT.NONE);
		
		// ////////////////////////////////////
// addField(new IntegerFieldEditor("Preferences.SCANNER_PREFIX_CODE",
// "Messages.ScannerPref_ScannerPrefix", getFieldEditorParent(), 10));
//
// addField(new IntegerFieldEditor("Preferences.SCANNER_POSTFIX_CODE",
// "Messages.ScannerPref_ScannerPostfix", getFieldEditorParent(), 10));
//
// addField(new IntegerFieldEditor("Preferences.BARCODE_LENGTH",
// "Messages.ScannerPref_Barcodelength", getFieldEditorParent(), 50));
//
// StringListFieldEditor slfe =
// new StringListFieldEditor(PREFS_OPERATEURE, "Operateure", "inputMessage",
// "String input", getFieldEditorParent());
//
// Button bttttn = new Button(getFieldEditorParent(), SWT.PUSH);
// bttttn.setText("test reading registry");
// bttttn.addSelectionListener(new SelectionListener() {
//
// @Override
// public void widgetSelected(SelectionEvent e){
// String templateDoc =
// "C:/Documents and Settings/Rob/My Documents/EclipseProjects/OpenOffice/MyTemplateDoc.odt";
//
// try {
// WriterDoc.createDoc("My Document Title", null, true);
// // doc.createDoc("My Document Title", templateDoc, true);
// } catch (Exception e2) {
// e2.printStackTrace();
// }
// // Registry.testit();
// }
//
// @Override
// public void widgetDefaultSelected(SelectionEvent e){
// // TODO Auto-generated method stub
//
// }
//
// });
	}
}
