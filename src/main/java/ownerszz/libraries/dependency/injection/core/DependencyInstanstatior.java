package ownerszz.libraries.dependency.injection.core;

import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class DependencyInstanstatior {
    protected static HashMap<Class, Supplier> getDependencySuppliers() {
        return dependencySuppliers;
    }
    private static final HashMap<Class, InvocationHandler> proxyHandlers = new HashMap<>();
    private static final HashMap<Class<Annotation>, Function<Object, Object>> annotationsToProxy = new HashMap<>();
    private static final HashMap<Class, Object> preProxyInstances = new HashMap<>();
    private static HashMap<Class, Supplier> dependencySuppliers = new HashMap<>();
    protected static void use(HashMap<Class, Supplier> classSupplierHashMap){
        dependencySuppliers = classSupplierHashMap;
    }
    protected static <T> T createSimpleInstance(Class<T> clazz){
        if (preProxyInstances.get(clazz) != null){
            return (T) preProxyInstances.get(clazz);
        }else {
            return (T) dependencySuppliers.get(clazz).get();
        }
    }
    
    protected static <T> T  createInstance(Class<T> clazz) throws Exception {
        if (dependencySuppliers.containsKey(clazz)){
            Supplier<Object> supplier = dependencySuppliers.get(clazz);
            Object instance;
            if (mustBeProxied(clazz)){
                //Registered annotation?
                Optional<Annotation> ann = AnnotationScanner.getAnnotationsOfClass(clazz).stream().filter(e-> annotationsToProxy.containsKey(e.annotationType())).findFirst();
                if (ann.isPresent()){
                    Object preProxy = supplier.get();
                    preProxyInstances.put(clazz,preProxy );
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

            return (T) instance;
        }else {
            throw new RuntimeException("Class: " + clazz.getName() + " is not registered. Make sure that it is annotated with @Dependency");
        }
    }

    protected static void registerProxyOnAnnotation(Class annotationClass,Function<Object, Object> instantiator){
        annotationsToProxy.put(annotationClass, instantiator);
    }
    
    protected static <T> void registerDependency(Class<T> clazz, Supplier<T> instantiator){
        dependencySuppliers.put(clazz, instantiator);
    }
    
    protected static void clear(){
        dependencySuppliers.clear();
        proxyHandlers.clear();
        annotationsToProxy.clear();
    }

    private static  <T> boolean mustBeProxied(Class<T> clazz){
        return clazz.isInterface() || proxyHandlers.containsKey(clazz)
                || AnnotationScanner.getAnnotationsOfClass(clazz).stream().anyMatch(e-> annotationsToProxy.containsKey(e.annotationType()));
    }
}
