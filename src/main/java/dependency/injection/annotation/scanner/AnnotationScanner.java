package dependency.injection.annotation.scanner;

import dependency.injection.core.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;


public class AnnotationScanner {
    private static HashMap<Class, Boolean> resolvedClasses = new HashMap<>();
    private static HashMap<Class, List<Annotation>> knownAnnotations = new HashMap<>();
    private static List<Class> slowClasses = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(AnnotationScanner.class);
    public static Boolean isResolvable(Class<?> clazz, int depth){
        if (clazz.getName().contains("DistribRegistration")){
            System.out.println("found it");
        }
        if (depth == 10){
            slowClasses.add(clazz);
            resolvedClasses.put(clazz, null);
            return null;
        }
        //It's too deep to bother with
        if (depth == 20){
            resolvedClasses.remove(clazz);
            return false;
        }
        if (clazz == Object.class || clazz==null){
            return false;
        }
        /*if (clazz.getName().contains("Stubbed")){
            System.out.println("ok");
        }*/
        Boolean result = resolvedClasses.get(clazz);
        if (slowClasses.contains(clazz)){
            return null;
        }else if (result == null){
            knownAnnotations.put(clazz,new ArrayList<>());
            if (clazz.isAnnotationPresent(Dependency.class) || clazz == Dependency.class){
                return resolve(clazz, clazz.getAnnotation(Dependency.class));
            }
            if (clazz.isAnnotation()){
                return isResolvableAnnotation(clazz, depth);
            }else {
                //scan class annotations
                Boolean success;
                for (Class interfaze: clazz.getInterfaces()) {
                    Boolean exists = resolvedClasses.get(interfaze);
                    int newDept = depth++;
                    success = Objects.requireNonNullElseGet(exists, () -> isResolvable(interfaze, newDept));
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
                if (isResolvableAnnotation(clazz, depth)) return true;
            }
            resolvedClasses.put(clazz, false);
            return false;
        }
        //Return the saved result
        else return result;
    }

    private static Boolean isResolvableAnnotation(Class<?> clazz, int depth) {
        Boolean success;
        int newDept = depth + 1;
        for (Annotation annotation: clazz.getAnnotations()) {
            Boolean exists = resolvedClasses.get(annotation.getClass());

            success = Objects.requireNonNullElseGet(exists, () -> isResolvable(annotation.getClass(), newDept));
            if (success == null){
                return null;
            }
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
        List<Annotation> annotations = knownAnnotations.get(clazz);
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
    public static void tryResolveSlowClasses(){
        for (Class clazz: slowClasses) {
            Boolean resolvable = isResolvable(clazz,11);
            if (resolvable != null && resolvable){
               logger.info("Successfully resolved class: " + clazz.getName() + " even when it was deep.");
            }else {
                logger.warn("Failed to resolve class: " + clazz.getName() + " because it is too deep for us to resolve it.");
            }
        }
        slowClasses.clear();
    }

}
