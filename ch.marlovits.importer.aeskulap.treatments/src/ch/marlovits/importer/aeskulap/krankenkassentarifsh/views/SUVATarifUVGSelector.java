package ch.marlovits.importer.aeskulap.krankenkassentarifsh.views;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;

import ch.elexis.core.ui.actions.JobPool;
import ch.elexis.core.ui.actions.LazyTreeLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.TreeContentProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.elexis.core.ui.views.codesystems.CodeSelectorFactory;
import ch.elexis.data.Query;
import ch.marlovits.importer.aeskulap.krankenkassentarifsh.data.SUVATarifUVG;

@SuppressWarnings("deprecation")
public class SUVATarifUVGSelector extends CodeSelectorFactory {
	
	private LazyTreeLoader<SUVATarifUVG> dataloader;
	private static final String LOADER_NAME = "AeskulapSUVATarifUVG";
	
	@SuppressWarnings("unchecked")
	public SUVATarifUVGSelector(){
		dataloader = (LazyTreeLoader<SUVATarifUVG>) JobPool.getJobPool().getJob(LOADER_NAME); //$NON-NLS-1$
		
		if (dataloader == null) {
			dataloader =
				new LazyTreeLoader<SUVATarifUVG>(LOADER_NAME, new Query<SUVATarifUVG>(
					SUVATarifUVG.class), SUVATarifUVG.FLD_PARENT, new String[] {
					SUVATarifUVG.FLD_CODE, SUVATarifUVG.FLD_TITEL
				}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			dataloader.setParentField(SUVATarifUVG.FLD_CODE);
			JobPool.getJobPool().addJob(dataloader);
		}
		JobPool.getJobPool().activate(LOADER_NAME, Job.SHORT); //$NON-NLS-1$
		
	}
	
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv){
		ViewerConfigurer vc =
			new ViewerConfigurer(new TreeContentProvider(cv, dataloader),
				new ViewerConfigurer.TreeLabelProvider(), new DefaultControlFieldProvider(cv,
					new String[] {
						SUVATarifUVG.FLD_CODE, SUVATarifUVG.FLD_TITEL
					}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
		return vc;
		
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public String getCodeSystemName(){
		return SUVATarifUVG.CODESYSTEM_NAME;
	}
	
	@Override
	public Class<SUVATarifUVG> getElementClass(){
		return SUVATarifUVG.class;
	}
}
