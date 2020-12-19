package dependency.injection.core;

import java.lang.reflect.Constructor;
import java.util.*;

public class DependencyResolver {

    public static void init(HashMap<Class, Constructor> classConstructorHashMap) throws Exception {
        HashMap<Class,Boolean> scannedClasses = new HashMap<>();
        for (Class clazz :ClassScanner.scan()) {
            scannedClasses.putIfAbsent(clazz, false);
        }
        for (Class clazz: scannedClasses.keySet()) {
            verifyClassDependencies(classConstructorHashMap,scannedClasses, clazz);
        }
    }

    public static void verifyClassDependencies(HashMap<Class,Constructor> ctors,HashMap<Class, Boolean> classes,Class clazz) {
        List<Constructor> constructors = Arrays.asList(clazz.getDeclaredConstructors());
        Constructor constructor;

        if (constructors.size() != 0){
            //Get constructor
            constructor = constructors.stream()
                    .filter(e-> e.isAnnotationPresent(ResolveDependencies.class))
                    .max(Comparator.comparing(Constructor::getParameterCount))
                    .orElseGet(() ->constructors.stream()
                            .filter(e-> e.getParameterCount() == 0)
                            .findFirst()
                            .orElseThrow(() ->
                                    new RuntimeException("No constructor marked with @ResolveDependencies found for class: " + clazz.getName())));

            for (Class parameterType:constructor.getParameterTypes()) {
                Boolean resolved = classes.get(parameterType);
                if (resolved == null){
                    throw new RuntimeException("Unknown dependency in constructor of type: " + parameterType.getSimpleName());
                }
                if (!resolved){
                    if(Arrays.stream(parameterType.getDeclaredFields()).anyMatch(e-> e.getType().equals(clazz))){
                        throw new RuntimeException("Circular dependency detected between: " + clazz.getName() + " and " + parameterType.getName());
                    }
                    if (parameterType.isAnnotationPresent(Dependency.class)){
                        verifyClassDependencies(ctors,classes, parameterType);
                    }else {
                        throw new RuntimeException("Class" + parameterType.getName() + "not marked with @Dependency but is found in a constructor marked with @ResolveDependencies");
                    }
                }
            }
        }else {
            constructor = null;
        }

        classes.put(clazz, true);
        ctors.put(clazz, constructor);
    }
}
