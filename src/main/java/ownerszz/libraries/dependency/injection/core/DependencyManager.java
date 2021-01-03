package ownerszz.libraries.dependency.injection.core;



import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * This is the DI container.
 * The DI container will keep track of the singletons and all running {@link Dependency Dependencies}.
 *
 * This class is also marked with {@link Dependency} and therefore is also injected if needed.
 */
@Dependency
public class DependencyManager {
    private static HashMap<Class, Supplier> classSupplierHashMap;
    private static HashMap<Class, InvocationHandler> proxyHandlers;
    private static HashMap<Class<Annotation>, Function<Object, Object>> annotationsToProxy;
    private static HashMap<Class, DependencyLifecycle> dependencyLifecycleHashMap;
    private static HashMap<Class, Object> singletons;
    private static HashMap<Class, Object> preProxyInstances;
    private static ThreadPoolExecutor threadPoolExecutor;
    private static boolean registratorsRunned = false;
    private static boolean runnableDependenciesRunned = false;

    /**
     * If you don't want to use class scanning us this method
     * @param objectInstantiators
     * @throws Exception
     */

    public static void use(HashMap<Class,Supplier> objectInstantiators) throws Exception {
        classSupplierHashMap = objectInstantiators;
        proxyHandlers = new HashMap<>();
        annotationsToProxy = new HashMap<>();
        dependencyLifecycleHashMap = new HashMap<>();
        singletons = new HashMap<>();
        preProxyInstances = new HashMap<>();
        forceRegisterClass(DependencyManager.class);
    }

    /**
     *
     * @param selfInit Do you want the container to initialise itself if not use {@link DependencyManager#use(HashMap)} and
     *                 {@link DependencyManager#invokeRegistrators()} and {@link DependencyManager#runRunnableDependencies()
     *
     * @throws Throwable
     */
    public static void run(boolean selfInit) throws Throwable {
        if (selfInit){
            classSupplierHashMap = new HashMap<>();
            proxyHandlers = new HashMap<>();
            annotationsToProxy = new HashMap<>();
            dependencyLifecycleHashMap = new HashMap<>();
            singletons = new HashMap<>();
            preProxyInstances = new HashMap<>();
            DependencyResolver.init(classSupplierHashMap);
            forceRegisterClass(DependencyManager.class);
            invokeRegistrators();
            runRunnableDependencies();
        }
    }

    /**
     * Invokes all classes marked with {@link DependencyRegistrator} the biggest constructor marked with {@link ResolveDependencies}
     * @throws Exception
     */
    public static void invokeRegistrators() throws Exception {
        if (!registratorsRunned){
            for (Class clazz: classSupplierHashMap.keySet()) {
                if (clazz.isAnnotationPresent(DependencyRegistrator.class)){
                    createInstance(clazz);
                }
            }
            registratorsRunned = true;
        }
    }

    /**
     * Run all runnable {@link Dependency dependencies}.
     * @see Dependency
     * @throws Exception
     */

    public static void runRunnableDependencies() throws Exception {
        if(!runnableDependenciesRunned){
            threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            for (Class<?> clazz: classSupplierHashMap.keySet().stream().filter(e-> !e.isAnnotation()).collect(Collectors.toList())) {
                Dependency dependency = AnnotationScanner.getAnnotation(clazz, Dependency.class);
                if(dependency != null && dependency.runnable()){
                    Runnable toRun = (Runnable) createInstance(clazz);
                    threadPoolExecutor.submit(toRun);
                }
            }
            runnableDependenciesRunned = true;
        }
    }

