package PlantUMLWorkbench.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.FileSystem;
import net.sourceforge.plantuml.OptionFlags;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.core.DiagramDescription;
import PlantUMLWorkbench.eclipse.Activator;
import PlantUMLWorkbench.eclipse.utils.LinkData;
import PlantUMLWorkbench.eclipse.utils.PlantumlConstants;
import PlantUMLWorkbench.eclipse.utils.WorkbenchUtil;
import net.sourceforge.plantuml.security.SFile;

public class DiagramData {

	// diagram source
	private final String textDiagram;
	// number of images, derived from textDiagram
	private int imageCount;
	// the file from which the diagram was derived/extracted, used as context for resolving includes
	private IPath original;
	// attributes from file marker
	private Map<String, Object> markerAttributes;

	private static final Pattern pattern = Pattern.compile("(?i)(?m)^\\W*newpage( .*)?$");

	public DiagramData(final String diagramText) {
		textDiagram = diagramText;
		imageCount = 1;
		// We must count the number of "newpage" instructions
		final Matcher matcherNewpage = pattern.matcher(diagramText);
		while (matcherNewpage.find()) {
			imageCount++;
		}
	}

	public String getTextDiagram() {
		return textDiagram;
	}

	public int getImageCount() {
		return imageCount;
	}

	public void setOriginal(final IPath original) {
		this.original = original;
	}

	public IPath getOriginal() {
		return original;
	}

	public Map<String, Object> getMarkerAttributes() {
		return markerAttributes;
	}

	public void setMarkerAttributes(final Map<String, Object> markerAttributes) {
		this.markerAttributes = markerAttributes;
	}


	public ImageData getImage(final int imageNum, final Collection<LinkData> links) {
		// generate the image for textDiagram and imageNumber
		if (original != null) {
			// find the real file, which may be linked and thus is not located under the root itself
			final IResource member = ResourcesPlugin.getWorkspace().getRoot().getFile(original);
			final SFile dirPath = SFile.fromFile(member.getLocation().toFile().getAbsoluteFile().getParentFile());
			FileSystem.getInstance().setCurrentDir(dirPath);
		} else {
			FileSystem.getInstance().reset();
		}
		OptionFlags.getInstance().setQuiet(true);
		return getImage(textDiagram, imageNum, links);
	}

	public ImageData getImage() {
		return getImage(0, null);
	}

	private static void setGraphvizPath() {
		final IPreferenceStore prefStore = Activator.getDefault().getPreferenceStore();
		final String dotPath = prefStore.getString(PlantumlConstants.GRAPHVIZ_PATH);
		if (dotPath != null && (! "".equals(dotPath))) {
			System.setProperty("GRAPHVIZ_DOT", dotPath);
		}
	}

	private static FileFormatOption imageFileFormatOption = new FileFormatOption(FileFormat.PNG);

	private static ImageData getImage(final String textDiagram, final int imageNum, final Collection<LinkData> links) {
		setGraphvizPath();
		ImageData imageData = null;
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			// image generation
			final SourceStringReader reader = new SourceStringReader(textDiagram);
			final DiagramDescription desc = reader.outputImage(os, imageNum, imageFileFormatOption);
			if (links != null) {
				final String cMapData = reader.getCMapData(0, imageFileFormatOption);
				if (cMapData != null) {
					parseImageMapString(cMapData, links);
				}
			}
			os.flush();
			if (desc != null && StringUtils.isNotEmpty(desc.getDescription())) {
				try (InputStream is = new ByteArrayInputStream(os.toByteArray())) {
					imageData = new ImageData(is);
				}
			}
		} catch (final IOException e) {
			WorkbenchUtil.errorBox("Error during image generation.", e);
		}
		return imageData;
	}

	private static void parseImageMapString(final String cMapData, final Collection<LinkData> links) {
		final String[] areaElements = cMapData.split(Pattern.quote("<area "));
		for (final String areaElement : areaElements) {
			final String coords = getAttributeValue(areaElement, "coords");
			if (coords != null) {
				final LinkData link = new LinkData();
				link.href = getAttributeValue(areaElement, "href");
				link.title = getAttributeValue(areaElement, "title");
				link.altText = getAttributeValue(areaElement, "alt");
				final String[] ints = coords.split(",");
				if (ints.length == 4) {
					try {
						final int x1 = Integer.valueOf(ints[0]), y1 = Integer.valueOf(ints[1]), x2 = Integer.valueOf(ints[2]), y2 = Integer.valueOf(ints[3]);
						link.rect = new Rectangle(x1, y1, x2 - x1, y2 - y1);
						links.add(link);
					} catch (final NumberFormatException e) {
					}
				}
			}
		}
		if (links != null && links.size() > 0) {
			scaleLinks(links);
		}
	}

	// assumes image is scaled, but the margin is the same
	private static void scaleLinks(final Collection<LinkData> links) {
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
		for (final LinkData link : links) {
			if (link.rect.x < minX) {
				minX = link.rect.x;
			}
			if (link.rect.y < minY) {
				minY = link.rect.y;
			}
		}
		// empirically identified
		final int marginX = 7, marginY = 7;
		final int sx = minX / marginX, sy = minY / marginY;
		for (final LinkData link : links) {
			link.rect.x /= sx;
			link.rect.y /= sy;
			link.rect.width /= sx;
			link.rect.height /= sy;
		}
	}

	private static String getAttributeValue(final String element, final String attributeName) {
		final String prefix = attributeName + "=\"";
		int start = element.indexOf(prefix);
		if (start >= 0) {
			start += prefix.length();
			final String suffix = "\"";
			final int end = element.indexOf(suffix, start);
			if (end > start) {
				return element.substring(start, end);
			}
		}
		return null;
	}

	private static FileFormatOption svgFileFormatOption = new FileFormatOption(FileFormat.SVG);

	public String getSvg(final int imageNum) {
		setGraphvizPath();
		try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			// image generation
			final SourceStringReader reader = new SourceStringReader(textDiagram);
			final DiagramDescription desc = reader.outputImage(os, imageNum, svgFileFormatOption);
			os.flush();
			if (desc != null && StringUtils.isNotEmpty(desc.getDescription())) {
				return new String(os.toByteArray(), StandardCharsets.UTF_8);
			}
		} catch (final IOException e) {
			WorkbenchUtil.errorBox("Error during image generation.", e);
		}
		return null;
	}
}
