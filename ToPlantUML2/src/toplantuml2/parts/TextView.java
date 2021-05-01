package toplantuml2.parts;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TimeZone;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Text;
import org.reflections.Reflections;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.events.MouseEvent;

public class TextView{
	private Label myLabelInView;
	private Text text;
	
	
	public static final java.util.List<Class<?>> getClassesInPackage(String packageName) {
        String path = packageName.replaceAll("\\.", File.separator);
        java.util.List<Class<?>> classes = new ArrayList<>();
        String[] classPathEntries = System.getProperty("java.class.path").split(
                System.getProperty("path.separator")
        );

        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                File jar = new File(classpathEntry);
                try {
                    JarInputStream is = new JarInputStream(new FileInputStream(jar));
                    JarEntry entry;
                    while((entry = is.getNextJarEntry()) != null) {
                        name = entry.getName();
                        if (name.endsWith(".class")) {
                            if (name.contains(path) && name.endsWith(".class")) {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[\\|/]", ".");
                                classes.add(Class.forName(classPath));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            } else {
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);
                    for (File file : base.listFiles()) {
                        name = file.getName();
                        if (name.endsWith(".class")) {
                            name = name.substring(0, name.length() - 6);
                            classes.add(Class.forName(packageName + "." + name));
                        }
                    }
                } catch (Exception ex) {
                    // Kiedys mo¿e siê przydaæ obs³uga wyj¹tków
                }
            }
        }

        return classes;
    }
	
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
		text.addListener(SWT.Verify, new Listener()
		{
		    @Override
		    public void handleEvent(Event e)
		    {
		        Text source = (Text) e.widget;
		        final String oldS = source.getText();
		        final String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);
		        System.out.println(oldS + " -> " + newS);
		    }
		});
		
		Button btnNewButton = new Button(parent, SWT.NONE);
		btnNewButton.setLayoutData(new RowData(172, 29));
		btnNewButton.setText("Generuj plik tekstowy");
		
		Label lblNewLabel_1 = new Label(parent, SWT.NONE);
		lblNewLabel_1.setLayoutData(new RowData(665, 34));
		lblNewLabel_1.setText("Klasy wewn¹trz pakietu");
		
		List list = new List(parent, SWT.BORDER);
		list.setLayoutData(new RowData(297, 253));
		//Reflections reflections = new Reflections("toplantuml2.parts");
		java.util.List<Class<?>> lista = getClassesInPackage("toplantuml2.parts");
        lista.forEach(System.out::println);

		String[] nowy = (String[]) lista.toArray();
		for(String print : nowy)
            list.add(print);
		
		 list.addListener(SWT.Selection, new Listener() {
	            @Override
	            public void handleEvent(Event arg0) {
	                if(list.getSelectionCount() > 0)
	                    System.out.println(Arrays.toString(list.getSelection()));
	            }
	        });
	}

	@Focus
	public void setFocus() {

	}
}
	