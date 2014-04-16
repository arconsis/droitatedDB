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
package com.arconsis.android.datarobot.cursor;

import java.io.Closeable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.database.Cursor;

import com.arconsis.android.datarobot.BaseContentProvider;
import com.arconsis.android.datarobot.CursorUtil;

/**
 * Cursor for objects of type <code>T</code> which works on the original {@link Cursor}. <br>
 * If the position of the original {@link Cursor} is changed, the position of the {@code ObjectCursor} is also changed
 * and vice versa.<br>
 * <br>
 * Resolve the {@code ObjectCursor} with an {@link CursorUtil#getObjectCursor(Cursor cursor)} from a {@link Cursor}
 * resolved with a {@link ContentResolver} from a {@link ContentProvider} derived by {@link BaseContentProvider}.
 * 
 * @author Falk Appel
 * @author Alexander Frank
 * 
 * @param <T>
 *            Type of the entity.
 */
public interface ObjectCursor<T> extends Iterable<T>, Closeable {

	/**
	 * @return a new mutable {@code Collection} containing all elements contained in this {@code ObjectCursor}. If the
	 *         {@code ObjectCursor} is empty an empty {@code Collection} is returned.
	 */
	Collection<T> getAll();

	/**
	 * @return the element at the current position of the {@code ObjectCursor}.
	 * 
	 *         If the {@link Cursor} is before the first element, the position is changed to the first position, if the
	 *         {@link Cursor} is after the last element, the position is changed to the last elements position
	 * 
	 * @throws NoSuchElementException
	 *             if there are no elements to be returned.
	 */
	T getCurrent();

	/**
	 * @return the element at the first position of the {@code ObjectCursor}.
	 * 
	 *         The {@link Cursor} is moved to the first position.
	 * 
	 * @throws NoSuchElementException
	 *             if there is no element to be returned.
	 */
	T getFirst();

	/**
	 * @return the element at the last position of the {@code ObjectCursor}.
	 * 
	 *         The {@link Cursor} is moved to the last position.
	 * 
	 * @throws NoSuchElementException
	 *             if there is no element to be returned.
	 */
	T getLast();

	/**
	 * Returns the element at the next position of the {@code ObjectCursor}.
	 *
	 * The {@link Cursor} is moved to the next position.
	 *
	 * @throws NoSuchElementException
	 *             if there is no element to be returned.
	 * @see #hasNext
	 */
	T getNext();

	/**
	 * @return a new mutable {@code Collection} containing the next <code>amount</code> elements contained in this
	 *         {@code ObjectCursor}. If the {@code ObjectCursor} contains less than <code>amount</code> elements
	 *         following the current position a collection with less than <code>amount</code> elements is returned.
	 * 
	 * @throws NoSuchElementException
	 *             if there are no next elements at all
	 */
	Collection<T> getNext(int amount);

	/**
	 * @return the element at the first position of the {@code ObjectCursor}. It is expected, that the {@link Cursor}
	 *         contains exactly one element.
	 * 
	 * @throws NoSuchElementException
	 *             if there is no element to be returned.
	 * @throws IllegalStateException
	 *             if the {@link Cursor} contains more than one element
	 */
	T getOne();

	/**
	 * @return the element at the position before the current position of the {@code ObjectCursor}.
	 * 
	 *         The {@link Cursor} is moved to the previous position.
	 * 
	 * @throws NoSuchElementException
	 *             if there is no element to be returned.
	 * @see #hasPrevious
	 */
	T getPrevious();

	/**
	 * @return a new mutable {@code Collection} containing the previous <code>amount</code> elements contained in this
	 *         {@code ObjectCursor}. If the {@code ObjectCursor} contains less than <code>amount</code> elements
	 *         preceding the current position a collection with less than <code>amount</code> elements is returned.
	 * 
	 * @throws NoSuchElementException
	 *             if there are no preceding elements at all
	 */
	Collection<T> getPrevious(int amount);

	/**
	 * @return <code>true</code> if there is at least one more element after the current position, <code>false</code>
	 *         otherwise.
	 * 
	 * @see #getNext
	 */
	boolean hasNext();

	/**
	 * @return <code>true</code> if there is at least one more element before the current position, <code>false</code>
	 *         otherwise.
	 * 
	 * @see #getPrevious
	 */
	boolean hasPrevious();

	/**
	 * @return an {@link Iterator} which does not support the {@link Iterator#remove()} operation.
	 * 
	 * @see java.lang.Iterable#iterator()
	 */
	@Override
	public Iterator<T> iterator();

	/**
	 * @return a count of how many elements this {@code ObjectCursor} contains.
	 */
	int size();

	/**
	 * @return <code>true</code> if this {@code ObjectCursor} is closed, otherwise <code>false</code>;
	 */
	boolean isClosed();

	/**
	 * @return the current position of the cursor.
	 */
	int getPosition();

	/**
	 * Move the cursor to an absolute position.
	 * 
	 * @return <code>true</code> if the move was succesful.
	 */
	boolean moveToPosition(int position);

	/**
	 * Move the cursor to the first row.
	 * 
	 * @return <code>true</code> if the move was succesful.
	 */
	boolean moveToFirst();

	/**
	 * Move the cursor to the last row.
	 * 
	 * @return <code>true</code> if the move was succesful.
	 */
	boolean moveToLast();

	/**
	 * Move the cursor to the next row.
	 * 
	 * @return <code>true</code> if the move was succesful.
	 */
	boolean moveToNext();

	/**
	 * Move the cursor to the previous row.
	 * 
	 * @return <code>true</code> if the move was succesful.
	 */
	boolean moveToPrevious();
}
