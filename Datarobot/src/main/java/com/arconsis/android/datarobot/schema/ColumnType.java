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
package com.arconsis.android.datarobot.schema;

import java.util.Date;

/**
 * Supported column types for SQLite.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public enum ColumnType {
	INTEGER("Integer"), REAL("Real"), BLOB("Blob"), TEXT("Text");

	private final String readable;

	ColumnType(final String readable) {
		this.readable = readable;
	}

	public String getReadable() {
		return readable;
	}

	/**
	 * Resolves the matching {@link ColumnType} to a fieldType.
	 *
	 * @param type String type to be resolved
	 * @return The resolved ColumnType
	 */
	public static ColumnType resolveColumnType(final String type) {
		if (matches(String.class, type)) {
			return ColumnType.TEXT;
		} else if (matches(Integer.class, type) || matches(int.class, type) || matches(Boolean.class, type) || matches(boolean.class, type) ||
				matches(Date.class, type) || matches(Long.class, type) || matches(long.class, type)) {
			return ColumnType.INTEGER;
		} else if (matches(Double.class, type) || matches(Float.class, type) || matches(double.class, type) || matches(float.class, type)) {
			return ColumnType.REAL;
		} else if (matches(byte[].class, type)) {
			return ColumnType.BLOB;
		} else {
			throw new IllegalStateException("The type " + type + " is no supported @Column type. Try @Relationship if you want to create an association.");
		}
	}

	private static boolean matches(final Class<?> ref, final String match) {
		return ref.getCanonicalName().equals(match);
	}
}
