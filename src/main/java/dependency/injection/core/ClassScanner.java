package dependency.injection.core;

import dependency.injection.annotation.scanner.AnnotationScanner;
import org.clapper.util.classutil.*;

import java.util.ArrayList;
import java.util.List;

public class ClassScanner {
    public static List<Class> scan(){
        List<Class> classes = new ArrayList<>();
        org.clapper.util.classutil.ClassFinder classFinder = new org.clapper.util.classutil.ClassFinder();
        classFinder.addClassPath();

        ClassFilter filter =
                new AndClassFilter();

        List<ClassInfo> foundClasses = new ArrayList<>();
        classFinder.findClasses(foundClasses, filter);
        for (ClassInfo classInfo: foundClasses) {
            try {
                Class clazz = Thread.currentThread().getContextClassLoader().loadClass(classInfo.getClassName());
                if (AnnotationScanner.isResolvable(clazz)){
                    classes.add(clazz);
                }
            }catch (Throwable ignored){

            }
        }
        return classes;
    }
}
