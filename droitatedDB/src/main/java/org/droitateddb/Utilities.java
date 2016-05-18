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
package org.droitateddb;

import org.droitateddb.schema.AbstractAttribute;
import org.droitateddb.schema.SchemaConstants;

import java.lang.reflect.Field;

/**
 * Provides utility methods for droitated DB.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class Utilities {
    public static RuntimeException handle(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        } else {
            throw new RuntimeException(e);
        }
    }

    static void setFieldValue(Field field, final Object data, Object value) {
        try {
            field.setAccessible(true);
            field.set(data, value);
        } catch (IllegalAccessException e) {
            throw handle(e);
        }
    }


    static String getLinkTableName(Class<?> linkTableSchema) {
        return getStaticFieldValue(linkTableSchema, SchemaConstants.TABLE_NAME);
    }

    static AbstractAttribute[] getLinkTableColumns(Class<?> linkTableSchema) {
        return getStaticFieldValue(linkTableSchema, SchemaConstants.ATTRIBUTES);
    }

    static String[] getLinkTableProjection(Class<?> linkTableSchema) {
        return getStaticFieldValue(linkTableSchema, SchemaConstants.PROJECTION);
    }

    static Number getPrimaryKey(final Object data, EntityData entityData) {
        return getFieldValue(data, entityData.primaryKey);
    }

    public static <T> T getStaticFieldValue(Class<?> aClass, String fieldName) {
        return getStaticFieldValue(getDeclaredField(aClass, fieldName));
    }

    static <T> T getFieldValue(Class<?> aClass, String fieldName, Object data) {
        return getFieldValue(data, getDeclaredField(aClass, fieldName));
    }

    public static Field getDeclaredField(Class<?> aClass, String fieldName) {
        try {
            // this call may fail sometimes on some devices, such as Samsung Galaxy S4 with Android 5.0.1
            return aClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException nsfe) {
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field field : declaredFields) {
                if (field.getName().equals(fieldName)) {
                    return field;
                }
            }
            throw handle(nsfe);
        }
    }

    static <T> T getStaticFieldValue(Field field) {
        return getFieldValue(null, field);
    }

    @SuppressWarnings("unchecked")
    static <T> T getFieldValue(final Object data, Field field) {
        try {
            field.setAccessible(true);
            return (T) field.get(data);
        } catch (IllegalAccessException e) {
            throw handle(e);
        }
    }

}
