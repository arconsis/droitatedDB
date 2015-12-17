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

import android.content.Context;
import android.database.Cursor;

import org.droitateddb.DbCreator;
import org.droitateddb.schema.AbstractAttribute;
import org.droitateddb.schema.EntityInfo;
import org.droitateddb.schema.SchemaConstants;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.droitateddb.Utilities.handle;

/**
 * @param <T> Entity type represented within the Cursor
 * @author Falk Appel
 * @author Alexander Frank
 */
public class CombinedCursorImpl<T> extends ProxyableCursor implements CombinedCursor<T> {

	@SuppressWarnings("unchecked")
	public static final <C> CombinedCursor<C> create(Context context, final Cursor originalCursor, final EntityInfo entityInfo, final Class<C> entityClass) {
		try {
			Class<?> definition = entityInfo.definition();
			Field dbAttributes = definition.getDeclaredField(SchemaConstants.ATTRIBUTES);
			dbAttributes.setAccessible(true);
			AbstractAttribute[] attributes = (AbstractAttribute[]) dbAttributes.get(null);
			final Context appContext = context.getApplicationContext();

			final CombinedCursorImpl<C> magicCursor = new CombinedCursorImpl<C>(originalCursor, entityClass, attributes);
			InvocationHandler handler = new InvocationHandler() {

				@Override
				public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
					Class<?>[] argTypes;
					if (args == null) {
						argTypes = new Class<?>[0];
					} else {
						argTypes = ReflectionUtil.getArgTypes(args);
					}

					try {
						if (ReflectionUtil.isCloseMethod(method)) {
							method.invoke(originalCursor, args);
							DbCreator.getInstance(appContext).reduceDatabaseConnection();
							return null;
						} else if (ReflectionUtil.isMethodOfType(method, argTypes, Cursor.class)) {
							return method.invoke(originalCursor, args);
						} else if (ReflectionUtil.isMethodOfType(method, argTypes, ObjectCursor.class)) {
							return method.invoke(magicCursor, args);
						} else {
							throw new UnsupportedOperationException("Unexpected method call for " + method.toString());
						}
					} catch (InvocationTargetException ite) {
						throw ite.getTargetException();
					}
				}

			};
			return (CombinedCursor<C>) Proxy.newProxyInstance(CombinedCursorImpl.class.getClassLoader(), new Class<?>[]{CombinedCursor.class}, handler);
		} catch (Exception e) {
			throw handle(e);
		}
	}

	private final AbstractAttribute[] attributes;
	private final Class<T>            entityClass;

	private final Cursor originalCursor;

	private CombinedCursorImpl(final Cursor originalCursor, final Class<T> entityClass, final AbstractAttribute[] attributes) {
		this.originalCursor = originalCursor;
		this.entityClass = entityClass;
		this.attributes = attributes;
	}

	private void assertEmptyCursor() {
		if (originalCursor.getCount() == 0) {
			throw new NoSuchElementException();
		}
	}

	private T construct() {
		try {
			Constructor<T> constructor = entityClass.getConstructor();
			T instance = constructor.newInstance();
			for (AbstractAttribute attribute : attributes) {
				Field field = entityClass.getDeclaredField(attribute.fieldName());
				field.setAccessible(true);

				field.set(instance, attribute.getValueFromCursor(originalCursor));
			}
			return instance;
		} catch (Exception e) {
			throw handle(e);
		}
	}

	@Override
	public Collection<T> getAll() {
		moveBeforeFirst();
		Collection<T> instances = new ArrayList<T>(originalCursor.getCount());
		while (originalCursor.moveToNext()) {
			instances.add(construct());
		}
		return instances;
	}

	private void moveBeforeFirst() {
		originalCursor.moveToFirst();
		originalCursor.moveToPrevious();
	}

	@Override
	public T getCurrent() {
		assertEmptyCursor();

		if (originalCursor.isBeforeFirst()) {
			originalCursor.moveToFirst();
		}
		if (originalCursor.isAfterLast()) {
			originalCursor.moveToLast();
		}
		return construct();
	}

	@Override
	public T getFirst() {
		assertEmptyCursor();
		originalCursor.moveToFirst();
		return construct();
	}

	@Override
	public T getLast() {
		assertEmptyCursor();
		originalCursor.moveToLast();
		return construct();
	}

	@Override
	public T getNext() {
		assertEmptyCursor();
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next element in cursor!");
		}
		originalCursor.moveToNext();
		return construct();
	}

	@Override
	public Collection<T> getNext(final int amount) {
		assertEmptyCursor();
		if (!hasNext()) {
			throw new NoSuchElementException("There is no next element in cursor!");
		}
		Collection<T> instances = new ArrayList<T>(originalCursor.getCount());
		int count = 0;
		while (originalCursor.moveToNext() && count < amount) {
			if (!originalCursor.isAfterLast()) {
				instances.add(construct());
				count++;
			}
		}
		return instances;
	}

	@Override
	public T getOne() {
		assertEmptyCursor();
		if (originalCursor.getCount() > 1) {
			throw new IllegalStateException("Expected only one element in cursor but there were " + originalCursor.getCount() + "!");
		}
		originalCursor.moveToFirst();
		return construct();
	}

	@Override
	public T getPrevious() {
		assertEmptyCursor();
		if (!hasPrevious()) {
			throw new NoSuchElementException("There is no previos element in cursor!");
		}
		originalCursor.moveToPrevious();
		return construct();
	}

	@Override
	public Collection<T> getPrevious(final int amount) {
		assertEmptyCursor();
		if (!hasPrevious()) {
			throw new NoSuchElementException("There is no next element in cursor!");
		}
		Collection<T> instances = new ArrayList<T>(originalCursor.getCount());
		int count = 0;
		while (originalCursor.moveToPrevious() && count < amount) {
			if (!originalCursor.isBeforeFirst()) {
				instances.add(construct());
				count++;
			}
		}
		return instances;
	}

	@Override
	public boolean hasNext() {
		return originalCursor.getCount() > 0 && !(originalCursor.isLast() || originalCursor.isAfterLast());
	}

	@Override
	public boolean hasPrevious() {
		return originalCursor.getCount() > 0 && !(originalCursor.isFirst() || originalCursor.isBeforeFirst());
	}

	@Override
	public Iterator<T> iterator() {
		moveBeforeFirst();
		return new Iterator<T>() {

			@Override
			public boolean hasNext() {
				return CombinedCursorImpl.this.hasNext();
			}

			@Override
			public T next() {
				return getNext();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public int size() {
		return originalCursor.getCount();
	}
}