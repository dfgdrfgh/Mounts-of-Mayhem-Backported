package zzik2.zreflex.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ZReflectionTool {

    private ZReflectionTool() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static <T> T getStaticFieldValue(Class<?> owner, String fieldName) {
        try {
            Field field = findField(owner, fieldName);
            makeAccessible(field, fieldName);
            @SuppressWarnings("unchecked")
            T value = (T) field.get(null);
            return value;
        } catch (IllegalAccessException e) {
            throw new ReflectionException("Failed to read static field " + owner.getName() + "." + fieldName, e);
        }
    }

    public static <T> T invokeMethod(Object target, String methodName, Object... args) {
        if (target == null) {
            throw new ReflectionException("Target cannot be null for " + methodName);
        }

        boolean staticCall = target instanceof Class<?>;
        Class<?> owner = staticCall ? (Class<?>) target : target.getClass();
        Object receiver = staticCall ? null : target;
        Method method = findMethod(owner, methodName, args);
        makeAccessible(method, methodName);

        try {
            @SuppressWarnings("unchecked")
            T value = (T) method.invoke(receiver, args);
            return value;
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new ReflectionException("Failed to invoke " + owner.getName() + "." + methodName, e);
        }
    }

    private static Field findField(Class<?> owner, String name) {
        for (Class<?> current = owner; current != null; current = current.getSuperclass()) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
            }
        }
        throw new ReflectionException("Field not found: " + owner.getName() + "." + name);
    }

    private static Method findMethod(Class<?> owner, String name, Object[] args) {
        for (Class<?> current = owner; current != null; current = current.getSuperclass()) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals(name) && parametersMatch(method.getParameterTypes(), args)) {
                    return method;
                }
            }
        }
        throw new ReflectionException("Method not found: " + owner.getName() + "." + name);
    }

    private static boolean parametersMatch(Class<?>[] declared, Object[] args) {
        if (declared.length != args.length) {
            return false;
        }
        for (int i = 0; i < declared.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                if (declared[i].isPrimitive()) {
                    return false;
                }
                continue;
            }
            if (!wrap(declared[i]).isAssignableFrom(arg.getClass())) {
                return false;
            }
        }
        return true;
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) return type;
        if (type == boolean.class) return Boolean.class;
        if (type == byte.class) return Byte.class;
        if (type == char.class) return Character.class;
        if (type == short.class) return Short.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == void.class) return Void.class;
        return type;
    }

    private static void makeAccessible(java.lang.reflect.AccessibleObject object, String name) {
        if (!object.trySetAccessible()) {
            throw new ReflectionException("Cannot access " + name);
        }
    }

    public static final class ReflectionException extends RuntimeException {
        public ReflectionException(String message) {
            super(message);
        }

        public ReflectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
