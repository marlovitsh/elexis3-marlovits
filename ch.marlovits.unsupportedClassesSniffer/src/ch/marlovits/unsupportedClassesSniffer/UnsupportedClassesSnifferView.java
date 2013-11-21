package ch.marlovits.unsupportedClassesSniffer;

import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.jface.viewers.CellEditor.LayoutData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

public class UnsupportedClassesSnifferView extends ViewPart {
	public static final String ID = "ch.marlovits.databaseconverter.views.MarlovitsDataBaseConverterView";

	public static Text snifferList;
	public static Text unresolvedClasses;
	public static Text snifferErrorsList;

	public UnsupportedClassesSnifferView() {
	}

	public void createPartControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		comp.setLayout(gl);
		LayoutData layoutData = new LayoutData();
		layoutData.verticalAlignment = SWT.TOP;
		comp.setLayoutData(layoutData);

		// ***********
		Label lbl1 = new Label(comp, SWT.None);
		lbl1.setText("Search for unresolved classes in the following fields:");

		snifferList = new Text(comp, SWT.BORDER + SWT.MULTI);
		String snifferListStr = "";
		for (String sniffy : UnsupportedClassesSniffer.snifferList)
			snifferListStr = snifferListStr + sniffy + "\n";
		snifferListStr = snifferListStr.replaceAll("\n\n", "\n");
		snifferListStr = snifferListStr.substring(0,
				snifferListStr.length() - 1);
		snifferList.setText(snifferListStr);

		// ***********
		Button btnSearchForUnresolvedClasses = new Button(comp, SWT.PUSH);
		btnSearchForUnresolvedClasses
				.setText("Search for unresolved classes now");
		btnSearchForUnresolvedClasses
				.addSelectionListener(new SelectionListener() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						unresolvedClasses.setText("");
						snifferErrorsList.setText("");
						HashSet<String> foundClasses = UnsupportedClassesSniffer
								.searchAllTablesForUnresolvedClasses(snifferList
										.getText());
						String result = "";
						Iterator<String> iterator = foundClasses.iterator();
						while (iterator.hasNext())
							result = result + iterator.next() + "\n";
						if (result.isEmpty())
							unresolvedClasses
									.setText("no unresolved classes found");
						else
							unresolvedClasses
									.setText("*** unresolved classes found ***\n"
											+ result);
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e) {
					}
				});

		unresolvedClasses = new Text(comp, SWT.BORDER + SWT.MULTI);
		GridData gridData = new GridData();
		gridData.widthHint = 500;
		gridData.heightHint = 150;
		gridData.grabExcessHorizontalSpace = true;
		unresolvedClasses.setLayoutData(gridData);

		// ***********
		Label lbl2 = new Label(comp, SWT.None);
		lbl2.setText("Errors on undef classes sniffing");

		snifferErrorsList = new Text(comp, SWT.BORDER + SWT.MULTI);
		snifferErrorsList.setLayoutData(gridData);
	}

	@Override
	public void setFocus() {
	}

}