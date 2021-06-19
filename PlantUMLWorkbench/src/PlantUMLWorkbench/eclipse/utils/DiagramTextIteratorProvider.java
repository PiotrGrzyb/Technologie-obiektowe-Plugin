package PlantUMLWorkbench.eclipse.utils;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;


public interface DiagramTextIteratorProvider extends DiagramTextProvider {
	// Computes an iterator of editor selections suitable for getDiagramText(IEditorPart, ISelection)
	public Iterator<ISelection> getDiagramText(final IEditorPart editorPart);
}
