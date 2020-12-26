package dependency.injection.core;

import java.util.Collection;
import java.util.Map;

public class ClassUtil {
    public static boolean isCollection(Class clazz){
        return Collection.class.isAssignableFrom(clazz) || Map.class.isAssignableFrom(clazz);
    }
}
