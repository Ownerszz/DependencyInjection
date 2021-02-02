package ownerszz.libraries.dependency.injection.core;

import ownerszz.libraries.dependency.injection.core.arguments.ArgumentSetter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ArgumentSetter(key = "loggingEnabled", value = "true")
public @interface EnableLogging {
}
