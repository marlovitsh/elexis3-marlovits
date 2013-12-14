/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
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

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.marlovits.global_inbox.DocHandle;
import ch.rgw.tools.TimeTool;

public class FileImportDialogMarlovits extends TitleAreaDialog {
	String file;
	DocHandle dh;
	Text tTitle;
	Text tKeywords;
	// +++++ ADD START
	DateTime dateFrom;
	Boolean showCreateCheckBox;
	Button cbCreateAufgebot;
	// +++++ ADD END
	
	Combo cbCategories;
	public String category;
	public String title;
	public String keywords;
	public String dateStr;
	public Boolean createAufgebot;
	
	Composite dialogParentComposite;
	
	LinkedList<String> additionalTextFields_Title = new LinkedList<String>();
	LinkedList<String> additionalTextFields_Text = new LinkedList<String>();
	LinkedList<Integer> additionalTextFields_NumOfLines = new LinkedList<Integer>();
	
	public FileImportDialogMarlovits(DocHandle dh, Boolean pShowCreateCheckBox){
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.dh = dh;
		file = dh.get("Titel");
		showCreateCheckBox = pShowCreateCheckBox;
	}
	
	public FileImportDialogMarlovits(String name, Boolean pShowCreateCheckBox){
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		file = name;
		showCreateCheckBox = pShowCreateCheckBox;
	}
	
	public void setCategory(String category){
		this.category = category;
		// cbCategories.setText(category);
	}
	
	public void setDatumDokument(String datum){
		this.dateStr = datum;
// TimeTool tt = new TimeTool();
// boolean isDate = tt.setDate(datum);
// if (isDate){
// dateFrom.setDate(tt.get(tt.YEAR), tt.get(tt.MONTH), tt.get(tt.DAY_OF_MONTH));
// }
	}
	
	public void addTextField(String title, String theText, int numOfLines){
// Label lbl = new Label(dialogParentComposite, SWT.NONE);
// lbl.setText(title);
//
// Text tKeywords = SWTHelper.createText(dialogParentComposite, numOfLines, SWT.NONE);
// tKeywords.setText(theText);
		additionalTextFields_Title.add(title);
		additionalTextFields_Text.add(theText);
		additionalTextFields_NumOfLines.add(numOfLines);
	}
	
	public void setTitel(String titel){
		this.title = titel;
		// tTitle.setText(titel);
	}
	
