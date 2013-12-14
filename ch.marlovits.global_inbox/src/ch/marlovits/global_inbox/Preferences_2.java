package ch.marlovits.global_inbox;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import ch.elexis.exchange.TwainAccess;
import ch.marlovits.global_inbox.OutsourceUiJob;
import ch.marlovits.global_inbox.Preferences_2;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.ui.exchange.IScannerAccess.ISource;
import ch.elexis.core.ui.preferences.SettingsPreferenceStore;

public class Preferences_2 extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String PREFBASE = "plugins/omnivore-direct";
	public static final String STOREFS = PREFBASE + "/store_in_fs";
	public static final String BASEPATH = PREFBASE + "/basepath";
	// public static final String DEFAULTSCANNER = PREFBASE + "/default_scanner";
	public static final String CATEGORIES = PREFBASE + "/categories";
	public static final String DEFAULTSCANNER = TwainAccess.DEFAULTSOURCE;
	
	Button outsource;
	boolean storeFs = CoreHub.localCfg.get(Preferences_2.STOREFS, false);
	boolean basePathSet = false;
	
	public Preferences_2(){
		super("Omnivore", GRID);
		setPreferenceStore(new SettingsPreferenceStore(CoreHub.localCfg));
		String basePath = CoreHub.localCfg.get(Preferences_2.BASEPATH, null);
		if (basePath != null) {
			if (basePath.length() > 0) {
				basePathSet = true;
			}
		}
	}
	
	@Override
	protected void createFieldEditors(){
		ISource[] sources = null;
		String[][] sourceNames = new String[0][2];
		try {
			TwainAccess twainAccess = new TwainAccess();
			sources = twainAccess.getSources();
			sourceNames = new String[sources.length][2];
			for (int i = 0; i < sources.length; i++) {
				sourceNames[i][0] = sources[i].getName();
				sourceNames[i][1] = sources[i].getName();
			}
		} catch (Exception e) {
			// 12.03.2010 patch ts: Auf Windows Server 2008 (SBS) crash, weil keine Twain Quellen
			// gefunden werden...
			// View wollen wir aber trotzdem verwenden...
		}
		
		BooleanFieldEditor bStoreFS =
			new BooleanFieldEditor(STOREFS, "In Dateisystem speichern", getFieldEditorParent()) {
				@Override
				protected void fireValueChanged(String property, Object oldValue, Object newValue){
					super.fireValueChanged(property, oldValue, newValue);
					storeFs = (Boolean) newValue;
					enableOutsourceButton();
				}
			};
		DirectoryFieldEditor dfStorePath =
			new DirectoryFieldEditor(BASEPATH, "Netzwerkpfad für Dokumente", getFieldEditorParent()) {
				@Override
				protected void fireValueChanged(String property, Object oldValue, Object newValue){
					super.fireValueChanged(property, oldValue, newValue);
					String basePath = (String) newValue;
					if (basePath != null) {
						if (basePath.length() > 0) {
							basePathSet = true;
						} else {
							basePathSet = false;
						}
					} else {
						basePathSet = false;
					}
					enableOutsourceButton();
				}
			};
		dfStorePath.setEmptyStringAllowed(true);
		RadioGroupFieldEditor rgScanner =
			new RadioGroupFieldEditor(DEFAULTSCANNER, "Standard-Scanner", 2, sourceNames,
				getFieldEditorParent());
		
		addField(bStoreFS);
		addField(dfStorePath);
		addField(rgScanner);
	}
	
	@Override
	protected Control createContents(Composite parent){
		Control c = super.createContents(parent);
		
		addSeparator();
		
		Label label = new Label(getFieldEditorParent(), SWT.NONE);
		label.setText("Datenbankeinträge auf Filesystem auslagern");
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);
		outsource = new Button(getFieldEditorParent(), SWT.PUSH);
		outsource.setText("Auslagern");
		outsource.setEnabled(false);
		outsource.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e){
				OutsourceUiJob job = new OutsourceUiJob();
				job.execute(getShell());
			}
		});
		
		enableOutsourceButton();
		
		return c;
	}
	
	private void addSeparator(){
		Label separator = new Label(getFieldEditorParent(), SWT.HORIZONTAL | SWT.SEPARATOR);
		GridData separatorGridData = new GridData();
		separatorGridData.horizontalSpan = 3;
		separatorGridData.grabExcessHorizontalSpace = true;
		separatorGridData.horizontalAlignment = GridData.FILL;
		separatorGridData.verticalIndent = 0;
		separator.setLayoutData(separatorGridData);
	}
	
	private void enableOutsourceButton(){
		if (storeFs && basePathSet)
			outsource.setEnabled(true);
		else
			outsource.setEnabled(false);
	}
	
	public void init(IWorkbench workbench){
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean okToLeave(){
		return true;
	}
	
	@Override
	protected void performApply(){
		CoreHub.globalCfg.flush();
		CoreHub.localCfg.flush();
		super.performApply();
	}
	
}
