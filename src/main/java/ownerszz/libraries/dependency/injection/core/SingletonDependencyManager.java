package ownerszz.libraries.dependency.injection.core;

import java.util.HashMap;




/**
 * Manages scoped dependencies
 * @see DependencyLifecycle#SINGLETON
 */
public class SingletonDependencyManager {
    private static HashMap<Class, Object> singletons;

    protected SingletonDependencyManager(){
        singletons = new HashMap<>();
    }
    protected <T> T createOrGetInstance(Class<T> clazz) throws Throwable {
        Object instance = singletons.get(clazz);
        if(instance == null){
            instance = DependencyInstanstatior.createInstance(clazz);
            singletons.put(clazz,instance);
        }
        return (T) instance;
    }
}
