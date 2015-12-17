/*
 * Copyright (C) 2014 The droitated DB Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droitateddb.cursor;

import java.lang.reflect.Method;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
class ReflectionUtil {
	static Class<?>[] getArgTypes(final Object[] args) {
		Class<?>[] argTypes = new Class<?>[args.length];
		for (int i = 0; i < args.length; i++) {
			argTypes[i] = args[i].getClass();
		}
		return argTypes;
	}


	static boolean isCloseMethod(Method method) {
		return method.getName().equals("close") && method.getParameterTypes().length == 0;
	}

	static boolean isMethodOfType(final Method method, final Class<?>[] argTypes, final Class<?> type) {
		Method[] declaredMethods = type.getDeclaredMethods();
		for (Method declared : declaredMethods) {
			if (declared.getName().equals(method.getName())) {
				Class<?>[] parameterTypes = declared.getParameterTypes();
				if (parameterTypes.length == argTypes.length) {
					boolean matches = true;
					for (int i = 0; i < parameterTypes.length; i++) {
						if (!matches(parameterTypes[i], argTypes[i])) {
							matches = false;
						}
					}
					if (matches) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private static boolean isPrimitiveAssignable(final Class<?> paramType, final Class<?> argType) {
		if (paramType.equals(java.lang.Boolean.TYPE) && argType.equals(java.lang.Boolean.class)) {
			return true;
		} else if (paramType.equals(java.lang.Integer.TYPE) && argType.equals(java.lang.Integer.class)) {
			return true;
		} else if (paramType.equals(java.lang.Character.TYPE) && argType.equals(java.lang.Character.class)) {
			return true;
		} else if (paramType.equals(java.lang.Float.TYPE) && argType.equals(java.lang.Float.class)) {
			return true;
		} else if (paramType.equals(java.lang.Double.TYPE) && argType.equals(java.lang.Double.class)) {
			return true;
		} else if (paramType.equals(java.lang.Long.TYPE) && argType.equals(java.lang.Long.class)) {
			return true;
		} else if (paramType.equals(java.lang.Short.TYPE) && argType.equals(java.lang.Short.class)) {
			return true;
		}
		// In correctness there should also be a mapping for the
		// primitive array types, but no method in
		// android.database.Cursor used arrays at this point (January
		// 2014)
		return false;
	}

	private static boolean matches(final Class<?> paramType, final Class<?> argType) {
		if (paramType.isAssignableFrom(argType)) {
			return true;
		} else {
			return isPrimitiveAssignable(paramType, argType);
		}
	}
}
