package ownerszz.libraries.dependency.injection.annotation.scanner.model;

import ownerszz.libraries.dependency.injection.core.Dependency;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Dependency
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Annotation1 {
}
