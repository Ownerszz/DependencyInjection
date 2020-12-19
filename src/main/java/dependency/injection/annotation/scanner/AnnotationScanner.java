package dependency.injection.annotation.scanner;

import dependency.injection.core.Dependency;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;

public class AnnotationScanner {
    private static HashMap<Class, Boolean> resolvedClasses = new HashMap<>();
    private static HashMap<Class, List<Annotation>> knownAnnotations = new HashMap<>();
    public static boolean isResolvable(Class<?> clazz){
        if (clazz == Object.class || clazz==null){
            return false;
        }
        /*if (clazz.getName().contains("Stubbed")){
            System.out.println("ok");
        }*/
        Boolean result = resolvedClasses.get(clazz);
        //Did we scan this class?
        if (result == null){
            knownAnnotations.put(clazz,new ArrayList<>());
            if (clazz.isAnnotationPresent(Dependency.class) || clazz == Dependency.class){
                return resolve(clazz, clazz.getAnnotation(Dependency.class));
            }
            if (clazz.isAnnotation()){
                if (isResolvableAnnotation(clazz)) return true;
            }else {
                //scan class annotations
                Boolean success;
                for (Class interfaze: clazz.getInterfaces()) {
                    Boolean exists = resolvedClasses.get(interfaze);
                    success = Objects.requireNonNullElseGet(exists, () -> isResolvable(interfaze));
                    if (success){
                        for (Annotation annotation: knownAnnotations.get(interfaze)) {
                            knownAnnotations.get(clazz).add(annotation);
                        }
                        if (interfaze.isAnnotation()){
                            //knownAnnotations.get(clazz).add(interfaze);
                        }
                        resolvedClasses.put(clazz, true);
                        return true;
                    }
                }
                if (isResolvableAnnotation(clazz)) return true;
            }
            resolvedClasses.put(clazz, false);
            return false;
        }
        //Return the saved result
        else return result;
    }

    private static boolean isResolvableAnnotation(Class<?> clazz) {
        Boolean success;
        for (Annotation annotation: clazz.getAnnotations()) {
            Boolean exists = resolvedClasses.get(annotation.getClass());
            success = Objects.requireNonNullElseGet(exists, () -> isResolvable(annotation.getClass()));
            if (success){
                for (Annotation ann: knownAnnotations.get(annotation.getClass())) {
                    knownAnnotations.get(clazz).add(ann);
                }
                knownAnnotations.get(clazz).add(annotation);
                resolvedClasses.put(clazz, true);
                return true;
            }
        }
        return false;
    }

    private static boolean resolve (Class<?> clazz, Annotation annotation){
        resolvedClasses.put(clazz, true);
        knownAnnotations.get(clazz).add(annotation);
        return true;
    }

    public static List<Annotation> getAnnotationsOfClass(Class<?> clazz){
       return Collections.unmodifiableList(knownAnnotations.get(clazz));
    }

    public static boolean isAnnotationPresent(Class clazz,Class annotation){
        List<Annotation> found = knownAnnotations.get(clazz);
        return knownAnnotations.get(clazz).stream().anyMatch(e -> e.annotationType().equals(annotation));
    }
    public static <A> A getAnnotation(Class<?> clazz, Class<?> annotation){
        List<Annotation> found = knownAnnotations.get(clazz);
        Annotation result = null;
        for (Annotation possibleAnnotation: found) {
            Class<Annotation> annotationClass = (Class<Annotation>) possibleAnnotation.annotationType();
            if (annotationClass == annotation){
                result = possibleAnnotation;
            }
        }
        if (result == null){
            throw new RuntimeException("Annotation: " +annotation.getName() + " not found.");
        }
        //A temp = (A) clazz.getAnnotation(result);

       return (A) result;
    }

}
