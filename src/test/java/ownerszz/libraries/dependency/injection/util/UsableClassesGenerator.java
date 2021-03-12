package ownerszz.libraries.dependency.injection.util;

import ownerszz.libraries.dependency.injection.core.ClassScanner;
import ownerszz.libraries.dependency.injection.core.DependencyResolver;

import java.util.HashMap;
import java.util.function.Supplier;

public class UsableClassesGenerator {
    public static HashMap<Class, Boolean> generateClasses() throws Throwable {
        HashMap<Class, Boolean> temp = new HashMap<>();
        for (Class clazz: ClassScanner.scan()) {
            String clazzName = clazz.getSimpleName().split("\\$")[0];
            if (clazzName.contains("Non") || clazzName.endsWith("Test") || clazzName.isEmpty()){
            }else {
                temp.putIfAbsent(clazz, false);
            }
        }
        return temp;
    }
    public static HashMap<Class, Supplier>  generateConstructors() throws Throwable {
        HashMap<Class, Supplier> constructors = new HashMap<>();
        HashMap<Class, Boolean> generatedClasses = generateClasses();
        for (Class clazz:generatedClasses.keySet()) {
            DependencyResolver.verifyClassDependencies(constructors,generatedClasses,clazz);
        }
        return constructors;
    }
}
