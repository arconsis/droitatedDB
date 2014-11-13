package com.arconsis.android.datarobot;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Handler that redirects the calls to the annotation methods and provides the correct parameter data.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
class DatabaseValidatorAnnotationHandler implements InvocationHandler {
    private final Object[] params;

    public DatabaseValidatorAnnotationHandler(Object[] params) {
        this.params = params;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        for (int i = 0; i < params.length; i = i + 2) {
            if (params[i].equals(method.getName())) {
                Object param = params[i + 1];
                if (Number.class.isAssignableFrom(param.getClass())) {
                    Class<?> returnType = method.getReturnType();
                    return handleNumber((Number) param, returnType);
                } else {
                    return param;
                }
            }
        }
        return null;
    }

    private Object handleNumber(Number number, Class<?> returnType) {
        if (returnType.equals(long.class)) {
            return number.longValue();
        } else if (returnType.equals(int.class)) {
            return number.intValue();
        } else if (returnType.equals(byte.class)) {
            return number.byteValue();
        } else if (returnType.equals(double.class)) {
            return number.doubleValue();
        } else if (returnType.equals(float.class)) {
            return number.floatValue();
        } else if (returnType.equals(short.class)) {
            return number.shortValue();
        }
        return null;
    }
}
