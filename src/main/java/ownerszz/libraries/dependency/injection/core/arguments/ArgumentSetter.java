package ownerszz.libraries.dependency.injection.core.arguments;

import ownerszz.libraries.dependency.injection.utils.DefaultValueGetter;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ArgumentSetter {
    String key() default "";
    String value() default "";
}
