package dependency.injection.core;


import dependency.injection.annotation.scanner.AnnotationScanner;
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Supplier;

import static dependency.injection.core.DependencyManager.createInstance;
import static dependency.injection.core.DependencyManager.createSimpleInstance;

/**
 * This class is responsible for verifying all {@link Dependency dependencies} in a constructor marked with {@link ResolveDependencies}
 * This class will also setup basic {@link Supplier instantiators} for the {@link Dependency dependencies}.
 */
public class DependencyResolver {
    private static HashMap<Class, Boolean> scannedClasses;

    /**
     * Reads all classes using {@link ClassScanner#scan()} and verifies the classes
     * @param classConstructorHashMap
     * @throws Throwable
     */
    public static void init(HashMap<Class, Supplier> classConstructorHashMap) throws Throwable {
        scannedClasses = new HashMap<>();
        for (Class clazz :ClassScanner.scan()) {
            scannedClasses.putIfAbsent(clazz, false);
        }
        for (Class clazz: scannedClasses.keySet()) {
            verifyClassDependencies(classConstructorHashMap,scannedClasses, clazz);
        }
    }

    public static void verifyClassDependencies(HashMap<Class,Supplier> ctors,Class clazz){
        verifyClassDependencies(ctors,scannedClasses,clazz);
    }

    /**
     * Verifies that the current class can be made using the registered {@link Dependency dependencies}
     * And will also setup a basic {@link Supplier instantiator} for the {@link Dependency dependency}.
     * @param ctors hashmap to add the {@link Supplier instantiator}
     * @param classes known classes
     * @param clazz current class to verify
     * @param <T> type parameter
     */
    public static<T> void verifyClassDependencies(HashMap<Class,Supplier> ctors,HashMap<Class, Boolean> classes,Class<T> clazz) {
        if (scannedClasses == null){
            scannedClasses = classes;
        }
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
                    if (AnnotationScanner.isAnnotationPresent(parameterType,Dependency.class)){
                        verifyClassDependencies(ctors,classes, parameterType);
                    }else {
                        throw new RuntimeException("Class: " + parameterType.getName() + " not marked with @Dependency but is found in a constructor marked with @ResolveDependencies");
                    }
                }
            }
        }else {
            constructor = null;
        }

        classes.put(clazz, true);
        ctors.put(clazz, ()-> {
            try {
                if (constructor == null){
                    if (clazz.isInterface()){
                        Object instance = CustomizedProxyGenerator.createInterfaceInstance(clazz);
                        return instance;
                        //return clazz.cast(ObjenesisHelper.newInstance(instance));
                    }else {
                        return ObjenesisHelper.newInstance(clazz);
                    }
                }
                Object[] contructorArgs = new Object[constructor.getParameterCount()];
                Class[] parameterTypes=constructor.getParameterTypes();
                for (int i = 0; i < constructor.getParameterCount(); i++) {
                    try {
                        contructorArgs[i] = parameterTypes[i].cast(createInstance(parameterTypes[i]));
                    }catch (Throwable e){
                        contructorArgs[i] = createSimpleInstance(parameterTypes[i]);
                    }
                }
                return constructor.newInstance(contructorArgs);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        });

    }

    /**
     * Adds a class to the scanned classes. Only useful if you run {@link DependencyResolver#verifyClassDependencies(HashMap, Class)} after this is called
     * @param clazz the class to add
     */
    protected static void addClassToScannedClasses(Class clazz){
        scannedClasses.putIfAbsent(clazz, false);
    }
}
