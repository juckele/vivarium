package io.vivarium.util;

public class Reflection
{
    public static boolean isPrimitive(Class<?> clazz)
    {
        return clazz.isPrimitive() || clazz == Boolean.class || clazz == Byte.class || clazz == Integer.class
                || clazz == Long.class || clazz == Float.class || clazz == Double.class || clazz == Character.class;
    }

}
