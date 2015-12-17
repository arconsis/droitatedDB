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
package org.droitateddb;

import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import org.droitateddb.test.data.Comment;
import org.droitateddb.test.data.SimpleWithoutAutoIncrement;

/**
 * @author Falk Appel
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = BasePersistenceTest.MANIFEST)
public class WithoutAutoIncrementTest extends BasePersistenceTest {
	@Test
	public void insertAndReadOk() {
		EntityService<SimpleWithoutAutoIncrement> entityService = entityService(SimpleWithoutAutoIncrement.class);

		SimpleWithoutAutoIncrement newObject = new SimpleWithoutAutoIncrement();
		newObject.setId(1234);
		newObject.setMyString("woohoo");
		entityService.save(newObject);
		SimpleWithoutAutoIncrement resolvedObject = entityService.get(1234);
		assertSameFields(newObject, resolvedObject);
	}

	@Test(expected=IllegalStateException.class)
	public void insertWithoutIdFails() {
		EntityService<SimpleWithoutAutoIncrement> entityService = entityService(SimpleWithoutAutoIncrement.class);
		SimpleWithoutAutoIncrement newObject = new SimpleWithoutAutoIncrement();
		newObject.setMyString("failing");
		entityService.save(newObject);
	}

	@Test
	public void saveWithDefinedDepthDoesntWipetoOneRelation() {
		EntityService<SimpleWithoutAutoIncrement> entityService = entityService(SimpleWithoutAutoIncrement.class);

		SimpleWithoutAutoIncrement newObject = new SimpleWithoutAutoIncrement();
		newObject.setId(1234);
		newObject.setMyString("woohoo");
		Comment c = new Comment("come");
		newObject.setComment(c);
		entityService.save(newObject);
		SimpleWithoutAutoIncrement resolvedObject = entityService.get(1234);
		assertSameFields(newObject, resolvedObject);
		Assertions.assertThat(resolvedObject.getComment()).isNull();
		entityService.resolveAssociations(resolvedObject);
		Assertions.assertThat(resolvedObject.getComment()).isNotNull();

		entityService.save(resolvedObject, 0);
		resolvedObject = entityService.get(1234);
		Assertions.assertThat(resolvedObject.getComment()).isNull();
		entityService.resolveAssociations(resolvedObject);
		Assertions.assertThat(resolvedObject.getComment()).isNotNull();
	}
}
