package dependency.injection.util;

import dependency.injection.core.ClassScanner;
import dependency.injection.core.DependencyResolver;

import java.lang.reflect.Constructor;
import java.util.HashMap;

public class UsableClassesGenerator {
    public static HashMap<Class, Boolean> generateClasses() throws Exception{
        HashMap<Class, Boolean> temp = new HashMap<>();
        for (Class clazz: ClassScanner.scan()) {
            if (!clazz.getSimpleName().contains("Non")){
                temp.putIfAbsent(clazz, false);
            }
        }
        return temp;
    }
    public static HashMap<Class, Constructor>  generateConstructors() throws Exception{
        HashMap<Class, Constructor> constructors = new HashMap<>();
        HashMap<Class, Boolean> generatedClasses = generateClasses();
        for (Class clazz:generatedClasses.keySet()) {
            DependencyResolver.verifyClassDependencies(constructors,generatedClasses,clazz);
        }
        return constructors;
    }
}
