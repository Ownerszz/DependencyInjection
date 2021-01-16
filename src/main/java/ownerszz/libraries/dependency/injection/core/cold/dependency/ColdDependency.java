package ownerszz.libraries.dependency.injection.core.cold.dependency;

import net.bytebuddy.dynamic.scaffold.InstrumentedType;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import ownerszz.libraries.dependency.injection.core.DependencyInstanstatior;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
    public Object invoke(@Origin String methodName, @AllArguments Object[] args) throws Exception {
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

        return result;
    }
}
