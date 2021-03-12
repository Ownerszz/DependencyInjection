package ownerszz.libraries.dependency.injection.core.cold.dependency;

import net.bytebuddy.dynamic.scaffold.InstrumentedType;

import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import org.objenesis.instantiator.annotations.Instantiator;
import ownerszz.libraries.dependency.injection.core.DependencyInstanstatior;
import ownerszz.libraries.dependency.injection.core.ExceptionFlipper;

import java.beans.Transient;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * This class will hold a dependency and resolve it within its {@link ColdDependency#dependency}
 */
public class ColdDependency {
    private CompletableFuture<Object> dependency;
    private Class<?> dependencyType;
    public ColdDependency(Class<?> target) {
        dependency = new CompletableFuture<>();
        dependencyType = target;
        Executors.newCachedThreadPool().submit(()->{
            try {
                dependency.complete(DependencyInstanstatior.createSimpleInstance(target));
            }catch (Exception e){
                dependency.completeExceptionally(e);
            }
        });

    }

    @RuntimeType
    public Object invoke(@This Object proxy, @Origin String methodName, @AllArguments Object[] args) throws Throwable {
        try {
            Class[] argTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                argTypes[i] = args[i].getClass();
            }
            Optional<Method> foundMethod = Arrays.stream(dependencyType.getDeclaredMethods())
                    .filter(e-> methodName.contains(e.getName())
                            && Arrays.equals(argTypes,e.getParameterTypes()))
                    .findFirst();
            if (foundMethod.isEmpty()){
                throw new NoSuchMethodException();
            }

            Method toInvoke = foundMethod.get();
            Object impl = dependency.get();
            Object result = toInvoke.invoke(impl,args);
            updateColdDependencyFieldsToMatchImpl(proxy);
            return result;
        }catch (Throwable e){
            throw ExceptionFlipper.flipException(e);
        }

    }

    private void updateColdDependencyFieldsToMatchImpl(Object proxy) throws Exception {
        Class proxyClass = proxy.getClass().getSuperclass();
        for (Field field:proxyClass.getDeclaredFields()) {
            field.setAccessible(true);
            Object implValue = field.get(dependency.get());
            field.set(proxy,implValue);
        }

    }

}
