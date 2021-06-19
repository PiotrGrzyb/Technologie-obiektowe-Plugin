package PlantUMLWorkbench.eclipse.views.actions;

import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import PlantUMLWorkbench.eclipse.utils.PlantumlConstants;
import PlantUMLWorkbench.util.DiagramImageData;


public class CopyAsciiAction extends DiagramImageAction<Display> {


	public CopyAsciiAction(final Supplier<DiagramImageData> diagramImageDataSupplier, final Display display) {
		super( diagramImageDataSupplier, display);
		setText(PlantumlConstants.COPY_ASCII_MENU);
	}

	@Override
	public void run() {
		String s = "empty";
		try {
			final DiagramImageData diagramImageData = getDiagramImageData();
			final String source = diagramImageData.getDiagramData().getTextDiagram();
			final SourceStringReader sourceStringReader = new SourceStringReader(source);
			final ByteArrayOutputStream os = new ByteArrayOutputStream();
			sourceStringReader.outputImage(os, diagramImageData.getImageNum(), new FileFormatOption(FileFormat.ATXT));
			os.close();
			s = new String(os.toByteArray());
		} catch (final Exception e) {
			e.printStackTrace();
			s = e.toString();
		}
		final Clipboard clipboard = new Clipboard(getContext());
		clipboard.setContents(new Object[]{s}, new Transfer[]{TextTransfer.getInstance()});
		clipboard.dispose();
	}
}
