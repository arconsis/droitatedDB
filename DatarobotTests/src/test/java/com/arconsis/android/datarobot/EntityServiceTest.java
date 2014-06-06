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

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.arconsis.android.datrobot.test.data.BidirectionalOne;
import com.arconsis.android.datrobot.test.data.BidirectionalTwo;
import com.arconsis.android.datrobot.test.data.Non;
import com.arconsis.android.datrobot.test.data.Simple;
import com.arconsis.android.datrobot.test.data.StageOne;
import com.arconsis.android.datrobot.test.data.StageTwo;
import com.arconsis.android.datrobot.test.data.WithAssociation;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = BasePersistenceTest.MANIFEST)
public class EntityServiceTest {

	private Context context;
	private DbCreator dbCreator;
	private SQLiteDatabase db;

	@Before
	public void init() {
		Cursor cursor = mock(Cursor.class);
		when(cursor.getCount()).thenReturn(1);
		when(cursor.getInt(0)).thenReturn(1);
		when(cursor.getString(1)).thenReturn("Test");

		db = mock(SQLiteDatabase.class);
		when(db.insertOrThrow(any(String.class), any(String.class), any(ContentValues.class))).thenReturn(1L);
		when(db.query(any(String.class), any(String[].class), any(String.class), any(String[].class), any(String.class), any(String.class), any(String.class)))
		.thenReturn(cursor);

		dbCreator = mock(DbCreator.class);
		when(dbCreator.getWritableDatabase()).thenReturn(db);

		context = mock(Context.class);
		when(context.getPackageName()).thenReturn("com.arconsis.android.datrobot.test.data");
	}

	@Test(expected = IllegalArgumentException.class)
	public void execptionWhenTryingToSaveNonEntity() {
		EntityService<Non> entityService = new EntityService<Non>(context, Non.class);
		entityService.save(new Non());
	}


	@Test
	public void saveNewEntityWithAssociationFieldButSetToNull() {
		WithAssociation emptyAssociation = new WithAssociation(null, "Test", null);

		EntityService<WithAssociation> entityService = new EntityService<WithAssociation>(context, WithAssociation.class, dbCreator);
		int id = entityService.save(emptyAssociation);

		assertThat(id).isEqualTo(1);
		assertThat(emptyAssociation.getId()).isEqualTo(1);
	}

	@Test
	public void savingNewEntityWithDirectedAssociation() {
		Simple testEntity = new Simple(null, "foo");
		WithAssociation withAssociation = new WithAssociation(null, "Test", testEntity);

		EntityService<WithAssociation> entityService = new EntityService<WithAssociation>(context, WithAssociation.class, dbCreator);
		int id = entityService.save(withAssociation);

		assertThat(id).isEqualTo(1);
		assertThat(withAssociation.getId()).isEqualTo(1);
		assertThat(testEntity.getId()).isEqualTo(1);
		verify(db, times(2)).insertOrThrow(any(String.class), any(String.class), any(ContentValues.class));
	}

	@Test
	public void updateEntityWithDirectedAsscociation() {
		Simple testEntity = new Simple(null, "foo");
		WithAssociation withAssociation = new WithAssociation(5, "Test", testEntity);

		EntityService<WithAssociation> entityService = new EntityService<WithAssociation>(context, WithAssociation.class, dbCreator);
		int id = entityService.save(withAssociation);

		assertThat(id).isEqualTo(5);
		assertThat(withAssociation.getId()).isEqualTo(5);
		assertThat(testEntity.getId()).isEqualTo(1);
		verify(db, times(1)).update(any(String.class), any(ContentValues.class), any(String.class), any(String[].class));
		verify(db, times(1)).insertOrThrow(any(String.class), any(String.class), any(ContentValues.class));
	}

	@Test
	public void savingNewEntitiesWithBidrectionalAssociation() {
		BidirectionalOne entityOne = new BidirectionalOne(null, "one");
		BidirectionalTwo entityTwo = new BidirectionalTwo(null, "two");
		entityOne.setEntity(entityTwo);
		entityTwo.setEntity(entityOne);

		EntityService<BidirectionalOne> entityService = new EntityService<BidirectionalOne>(context, BidirectionalOne.class, dbCreator);
		int id = entityService.save(entityOne);


		assertThat(id).isEqualTo(1);
		assertThat(entityOne.getId()).isEqualTo(1);
		assertThat(entityTwo.getId()).isEqualTo(1);
		verify(db, times(2)).insertOrThrow(any(String.class), any(String.class), any(ContentValues.class));
	}

	@Test
	public void savingEntityWithObjectGraphDepthOfZero() {
		Simple testEntity = new Simple(null, "foo");
		WithAssociation withAssociation = new WithAssociation(null, "Test", testEntity);

		EntityService<WithAssociation> entityService = new EntityService<WithAssociation>(context, WithAssociation.class, dbCreator);
		int id = entityService.save(withAssociation, 0);

		assertThat(id).isEqualTo(1);
		assertThat(withAssociation.getId()).isEqualTo(1);
		assertThat(testEntity.getId()).isNull();
		verify(db, times(1)).insertOrThrow(any(String.class), any(String.class), any(ContentValues.class));
	}

	@Test
	public void savingObjectWithDepthOfOne() {
		Simple simple = new Simple(null, "test");
		StageTwo two = new StageTwo(null, "two");
		two.setEntity(simple);
		StageOne one = new StageOne(null, "one");
		one.setEntity(two);

		EntityService<StageOne> entityService = new EntityService<StageOne>(context, StageOne.class, dbCreator);
		int id = entityService.save(one, 1);

		assertThat(id).isEqualTo(1);
		assertThat(one.getId()).isEqualTo(1);
		assertThat(two.getId()).isEqualTo(1);
		assertThat(simple.getId()).isNull();
		verify(db, times(2)).insertOrThrow(any(String.class), any(String.class), any(ContentValues.class));
	}

}
