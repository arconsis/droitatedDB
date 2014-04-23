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

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;
import android.database.CursorWrapper;

import com.arconsis.android.datarobot.cursor.ObjectCursor;

/**
 * Provides utility methods for cursors.
 * 
 * @author Falk Appel
 * @author Alexander Frank
 */
public class CursorUtil {

	/**
	 * Convert a standard Android {@link Cursor} into a {@link ObjectCursor}.<br>
	 * This is only possible for {@link Cursor}s that where queried over a {@link ContentResolver} from a
	 * {@link ContentProvider} derived by {@link BaseContentProvider}.
	 *
	 * @param cursor
	 *            Android {@link Cursor}
	 * @return The {@link ObjectCursor} representation of the given Android {@link Cursor}
	 *
	 * @throws NullPointerException
	 *             When the given cursor is null
	 *
	 * @throws IllegalArgumentException
	 *             When the given cursor is not of the type {@link CursorWrapper}, which will be returned by a
	 *             {@link ContentResolver}
	 *
	 * @throws IllegalStateException
	 *             When the wrapped cursor within the given cursor is not of type {@link ObjectCursor}. This indicates
	 *             that the given cursor was not queried from a derivation {@link BaseContentProvider}
	 */
	@SuppressWarnings("unchecked")
	public static <T> ObjectCursor<T> getObjectCursor(final Cursor cursor) {
		if (cursor == null) {
			throw new NullPointerException("The given cursor is null");
		}
		if (!(cursor instanceof CursorWrapper)) {
			throw new IllegalArgumentException("The given cursor is not of type " + CursorWrapper.class.getCanonicalName() + ". It has type "
					+ cursor.getClass().getCanonicalName() + ". Was it queried with a ContentResolver?");
		}

		CursorWrapper wrapper = (CursorWrapper) cursor;
		Cursor wrappedCursor = wrapper.getWrappedCursor();

		if (!(wrappedCursor instanceof ObjectCursor)) {
			throw new IllegalStateException("The wrapped cursor of the given CursorWrapper is not of type " + ObjectCursor.class.getCanonicalName()
					+ ". It has type " + wrappedCursor.getClass().getCanonicalName()
					+ ". Was it queried over a ContentResolver from BaseContentProvider derived ContentProvider?");
		}
		return (ObjectCursor<T>) wrappedCursor;
	}

}
