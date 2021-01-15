package ownerszz.libraries.dependency.injection.core;



import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
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


    private static HashMap<Class, DependencyLifecycle> dependencyLifecycleHashMap;


    private static ThreadPoolExecutor threadPoolExecutor;
    private static boolean registratorsRunned = false;
    private static boolean runnableDependenciesRunned = false;
    private static SingletonDependencyManager singletonDependencyManager;
    private static ScopedDependencyManager scopedDependencyManager;

    /**
     * If you don't want to use class scanning us this method
     * @param objectInstantiators
     * @throws Exception
     */

    public static void use(HashMap<Class,Supplier> objectInstantiators) throws Exception {
        DependencyInstanstatior.use(objectInstantiators);
        dependencyLifecycleHashMap = new HashMap<>();
        scopedDependencyManager = new ScopedDependencyManager();
        singletonDependencyManager = new SingletonDependencyManager();
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
            dependencyLifecycleHashMap = new HashMap<>();
            singletonDependencyManager = new SingletonDependencyManager();
            scopedDependencyManager = new ScopedDependencyManager();
            DependencyResolver.init();
            forceRegisterClass(DependencyManager.class);
            invokeRegistrators();
            runRunnableDependencies();
        }
    }

    /**
     * Invokes all classes marked with {@link DependencyRegistrator} the biggest constructor marked with {@link ResolveDependencies}
     * @throws Exception
     */
    public static void invokeRegistrators() throws Throwable {
        if (!registratorsRunned){
            for (Class clazz: DependencyInstanstatior.getDependencySuppliers().keySet()) {
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
    public static void runRunnableDependencies() throws Throwable {
        if(!runnableDependenciesRunned){
            threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            for (Class<?> clazz: DependencyInstanstatior.getDependencySuppliers().keySet().stream().filter(e-> !e.isAnnotation()).collect(Collectors.toList())) {
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
    public static  <T> T createInstance(Class<T> clazz) throws Throwable {
        Dependency dependency = AnnotationScanner.getAnnotation(clazz, Dependency.class);
        if (dependency != null && dependency.lifecycle() == DependencyLifecycle.SINGLETON){
           return singletonDependencyManager.createOrGetInstance(clazz);
        }else {
            return DependencyInstanstatior.createInstance(clazz);
        }
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
        DependencyInstanstatior.registerDependency(clazz,instantiator);
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
        DependencyInstanstatior.registerProxyOnAnnotation(annotationClass, instantiator);
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
        DependencyResolver.verifyClassDependencies(DependencyInstanstatior.getDependencySuppliers(),clazz);
        dependencyLifecycleHashMap.put(clazz, dependencyLifecycle);
    }

    /**
     * Creates an instance without triggering any {@link DependencyManager#registerPoxyOnAnnotation(Class, Function) Function}
     * Useful to get an instance of a proxy
     * @param clazz The class to instantiate
     * @return instance
     */
    public static <T> T createSimpleInstance(Class<T> clazz){
        return DependencyInstanstatior.createSimpleInstance(clazz);
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
        DependencyInstanstatior.clear();
    }

    /**
     * Creates a new key to use for adding instances within the scope. Scopes are never automatically created.
     *
     * If you no longer need the scope just call {@link DependencyManager#destroyScope(String)}
     *
     * @return The key of the scope
     */
    public String createScope(){
        return scopedDependencyManager.createScope();
    }

    /**
     * Will remove the scope and all scoped instances if they are no longer used within the application.
     * @param key The key to destroy
     */
    public void destroyScope(String key){
        scopedDependencyManager.destroyScope(key);
    }

    /**
     * Created or get a scoped instance associated with the specified key
     *
     * Limitations: This will never fetch {@link DependencyManager#createSimpleInstance(Class) simple instances}
     * and will therefore always trigger the proxy functions.
     *
      * @param key The scope key
     * @param clazz The class to instantiate or get
     * @return the scoped instance
     * @throws Exception when key not found
     */
    public <T> T createOrGetScopedInstance(String key,Class<T> clazz) throws Throwable {
        return scopedDependencyManager.createOrGetScopedInstance(key, clazz);
    }

    public static DependencyManager getInstance() throws Throwable {
        return createInstance(DependencyManager.class);
    }
}
