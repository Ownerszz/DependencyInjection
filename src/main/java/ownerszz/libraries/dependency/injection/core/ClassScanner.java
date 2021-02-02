package ownerszz.libraries.dependency.injection.core;

import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ownerszz.libraries.dependency.injection.logging.ContainerLogger;


import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Copied from:
 *
 * Find classes in the classpath (reads JARs and classpath folders).
 * https://gist.github.com/pal
 * @author P&aring;l Brattberg, brattberg@gmail.com
 *
 */

public class ClassScanner {
    private static final Logger logger = LoggerFactory.getLogger(ClassScanner.class);
    public static List<Class> scan() throws Throwable{
        List<Class> classes = new ArrayList<>();
        List<Class> temp = null;
        try {
            temp = getAllKnownClasses();
        }catch (Throwable ignored){
            ContainerLogger.logDebug(logger,ignored.getMessage());
        }
        if (temp == null || temp.size() == 0){
            throw new RuntimeException("No classes found");
        }
        for (Class clazz: temp) {
            try {
                Boolean resolvable =AnnotationScanner.isResolvable(clazz,1);
                if (resolvable!= null && resolvable){
                    classes.add(clazz);
                }
            }catch (Throwable ignored){

            }
        }
        AnnotationScanner.tryResolveSlowClasses();
        AnnotationScanner.cleanup();
        ContainerLogger.logDebug(logger,"Successfully scanned {} classes", temp.size());
        return classes;
    }

    public static List<Class> getAllKnownClasses() {
        List<Class> classFiles = new ArrayList<Class>();
        List<File> classLocations = getClassLocationsForCurrentClasspath();
        for (File file : classLocations) {
            try {
                classFiles.addAll(getClassesFromPath(file));
            }catch (Throwable ignored){
                ContainerLogger.logDebug(logger,ignored.getMessage());
            }
        }
        return classFiles;
    }

    public static List<Class> getMatchingClasses(Class interfaceOrSuperclass) {
        List<Class> matchingClasses = new ArrayList<Class>();
        List<Class> classes = getAllKnownClasses();
        for (Class clazz : classes) {
            if (interfaceOrSuperclass.isAssignableFrom(clazz)) {
                matchingClasses.add(clazz);
            }
        }
        return matchingClasses;
    }

    public static List<Class> getMatchingClasses(String validPackagePrefix, Class interfaceOrSuperclass) {
        throw new IllegalStateException("Not yet implemented!");
    }

    public static List<Class> getMatchingClasses(String validPackagePrefix) {
        throw new IllegalStateException("Not yet implemented!");
    }

    private static Collection<? extends Class> getClassesFromPath(File path) {
        if (path.isDirectory()) {
            return getClassesFromDirectory(path);
        } else {
            return getClassesFromJarFile(path);
        }
    }

    private static String fromFileToClassName(final String fileName) {
        return fileName.substring(0, fileName.length() - 6).replaceAll("/|\\\\", "\\.");
    }

    private static List<Class> getClassesFromJarFile(File path) {
        List<Class> classes = new ArrayList<Class>();

        try {
            if (path.canRead()) {
                JarFile jar = new JarFile(path);
                Enumeration<JarEntry> en = jar.entries();
                while (en.hasMoreElements()) {
                    try {
                        JarEntry entry = en.nextElement();
                        if (entry.getName().endsWith("class")) {
                            String className = fromFileToClassName(entry.getName());
                            loadClass(classes, className);
                        }
                    }catch (Throwable e){

                    }

                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read classes from jar file: " + path, e);
        }

        return classes;
    }

    private static List<Class> getClassesFromDirectory(File path) {
        List<Class> classes = new ArrayList<Class>();
        // get jar files from top-level directory
        List<File> jarFiles = listFiles(path, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        }, false);
        for (File file : jarFiles) {
            classes.addAll(getClassesFromJarFile(file));
        }

        // get all class-files
        List<File> classFiles = listFiles(path, new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".class");
            }
        }, true);

        // List<URL> urlList = new ArrayList<URL>();
        // List<String> classNameList = new ArrayList<String>();
        int substringBeginIndex = path.getAbsolutePath().length() + 1;
        for (File classfile : classFiles) {
            try {
                String className = classfile.getAbsolutePath().substring(substringBeginIndex);
                className = fromFileToClassName(className);
                loadClass(classes, className);
            }catch (Throwable ignored){

            }
        }

        return classes;
    }

    private static List<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
        List<File> files = new ArrayList<File>();
        File[] entries = directory.listFiles();

        // Go over entries
        for (File entry : entries) {
            // If there is no filter or the filter accepts the
            // file / directory, add it to the list
            if (filter == null || filter.accept(directory, entry.getName())) {
                files.add(entry);
            }

            // If the file is a directory and the recurse flag
            // is set, recurse into the directory
            if (recurse && entry.isDirectory()) {
                files.addAll(listFiles(entry, filter, recurse));
            }
        }

        // Return collection of files
        return files;
    }

    public static List<File> getClassLocationsForCurrentClasspath() {
        List<File> urls = new ArrayList<File>();
        String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null) {
            for (String path : javaClassPath.split(File.pathSeparator)) {
                urls.add(new File(path));
            }
        }
        return urls;
    }

    // todo: this is only partial, probably
    public static URL normalize(URL url) throws MalformedURLException {
        String spec = url.getFile();

        // get url base - remove everything after ".jar!/??" , if exists
        final int i = spec.indexOf("!/");
        if (i != -1) {
            spec = spec.substring(0, spec.indexOf("!/"));
        }

        // uppercase windows drive
        url = new URL(url, spec);
        final String file = url.getFile();
        final int i1 = file.indexOf(':');
        if (i1 != -1) {
            String drive = file.substring(i1 - 1, 2).toUpperCase();
            url = new URL(url, file.substring(0, i1 - 1) + drive + file.substring(i1));
        }

        return url;
    }


    private static void loadClass(List<Class> classes, String className) {
        try {
            Class clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            classes.add(clazz);
        } catch (Throwable ignored) {
            ContainerLogger.logDebug(logger,"Failed to load class: " +  className);
        }
    }
}
