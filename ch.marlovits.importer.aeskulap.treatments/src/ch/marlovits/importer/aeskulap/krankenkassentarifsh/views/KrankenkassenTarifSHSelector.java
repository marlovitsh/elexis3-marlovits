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
import ch.marlovits.importer.aeskulap.krankenkassentarifsh.data.KrankenkassenTarifSH;

@SuppressWarnings("deprecation")
public class KrankenkassenTarifSHSelector extends CodeSelectorFactory {
	
	private LazyTreeLoader<KrankenkassenTarifSH> dataloader;
	private static final String LOADER_NAME = "AeskulapKrankenkassentarifSH";
	
	@SuppressWarnings("unchecked")
	public KrankenkassenTarifSHSelector(){
		dataloader =
			(LazyTreeLoader<KrankenkassenTarifSH>) JobPool.getJobPool().getJob(LOADER_NAME); //$NON-NLS-1$
		
		if (dataloader == null) {
			dataloader =
				new LazyTreeLoader<KrankenkassenTarifSH>(LOADER_NAME,
					new Query<KrankenkassenTarifSH>(KrankenkassenTarifSH.class),
					KrankenkassenTarifSH.FLD_PARENT, new String[] {
						KrankenkassenTarifSH.FLD_CODE, KrankenkassenTarifSH.FLD_TITEL
					}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			dataloader.setParentField(KrankenkassenTarifSH.FLD_CODE);
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
						KrankenkassenTarifSH.FLD_CODE, KrankenkassenTarifSH.FLD_TITEL
					}), new ViewerConfigurer.DefaultButtonProvider(), new SimpleWidgetProvider(
					SimpleWidgetProvider.TYPE_TREE, SWT.NONE, null));
		return vc;
		
	}
	
	@Override
	public void dispose(){}
	
	@Override
	public String getCodeSystemName(){
		return KrankenkassenTarifSH.CODESYSTEM_NAME;
	}
	
	@Override
	public Class<KrankenkassenTarifSH> getElementClass(){
		return KrankenkassenTarifSH.class;
	}
}
