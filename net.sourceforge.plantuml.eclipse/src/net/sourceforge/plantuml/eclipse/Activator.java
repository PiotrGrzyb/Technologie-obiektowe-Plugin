package net.sourceforge.plantuml.eclipse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import net.sourceforge.plantuml.eclipse.utils.DiagramTextProvider;
import net.sourceforge.plantuml.eclipse.utils.ILinkOpener;
import net.sourceforge.plantuml.preferences.DiagramIntentProvidersPreferencePage;
import net.sourceforge.plantuml.util.DiagramIntentProvider;
import net.sourceforge.plantuml.util.DiagramTextIntentProvider;

/**
 * The activator class controls the plug-in life cycle
 *
 * @author durif_c
 */
public class Activator extends AbstractUIPlugin implements DiagramIntentProviderRegistry {

	// The plug-in ID
	public static final String PLUGIN_ID = "net.sourceforge.plantuml.eclipse";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
		// Empty constructor
	}

	private IResourceChangeListener resourceListener;

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		resourceListener = null; // TODO: re-enable PlantumlUtil.createResourceListener();
		if (resourceListener != null) {
			ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener, IResourceChangeEvent.POST_BUILD);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		if (resourceListener != null) {
			ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);
		}
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	private List<DiagramIntentProvider> diagramIntentProviders = null;

	private Map<DiagramIntentProvider, DiagramIntentProviderInfo> diagramIntentProviderInfo = null;

	public boolean isEnabled(final DiagramIntentProvider diagramIntentProvider) {
		final IPreferenceStore preferenceStore = getPreferenceStore();
		final String id = getDiagramIntentProviderId(diagramIntentProvider);
		return ! preferenceStore.getBoolean(DiagramIntentProvidersPreferencePage.getDiagramTextProviderDisablementKey(id));
	}

	private final Collection<DiagramTextProviderProcessor> diagramTextProviderProcessors = new ArrayList<>();

	public void addDiagramTextProviderProcessor(final DiagramTextProviderProcessor diagramTextProviderProcessor) {
		if (diagramIntentProviders == null) {
			diagramTextProviderProcessors.add(diagramTextProviderProcessor);
		} else {
			diagramTextProviderProcessor.processDiagramIntentProviders(this);
		}
	}

	public DiagramIntentProvider[] getDiagramIntentProviders(final Boolean enabled) {
		if (diagramIntentProviders == null) {
			diagramIntentProviders = new ArrayList<DiagramIntentProvider>();
			diagramIntentProviderInfo = new HashMap<DiagramIntentProvider, DiagramIntentProviderInfo>();
			processDiagramTextProviders();
			for (final DiagramTextProviderProcessor diagramTextProviderProcessor : diagramTextProviderProcessors) {
				diagramTextProviderProcessor.processDiagramIntentProviders(this);
			}
			processDiagramIntentProviders();
		}
		Collection<DiagramIntentProvider> diagramIntentProviders = this.diagramIntentProviders;
		if (enabled != null) {
			diagramIntentProviders = new ArrayList<>(diagramIntentProviders);
			final Iterator<DiagramIntentProvider> it = diagramIntentProviders.iterator();
			while (it.hasNext()) {
				if (enabled != isEnabled(it.next())) {
					it.remove();
				}
			}
		}
		return diagramIntentProviders.toArray(new DiagramIntentProvider[diagramIntentProviders.size()]);
	}

	public String getDiagramIntentProviderId(final DiagramIntentProvider diagramIntentProvider) {
		if (diagramIntentProviderInfo != null) {
			final DiagramIntentProviderInfo info = diagramIntentProviderInfo.get(diagramIntentProvider);
			final String id = info != null ? info.id : null;
			return id != null ? id : diagramIntentProvider.getClass().getName();
		}
		return null;
	}

	public String getDiagramIntentProviderLabel(final DiagramIntentProvider diagramIntentProvider) {
		if (diagramIntentProviderInfo != null) {
			final DiagramIntentProviderInfo info = diagramIntentProviderInfo.get(diagramIntentProvider);
			return info != null ? info.label : null;
		}
		return null;
	}

	public final static int DEFAULT_PRIORITY = 0, NORMAL_PRIORITY = 5, CUSTOM_PRIORITY = 10;

	public int getDiagramIntentProviderPriority(final DiagramIntentProvider diagramIntentProvider) {
		if (diagramIntentProviderInfo != null) {
			final DiagramIntentProviderInfo info = diagramIntentProviderInfo.get(diagramIntentProvider);
			return info != null ? info.priority : NORMAL_PRIORITY;
		}
		return NORMAL_PRIORITY;
	}

	private void processDiagramTextProviders() {
		final IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(getBundle().getSymbolicName() + ".diagramTextProvider");
		final IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			for (final IConfigurationElement ces: extensions[i].getConfigurationElements()) {
				final String name = ces.getName();
				if ("diagramTextProvider".equals(name)) {
					try {
						final DiagramTextProvider diagramTextProvider = (DiagramTextProvider) ces.createExecutableExtension("providerClass");
						final DiagramIntentProviderInfo info = getProviderInfo(ces);
						registerDiagramIntentProvider(new DiagramTextIntentProvider(diagramTextProvider), info);
					} catch (final InvalidRegistryObjectException e) {
					} catch (final CoreException e) {
					}
				}
			}
		}
	}

	private void processDiagramIntentProviders() {
		final IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(getBundle().getSymbolicName() + ".diagramIntentProvider");
		final IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			for (final IConfigurationElement ces: extensions[i].getConfigurationElements()) {
				final String name = ces.getName();
				if ("diagramIntentProvider".equals(name)) {
					try {
						final DiagramIntentProvider diagramIntentProvider = (DiagramIntentProvider) ces.createExecutableExtension("providerClass");
						final DiagramIntentProviderInfo info = getProviderInfo(ces);
						registerDiagramIntentProvider(diagramIntentProvider, info);
					} catch (final InvalidRegistryObjectException e) {
					} catch (final CoreException e) {
						System.err.println(e);
					}
				}
			}
		}
	}

	private DiagramIntentProviderInfo getProviderInfo(final IConfigurationElement ces) {
		final String priorityValue = ces.getAttribute("priority");
		int priority = NORMAL_PRIORITY;
		if ("custom".equals(priorityValue)) {
			priority = CUSTOM_PRIORITY;
		} else if ("normal".equals(priorityValue)) {
			priority = NORMAL_PRIORITY;
		} else if ("default".equals(priorityValue)) {
			priority = DEFAULT_PRIORITY;
		} else {
			try {
				priority = Integer.valueOf(priorityValue);
			} catch (final NumberFormatException e) {
			}
		}
		final DiagramIntentProviderInfo info = new DiagramIntentProviderInfo();
		info.id = ces.getAttribute("id");
		info.label = ces.getAttribute("label");
		info.priority = priority;
		return info;
	}

	private final Comparator<DiagramIntentProvider> priorityComparator = new Comparator<DiagramIntentProvider>() {
		@Override
		public int compare(final DiagramIntentProvider dtp2, final DiagramIntentProvider dtp1) {
			return Activator.this.diagramIntentProviderInfo.get(dtp1).priority - Activator.this.diagramIntentProviderInfo.get(dtp2).priority;
		}
	};

	@Override
	public void registerDiagramIntentProvider(final DiagramIntentProvider diagramIntentProvider, final DiagramIntentProviderInfo info) {
		this.diagramIntentProviderInfo.put(diagramIntentProvider, info);
		int pos = 0;
		while (pos < diagramIntentProviders.size()) {
			if (priorityComparator.compare(diagramIntentProvider, diagramIntentProviders.get(pos)) < 0) {
				break;
			}
			pos++;
		}
		this.diagramIntentProviders.add(pos, diagramIntentProvider);
	}

	private List<ILinkOpener> linkOpeners;

	public ILinkOpener[] getLinkOpeners() {
		if (linkOpeners == null) {
			linkOpeners = new ArrayList<ILinkOpener>();
			processLinkOpeners();
		}
		return linkOpeners.toArray(new ILinkOpener[linkOpeners.size()]);
	}

	private void processLinkOpeners() {
		final IExtensionPoint ep = Platform.getExtensionRegistry().getExtensionPoint(getBundle().getSymbolicName() + ".linkOpener");
		final IExtension[] extensions = ep.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			for (final IConfigurationElement ces: extensions[i].getConfigurationElements()) {
				final String name = ces.getName();
				if ("linkOpener".equals(name)) {
					try {
						final ILinkOpener linkOpener = (ILinkOpener) ces.createExecutableExtension("linkOpenerClass");
						linkOpeners.add(linkOpener);
					} catch (final InvalidRegistryObjectException e) {
					} catch (final CoreException e) {
					}
				}
			}
		}
	}
}
