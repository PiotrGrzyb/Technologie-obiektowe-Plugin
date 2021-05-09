package toplantuml2.parts;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GetClassesDirectory {
    public static void main(String[] argv) throws Exception {
        File directory = new File("C:/Users/Piotr/Desktop/Technologie-obiektowe-Plugin-Python/testclass/src/testclass");
        String packageName = "toplantuml2.parts";
        System.out.println(findClasses(directory, packageName));
    }
    @SuppressWarnings("rawtypes")
    public static List<String> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<String> classes = new ArrayList<String>();
        String tmp;
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if(file.getName().endsWith(".java")){
            System.out.println(file.getName().substring(0, file.getName().length() - 5));}
				/*classes.add(tmp);*/
        }
        return classes;
    }  
}
