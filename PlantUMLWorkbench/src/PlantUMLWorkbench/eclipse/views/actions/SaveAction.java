package PlantUMLWorkbench.eclipse.views.actions;

import java.util.function.Supplier;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SaveAsDialog;

import PlantUMLWorkbench.eclipse.utils.PlantumlConstants;
import PlantUMLWorkbench.eclipse.utils.PlantumlUtil;
import PlantUMLWorkbench.eclipse.utils.WorkbenchUtil;
import PlantUMLWorkbench.util.DiagramImageData;

public class SaveAction extends DiagramImageAction<Shell> {

	public SaveAction(final Supplier<DiagramImageData> diagramImageDataSupplier, final Shell shell) {
		super(diagramImageDataSupplier, shell);
		setText(PlantumlConstants.SAVE_MENU);
	}

	@Override
	public void run() {
		final SaveAsDialog fileDialog = new SaveAsDialog(getContext());
		if (fileDialog.open() == Dialog.OK) {
			final IPath path = fileDialog.getResult();
			if (path != null) {
				try {
					PlantumlUtil.saveDiagramImage(getDiagramImageData(), path, true);
				} catch (final Exception e) {
					WorkbenchUtil.errorBox("Error during file generation: " + e);
				}
			}
		}
	}
}
