package dependency.injection.core;


import be.kdg.distrib.skeletonFactory.SkeletonFactory;
import be.kdg.distrib.stubFactory.StubFactory;
import dependency.injection.annotation.scanner.AnnotationScanner;

import org.objenesis.ObjenesisHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Dependency
public class DependencyManager {
    private static HashMap<Class, Supplier> classSupplierHashMap;
    private static HashMap<Class, InvocationHandler> proxyHandlers;
    private static HashMap<Class<Annotation>, Function<Object, Object>> annotationsToProxy;
    private static HashMap<Class, DependencyLifecycle> dependencyLifecycleHashMap;
    private static HashMap<Class, Object> singletons;
    private static ThreadPoolExecutor threadPoolExecutor;
    private static boolean registratorsRunned = false;
    private static boolean runnableDependenciesRunned = false;



    public static void use(HashMap<Class,Supplier> objectInstantiators) throws Exception {
        classSupplierHashMap = objectInstantiators;
        proxyHandlers = new HashMap<>();
        annotationsToProxy = new HashMap<>();
        dependencyLifecycleHashMap = new HashMap<>();
        singletons = new HashMap<>();
        forceRegisterClass(DependencyManager.class);
    }
    //Always static
    public static void run(boolean selfInit) throws Exception {
        if (selfInit){
            classSupplierHashMap = new HashMap<>();
            proxyHandlers = new HashMap<>();
            annotationsToProxy = new HashMap<>();
            DependencyResolver.init(classSupplierHashMap);
            forceRegisterClass(DependencyManager.class);
            invokeRegistrators();
            runRunnableDependencies();
        }
    }

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


    protected static void runRunnableDependencies() throws Exception {
        if(!runnableDependenciesRunned){
            threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            for (Class<?> clazz: classSupplierHashMap.keySet().stream().filter(e-> !e.isAnnotation()).collect(Collectors.toList())) {
                Dependency dependency = AnnotationScanner.getAnnotation(clazz, Dependency.class);
                if(dependency != null && dependency.runnable()){
                    Runnable toRun = (Runnable) createInstance(clazz);
                    threadPoolExecutor.submit(toRun);
                }
            }
        }
    }

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
                    instance = annotationsToProxy.get(ann.get().annotationType()).apply(supplier.get());;
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
        List<Annotation> annotations = AnnotationScanner.getAnnotationsOfClass(clazz);
        return clazz.isInterface() || proxyHandlers.containsKey(clazz)
                || AnnotationScanner.getAnnotationsOfClass(clazz).stream().anyMatch(e-> annotationsToProxy.containsKey(e.annotationType()));
    }


    public  <T> void registerDependency(Class<T> clazz, Supplier<T> instantiator) throws Exception{
        registerDependency(clazz, instantiator, DependencyLifecycle.TRANSIENT);
    }
    public  <T> void registerDependency(Class<T> clazz, Supplier<T> instantiator, DependencyLifecycle dependencyLifecycle) throws Exception{
        classSupplierHashMap.put(clazz, instantiator);
        dependencyLifecycleHashMap.put(clazz, dependencyLifecycle);
    }


    public void registerPoxyOnAnnotation(Class annotationClass,Function<Object, Object> instantiator) throws Exception{
        registerPoxyOnAnnotation(annotationClass, instantiator, DependencyLifecycle.TRANSIENT);
    }
    public void registerPoxyOnAnnotation(Class annotationClass,Function<Object, Object> instantiator, DependencyLifecycle dependencyLifecycle) throws Exception{
        annotationsToProxy.put(annotationClass, instantiator);
        dependencyLifecycleHashMap.put(annotationClass, dependencyLifecycle);
    }


    public static void forceRegisterClass(Class clazz) throws Exception {
        forceRegisterClass(clazz, DependencyLifecycle.TRANSIENT);
    }
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
