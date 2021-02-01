package ownerszz.libraries.dependency.injection.utils;

import static ownerszz.libraries.dependency.injection.utils.ClassUtils.*;

public class DefaultValueGetter {
    // These gets initialized to their default values
    private static boolean DEFAULT_BOOLEAN;
    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;

    public static Object getDefaultValue(Class clazz) {
        if (isBoolean(clazz)) {
            return DEFAULT_BOOLEAN;
        } else if (isByte(clazz)) {
            return DEFAULT_BYTE;
        } else if (isShort(clazz)) {
            return DEFAULT_SHORT;
        } else if (isInteger(clazz)) {
            return DEFAULT_INT;
        } else if (isLong(clazz)) {
            return DEFAULT_LONG;
        } else if (isFloat(clazz)) {
            return DEFAULT_FLOAT;
        } else if (isDouble(clazz)) {
            return DEFAULT_DOUBLE;
        } else {
            return null;
        }
    }
}
