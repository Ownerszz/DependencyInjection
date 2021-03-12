package ownerszz.libraries.dependency.injection.core.configuration;

import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;
import ownerszz.libraries.dependency.injection.core.*;
import ownerszz.libraries.dependency.injection.core.arguments.ArgumentReader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static ownerszz.libraries.dependency.injection.core.DependencyManager.createInstance;

public class DependencyConfigurer {
    public static void readConfigurations(List<Class> classes) throws Throwable {
        for (Class clazz: classes) {
            if (clazz.isAnnotationPresent(ContainerConfig.class)){
                Object configurer =  createInstance(clazz);
                readDependenciesFromClass(clazz,configurer);
            }
        }
    }

    private static void readDependenciesFromClass(Class clazz,Object configurer) throws Throwable {
        ArgumentReader.readArgumentsFromConfigClass(clazz);
        for (Method method: Arrays.stream(clazz.getDeclaredMethods()).filter(e-> e.isAnnotationPresent(Dependency.class)).collect(Collectors.toList())) {
            Dependency dependency = method.getAnnotation(Dependency.class);
            Class<?> toRegister = method.getReturnType();
            Supplier instantiator = ()->{
                Class[] params = method.getParameterTypes();
                Object[] args = new Object[params.length];
                for (int i = 0; i < params.length; i++) {
                    try {
                        args[i] = createInstance(params[i]);
                    } catch (Throwable throwable) {
                        throw new RuntimeException(throwable.getCause());
                    }
                }
                try {
                    return method.invoke(configurer,args);
                } catch (Throwable e) {
                    throw ExceptionFlipper.flipException(e);
                }
            };
            DependencyManager.getInstance().registerDependency(toRegister,instantiator,dependency);
        }
    }
}
