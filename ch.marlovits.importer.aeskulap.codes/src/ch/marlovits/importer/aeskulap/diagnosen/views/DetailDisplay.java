package ch.marlovits.importer.aeskulap.diagnosen.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.util.LabeledInputField;
import ch.elexis.core.ui.util.LabeledInputField.InputData;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.marlovits.importer.aeskulap.diagnosen.data.MarlovitsCodes;

public class DetailDisplay implements IDetailDisplay {
	Form form;
	LabeledInputField.AutoForm tblPls;
	InputData[] data = new InputData[] {
		new InputData("Kuerzel"), //$NON-NLS-1$
		new InputData("Text"),
	};
	Text tComment;
	
	public Composite createDisplay(Composite parent, IViewSite site){
		form = UiDesk.getToolkit().createForm(parent);
		TableWrapLayout twl = new TableWrapLayout();
		form.getBody().setLayout(twl);
		
		tblPls = new LabeledInputField.AutoForm(form.getBody(), data);
		
		TableWrapData twd = new TableWrapData(TableWrapData.FILL_GRAB);
		twd.grabHorizontal = true;
		tblPls.setLayoutData(twd);
		TableWrapData twd2 = new TableWrapData(TableWrapData.FILL_GRAB);
		tComment = UiDesk.getToolkit().createText(form.getBody(), "", SWT.BORDER);
		tComment.setLayoutData(twd2);
		return form.getBody();
		
	}
	
	public void display(Object obj){
		if (obj instanceof MarlovitsCodes) { // should always be true...
			MarlovitsCodes ls = (MarlovitsCodes) obj;
			form.setText(ls.getLabel());
			tblPls.reload(ls);
			tComment.setText(ls.get("Kommentar"));
		}
	}
	
	public Class getElementClass(){
		return MarlovitsCodes.class;
	}
	
	public String getTitle(){
		return MarlovitsCodes.CODESYSTEM_NAME;
	}
	
}
