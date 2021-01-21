package ownerszz.libraries.dependency.injection.core;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodCall;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.ObjenesisHelper;
import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;
import ownerszz.libraries.dependency.injection.core.cold.dependency.ColdDependency;

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
        ByteBuddyAgent.install();
    }
    public static <T> T createSimpleInstance(Class<T> clazz){
        if (preProxyInstances.get(clazz) != null){
            return (T) preProxyInstances.get(clazz);
        }else {
            return (T) dependencySuppliers.get(clazz).get();
        }
    }
    
    public static <T> T  createInstance(Class<T> clazz) throws Exception {
        if (dependencySuppliers.containsKey(clazz)){
            Supplier<Object> supplier = dependencySuppliers.get(clazz);
            Object instance;
            Dependency dependency = AnnotationScanner.getAnnotation(clazz, Dependency.class);
            if (dependency.creationType() == DependencyCreation.COLD){
                ColdDependency coldDependency = new ColdDependency(clazz);
                Class<? extends T> coldClass = new ByteBuddy()
                        .subclass(clazz)
                        .method(ElementMatchers.any()).intercept(MethodDelegation.to(coldDependency,ColdDependency.class))
                        .make()
                        .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                        .getLoaded();
                instance = ObjenesisHelper.newInstance(coldClass);
                //coldDependency.bindColdDependencyWith(instance);
            }else if (mustBeProxied(clazz)){
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
