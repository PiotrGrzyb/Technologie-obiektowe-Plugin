package testclass;
import java.io.File;

import java.net.URL;
import java.util.ArrayList;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Main {
    public static void main(String[] argv) throws Exception {
        String string1 = "C:/Users/Piotr/Desktop/Technologie-obiektowe-Plugin-Python/ToPlantUML2/src";
        String string2 = "C:/Users/Piotr/Desktop/Technologie-obiektowe-Plugin-Python/testclass/src/testclass";
        File directory = new File("C:/Users/Piotr/Desktop/Technologie-obiektowe-Plugin-Python/testclass/src/testclass");
        String packageName = "toplantuml2.parts";
        System.out.println(findClasses(directory, packageName));
    }//from  www. j a v a2  s  . c  o  m
    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    public static List<Class> findClasses(File directory, String packageName)
            throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if(file.getName().endsWith(".java")){
            System.out.println(file.getName().substring(0, file.getName().length() - 5));}

            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file,
                        packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName
                        + '.'
                        + file.getName().substring(0,
                        file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static List<Class> findClasses(String path, String packageName) {
        List<Class> classes = new ArrayList<Class>();
        try {
            if (path.startsWith("file:")) {

                URL jar = new URL(path);
                ZipInputStream zip = new ZipInputStream(jar.openStream());
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    if (entry.getName().endsWith(".class")) {
                        String className = entry.getName()
                                .replaceAll("[$].*", "")
                                .replaceAll("[.]class", "")
                                .replace('/', '.');
                        if (className.startsWith(packageName)) {
                            classes.add(Class.forName(className));
                        }
                    }
                }
            }
            File dir = new File(path);
            if (!dir.exists()) {
                return classes;
            }
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    assert !file.getName().contains(".");
                    classes.addAll(findClasses(file.getAbsolutePath(),
                            packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = packageName
                            + '.'
                            + file.getName().substring(0,
                            file.getName().length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return classes;
    }
}
