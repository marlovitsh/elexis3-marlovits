package ch.marlovits.importer.aeskulap.diagnosen.views;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import ch.elexis.data.Query;
import ch.elexis.core.ui.actions.JobPool;
import ch.elexis.core.ui.actions.LazyTreeLoader;
import ch.elexis.core.ui.util.viewers.CommonViewer;
import ch.elexis.core.ui.util.viewers.DefaultControlFieldProvider;
import ch.elexis.core.ui.util.viewers.SimpleWidgetProvider;
import ch.elexis.core.ui.util.viewers.TreeContentProvider;
import ch.elexis.core.ui.util.viewers.ViewerConfigurer;
import ch.marlovits.importer.aeskulap.diagnosen.data.MarlovitsCodes;

public class CodeSelectorFactory extends
		ch.elexis.core.ui.views.codesystems.CodeSelectorFactory {
	private LazyTreeLoader<MarlovitsCodes> dataloader;
	private static final String LOADER_NAME="AeskulapICD9";
	

	@SuppressWarnings("unchecked")
	public CodeSelectorFactory(){
		dataloader=(LazyTreeLoader<MarlovitsCodes>) JobPool.getJobPool().getJob(LOADER_NAME); //$NON-NLS-1$
		
		if(dataloader==null){
			dataloader=new LazyTreeLoader<MarlovitsCodes>(LOADER_NAME,new Query<MarlovitsCodes>(MarlovitsCodes.class),"parent",new String[]{"Kuerzel","Text"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			dataloader.setParentField("Kuerzel");
			JobPool.getJobPool().addJob(dataloader);
		}
		JobPool.getJobPool().activate(LOADER_NAME,Job.SHORT); //$NON-NLS-1$

	}
	@Override
	public ViewerConfigurer createViewerConfigurer(CommonViewer cv) {
		ViewerConfigurer vc=new ViewerConfigurer(
				new TreeContentProvider(cv,dataloader),
				new ViewerConfigurer.TreeLabelProvider(),
				new DefaultControlFieldProvider(cv, new String[]{"Kuerzel","Text"}), //$NON-NLS-1$
				new ViewerConfigurer.DefaultButtonProvider(),
				new SimpleWidgetProvider(SimpleWidgetProvider.TYPE_TREE, SWT.NONE,null)
				);
		return vc;

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCodeSystemName() {
		return MarlovitsCodes.CODESYSTEM_NAME;
	}

	@Override
	public Class getElementClass() {
		return MarlovitsCodes.class;
	}

}
