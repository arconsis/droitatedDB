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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import android.os.Parcel;
import android.os.Parcelable;

import com.arconsis.android.datarobot.entity.Relationship;

/**
 * Provides the possibility to wrap a data object into a {@link Parcel}.<br>
 * It only wraps primitive and String values of the given object.<br>
 * <b>Custom classes within the given object are not written to the {@link Parcel}!</b>
 * 
 * @author Alexander Frank
 * @author Falk Appel
 * @param <E>
 *            Type of the data object to wrap
 */
public class FlatEntityParcelable<E> implements Parcelable {

	private final E data;
	private final Field[] fields;

	private static final String SKIPPED = "__##SKIPPED##__";

	public static final Creator<?> CREATOR = new Creator<Object>() {
		@Override
		public Object createFromParcel(final Parcel source) {
			return new FlatEntityParcelable<Object>(source);
		}

		@Override
		public Object[] newArray(final int size) {
			return new FlatEntityParcelable[size];
		}
	};

	public FlatEntityParcelable(final E data) {
		assertNonArgsContructor(data);
		this.data = data;
		fields = data.getClass().getDeclaredFields();
	}

	private void assertNonArgsContructor(final E data) {
		try {
			data.getClass().getConstructor();
		} catch (NoSuchMethodException e) {
			throw new IllegalArgumentException("The given object has to have a non-args constructor");
		}
	}

	@SuppressWarnings("unchecked")
	public FlatEntityParcelable(final Parcel in) {
		String className = in.readString();
		try {
			Class<?> entityClass = Class.forName(className);
			Constructor<?> constructor = entityClass.getConstructor();
			Object instance = constructor.newInstance();
			fields = entityClass.getDeclaredFields();
			for (Field field : fields) {
				Object value = in.readValue(Thread.currentThread().getContextClassLoader());
				if (!SKIPPED.equals(value)) {
					field.setAccessible(true);
					field.set(instance, value);
				}
			}
			data = (E) instance;
		} catch (Exception e) {
			throw new IllegalStateException("Couldn't recreate @Entity from given Parcel data", e);
		}
	}

	/**
	 * Retrieve the wrapped data object.<br>
	 * It is possible that the returned object is not the same instance as the one you provided, when marshalling
	 * occurred during transfer.
	 * 
	 * @return The wrapped data object
	 */
	public E getEntity() {
		return data;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(data.getClass().getCanonicalName());
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				if (field.getAnnotation(Relationship.class) == null && isPrimitive(field)) {
					dest.writeValue(field.get(data));
				} else {
					dest.writeValue(SKIPPED);
				}
			} catch (Exception e) {
				throw new IllegalStateException("Couldn't convert entity into Parcelable", e);
			}
		}
	}

	private boolean isPrimitive(final Field field) {
		Class<?> fieldType = field.getType();
		if (fieldType.equals(java.lang.Boolean.class) || fieldType.equals(boolean.class)) {
			return true;
		} else if (fieldType.equals(java.lang.Integer.class) || fieldType.equals(int.class)) {
			return true;
		} else if (fieldType.equals(java.lang.Character.class) || fieldType.equals(char.class)) {
			return true;
		} else if (fieldType.equals(byte[].class)) {
			return true;
		} else if (fieldType.equals(java.lang.Float.class) || fieldType.equals(float.class)) {
			return true;
		} else if (fieldType.equals(java.lang.Double.class) || fieldType.equals(double.class)) {
			return true;
		} else if (fieldType.equals(java.lang.Long.class) || fieldType.equals(long.class)) {
			return true;
		} else if (fieldType.equals(java.lang.Short.class) || fieldType.equals(short.class)) {
			return true;
		} else if (fieldType.equals(String.class)) {
			return true;
		}
		return false;
	}
}