    /**
     * Creates an instance of the supplied class.
     * @param clazz The class to create
     * @param <T>
     * @return an instance of the class (can be proxied {@link DependencyManager#registerPoxyOnAnnotation(Class, Function)})
     * @throws Exception
     * @see Dependency
     * @see DependencyRegistrator
     */
    public static  <T> Object createInstance(Class<T> clazz) throws Exception{
        if (classSupplierHashMap.containsKey(clazz)){
            Dependency dependency = AnnotationScanner.getAnnotation(clazz, Dependency.class);
            Object instance = singletons.get(clazz);
            if (dependency.lifecycle() == DependencyLifecycle.SINGLETON && instance != null){

                return instance;
            }
            Supplier<T> supplier = classSupplierHashMap.get(clazz);
            if (mustBeProxied(clazz)){
                //Registered annotation?
                Optional<Annotation> ann = AnnotationScanner.getAnnotationsOfClass(clazz).stream().filter(e-> annotationsToProxy.containsKey(e.annotationType())).findFirst();
                if (ann.isPresent()){
                    Object preProxy = supplier.get();
                    preProxyInstances.putIfAbsent(clazz,preProxy );
                    instance = annotationsToProxy.get(ann.get().annotationType()).apply(preProxy);;
                }else if (supplier != null){
                    instance =  supplier.get();
                }else {
                    InvocationHandler handler = proxyHandlers.get(clazz);
                    if (handler == null){
                        throw new Exception("Class: " + clazz.getName() + " is an interface but is not registered to be proxied");
                    }
                    instance =  Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, handler);
                }
            }else if(supplier == null){
                throw new Exception("No supplier found for dependency: " + clazz.getName());
            }else {
                instance= supplier.get();
            }
            if (dependency.lifecycle() == DependencyLifecycle.SINGLETON){
                singletons.put(clazz, instance);
            }
            return instance;
        }else {
            throw new RuntimeException("Class: " + clazz.getName() + " is not registered. Make sure that it is annotated with @Dependency");
        }
    }



    private static  <T> boolean mustBeProxied(Class<T> clazz){
        return clazz.isInterface() || proxyHandlers.containsKey(clazz)
                || AnnotationScanner.getAnnotationsOfClass(clazz).stream().anyMatch(e-> annotationsToProxy.containsKey(e.annotationType()));
    }

    /**
     * Forcefully register a dependency. The dependency doesn't have to be marked with {@link Dependency}
     * Useful if you don't want to change huge amount of code or show that you are using a DI container
     * Also useful if the {@link ClassScanner} doesn't pick it up.
     *
     * This will also set it's life time to {@link DependencyLifecycle#TRANSIENT}
     * @param clazz the class
     * @param instantiator how to instantiate this class using a {@link Supplier}
     * @param <T>
     * @throws Exception
     * @see DependencyLifecycle
     */
    public  <T> void registerDependency(Class<T> clazz, Supplier<T> instantiator) throws Exception{
        registerDependency(clazz, instantiator, DependencyLifecycle.TRANSIENT);
    }
    /**
     * Forcefully register a dependency. The dependency doesn't have to be marked with {@link Dependency}
     * Useful if you don't want to change huge amount of code or show that you are using a DI container
     * Also useful if the {@link ClassScanner} doesn't pick it up.
     *
     * @param clazz the class
     * @param instantiator how to instantiate this class using a {@link Supplier}
     * @param <T>
     * @throws Exception
     * @see DependencyLifecycle
     */
    public  <T> void registerDependency(Class<T> clazz, Supplier<T> instantiator, DependencyLifecycle dependencyLifecycle) throws Exception{
        classSupplierHashMap.put(clazz, instantiator);
        dependencyLifecycleHashMap.put(clazz, dependencyLifecycle);
    }

    /**
     * Register a function to invoke after a instance marked with {@code annotationClass} is {@link DependencyManager#createInstance(Class) created}
     * This will also set it's life time to {@link DependencyLifecycle#TRANSIENT}
     * @param annotationClass The annotation to trigger the instantiator
     * @param instantiator How to instantiate this class using a {@link Function} with 2 type parameters
     *                     1: Our supplied instance (or class if class is an interface)
     *                     2: Your custom return object (proxy?)
     * @throws Exception
     */
    public void registerPoxyOnAnnotation(Class annotationClass,Function<Object, Object> instantiator) throws Exception{
        registerPoxyOnAnnotation(annotationClass, instantiator, DependencyLifecycle.TRANSIENT);
    }
    /**
     * Register a function to invoke after a instance marked with {@code annotationClass} is {@link DependencyManager#createInstance(Class) created}
     * @param annotationClass The annotation to trigger the instantiator
     * @param instantiator How to instantiate this class using a {@link Function} with 2 type parameters
     *                     1: Our supplied instance (or class if class is an interface)
     *                     2: Your custom return object (proxy?)
     * @throws Exception
     */
    public void registerPoxyOnAnnotation(Class annotationClass,Function<Object, Object> instantiator, DependencyLifecycle dependencyLifecycle) throws Exception{
        annotationsToProxy.put(annotationClass, instantiator);
        dependencyLifecycleHashMap.put(annotationClass, dependencyLifecycle);
    }

    /**
     *
     * @param clazz
     * @throws Exception
     * @see DependencyManager#registerDependency(Class, Supplier)
     */
    public static void forceRegisterClass(Class clazz) throws Exception {
        forceRegisterClass(clazz, DependencyLifecycle.TRANSIENT);
    }
    /**
     *
     * @param clazz
     * @throws Exception
     * @see DependencyManager#registerDependency(Class, Supplier, DependencyLifecycle)
     */
    public static void forceRegisterClass(Class clazz, DependencyLifecycle dependencyLifecycle) throws Exception {
        DependencyResolver.addClassToScannedClasses(clazz);
        Boolean result = AnnotationScanner.isResolvable(clazz,1);
        if (result == null){
            AnnotationScanner.tryResolveSlowClasses();
        }else if (!result) {
            throw new Exception("Class: " + clazz.getName() + "is not resolvable;");
        }
        DependencyResolver.verifyClassDependencies(classSupplierHashMap,clazz);
        dependencyLifecycleHashMap.put(clazz, dependencyLifecycle);
    }

    /**
     * Creates an instance without triggering any {@link DependencyManager#registerPoxyOnAnnotation(Class, Function) Function}
     * Useful to get an instance of a proxy
     * @param clazz The class to instantiate
     * @return instance
     */
    public static Object createSimpleInstance(Class clazz){
        if (preProxyInstances.get(clazz) != null){
            return preProxyInstances.get(clazz);
        }else {
            return classSupplierHashMap.get(clazz).get();
        }
    }

    /**
     * Clears the context. Shuts down all running {@link Dependency dependencies}
     */
    public static void refreshContext(){
        registratorsRunned = false;
        runnableDependenciesRunned=false;
        if (threadPoolExecutor != null){
            threadPoolExecutor.shutdownNow();
        }
        classSupplierHashMap.clear();
        proxyHandlers.clear();
        annotationsToProxy.clear();
    }

}
