package dependency.injection.core;

import dependency.injection.annotation.scanner.AnnotationScanner;
import org.clapper.util.classutil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClassScanner {
    public static List<Class> scan(){
        List<Class> classes = new ArrayList<>();
        org.clapper.util.classutil.ClassFinder classFinder = new org.clapper.util.classutil.ClassFinder();
        classFinder.addClassPath();

        ClassFilter filter =
                new AndClassFilter(/*new NotClassFilter(new AbstractClassFilter())*/);

        List<ClassInfo> foundClasses = new ArrayList<>();
        classFinder.findClasses(foundClasses, filter);
        //List<ClassInfo> ourClasses = foundClasses.stream().filter(e-> e.getClassName().contains("dependency.injection")).collect(Collectors.toList());
        for (ClassInfo classInfo: foundClasses) {
            try {

                Class clazz = Thread.currentThread().getContextClassLoader().loadClass(classInfo.getClassName());

                Boolean resolvable =AnnotationScanner.isResolvable(clazz,1);
                if (resolvable!= null && resolvable){
                    classes.add(clazz);
                }
            }catch (Throwable ignored){

            }
        }
        AnnotationScanner.tryResolveSlowClasses();
        return classes;
    }
}
