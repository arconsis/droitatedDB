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

import android.database.Cursor;

/**
 * Abstract definition of an attribute.
 * 
 * @author Falk Appel
 * @author Alexander Frank
 */
public abstract class AbstractAttribute {

	private final String fieldName;
	private final String columnName;
	private final Class<?> fieldType;
	private final ColumnType type;
	private final int columnIdx;

	public AbstractAttribute(final ColumnType type, final String fieldName, final Class<?> fieldType, final int columnIdx) {
		this(type, fieldName, fieldName, fieldType, columnIdx);
	}

	public AbstractAttribute(final ColumnType type, final String fieldName, final String columnName, final Class<?> fieldType, final int columnIdx) {
		this.type = type;
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.fieldType = fieldType;
		this.columnIdx = columnIdx;
	}

	public String fieldName() {
		return fieldName;
	}

	public String columnName() {
		return columnName;
	}

	public int columnIndex() {
		return columnIdx;
	}

	public Class<?> type() {
		return fieldType;
	}

	public ColumnType sqliteType() {
		return type;
	}
	/** 
	 * Returns also the columnName of the attribute
	 */
	@Override
	public String toString() {
		return columnName();
	}

	public Object getValueFromCursor(final Cursor originalCursor) {
		if (originalCursor.isNull(columnIdx) && !isPrimitiveField()) {
			return null;
		}
		return getNonNullValueFromCursor(originalCursor);
	}

	protected abstract Object getNonNullValueFromCursor(final Cursor originalCursor);

	private final boolean isPrimitiveField() {
		Class<?> paramType = type();
		return paramType.equals(java.lang.Boolean.TYPE) //
				|| paramType.equals(java.lang.Integer.TYPE) //
				|| paramType.equals(java.lang.Character.TYPE) //
				|| paramType.equals(java.lang.Float.TYPE) //
				|| paramType.equals(java.lang.Double.TYPE) //
				|| paramType.equals(java.lang.Long.TYPE) //
				|| paramType.equals(java.lang.Short.TYPE);
	}
}
