package PlantUMLWorkbench.eclipse.views.actions;

import java.util.function.Supplier;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import PlantUMLWorkbench.eclipse.utils.PlantumlConstants;
import PlantUMLWorkbench.util.DiagramData;


public class CopySourceAction extends DiagramAction<DiagramData, Display> {


	public CopySourceAction(final Supplier<DiagramData> diagramDataSupplier, final Display display) {
		super(diagramDataSupplier, display);
		setText(PlantumlConstants.COPY_SOURCE_MENU);
	}

	@Override
	public void run() {
		final Clipboard clipboard = new Clipboard(getContext());
		clipboard.setContents(new Object[]{getDiagramData().getTextDiagram()}, new Transfer[]{TextTransfer.getInstance()});
		clipboard.dispose();
	}
}
