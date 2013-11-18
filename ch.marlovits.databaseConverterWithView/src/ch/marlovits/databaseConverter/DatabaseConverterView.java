package ch.marlovits.databaseConverter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

public class DatabaseConverterView extends ViewPart {
	public static final String ID = "ch.marlovits.databaseconverter.views.MarlovitsDataBaseConverterView";

	public DatabaseConverterView() {
	}

	public void createPartControl(Composite parent) {
		 Composite comp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		comp.setLayout(gl);

		Label lbl = new Label(comp, SWT.None);
		lbl.setText("Konvertieren aller MFULists von alter zu neuer Version");

		Button btnConvertMFULists = new Button(comp, SWT.PUSH);
		btnConvertMFULists.setText("Convert MFULists");
		btnConvertMFULists.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DatabaseConverters.convertAllMFUEntries();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
	}

	@Override
	public void setFocus() {
	}

}