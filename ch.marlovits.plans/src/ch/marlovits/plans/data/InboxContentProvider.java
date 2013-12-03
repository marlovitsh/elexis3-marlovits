/*******************************************************************************
 * Copyright (c) 2010, G. Weirich and Elexis
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    G. Weirich - initial implementation
 *    
 *******************************************************************************/

package ch.marlovits.plans.data;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import ch.elexis.data.Patient;
import ch.elexis.data.Query;
import ch.elexis.core.data.services.GlobalServiceDescriptors;
import ch.elexis.core.data.services.IDocumentManager;
import ch.elexis.core.ui.text.GenericDocument;
import ch.elexis.core.data.util.Extensions;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.viewers.CommonContentProviderAdapter;
import ch.marlovits.plans.views.PlansView;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;

public class InboxContentProvider extends CommonContentProviderAdapter {
	ArrayList<File> files = new ArrayList<File>();
	PlansView view;
	LoadJob loader;
	List<GenericDocument> docs = new ArrayList<GenericDocument>();
	
	public void setView(PlansView view){
		this.view = view;
	}
	
	@Override
	public void dispose(){
		super.dispose();
	}
	
	public void reload(){
		loader.run(null);
	}
	
	public InboxContentProvider(){
		loader = new LoadJob();
		loader.schedule(1000);
	}
	
	@Override
	public Object[] getElements(Object inputElement){
		return files == null ? null : files.toArray();
	}
	
	Pattern patMatch = Pattern.compile("([0-9]+)_(.+)");
	
	private void addFiles(List<File> list, File dir){
		File[] contents = dir.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File f){
				if (f.getName().startsWith(".")) {
					return false;
				}
				if (!f.getName().endsWith(".pdf")) {
					return false;
				}
				return f.canWrite();
			}
			
		});
		for (File file : contents) {
			if (file.isDirectory()) {
				addFiles(list, file);
			} else {
				Matcher matcher = patMatch.matcher(file.getName());
				if (matcher.matches()) {
					String num = matcher.group(1);
					String nam = matcher.group(2);
					List<Patient> lPat = new Query(Patient.class, Patient.FLD_PATID, num).execute();
					if (lPat.size() == 1) {
						Patient pat = lPat.get(0);
						String cat = "dddd"; // +++++Activator.getDefault().getCategory(file);
						if (cat.equals("-") || cat.equals("??")) {
							cat = null;
						}
						IDocumentManager dm =
							(IDocumentManager) Extensions
								.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
						try {
							
							GenericDocument fd =
								new GenericDocument(pat, nam, cat, file,
									new TimeTool().toString(TimeTool.DATE_GER), "", null);
							Iterator<GenericDocument> it = docs.iterator();
							while (it.hasNext()) {
								GenericDocument gd = it.next();
								if (isSame(gd, fd)) {
									dm.removeDocument(gd.getGUID());
									it.remove();
								}
							}
							if (file.delete()) {
								dm.addDocument(fd);
								docs.add(fd);
								Activator.getDefault().getContentProvider().reload();
							}
							return;
						} catch (Exception ex) {
							ExHandler.handle(ex);
							SWTHelper.alert("Messages.InboxView_error", "ex.getMessage()");
						}
					}
				}
				list.add(file);
			}
		}
	}
	
	public boolean isSame(GenericDocument o1, GenericDocument o2){
		if (StringTool.compareWithNull(o1.getTitle(), o2.getTitle()) == 0) {
			if (StringTool.compareWithNull(o1.getCategory(), o2.getCategory()) == 0) {
				if (o1.getCreationDate().equals(o2.getCreationDate())) {
					if ((o1.getPatient() == null && o2.getPatient() == null)
						|| (o1.getPatient().equals(o2.getPatient()))) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	class LoadJob extends Job {
		
		public LoadJob(){
			super("GlobalInbox"); //$NON-NLS-1$
			setPriority(DECORATE);
			setUser(false);
			setSystem(true);
		}
		
		@Override
		protected IStatus run(IProgressMonitor monitor){
			Activator.getDefault();
			File dir = Activator.getOPPlanDir();
			if (dir != null) {
				Object dm =
					Extensions.findBestService(GlobalServiceDescriptors.DOCUMENT_MANAGEMENT);
				if (dm == null) {
					return new Status(Status.ERROR, Activator.PLUGIN_ID,
						"Messages.InboxContentProvider_thereIsNoDocumentManagerHere"); // +++++
				}
				files.clear();
				addFiles(files, dir);
				if (view != null) {
					view.reload();
				}
				schedule(120000L);
			}
			return Status.OK_STATUS;
		}
		
	}
	
}
