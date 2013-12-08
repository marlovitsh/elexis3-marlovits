package ch.marlovits.importer.aeskulap.krankenkassentarifsh.views;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.selectors.DisplayPanel;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.elexis.data.PersistentObject;
import ch.marlovits.importer.aeskulap.krankenkassentarifsh.data.SUVATarifUVG;

public class SUVATarifUVGDetailDisplay implements IDetailDisplay {
	Form form;
	DisplayPanel panel;
	FieldDescriptor<?>[] fields=
	{
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_PARENT, SUVATarifUVG.FLD_PARENT, Typ.STRING,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_POSITION, SUVATarifUVG.FLD_CODE, Typ.STRING,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_BEZEICHNUNG, SUVATarifUVG.FLD_TITEL, Typ.STRING,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_KURZBEZ, SUVATarifUVG.FLD_KURZBEZ, Typ.STRING,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_DRUCKBEZ, SUVATarifUVG.FLD_DRUCKBEZ, Typ.STRING,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_KOMMENTAR, SUVATarifUVG.FLD_KOMMENTAR, Typ.STRING,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_PREISMIN, SUVATarifUVG.FLD_PREISMIN, Typ.CURRENCY,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_PREISMAX, SUVATarifUVG.FLD_PREISMAX, Typ.CURRENCY,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_DATUMVON, SUVATarifUVG.FLD_DATUMVON, Typ.DATE,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_DATUMBIS, SUVATarifUVG.FLD_DATUMBIS, Typ.DATE,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_AUSSCHLUSSCODE, SUVATarifUVG.FLD_AUSSCHLUSSCODE, Typ.STRING,null),
		new FieldDescriptor<SUVATarifUVG>(Messages.DetailDisplay_UMTRIEBSCODE, SUVATarifUVG.FLD_UMTRIEBSCODE, Typ.STRING,null),
	};
	
	public void display(Object obj) {
		if(obj instanceof SUVATarifUVG){
			form.setText(((PersistentObject)obj).getLabel());
			panel.setObject((PersistentObject)obj);
		}
	}

	public Class<? extends PersistentObject> getElementClass() {
		return SUVATarifUVG.class;
	}

	public String getTitle() {
		return "Aeskulap SUVA Tarif UVG"; //$NON-NLS-1$
	}

	public Composite createDisplay(Composite parent, IViewSite site) {
		form=UiDesk.getToolkit().createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		form.getBody().setLayout(new GridLayout());
		panel=new DisplayPanel(form.getBody(),fields,1,1);
		panel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return panel;
	}

}
