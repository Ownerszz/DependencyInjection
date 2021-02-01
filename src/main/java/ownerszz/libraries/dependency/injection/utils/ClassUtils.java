package ownerszz.libraries.dependency.injection.utils;

public class ClassUtils {
    public static boolean isCharacter(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("character") || getClassName(clazz).equalsIgnoreCase("char");
    }

    public static boolean isString(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("string") || getClassName(clazz).equalsIgnoreCase("stringbuilder");
    }

    public static boolean isBoolean(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("boolean");
    }

    public static boolean isDouble(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("double");
    }

    public static boolean isFloat(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("float");
    }

    public static boolean isByte(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("byte");
    }

    public static boolean isShort(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("short");
    }

    public static boolean isLong(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("long");
    }

    public static boolean isInteger(Class clazz) {
        return getClassName(clazz).equalsIgnoreCase("integer") || getClassName(clazz).equalsIgnoreCase("int");
    }

    public boolean isStringOrPrimitive(Class clazz) {
        return isPrimitiveType(clazz) || isString(clazz);
    }

    public boolean isPrimitiveType(Class clazz) {
        return clazz.isPrimitive()
                || isCharacter(clazz)
                || isBoolean(clazz)
                || isLong(clazz)
                || isFloat(clazz)
                || isDouble(clazz)
                || isByte(clazz)
                || isInteger(clazz)
                || isShort(clazz);

    }
    public static String getClassName(Class clazz){
        return clazz.getSimpleName();
    }
}
