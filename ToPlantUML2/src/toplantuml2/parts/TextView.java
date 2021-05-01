package toplantuml2.parts;

import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Text;
import org.reflections.Reflections;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.MouseEvent;

public class TextView {
	private Label myLabelInView;
	private Text text;
	
	Reflections reflections = new Reflections("toplantuml2.parts");
	 
	@PostConstruct
	public void createPartControl(Composite parent) {
		parent.addMouseWheelListener(new MouseWheelListener() {
			public void mouseScrolled(MouseEvent e) {
			}
		});
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		parent.setLayout(layout);
		
		String[] ids = TimeZone.getAvailableIDs();
		
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new RowData(621, 40));
		lblNewLabel.setText("Podaj pakiet, który chcesz przekonwertowaæ do PlantUML");
		
		text = new Text(parent, SWT.BORDER);
		text.setLayoutData(new RowData(377, 25));
		
		Button btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.setLayoutData(new RowData(172, 29));
		btnNewButton.setText("Generuj plik tekstowy");
		
		Label lblNewLabel_1 = new Label(parent, SWT.NONE);
		lblNewLabel_1.setLayoutData(new RowData(665, 34));
		lblNewLabel_1.setText("Klasy wewn¹trz pakietu");
		
		List list = new List(parent, SWT.BORDER);
		list.setLayoutData(new RowData(297, 253));
		
	}

	@Focus
	public void setFocus() {

	}
}
	