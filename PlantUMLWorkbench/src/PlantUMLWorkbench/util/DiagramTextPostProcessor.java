package PlantUMLWorkbench.util;

public interface DiagramTextPostProcessor {

	public String getDiagramText(String diagramText, AbstractDiagramIntent<?> diagramIntent);
}
