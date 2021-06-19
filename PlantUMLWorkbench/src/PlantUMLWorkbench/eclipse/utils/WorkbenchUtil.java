package PlantUMLWorkbench.eclipse.utils;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

//Class to manage some basic functionalities on the Workbench (message box, get system objects, ...)

public class WorkbenchUtil {


	// get the current active window

    public static IWorkbenchWindow getCurrentActiveWindows() {
        return getWorkbench().getActiveWorkbenchWindow();
    }

    // get the workbench instance

    public static IWorkbench getWorkbench() {
        return PlatformUI.getWorkbench();
    }

    // get the shell of the current active window

    public static Shell getShell() {
        return getCurrentActiveWindows().getShell();
    }

    // open an error box.

    public static void errorBox(String message) {
        errorBox("ERROR", message, null);
    }

    public static void errorBox(String title, String message, Throwable e) {
        MessageDialog.openError(getShell(), title, message + "\n"
                + (e != null ? e.getMessage() : ""));

    }
    
    public static void errorBox(String message, Throwable e) {
        errorBox("ERROR", message, e);
    }
}
