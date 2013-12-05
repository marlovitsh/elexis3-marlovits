/*******************************************************************************
 * Copyright (c) 2006-2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    H. Marlovits - added case-selection
 *    
 *  $Id: 
 *******************************************************************************/

package ch.marlovits_auf;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.events.ElexisEventListener;
import ch.elexis.core.data.events.ElexisEventListenerImpl;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.dialogs.EditAUFDialog;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.core.ui.util.viewers.DefaultLabelProvider;
import ch.elexis.core.ui.views.AUFZeugnis;
import ch.elexis.data.AUF;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;

/**
 * Arbeitsunfähigkeitszeugnisse erstellen und verwalten.
 * 
 * @author gerry
 * 
 */
public class AUF22 extends ViewPart implements IActivationListener {
	public static final String ID = "ch.marlovits.auf"; //$NON-NLS-1$
	private static final String ICON = "auf_view"; //$NON-NLS-1$
	TableViewer tv;
	private Action caseFilter, newAUF, delAUF, modAUF, printAUF;
	
	public String getAUFList(){
		return "This is just a test";
	}
	
	private ElexisUiEventListenerImpl eli_auf = new ElexisUiEventListenerImpl(AUF.class) {
		
		public void runInUi(ElexisEvent ev){
			if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
				// *** AUF selected
				// addAUF, delAUF, modAUF, printAUF
				delAUF.setEnabled(true);
				delAUF.setToolTipText(Messages.getString("AUF22.deleteCertificate")); //$NON-NLS-1$
				modAUF.setEnabled(true);
				modAUF.setToolTipText(Messages.getString("AUF22.editCertificate")); //$NON-NLS-1$
				printAUF.setEnabled(true);
				printAUF.setToolTipText(Messages.getString("AUF22.openPrintViewForSelectedAUF")); //$NON-NLS-1$
			} else {
				// *** AUF deselected
				delAUF.setEnabled(false);
				delAUF
					.setToolTipText(Messages.getString("AUF22.deleteCertificate") + "\n" + Messages.getString("AUF22.noCertificateSelected")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				modAUF.setEnabled(false);
				modAUF
					.setToolTipText(Messages.getString("AUF22.editCertificate") + "\n" + Messages.getString("AUF22.noCertificateSelected")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	};
	
	public TableViewer getTableViewer(){
		return tv;
	}
	
	private ElexisUiEventListenerImpl eli_fall = new ElexisUiEventListenerImpl(Fall.class) {
		
		public void runInUi(final ElexisEvent ev){
			if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
				tv.refresh();
				ElexisEventDispatcher.clearSelection(AUF.class);
				newAUF.setEnabled(true);
				newAUF.setToolTipText(Messages.getString("AUF22.createNewCert")); //$NON-NLS-1$
				printAUF.setEnabled(true);
				printAUF.setToolTipText(Messages.getString("AUF22.print")); //$NON-NLS-1$
			} else {
				newAUF.setEnabled(false);
				modAUF.setEnabled(false);
				delAUF.setEnabled(false);
			}
		}
	};
	
	private ElexisUiEventListenerImpl eli_pat = new ElexisUiEventListenerImpl(Patient.class) {
		
		public void runInUi(ElexisEvent ev){
			if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
				tv.refresh();
				ElexisEventDispatcher.clearSelection(AUF.class);
				newAUF.setEnabled(false);
				newAUF
					.setToolTipText(Messages.getString("AUF22.createNewCert") + "\n" + Messages.getString("AUF22.noCaseSelected")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				printAUF.setEnabled(false);
				printAUF
					.setToolTipText(Messages.getString("AUF22.print") + "\n" + Messages.getString("AUF22.noCaseOrPatientSelected")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				delAUF.setEnabled(false);
				delAUF
					.setToolTipText(Messages.getString("AUF22.deleteCertificate") + "\n" + Messages.getString("AUF22.noCertificateSelected")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			} else {
				modAUF.setEnabled(false);
				newAUF.setEnabled(false);
				newAUF.setToolTipText(Messages.getString("AUF22.createNewCert")); //$NON-NLS-1$
				printAUF.setEnabled(false);
				printAUF
					.setToolTipText(Messages.getString("AUF22.print") + "\n" + Messages.getString("AUF22.noCaseOrPatientSelected")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				delAUF.setEnabled(false);
				delAUF
					.setToolTipText(Messages.getString("AUF22.deleteCertificate") + "\n" + Messages.getString("AUF22.noCertificateSelected")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}
	};
	
	public AUF22(){
		setTitleImage(UiDesk.getImage(ICON));
	}
	
	@Override
	public void createPartControl(Composite parent){
		// setTitleImage(Desk.getImage(ICON));
		setPartName(Messages.getString("AUF22.certificate")); //$NON-NLS-1$
		tv = new TableViewer(parent);
		tv.setLabelProvider(new DefaultLabelProvider());
		tv.setContentProvider(new AUFContentProvider());
		makeActions();
		ViewMenus menus = new ViewMenus(getViewSite());
		menus.createMenu(newAUF, delAUF, modAUF, printAUF);
		menus.createToolbar(caseFilter, newAUF, delAUF, modAUF, printAUF);
		tv.setUseHashlookup(true);
		GlobalEventDispatcher.addActivationListener(this, this);
		tv.addSelectionChangedListener(new AUF22SelectionChangedListener());
		tv.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event){
				modAUF.run();
			}
		});
		tv.setInput(getViewSite());
		
	}
	
	class AUF22SelectionChangedListener implements ISelectionChangedListener {
		public void selectionChanged(final SelectionChangedEvent event){
			ISelection sel = tv.getSelection();
			if (sel == null) {
				ElexisEventDispatcher.clearSelection(AUF.class);
			} else {
				AUF currAUF = getSelectedAUF();
				if (currAUF == null) {
					ElexisEventDispatcher.clearSelection(AUF.class);
				} else {
					ElexisEventDispatcher.fireSelectionEvent((AUF) currAUF);
				}
			}
			caseFilter.run();
		}
	}
	
	@Override
	public void dispose(){
		GlobalEventDispatcher.removeActivationListener(this, this);
	}
	
	@Override
	public void setFocus(){
	}
	
	private void makeActions(){
		caseFilter = new Action(Messages.getString("AUF22.filterByCase"), Action.AS_CHECK_BOX) { //$NON-NLS-1$
				{
					setImageDescriptor(Images.IMG_FILTER.getImageDescriptor());
					setToolTipText(Messages.getString("AUF22.filterByCase")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					if (caseFilter.isChecked()) {
						caseFilter.setChecked(true);
						printAUF.setToolTipText(Messages.getString("AUF22.openPrintViewForCase")); //$NON-NLS-1$
					} else {
						caseFilter.setChecked(false);
						printAUF
							.setToolTipText(Messages.getString("AUF22.openPrintViewForPatient")); //$NON-NLS-1$
					}
					if (tv.getTable().getSelectionIndex() >= 0) {
						printAUF.setToolTipText(Messages
							.getString("AUF22.openPrintViewForSelectedAUF")); //$NON-NLS-1$
					}
					tv.refresh(false);
				}
			};
		newAUF = new Action(Messages.getString("AUF22.new")) { //$NON-NLS-1$
				{
					setImageDescriptor(Images.IMG_NEW.getImageDescriptor());
					setEnabled(false);
					setToolTipText(Messages.getString("AUF22.createNewCert")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					Patient pat = ElexisEventDispatcher.getSelectedPatient();
					if (pat == null) {
						SWTHelper.showError(Messages.getString("AUF22.NoPatientSelected"), //$NON-NLS-1$
							Messages.getString("AUF22.PleaseDoSelectPatient")); //$NON-NLS-1$
						return;
					}
					Konsultation kons =
						(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
					Fall fall = null;
					if (kons != null) {
						fall = kons.getFall();
						if (fall == null) {
							SWTHelper
								.showError(
									Messages.getString("AUF22.noCaseSelected"), Messages.getString("AUF22.selectCase")); //$NON-NLS-1$ //$NON-NLS-2$
							return;
							
						}
						if (!fall.getPatient().equals(pat)) {
							kons = null;
						}
					}
					if (kons == null) {
						kons = pat.getLetzteKons(false);
						if (kons == null) {
							SWTHelper
								.showError(
									Messages.getString("AUF22.noCaseSelected"), Messages.getString("AUF22.selectCase")); //$NON-NLS-1$ //$NON-NLS-2$
							return;
						}
						fall = kons.getFall();
					}
					new EditAUFDialog(getViewSite().getShell(), null, fall).open();
					tv.refresh(false);
				}
			};
		delAUF = new Action(Messages.getString("AUF22.delete")) { //$NON-NLS-1$
				{
					setImageDescriptor(Images.IMG_DELETE.getImageDescriptor());
					setEnabled(false);
					setToolTipText(Messages.getString("AUF22.deleteCertificate") + "\n" + Messages.getString("AUF22.noCertificateSelected")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				
				@Override
				public void run(){
					AUF sel = getSelectedAUF();
					if (sel != null) {
						if (MessageDialog
							.openConfirm(
								getViewSite().getShell(),
								Messages.getString("AUF22.deleteReally"), Messages.getString("AUF22.doyoywantdeletereally"))) { //$NON-NLS-1$ //$NON-NLS-2$
							sel.delete();
							tv.refresh(false);
						}
					}
				}
			};
		modAUF = new Action(Messages.getString("AUF22.edit")) { //$NON-NLS-1$
				{
					setImageDescriptor(Images.IMG_EDIT.getImageDescriptor());
					setToolTipText(Messages.getString("AUF22.editCertificate")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					AUF sel = getSelectedAUF();
					if (sel != null) {
						new EditAUFDialog(getViewSite().getShell(), sel, sel.getFall()).open();
						tv.refresh(true);
					}
				}
			};
		printAUF = new Action(Messages.getString("AUF22.print")) { //$NON-NLS-1$
				{
					setImageDescriptor(Images.IMG_PRINTER.getImageDescriptor());
					setEnabled(false);
					setToolTipText(Messages.getString("AUF22.createPrint")); //$NON-NLS-1$
				}
				
				@Override
				public void run(){
					Fall savedFall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
					try {
						if (tv.getTable().getItemCount() > 0) {
							AUF actAUF =
								(ch.elexis.data.AUF) ElexisEventDispatcher.getSelected(AUF.class);
							AUFZeugnis az =
								(AUFZeugnis) getViewSite().getPage().showView(AUFZeugnis.ID);
							if (caseFilter.isChecked()) {
								// *** print case data or selected AUF
								if (savedFall == null) {
									SWTHelper.alert(StringTool.leer,
										Messages.getString("AUF22.youHaveNotYetSelectedACase")); //$NON-NLS-1$
								} else {
									az.createAUZ(actAUF);
								}
							} else {
								// *** deselect case -> force printing of all AUFs
								// for Patient
								ElexisEventDispatcher.clearSelection(Fall.class);
								// GlobalEventDispatcher.
								az.createAUZ(actAUF);
								// *** re-select saved case
								ElexisEventDispatcher.fireSelectionEvent(savedFall);
							}
						} else {
							SWTHelper.alert(StringTool.leer,
								Messages.getString("AUF22.thereIsNoSelectedAUFForPrinting")); //$NON-NLS-1$
						}
					} catch (Exception ex) {
						ExHandler.handle(ex);
					} finally {}
					
				}
			};
	}
	
	private AUF getSelectedAUF(){
		IStructuredSelection sel = (IStructuredSelection) tv.getSelection();
		if ((sel == null) || (sel.isEmpty())) {
			return null;
		}
		return (AUF) sel.getFirstElement();
	}
	
	class AUFContentProvider implements IStructuredContentProvider {
		
		public Object[] getElements(Object inputElement){
			Patient pat = (Patient) ElexisEventDispatcher.getSelected(Patient.class);
			if (pat == null) {
				return new Object[0];
			}
			Fall fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
			Query<AUF> qbe = new Query<AUF>(AUF.class);
			qbe.add(AUF.FLD_PATIENT_ID, Query.EQUALS, pat.getId());
			if ((fall != null) && (caseFilter.isChecked())) {
				qbe.add(AUF.FLD_CASE_ID, Query.EQUALS, fall.getId());
			}
			qbe.orderBy(true, AUF.FLD_DATE_FROM, AUF.FLD_DATE_UNTIL);
			List<AUF> list = qbe.execute();
			return list.toArray();
		}
		
		public void dispose(){ /* leer */
		}
		
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
			/* leer */
		}
		
	}
	
	public void activation(boolean mode){ /* egal */
	}
	
	public void visible(boolean mode){
		if (mode) {
			ElexisEventDispatcher.getInstance().addListeners(eli_auf, eli_pat, eli_fall);
			eli_pat.catchElexisEvent(new ElexisEvent(ElexisEventDispatcher.getSelectedPatient(),
				null, ElexisEvent.EVENT_SELECTED));
			eli_fall.catchElexisEvent(new ElexisEvent(
				ElexisEventDispatcher.getSelected(Fall.class), null, ElexisEvent.EVENT_SELECTED));
		} else {
			ElexisEventDispatcher.getInstance().removeListeners(eli_auf, eli_pat, eli_fall);
		}
	}
	
}
