package net.sourceforge.plantuml.eclipse.views.actions;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.StringUtils;
import net.sourceforge.plantuml.eclipse.utils.PlantumlConstants;
import net.sourceforge.plantuml.eclipse.utils.PlantumlUtil;
import net.sourceforge.plantuml.eclipse.utils.WorkbenchUtil;
import net.sourceforge.plantuml.util.DiagramImageData;

/**Manage the Export of the diagram.
 *
 * @author durif_c
 *
 */
public class ExportAction extends DiagramImageAction<Shell> {

	public ExportAction(final Supplier<DiagramImageData> diagramImageDataSupplier, final Shell shell) {
		super(diagramImageDataSupplier, shell);
		setText(PlantumlConstants.EXPORT_MENU);
	}

	@Override
	public void run() {
		final FileDialog fDialog = new FileDialog(getContext(), SWT.SAVE);
		fDialog.setFilterExtensions(new String[] { "*.png", "*.svg", "*.jpg", "*.gif" });
		fDialog.open();

		// get return from user.
		final String fileName = fDialog.getFileName();
		if (StringUtils.isNotEmpty(fileName)) {
			final String filePathName = fDialog.getFilterPath() + System.getProperty("file.separator") + fileName;
			final String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
			if ("svg".equals(ext)) {
				createImageFileSvg(filePathName, getDiagramImageData().getDiagramData().getTextDiagram());
			} else {
				createImageFile(filePathName, ext);
			}
		}
	}

	private void createImageFile(final String filePathName, final String format) {
		final Function<Integer, String> fileNameProvider = PlantumlUtil.getImageFileNameProvider(filePathName);
		if (fileNameProvider != null) {
			final int imageCount = getDiagramImageData().getDiagramData().getImageCount();
			for (int i = 0; i < imageCount; i++) {
				createImageFile(fileNameProvider.apply(i), format, getDiagramImageData().getDiagramData().getImage(i, null));
			}
		} else {
			createImageFile(filePathName, format, getImage());
		}
	}

	private void createImageFile(final String filePathName, final String format, final ImageData imageData) {
		final ImageLoader loader = new ImageLoader();
		loader.data = new ImageData[]{imageData};
		try {
			final int swtFormat = SWT.class.getField("IMAGE_" + format.toUpperCase()).getInt(null);
			loader.save(filePathName, swtFormat);
		} catch (final Exception e) {
			WorkbenchUtil.errorBox("Error during file generation for export.");
		}
	}

	private void createImageFileSvg(final String filePathName, final String textDiagram) {
		final Function<Integer, String> fileNameProvider = PlantumlUtil.getImageFileNameProvider(filePathName);
		if (fileNameProvider != null) {
			final int imageCount = getDiagramImageData().getDiagramData().getImageCount();
			for (int i = 0; i < imageCount; i++) {
				createImageFileSvg(fileNameProvider.apply(i), textDiagram, i);
			}
		} else {
			createImageFileSvg(filePathName, textDiagram, getImageNum());
		}
	}

	private void createImageFileSvg(final String filePathName, final String textDiagram, final int imageNum) {
		try (FileOutputStream fos = new FileOutputStream(filePathName)) {
			final SourceStringReader reader = new SourceStringReader(textDiagram);
			reader.outputImage(fos, imageNum, new FileFormatOption(FileFormat.SVG));
		} catch (final IOException e) {
			WorkbenchUtil.errorBox("Error during file generation for export.");
		}
	}
}
