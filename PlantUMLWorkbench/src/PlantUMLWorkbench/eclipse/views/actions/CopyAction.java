package PlantUMLWorkbench.eclipse.views.actions;

import java.util.function.Supplier;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import PlantUMLWorkbench.eclipse.utils.PlantumlConstants;
import PlantUMLWorkbench.util.DiagramImageData;

/**Manage the copy action.
 *
 * @author durif_c
 *
 */
public class CopyAction extends DiagramImageAction<Display> {

	/**
	 *
	 * @param diagram Diagram
	 */
	public CopyAction(final Supplier<DiagramImageData> diagramImageDataSupplier, final Display display) {
		super(diagramImageDataSupplier, display);
		setText(PlantumlConstants.COPY_MENU);
	}

	/**
	 *
	 */
	@Override
	public void run() {
		final Clipboard clipboard = new Clipboard(getContext());
		clipboard.setContents(new Object[]{getImage()}, new Transfer[]{ImageTransfer.getInstance()});
		clipboard.dispose();
	}
}
