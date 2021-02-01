package ownerszz.libraries.dependency.injection.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

public class AnnotationUtils {

    public static boolean isDefaultAnnotation(Class annotation){
        return annotation == Retention.class
                || annotation== Target.class
                || annotation == Documented.class;
    }
}
