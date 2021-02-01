package ownerszz.libraries.dependency.injection.core.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ArgumentSetter(key = "test",value = "hey")
public @interface TestSetter {

}
