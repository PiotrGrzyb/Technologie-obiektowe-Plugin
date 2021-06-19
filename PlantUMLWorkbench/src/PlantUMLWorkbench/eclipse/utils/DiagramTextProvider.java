package PlantUMLWorkbench.eclipse.utils;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewPart;

// Functionality for generating diagrams from contents of active editors, optionally based on editor selection

public interface DiagramTextProvider {

// Tells if the specified editor (or its input) is supported

	public default boolean supportsEditor(final IEditorPart editorPart) {
		return false;
	}

	 // Tells if the specified view is supported

	public default boolean supportsView(final IViewPart viewPart) {
		return false;
	}

	
	 // Tells whether the specified editor or view selection is supported

	public boolean supportsSelection(ISelection selection);

	 // Computes the diagram text for the specific editor part and selection

	public default String getDiagramText(final IEditorPart editorPart, final ISelection selection) {
		return null;
	}

    // Computes the diagram text for the specific view part and selection

	public default String getDiagramText(final IViewPart viewPart, final ISelection selection) {
		return null;
	}
}
