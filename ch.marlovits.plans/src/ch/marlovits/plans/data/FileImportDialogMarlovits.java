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

package ch.marlovits.plans.data;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.data.util.Extensions;


public class FileImportDialogMarlovits extends TitleAreaDialog {
	String file;
	DocHandle_Mv dh;
	Text tTitle;
	Text tKeywords;
	// +++++ ADD START
	DateTime dateFrom;
	Boolean showCreateCheckBox;
	Button cbCreateAufgebot;
	// +++++ ADD END
	
	Combo cbCategories;
	public String title;
	public String keywords;
	public String category;
	public String dateStr;
	public Boolean createAufgebot;
	
	public FileImportDialogMarlovits(DocHandle_Mv dh, Boolean pShowCreateCheckBox){
		//super(Hub.getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell());
		super(UiDesk.getTopShell());
		this.dh = dh;
		file = dh.get("Titel");
		showCreateCheckBox = pShowCreateCheckBox;
	}
	
	public FileImportDialogMarlovits(String name, Boolean pShowCreateCheckBox){
		//super(Hub.getPlugin().getWorkbench().getActiveWorkbenchWindow().getShell());
		super(UiDesk.getTopShell());
		file = name;
		showCreateCheckBox = pShowCreateCheckBox;
	}
	
	@Override
	protected Control createDialogArea(Composite parent){
		Composite ret = new Composite(parent, SWT.NONE);
		ret.setLayout(new GridLayout());
		ret.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		new Label(ret, SWT.None).setText("Kategorie");
		Composite cCats = new Composite(ret, SWT.NONE);
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
					DocHandle_Mv.addMainCategory(id.getValue());
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
					DocHandle_Mv.renameCategory(old, nn);
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
					DocHandle_Mv.removeCategory(old, id.getValue());
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
// List<String> cats = DocHandle_Mv.getMainCategoryNames();
// if (cats.size() > 0) {
// cbCategories.setItems(cats.toArray(new String[0]));
// cbCategories.select(0);
// }
		
		// ++++ ADD START
		new Label(ret, SWT.NONE).setText("Datum Dokument");
		dateFrom = new DateTime(ret, SWT.BORDER | SWT.DATE | SWT.DROP_DOWN);
		dateFrom.setDate(Calendar.getInstance().getTime().getYear(), Calendar.getInstance()
			.getTime().getMonth(), Calendar.getInstance().getTime().getDate());
		// ++++ ADD END
		
		new Label(ret, SWT.NONE).setText("Titel");
		tTitle = SWTHelper.createText(ret, 1, SWT.NONE);
		new Label(ret, SWT.NONE).setText("Stichwörter");
		tKeywords = SWTHelper.createText(ret, 4, SWT.NONE);
		tTitle.setText(file);
		if (dh != null) {
			tKeywords.setText(dh.get("Keywords"));
			cbCategories.setText(dh.getCategoryName());
		}
		
		// ++++ ADD START
		// *** create Aufgebot oder eben nicht
		if (showCreateCheckBox) {
			// new Label(ret, SWT.NONE).setText("Datum Dokument");
			cbCreateAufgebot = new Button(ret, SWT.CHECK);
			cbCreateAufgebot.setText("Aufgebot erstellen und öffnen");
			cbCreateAufgebot.setSelection(true);
		}
		// ++++ ADD END
		
		return ret;
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
