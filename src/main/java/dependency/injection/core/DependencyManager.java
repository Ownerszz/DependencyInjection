package dependency.injection.core;


import be.kdg.distrib.skeletonFactory.SkeletonFactory;
import be.kdg.distrib.stubFactory.StubFactory;
import dependency.injection.annotation.scanner.AnnotationScanner;

import org.objenesis.ObjenesisHelper;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
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
    private static ThreadPoolExecutor threadPoolExecutor;
    private static boolean registratorsRunned = false;
    private static boolean runnableDependenciesRunned = false;
    public static void init() throws Exception {
        classSupplierHashMap = new HashMap<>();
        proxyHandlers = new HashMap<>();
        annotationsToProxy = new HashMap<>();
        DependencyResolver.init(classSupplierHashMap);
        forceRegisterClass(DependencyManager.class);

    }
    public static void use(HashMap<Class,Supplier> objectInstantiators){
        classSupplierHashMap = objectInstantiators;
        forceRegisterClass(DependencyManager.class);
    }
    public static void run(boolean selfInit) throws Exception {
        if (selfInit){
            init();
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

    private static void runRunnableDependencies() throws Exception {
        if(!runnableDependenciesRunned){
            threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
            for (Class<?> clazz: classSupplierHashMap.keySet().stream().filter(e-> !e.isAnnotation()).collect(Collectors.toList())) {
                if(clazz.getName().contains("SkeletonImpl")){
                    System.out.println("ok");
                }
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
            Supplier<T> supplier = classSupplierHashMap.get(clazz);
            if (mustBeProxied(clazz)){
                    InvocationHandler handler = proxyHandlers.get(clazz);
                    if (handler == null){
                        throw new Exception("Class: " + clazz.getName() + " is an interface but is not registered to be proxied");
                    }
                    return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, handler);
            }
            if(supplier == null){
                throw new Exception("No supplier found for dependency: " + clazz.getName());
            }else {
                return supplier.get();
            }
        }else {
            throw new RuntimeException("Class: " + clazz.getName() + " is not registered. Make sure that it is annotated with @Dependency");
        }
    }

    /*private static <T> Object createStub(Class<T> clazz) {
        Stub annotation = AnnotationScanner.getAnnotation(clazz, Stub.class);
        if (annotation.resultAddress().equals("")){
            return StubFactory.createStub(clazz, annotation.skeletonAddress(), annotation.skeletonPort());
        }
        return StubFactory.createStub(clazz,annotation.skeletonAddress(), annotation.skeletonPort(), annotation.resultAddress(), annotation.resultPort());
    }

    private static <T> be.kdg.distrib.skeletonFactory.Skeleton createSkeleton(Class<T> clazz) throws Exception {
        if (classSupplierHashMap.containsKey(clazz)){
            Supplier<T> supplier = classSupplierHashMap.get(clazz);
            T implementation;
            if(supplier == null ){
                throw new Exception("No supplier found for dependency: " + clazz.getName());
            }else {
                implementation = supplier.get();
            }
            return (be.kdg.distrib.skeletonFactory.Skeleton) SkeletonFactory.createSkeleton(implementation);
        }else {
            throw new RuntimeException("Class: " + clazz.getName() + " is not registered. Make sure that it is annotated with @Dependency");
        }
    }*/

    private static <T> boolean mustBeProxied(Class<T> clazz){
        return clazz.isInterface() || proxyHandlers.containsKey(clazz)
                || AnnotationScanner.getAnnotationsOfClass(clazz).stream().anyMatch(e-> annotationsToProxy.containsKey(e.annotationType()));
    }

   /* private static <T> boolean isKnownProxy(Class<T> clazz){
       return AnnotationScanner.getAnnotationsOfClass(clazz).stream()
               .anyMatch(e->
                       e.annotationType() == Skeleton.class ||
                       e.annotationType() == Stub.class);
    }*/

    public  <T> void registerDependency(Class<T> clazz, Supplier<T> instantiator) throws Exception{
        classSupplierHashMap.put(clazz, instantiator);
    }
    public void registerPoxyOnAnnotation(Class annotationClass,Function<Object, Object> instantiator) throws Exception{
        annotationsToProxy.put(annotationClass, instantiator);
    }

    public static void forceRegisterClass(Class clazz){
        DependencyResolver.addClassToScannedClasses(clazz);
        DependencyResolver.verifyClassDependencies(classSupplierHashMap,clazz);
    }

}
