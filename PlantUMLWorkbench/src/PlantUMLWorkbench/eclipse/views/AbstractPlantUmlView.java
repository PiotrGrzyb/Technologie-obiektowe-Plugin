package PlantUMLWorkbench.eclipse.views;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Composite;

import PlantUMLWorkbench.eclipse.Activator;
import imagecontrol.ILinkSupport;
import PlantUMLWorkbench.eclipse.utils.ILinkOpener;
import PlantUMLWorkbench.eclipse.utils.LinkData;
import PlantUMLWorkbench.eclipse.utils.PlantumlUtil;
import PlantUMLWorkbench.util.AbstractDiagramIntent;
import PlantUMLWorkbench.util.DiagramData;
import PlantUMLWorkbench.util.DiagramIntent;
import PlantUMLWorkbench.util.ResourceInfo;

public abstract class AbstractPlantUmlView extends AbstractDiagramSourceView implements ILinkSupport {

	protected Composite composite;

	@Override
	public void createPartControl(final Composite parent) {
		composite = parent;
		createDiagramControl(parent);
		super.createPartControl(parent);
	}

	protected abstract void createDiagramControl(final Composite parent);

	//

	protected DiagramData diagramData = null;

	protected boolean shouldUpdateView(final DiagramData diagramData) {
		return this.diagramData == null || this.diagramData == diagramData;
	}

	@Override
	public String getDiagramText() {
		return diagramData != null ? diagramData.getTextDiagram() : null;
	}

	@Override
	protected void updateDiagramText(final String textDiagram, final DiagramIntent diagramIntent, final IProgressMonitor monitor) {
		if (isVisible() && textDiagram != null && (diagramData == null || (! textDiagram.equals(diagramData.getTextDiagram())))) {
			this.diagramData = null;
			final DiagramData diagramData = new DiagramData(textDiagram);
			setDiagramViewStatus(ViewStatus.DIAGRAM_VIEW_TEXT, textDiagram);
			if (diagramIntent instanceof AbstractDiagramIntent<?>) {
				final ResourceInfo resourceInfo = ((AbstractDiagramIntent<?>) diagramIntent).getResourceInfo();
				if (resourceInfo != null) {
					if (resourceInfo.getOriginalPath() != null) {
						diagramData.setOriginal(new Path(resourceInfo.getOriginalPath()));
					}
					diagramData.setMarkerAttributes(resourceInfo.getMarkerAttributes());
				}
			}
			updateDiagram(diagramData, monitor);
			if (monitor == null || (! monitor.isCanceled())) {
				this.diagramData = diagramData;
				updateDiagramMarkers();
			}
		}
	}

	protected void updateDiagram(final IProgressMonitor monitor) {
		if (this.diagramData != null) {
			updateDiagram(diagramData, monitor);
		}
	}

	protected abstract void updateDiagram(final DiagramData diagramData, IProgressMonitor monitor);

	@Override
	public void setVisible(final boolean visible) {
		super.setVisible(visible);
		if (visible && diagramData != null) {
			updateDiagramText(true, null, null);
		}
	}

	protected Runnable layoutComposite = () -> {
		if (! composite.isDisposed()) {
			composite.layout();
		}
	};

	protected void updateDiagramMarkers() {
		if (diagramData.getOriginal() != null) {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(diagramData.getOriginal());
			if (file != null && file.exists()) {
				PlantumlUtil.updateMarker(file, diagramData.getTextDiagram(), null, true, diagramData.getMarkerAttributes());
			}
		}
	}

	private Collection<ILinkOpener> linkOpeners = null;

	private Collection<ILinkOpener> getLinkOpeners() {
		if (linkOpeners == null) {
			linkOpeners = Arrays.asList(Activator.getDefault().getLinkOpeners());
		}
		return linkOpeners;
	}

	@Override
	public void openLink(final Object link) {
		LinkData linkData = null;
		if (link instanceof LinkData) {
			linkData = (LinkData) link;
		} else if (link != null) {
			try {
				final URI uri = new URI(link.toString());
				linkData = new LinkData();
				linkData.href = uri.toString();
			} catch (final URISyntaxException e) {
			}
		}
		if (linkData != null) {
			openLink(linkData);
		}
	}

	protected boolean openLink(final LinkData linkData) {
		final ILinkOpener linkOpener = findBestLinkOpener(linkData, ILinkOpener.DEFAULT_SUPPORT);
		if (linkOpener != null) {
			linkOpener.openLink(linkData);
			return true;
		}
		return false;
	}

	private ILinkOpener findBestLinkOpener(final LinkData link, final int minSupport) {
		int bestSupport = ILinkOpener.NO_SUPPORT;
		ILinkOpener best = null;
		for (final ILinkOpener linkOpener : getLinkOpeners()) {
			int support = ILinkOpener.NO_SUPPORT;
			try {
				support = linkOpener.supportsLink(link);
			} catch (final Exception e) {
			}
			if (support >= bestSupport) {
				bestSupport = support;
				best = linkOpener;
			}
		}
		return (bestSupport >= minSupport ? best : null);
	}
}
