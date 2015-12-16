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

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;

import com.arconsis.android.datarobot.cursor.ObjectCursor;
import com.arconsis.android.datrobot.test.data.Comment;
import com.arconsis.android.datrobot.test.data.Single;
import com.arconsis.android.datrobot.test.data.generated.CommentContentProvider;
import com.arconsis.android.datrobot.test.data.generated.DB;
import com.arconsis.android.datrobot.test.data.generated.SingleContentProvider;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = BasePersistenceTest.MANIFEST, shadows = { CustomSQLiteShadow.class })
public class ContentProviderTest extends BasePersistenceTest {


	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		CommentContentProvider provider = new CommentContentProvider();
		provider.onCreate();
		ShadowContentResolver.registerProvider("com.arconsis.android.datrobot.test.data.generated.provider.comment", provider);
		SingleContentProvider p = new SingleContentProvider();
		p.onCreate();
		ShadowContentResolver.registerProvider("com.arconsis.android.datrobot.test.data.generated.provider.single", p);

	}

	@Test
	public void testURIS() {
		Uri uriForItem = CommentContentProvider.uriForItem(DB.CommentTable.TABLE_NAME, 1);
		assertThat(uriForItem.toString()).isEqualTo("content://com.arconsis.android.datrobot.test.data.generated.provider.comment/comment/1");
		Uri uri = CommentContentProvider.uri(DB.CommentTable.TABLE_NAME);
		assertThat(uri.toString()).isEqualTo("content://com.arconsis.android.datrobot.test.data.generated.provider.comment/comment");
	}

	@Test
	public void crudTest() {

		ContentResolver resolver = context.getContentResolver();
		Comment c1 = new Comment("asdf");
		EntityService<Comment> entityService = entityService(Comment.class);

		entityService.save(c1);
		assertThat(entityService.get()).hasSize(1);
		Uri uri = CommentContentProvider.uri(DB.CommentTable.TABLE_NAME);

		// create
		ContentValues values = new ContentValues();
		values.put("name", "aName");
		Uri itemLocation = resolver.insert(uri, values);
		assertThat("content://com.arconsis.android.datrobot.test.data.generated.provider.comment/comment/2").isEqualTo(itemLocation.toString());
		assertThat(entityService.get()).hasSize(2);
		assertThat(entityService.get(2).getName()).isEqualTo("aName");

		// update
		values.put("name", "otherName");
		int update = resolver.update(itemLocation, values, null, null);
		assertThat(1).isEqualTo(update);
		assertThat(entityService.get(2).getName()).isEqualTo("otherName");

		// read
		Cursor cursor = resolver.query(itemLocation, DB.CommentTable.PROJECTION, null, null, null);
		assertThat(cursor).isNotNull();
		// Wrap to unwrap ... this is a bit strange, normally Android wraps the Cursor but robolectric doesn't!
		CursorWrapper wrapped = new CursorWrapper(cursor);
		ObjectCursor<Comment> objectCursor = CursorUtil.getObjectCursor(wrapped);

		assertThat(objectCursor.size()).isEqualTo(1);
		Comment curserloadedObject = objectCursor.getOne();
		assertThat(curserloadedObject.getName()).isEqualTo("otherName");
		assertSameFields(curserloadedObject, entityService.get(curserloadedObject.getId()));

		// read all
		Cursor nextCursor = resolver.query(uri, DB.CommentTable.PROJECTION, null, null, null);
		assertThat(nextCursor).isNotNull();
		// Wrap to unwrap ... this is a bit strange, normally Android wraps the Cursor but robolectric doesn't!
		CursorWrapper nextWrapped = new CursorWrapper(nextCursor);
		ObjectCursor<Comment> allObjectCursor = CursorUtil.getObjectCursor(nextWrapped);

		assertThat(allObjectCursor.size()).isEqualTo(2);

		// delete
		int delete = resolver.delete(itemLocation, null, null);
		assertThat(entityService.get()).hasSize(1);
		assertThat(1).isEqualTo(delete);

	}

	@Test(expected = IllegalStateException.class)
	public void wrongTablenameThrowsException() {
		CommentContentProvider.uri("fails");
	}

	@Test
	public void cursorTest() {
		// create some data
		Single c1 = new Single("c1");
		Single c2 = new Single("c2");
		Single c3 = new Single("c3");
		ArrayList<Single> objects = newArrayList(c1, c2, c3);


		EntityService<Single> service = entityService(Single.class);
		service.save(objects);
		Cursor cursor = getSingleCursor();
		ObjectCursor<Single> objectCursor = CursorUtil.getObjectCursor(cursor);

		assertThat(objectCursor.size()).isEqualTo(3);
		assertThat(cursor.isBeforeFirst()).isTrue();
		assertThat(objectCursor.getCurrent()).isNotNull();
		assertThat(cursor.isFirst()).isTrue();
		assertThat(objectCursor.getLast()).isNotNull();
		assertThat(cursor.isLast()).isTrue();
		assertThat(objectCursor.getFirst()).isNotNull();
		assertThat(cursor.isFirst()).isTrue();

		assertThat(objectCursor.size()).isEqualTo(3);
		Collection<Single> all = newArrayList(objectCursor.getAll());
		assertThat(all).contains(c1, c2, c3);

		int i = 0;
		for (Single s : objectCursor) {
			assertThat(s).isIn(objects);
			i++;
		}
		assertThat(i).isEqualTo(3);

		cursor.moveToFirst();
		cursor.moveToPrevious();

		assertThat(objectCursor.hasNext()).isTrue();
		assertThat(objectCursor.hasPrevious()).isFalse();
		Single current = objectCursor.getCurrent();
		assertThat(current).isIn(objects);
		assertThat(objectCursor.getCurrent()).isEqualTo(current);
		assertThat(objectCursor.getNext()).isIn(objects);
		assertThat(objectCursor.hasNext()).isTrue();
		assertThat(objectCursor.hasPrevious()).isTrue();
		assertThat(objectCursor.getNext()).isIn(objects);
		assertThat(objectCursor.hasNext()).isFalse();
		assertThat(objectCursor.hasPrevious()).isTrue();
		assertThat(objectCursor.getPrevious()).isIn(objects);
		assertThat(objectCursor.hasNext()).isTrue();
		assertThat(objectCursor.hasPrevious()).isTrue();
		Collection<Single> previous5 = objectCursor.getPrevious(5);
		assertThat(previous5).contains(current);
		assertThat(previous5.size()).isEqualTo(1);
		Collection<Single> next5 = objectCursor.getNext(5);
		assertThat(next5).contains(c1, c2, c3);
		assertThat(next5.size()).isEqualTo(3);

	}

	@Test
	public void emptyCursor() {
		ObjectCursor<Single> objectCursor = getObjectCursor();
		assertThat(objectCursor.hasNext()).isFalse();
		assertThat(objectCursor.hasPrevious()).isFalse();
	}

	@Test(expected = NoSuchElementException.class)
	public void emptyCursorGetFirstFails() {
		ObjectCursor<Single> objectCursor = getObjectCursor();
		objectCursor.getFirst();
	}

	@Test(expected = NoSuchElementException.class)
	public void emptyCursorGetLastFails() {
		ObjectCursor<Single> objectCursor = getObjectCursor();
		objectCursor.getLast();
	}

	@Test(expected = NoSuchElementException.class)
	public void emptyCursorGetCurrentFails() {
		ObjectCursor<Single> objectCursor = getObjectCursor();
		objectCursor.getCurrent();
	}

	@Test(expected = NoSuchElementException.class)
	public void emptyCursorGetNextFails() {
		ObjectCursor<Single> objectCursor = getObjectCursor();
		objectCursor.getNext();
	}

	@Test(expected = NoSuchElementException.class)
	public void emptyCursorGetPreviosFails() {
		ObjectCursor<Single> objectCursor = getObjectCursor();
		objectCursor.getPrevious();
	}

	@Test(expected = NoSuchElementException.class)
	public void getNextFailsIfUnavailable() {
		createOnObject();
		ObjectCursor<Single> objectCursor = getObjectCursor();
		objectCursor.getNext();
		objectCursor.getNext();
	}

	@Test(expected = NoSuchElementException.class)
	public void getPreviosFailsIfUnavailable() {
		createOnObject();
		ObjectCursor<Single> objectCursor = getObjectCursor();
		objectCursor.getPrevious();
	}

	@Test(expected = IllegalStateException.class)
	public void getOneFailsOnCursorWith() {
		Single s1 = new Single("s1");
		Single s2 = new Single("s2");
		EntityService<Single> service = entityService(Single.class);
		service.save(s1);
		service.save(s2);
		ObjectCursor<Single> objectCursor = getObjectCursor();
		objectCursor.getOne();
	}

	@Test
	public void getCurrentReturnsLastIfAfterLast() {
		Single s1 = new Single("s1");
		Single s2 = new Single("s2");
		EntityService<Single> service = entityService(Single.class);
		service.save(s1);
		service.save(s2);
		Cursor cursor = getSingleCursor();

		ObjectCursor<Single> objectCursor = CursorUtil.getObjectCursor(cursor);

		cursor.moveToLast();
		cursor.moveToNext();
		assertThat(objectCursor.getCurrent()).isEqualTo(objectCursor.getLast());

	}

	private ObjectCursor<Single> getObjectCursor() {
		Cursor wrapped = getSingleCursor();
		ObjectCursor<Single> objectCursor = CursorUtil.getObjectCursor(wrapped);
		return objectCursor;
	}

	private Cursor getSingleCursor() {
		Cursor cursor = context.getContentResolver().query(CommentContentProvider.uri(DB.SingleTable.TABLE_NAME), DB.SingleTable.PROJECTION, null, null, null);
		// Wrap to unwrap ... this is a bit strange, normally Android wraps the Cursor but robolectric doesn't!
		CursorWrapper wrapped = new CursorWrapper(cursor);
		return wrapped;
	}

	private void createOnObject() {
		Single s1 = new Single("s1");
		EntityService<Single> service = entityService(Single.class);
		service.save(s1);
	}
}
