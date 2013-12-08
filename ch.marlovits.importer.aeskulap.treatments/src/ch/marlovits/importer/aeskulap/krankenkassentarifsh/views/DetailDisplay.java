package ch.marlovits.importer.aeskulap.krankenkassentarifsh.views;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.forms.widgets.Form;

import ch.elexis.core.ui.UiDesk;
import ch.elexis.data.PersistentObject;
import ch.elexis.core.ui.selectors.DisplayPanel;
import ch.elexis.core.ui.selectors.FieldDescriptor;
import ch.elexis.core.ui.selectors.FieldDescriptor.Typ;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.IDetailDisplay;
import ch.marlovits.importer.aeskulap.krankenkassentarifsh.data.KrankenkassenTarifSH;

public class DetailDisplay implements IDetailDisplay {
	Form form;
	DisplayPanel panel;
	FieldDescriptor<?>[] fields = {
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_PARENT,
			KrankenkassenTarifSH.FLD_PARENT, Typ.STRING, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_POSITION,
			KrankenkassenTarifSH.FLD_CODE, Typ.STRING, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_BEZEICHNUNG,
			KrankenkassenTarifSH.FLD_TITEL, Typ.STRING, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_KURZBEZ,
			KrankenkassenTarifSH.FLD_KURZBEZ, Typ.STRING, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_DRUCKBEZ,
			KrankenkassenTarifSH.FLD_DRUCKBEZ, Typ.STRING, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_KOMMENTAR,
			KrankenkassenTarifSH.FLD_KOMMENTAR, Typ.STRING, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_PREISMIN,
			KrankenkassenTarifSH.FLD_PREISMIN, Typ.CURRENCY, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_PREISMAX,
			KrankenkassenTarifSH.FLD_PREISMAX, Typ.CURRENCY, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_DATUMVON,
			KrankenkassenTarifSH.FLD_DATUMVON, Typ.DATE, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_DATUMBIS,
			KrankenkassenTarifSH.FLD_DATUMBIS, Typ.DATE, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_AUSSCHLUSSCODE,
			KrankenkassenTarifSH.FLD_AUSSCHLUSSCODE, Typ.STRING, null),
		new FieldDescriptor<KrankenkassenTarifSH>(Messages.DetailDisplay_UMTRIEBSCODE,
			KrankenkassenTarifSH.FLD_UMTRIEBSCODE, Typ.STRING, null),
	};
	
	public void display(Object obj){
		if (obj instanceof KrankenkassenTarifSH) {
			form.setText(((PersistentObject) obj).getLabel());
			panel.setObject((PersistentObject) obj);
		}
	}
	
	public Class<? extends PersistentObject> getElementClass(){
		return KrankenkassenTarifSH.class;
	}
	
	public String getTitle(){
		return "Aeskulap Krankenkassentarif SH"; //$NON-NLS-1$
	}
	
	public Composite createDisplay(Composite parent, IViewSite site){
		form = UiDesk.getToolkit().createForm(parent);
		form.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		form.getBody().setLayout(new GridLayout());
		panel = new DisplayPanel(form.getBody(), fields, 1, 1);
		panel.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));
		return panel;
	}
	
}
