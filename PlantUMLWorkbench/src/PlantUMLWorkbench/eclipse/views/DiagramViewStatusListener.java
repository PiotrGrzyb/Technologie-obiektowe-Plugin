package PlantUMLWorkbench.eclipse.views;

public interface DiagramViewStatusListener {

	public void diagramViewStatusChanged(AbstractDiagramSourceView view, AbstractDiagramSourceView.ViewStatus status, Object diagram);
}