	public void setKeywords(String keywords){
		this.keywords = keywords;
		// tKeywords.setText(keywords);
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		dialogParentComposite = new Composite(parent, SWT.NONE);
		dialogParentComposite.setLayout(new GridLayout());
		dialogParentComposite.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		new Label(dialogParentComposite, SWT.None).setText("Kategorie");
		Composite cCats = new Composite(dialogParentComposite, SWT.NONE);
		cCats.setLayoutData(SWTHelper.getFillGridData(1, true, 1, false));
		// RowLayout rl=new RowLayout(SWT.HORIZONTAL);
		// rl.fill=true;
		
		cCats.setLayout(new GridLayout(4, false));
		cbCategories = new Combo(cCats, SWT.SINGLE | SWT.DROP_DOWN | SWT.READ_ONLY);
		cbCategories.setLayoutData(SWTHelper.getFillGridData());
		
		Button bNewCat = new Button(cCats, SWT.PUSH);
		bNewCat.setImage(Images.IMG_NEW.getImage());
		bNewCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				InputDialog id =
					new InputDialog(getShell(), "Neue Kategorie",
						"Geben Sie bitte einen Namen für die neue Kategorie ein", null, null);
				if (id.open() == Dialog.OK) {
					DocHandle.addMainCategory(id.getValue());
					cbCategories.add(id.getValue());
					cbCategories.setText(id.getValue());
				}
			}
		});
		
		Button bEditCat = new Button(cCats, SWT.PUSH);
		bEditCat.setImage(Images.IMG_EDIT.getImage());
		bEditCat.setToolTipText("Kategorie umbenennen");
		bEditCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				String old = cbCategories.getText();
				InputDialog id =
					new InputDialog(getShell(), MessageFormat.format("Kategorie '{0}' umbenennen.",
						old), "Geben Sie bitte einen neuen Namen für die Kategorie ein", old, null);
				if (id.open() == Dialog.OK) {
					String nn = id.getValue();
					DocHandle.renameCategory(old, nn);
					cbCategories.remove(old);
					cbCategories.add(nn);
				}
			}
		});
		
		Button bDeleteCat = new Button(cCats, SWT.PUSH);
		bDeleteCat.setImage(Images.IMG_DELETE.getImage());
		bDeleteCat.setToolTipText("Kategorie löschen");
		bDeleteCat.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent ev){
				String old = cbCategories.getText();
				InputDialog id =
					new InputDialog(
						getShell(),
						MessageFormat.format("Kategorie {0} löschen", old),
						"Geben Sie bitte an, in welche andere Kategorie die Dokumente dieser Kategorie verschoben werden sollen",
						"", null);
				if (id.open() == Dialog.OK) {
					DocHandle.removeCategory(old, id.getValue());
					cbCategories.remove(id.getValue());
				}
			}
		});
		IDocumentManager myDM =
			(IDocumentManager) Extensions
				.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
		String[] categories = myDM.getCategories();
		if (categories.length > 0) {
			cbCategories.setItems(categories);
			cbCategories.select(0);
		}
		if (category != null)
			cbCategories.setText(category);
		
		// ++++ ADD START
		dateFrom = new DateTime(dialogParentComposite, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
		new Label(dialogParentComposite, SWT.NONE).setText("Datum Dokument");
		boolean isDate = false;
		if ((dateStr != null) && (!dateStr.isEmpty())) {
			TimeTool tt = new TimeTool();
			isDate = tt.setDate(dateStr);
			if (isDate)
				dateFrom.setDate(tt.get(TimeTool.YEAR), tt.get(TimeTool.MONTH),
					tt.get(TimeTool.DAY_OF_MONTH));
		}
		if (!isDate) {
			dateFrom.setDate(Calendar.getInstance().getTime().getYear(), Calendar.getInstance()
				.getTime().getMonth(), Calendar.getInstance().getTime().getDate());
		}
		// ++++ ADD END
		
		new Label(dialogParentComposite, SWT.NONE).setText("Titel");
		tTitle = SWTHelper.createText(dialogParentComposite, 1, SWT.NONE);
		new Label(dialogParentComposite, SWT.NONE).setText("Stichwörter");
		tKeywords = SWTHelper.createText(dialogParentComposite, 4, SWT.NONE);
		if ((title != null) && !title.isEmpty())
			tTitle.setText(title);
		else
			tTitle.setText(file);
		
		if ((keywords != null) && (!keywords.isEmpty()))
			tKeywords.setText(keywords);
		if (dh != null) {
			tKeywords.setText(dh.get("Keywords"));
			cbCategories.setText(dh.getCategoryName());
		}
		
		// ++++ ADD START
		// *** create Aufgebot oder eben nicht
		if (showCreateCheckBox) {
			// new Label(ret, SWT.NONE).setText("Datum Dokument");
			cbCreateAufgebot = new Button(dialogParentComposite, SWT.CHECK);
			cbCreateAufgebot.setText("Aufgebot erstellen und öffnen");
			cbCreateAufgebot.setSelection(true);
		}
		// ++++ ADD END
		
// setCategory("OP-Pläne");
// setDatumDokument("01.05.2017");
// setTitel("replaced asdfasdfa");
// setKeywords("Eingang xx.dd.yy\nasdfasd");
// addTextField( "Titolo", "Texto", 10);
		
		for (int i = 0; i < additionalTextFields_Title.size(); i++) {
			Label lbl = new Label(dialogParentComposite, SWT.NONE);
			lbl.setText(additionalTextFields_Title.get(i));
			Text tKeywords =
				SWTHelper.createText(dialogParentComposite, additionalTextFields_NumOfLines.get(i),
					SWT.NONE);
			tKeywords.setText(additionalTextFields_Text.get(i));
		}
		return dialogParentComposite;
	}
	
	@Override
	public void create(){
		super.create();
		setTitle(file);
		getShell().setText("Datei importieren");
		setMessage("Geben Sie bitte einen Titel und ggf. einige Stichwörter für dieses Dokument ein");
	}
	
	@Override
	protected void okPressed(){
		keywords = tKeywords.getText();
		title = tTitle.getText();
		category = cbCategories.getText();
		dateStr = dateFrom.getDay() + "." + (dateFrom.getMonth() + 1) + "." + dateFrom.getYear();
		if (showCreateCheckBox)
			createAufgebot = cbCreateAufgebot.getSelection();
		else
			createAufgebot = false;
		try {
			DateFormat formatter;
			Date date;
			formatter = new SimpleDateFormat("dd.mm.yyyy");
			date = (Date) formatter.parse(dateStr);
			dateStr = formatter.format(date);
		} catch (ParseException e) {
			System.out.println("Exception :" + e);
		}
		
		if (dh != null) {
			dh.set("Cat", category);
			dh.set("Titel", title);
			dh.set("Keywords", keywords);
			dh.set("Datum", dateStr);
			// FLD_PATID
		}
		super.okPressed();
	}
	
}
