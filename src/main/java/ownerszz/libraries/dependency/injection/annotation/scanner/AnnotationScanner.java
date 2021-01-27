package ownerszz.libraries.dependency.injection.annotation.scanner;

import ownerszz.libraries.dependency.injection.core.Dependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/** This class has utility methods and keeps track of resolved classes
 *
 *
 */
public class AnnotationScanner {
    private static HashMap<Class, Boolean> resolvedClasses = new HashMap<>();
    private static HashMap<Class, List<Annotation>> knownAnnotations = new HashMap<>();
    private static List<Class> slowClasses = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(AnnotationScanner.class);

    /**
     * Checks if a class is resolvable.
     * This means that the class has an annotation that links to {@link Dependency} or is inheriting/implementing a class that links to {@link Dependency}
     *
     * @param clazz The class to check
     * @param depth The current dept (start always less then 10)
     * @return Boolean (true, false, null if it takes to much effort {@link #tryResolveSlowClasses()})
     */
    public static Boolean isResolvable(Class<?> clazz, int depth){
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

    /**
     * Checks if an annotation is resolvable.
     * This means that the annotation links to {@link Dependency} or is "inheriting" another annotation that links to {@link Dependency}
     * @param clazz The annotation to check
     * @param depth The current dept
     * @return Boolean (true, false, null if it takes to much effort {@link #tryResolveSlowClasses()})
     */
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

    /**
     *
     * @param clazz the class to add to the resolved list
     * @param annotation the annotation to add to the class
     * @return true
     */
    private static boolean resolve (Class<?> clazz, Annotation annotation){
        resolvedClasses.put(clazz, true);
        knownAnnotations.get(clazz).add(annotation);
        return true;
    }

    /**
     * Utility method to get all annotations of a class.
     * The standard {@link Class#getAnnotations()} is not enough for us
     * @param clazz The class to get all known annotations from
     * @return Unmodifiable list containing all annotations in this class or null if class isn't known
     */
    public static List<Annotation> getAnnotationsOfClass(Class<?> clazz){
        List<Annotation> annotations = knownAnnotations.get(clazz);
       return Collections.unmodifiableList(knownAnnotations.get(clazz));
    }

    /**
     * Utility method to know if a certain annotation is present.
     * The standard {@link Class#isAnnotationPresent(Class)} is not enough for us
     * @param clazz The class to get all known annotations from
     * @param annotation The annotation to verify.
     * @return true if annotation is present
     */
    public static boolean isAnnotationPresent(Class clazz,Class annotation){
        return knownAnnotations.get(clazz).stream().anyMatch(e -> e.annotationType().equals(annotation));
    }

    /**
     * Utility method to get a certain annotation.
     * The standard {@link Class#getAnnotation(Class)} is not enough for us
     * @param clazz The class to get the annotation from
     * @param annotation The annotation that we want to get
     * @param <A> Generic
     * @return The annotation
     * @throws RuntimeException if no annotation is found
     */
    public static <A> A getAnnotation(Class<?> clazz, Class<?> annotation){
        List<Annotation> found = knownAnnotations.get(clazz);
        if (found == null){
            found = new ArrayList<>();
            for (Class interfaze: clazz.getInterfaces()) {
                found.add(getAnnotation(interfaze,annotation));
            }
            if (found.size() == 0){
                throw new RuntimeException("Annotation: "+ annotation.getName() + " not found in: " + clazz.getName());
            }
        }
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

    /**
     * Resolve all classes that took too long.
     */
    public static void tryResolveSlowClasses(){
        for (Class clazz: slowClasses) {
            Boolean resolvable = isResolvable(clazz,11);
            if (resolvable != null && resolvable){
               logger.debug("Successfully resolved class: " + clazz.getName() + " even when it was deep.");
            }else {
                logger.debug("Failed to resolve class: " + clazz.getName() + " because it is too deep for us to resolve it.");
            }
        }
        slowClasses.clear();
    }

}
