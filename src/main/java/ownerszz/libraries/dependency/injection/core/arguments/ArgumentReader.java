package ownerszz.libraries.dependency.injection.core.arguments;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ownerszz.libraries.dependency.injection.annotation.scanner.AnnotationScanner;
import ownerszz.libraries.dependency.injection.utils.DefaultValueGetter;

import java.util.HashMap;

import static ownerszz.libraries.dependency.injection.utils.ClassUtils.*;

public class ArgumentReader {
    private static HashMap<String, String> arguments = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(ArgumentReader.class);
    public static void readArguments(Object... args){
        for (Object arg: args) {
            String key = arg.toString().split("=")[0];
            String value = arg.toString().split("=")[1];
            arguments.put(key,value);
        }
    }
    public static void readArgumentsFromConfigClass(Class clazz) throws Exception{
        for (ArgumentSetter argumentSetter: AnnotationScanner.findAnnotationsInClass(clazz,ArgumentSetter.class)) {
            arguments.put(argumentSetter.key(),argumentSetter.value());
        }
    }

    public static <T> T getValueFromArgumentsAs(String key, Class<T> type){
        String value = arguments.get(key);
        if (value == null){
           logger.warn("Key: {}   not found.",key );
           return (T) DefaultValueGetter.getDefaultValue(type);
        }
        if (isInteger(type)) {
            return (T) Integer.valueOf(value);
        } else if (isByte(type)) {
            return (T) Byte.valueOf(value);
        } else if (isDouble(type)) {
            return (T) (Double.valueOf(value));
        } else if (isShort(type)) {
            return (T) (Short.valueOf(value));
        } else if (isFloat(type)) {
            return (T) (Float.valueOf(value));
        } else if (isLong(type)) {
            return (T) (Long.valueOf(value));
        } else if (isBoolean(type)) {
            //The check in Boolean.valueOf() is weak.
            // It will take any other argument and will try to equal it with "true" resulting in always getting a boolean value even when the argument is not a boolean.
            if (value.equalsIgnoreCase("true") ||value.equalsIgnoreCase("false")){
                return (T) (Boolean.valueOf(value));
            }
            throw new ClassCastException("Was not given an boolean");
        } else if (isCharacter(type)) {
            if (value.length() > 1){
                throw new ClassCastException("Was not given an char");
            }
            return (T) (Character.valueOf(value.charAt(0)));
        } else if (isString(type)) {
            if (type.getSimpleName().equalsIgnoreCase("stringbuilder")){
                return (T) new StringBuilder(value);
            }
            return (T) (String.valueOf(value));
        } else {
            throw new RuntimeException("Nested classes not supported");
        }
    }



}
