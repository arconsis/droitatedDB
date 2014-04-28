/*
 * Copyright (C) 2014 The Datarobot Authors
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
package com.arconsis.android.datarobot;

import static com.arconsis.android.datarobot.schema.SchemaConstants.ATTRIBUTES;
import static com.arconsis.android.datarobot.schema.SchemaConstants.PROJECTION;
import static com.arconsis.android.datarobot.schema.SchemaConstants.TABLE_NAME;

import java.lang.reflect.Field;

import com.arconsis.android.datarobot.schema.AbstractAttribute;

/**
 * Provides utility methods for datarobot.
 * 
 * @author Falk Appel
 * @author Alexander Frank
 */
public class Utilities {
	public static RuntimeException handle(Exception e) {
		if(e instanceof RuntimeException) {
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

	static Object getFieldValue(final Object data, Field field) {
		try {
			field.setAccessible(true);
			return field.get(data);
		} catch (IllegalAccessException e) {
			throw handle(e);
		}
	}

	static String getLinkTableName(Class<?> linkTableSchema) {
		try {
			return (String) linkTableSchema.getDeclaredField(TABLE_NAME).get(null);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	static AbstractAttribute[] getLinkTableColumns(Class<?> linkTableSchema) {
		try {
			return (AbstractAttribute[]) linkTableSchema.getDeclaredField(ATTRIBUTES).get(null);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	static String[] getLinkTableProjection(Class<?> linkTableSchema) {
		try {
			return (String[]) linkTableSchema.getDeclaredField(PROJECTION).get(null);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	static Integer getPrimaryKey(final Object data, EntityData entityData) {
		return (Integer) getFieldValue(data, entityData.primaryKey);
	}
}
