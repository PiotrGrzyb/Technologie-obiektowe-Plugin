package PlantUMLWorkbench.eclipse;

import PlantUMLWorkbench.util.DiagramIntentProvider;

public interface DiagramIntentProviderRegistry {
	public void registerDiagramIntentProvider(final DiagramIntentProvider diagramTextProvider, final DiagramIntentProviderInfo info);
}
