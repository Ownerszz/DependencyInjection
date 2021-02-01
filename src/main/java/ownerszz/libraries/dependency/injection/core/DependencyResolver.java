package ownerszz.libraries.dependency.injection.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;
import org.objenesis.ObjenesisHelper;
import ownerszz.libraries.dependency.injection.core.arguments.ArgumentReader;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

/**
 * This class is responsible for verifying all {@link Dependency dependencies} in a constructor marked with {@link ResolveDependencies}
 * This class will also setup basic {@link Supplier instantiators} for the {@link Dependency dependencies}.
 */
public class DependencyResolver {
    private static HashMap<Class, Boolean> scannedClasses = new HashMap<>();;
    private static final Logger logger = LoggerFactory.getLogger(DependencyResolver.class);
    /**
     * Reads all classes using {@link ClassScanner#scan()} and verifies the classes
     * @throws Throwable
     */
    public static void init() throws Throwable {
            logger.info("Start class scanning");
            for (Class clazz :ClassScanner.scan()) {
                scannedClasses.putIfAbsent(clazz, false);
            }
            for (Class clazz: scannedClasses.keySet()) {
                verifyClassDependencies(DependencyInstanstatior.getDependencySuppliers(),scannedClasses, clazz);
            }
            logger.info("Finished class scanning");
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
        logger.debug("Creating a supplier for class: " + clazz.getName());
        if (scannedClasses == null){
            scannedClasses = classes;
        }
        findBestConstructor(clazz);
        classes.put(clazz, true);
        ctors.put(clazz,createSupplier(clazz));



    }

    private static Constructor findBestConstructor(Class clazz){
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
                Boolean resolved = scannedClasses.get(parameterType);
                if (resolved == null && !ClassUtil.isCollection(parameterType)){
                    throw new RuntimeException("Unknown dependency in constructor of type: " + parameterType.getSimpleName());
                }
                if (resolved != null && !resolved){
                    if(Arrays.stream(parameterType.getDeclaredFields()).anyMatch(e-> e.getType().equals(clazz))){
                        throw new RuntimeException("Circular dependency detected between: " + clazz.getName() + " and " + parameterType.getName());
                    }
                    if (AnnotationScanner.isAnnotationPresent(parameterType,Dependency.class)){
                        verifyClassDependencies(DependencyInstanstatior.getDependencySuppliers(),scannedClasses, parameterType);
                    }else {
                        throw new RuntimeException("Class: " + parameterType.getName() + " not marked with @Dependency but is found in a constructor marked with @ResolveDependencies");
                    }
                }
            }
        }else {
            constructor = null;
        }
        return constructor;
    }

    public static Supplier createSupplier(Class clazz){
        return ()-> {
            try {
                Constructor constructor = findBestConstructor(clazz);
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
                        if (ClassUtil.isCollection(parameterTypes[i])){
                            Collection collection = new ArrayList();
                            Class type = (Class) ((ParameterizedType) constructor.getParameters()[i].getParameterizedType()).getActualTypeArguments()[0];
                            for (Class implType: ClassScanner.getMatchingClasses(type)) {
                                if (implType != type){
                                    try {
                                        collection.add(DependencyManager.createInstance(implType));
                                    }catch (Throwable e){
                                        collection.add(DependencyManager.createSimpleInstance(implType));
                                    }
                                }
                            }
                            contructorArgs[i] = collection;
                        }else {
                            contructorArgs[i] = parameterTypes[i].cast(DependencyManager.createInstance(parameterTypes[i]));
                        }
                    }catch (Throwable e){
                        contructorArgs[i] = DependencyManager.createSimpleInstance(parameterTypes[i]);
                    }
                }
                return constructor.newInstance(contructorArgs);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        };
    }

    /**
     * Adds a class to the scanned classes. Only useful if you run {@link DependencyResolver#verifyClassDependencies(HashMap, Class)} after this is called
     * @param clazz the class to add
     */
    protected static void addClassToScannedClasses(Class clazz){
        scannedClasses.putIfAbsent(clazz, false);
    }
}
