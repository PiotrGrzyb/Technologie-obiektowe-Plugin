package toplantuml.handlers;

import java.util.Set;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.reflections.Reflections;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class SampleHandler extends AbstractHandler {
	 protected Shell shell;
	 private Text text;
	 
	 Reflections reflections = new Reflections("toplantuml.handlers");

	 Set<Class<? extends Object>> allClasses = 
	     reflections.getSubTypesOf(Object.class);
	 String message = "To jest nowa informacja";
	 

	@Override     
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		MessageDialog.openInformation(
				window.getShell(),
				"ToPlantUML",
				message);
		return null;
	}
}
