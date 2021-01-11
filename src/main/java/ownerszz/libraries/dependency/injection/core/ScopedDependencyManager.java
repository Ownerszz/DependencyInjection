package ownerszz.libraries.dependency.injection.core;

import java.util.HashMap;
import java.util.UUID;

import static ownerszz.libraries.dependency.injection.core.DependencyManager.createInstance;

public  class ScopedDependencyManager {
    private static HashMap<String, HashMap<Class,Object>> scopedDependencies;
    protected ScopedDependencyManager(){
        scopedDependencies = new HashMap<>();
    }

    /**
     * Creates a new key to use for adding instances within the scope. Scopes are never automatically created.
     *
     * If you no longer need the scope just call {@link DependencyManager#destroyScope(String)}
     *
     * @return The key of the scope
     */
    protected  String createScope(){
        String key;
        do {
            key = UUID.randomUUID().toString();
        }while (scopedDependencies.containsKey(key));
        scopedDependencies.putIfAbsent(key,new HashMap<>());
        return key;
    }

    /**
     * Will remove the scope and all scoped instances if they are no longer used within the application.
     * @param key The key to destroy
     */
    protected  void destroyScope(String key){
        scopedDependencies.remove(key);
    }

    /**
     * Created or get a scoped instance associated with the specified key
     *
     * Limitations: This will never fetch {@link DependencyManager#createSimpleInstance(Class) simple instances}
     * and will therefore always trigger the proxy functions.
     *
     * @param key The scope key
     * @param clazz The class to instantiate or get
     * @return the scoped instance
     * @throws Exception when key not found
     */
    protected  Object createOrGetScopedInstance(String key,Class clazz) throws Throwable {
        if (scopedDependencies.containsKey(key)){
            if (scopedDependencies.get(key).containsKey(clazz)){
                return scopedDependencies.get(key).get(clazz);
            }else {
                Object instance = createInstance(clazz);
                scopedDependencies.get(key).put(clazz,instance);
                return instance;
            }
        }else {
            throw new Exception("Scope doesn't exist.");
        }
    }


}
