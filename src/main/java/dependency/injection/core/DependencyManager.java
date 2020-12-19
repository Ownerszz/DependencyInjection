package dependency.injection.core;


import be.kdg.distrib.communication.NetworkAddress;
import be.kdg.distrib.skeletonFactory.SkeletonFactory;
import be.kdg.distrib.stubFactory.StubFactory;
import dependency.injection.annotation.scanner.AnnotationScanner;
import dependency.injection.distrib.Skeleton;
import dependency.injection.distrib.Stub;
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.stream.Collectors;

public class DependencyManager {
    private static HashMap<Class, Constructor> classConstructorHashMap;
    private static HashMap<Class, InvocationHandler> proxyHandlers;
    public static void init() throws Exception {
        classConstructorHashMap = new HashMap<>();
        proxyHandlers = new HashMap<>();
        DependencyResolver.init(classConstructorHashMap);
    }
    public static void use(HashMap<Class,Constructor> objectInstantiators){
        classConstructorHashMap = objectInstantiators;
    }
    public static void run(boolean selfInit) throws Exception {
        if (selfInit){
            init();
        }
        runRunnableDependencies();
    }

    private static void runRunnableDependencies() throws Exception {
        for (Class<?> clazz: classConstructorHashMap.keySet().stream().filter(e-> !e.isAnnotation()).collect(Collectors.toList())) {
            if(clazz.getName().contains("SkeletonImpl")){
                System.out.println("ok");
            }
            Dependency dependency = AnnotationScanner.getAnnotation(clazz, Dependency.class);
            if(dependency != null && dependency.runnable()){
                if (AnnotationScanner.isAnnotationPresent(clazz, Skeleton.class)){
                    be.kdg.distrib.skeletonFactory.Skeleton skeleton = createSkeleton(clazz);
                    skeleton.run();
                }
            }
        }
    }

    public static  <T> Object createInstance(Class<T> clazz) throws Exception{
        if (classConstructorHashMap.containsKey(clazz)){
            Constructor<T> ctor = classConstructorHashMap.get(clazz);
            if (mustBeProxied(clazz)){
                if (AnnotationScanner.isAnnotationPresent(clazz, Skeleton.class)){
                    return createSkeleton(clazz);
                }else if (AnnotationScanner.isAnnotationPresent(clazz, Stub.class)){
                    return createStub(clazz);
                }else {
                    InvocationHandler handler = proxyHandlers.get(clazz);
                    if (handler == null){
                        throw new Exception("Class: " + clazz.getName() + " is an interface but is not registered to be proxied");
                    }
                    return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, handler);
                }
            }
            if(ctor == null || ctor.getParameterCount() == 0){
                return ObjenesisHelper.newInstance(clazz);
            }
            Object[] contructorArgs = new Object[ctor.getParameterCount()];
            Class[] parameterTypes=ctor.getParameterTypes();
            for (int i = 0; i < ctor.getParameterCount(); i++) {
                contructorArgs[i] = createInstance(parameterTypes[i]);
            }
            return ctor.newInstance(contructorArgs);
        }else {
            throw new RuntimeException("Class: " + clazz.getName() + " is not registered. Make sure that it is annotated with @Dependency");
        }
    }

    private static <T> Object createStub(Class<T> clazz) {
        Stub annotation = AnnotationScanner.getAnnotation(clazz, Stub.class);
        if (annotation.resultAddress().equals("")){
            return StubFactory.createStub(clazz, annotation.skeletonAddress(), annotation.skeletonPort());
        }
        return StubFactory.createStub(clazz,annotation.skeletonAddress(), annotation.skeletonPort(), annotation.resultAddress(), annotation.resultPort());
    }

    private static <T> be.kdg.distrib.skeletonFactory.Skeleton createSkeleton(Class<T> clazz) throws Exception {
        if (classConstructorHashMap.containsKey(clazz)){
            Constructor<T> ctor = classConstructorHashMap.get(clazz);
            Object implementation;
            if(ctor == null || ctor.getParameterCount() == 0){
                implementation = ObjenesisHelper.newInstance(clazz);
            }else {
                Object[] contructorArgs = new Object[ctor.getParameterCount()];
                Class[] parameterTypes=ctor.getParameterTypes();
                for (int i = 0; i < ctor.getParameterCount(); i++) {
                    contructorArgs[i] = createInstance(parameterTypes[i]);
                }
                implementation = ctor.newInstance(contructorArgs);
            }
            return (be.kdg.distrib.skeletonFactory.Skeleton) SkeletonFactory.createSkeleton(implementation);
        }else {
            throw new RuntimeException("Class: " + clazz.getName() + " is not registered. Make sure that it is annotated with @Dependency");
        }
    }

    private static <T> boolean mustBeProxied(Class<T> clazz){
        return clazz.isInterface() || isKnownProxy(clazz);
    }

    private static <T> boolean isKnownProxy(Class<T> clazz){
       return AnnotationScanner.getAnnotationsOfClass(clazz).stream()
               .anyMatch(e->
                       e.annotationType() == Skeleton.class ||
                       e.annotationType() == Stub.class);
    }


}
