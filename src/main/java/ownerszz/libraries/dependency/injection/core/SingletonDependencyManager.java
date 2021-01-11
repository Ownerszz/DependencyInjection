package ownerszz.libraries.dependency.injection.core;

import java.util.HashMap;

import static ownerszz.libraries.dependency.injection.core.DependencyManager.createInstanceNoLifecycleChecks;


/**
 * Manages scoped dependencies
 * @see DependencyLifecycle#SINGLETON
 */
public class SingletonDependencyManager {
    private static HashMap<Class, Object> singletons;

    protected SingletonDependencyManager(){
        singletons = new HashMap<>();
    }
    protected Object createOrGetInstance(Class clazz) throws Throwable {
        Object instance = singletons.get(clazz);
        if(instance == null){
            instance = createInstanceNoLifecycleChecks(clazz);
            singletons.put(clazz,instance);
        }
        return instance;
    }
}
