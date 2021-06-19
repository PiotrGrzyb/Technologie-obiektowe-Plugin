package PlantUMLWorkbench.util;

public interface DiagramIntent {
	
	// Returns the label to show for this intent, without necessarily having computed the diagram text.
	public String getLabel();

	// Computes and returns the diagram text.
	public String getDiagramText();

	// Returns the priority, to select the appropriate intent.
	public int getPriority();
}
