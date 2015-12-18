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
package org.droitateddb;

import org.droitateddb.test.data.Non;
import org.droitateddb.test.data.Simple;
import org.droitateddb.test.data.StageOne;
import org.droitateddb.test.data.StageTwo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = BasePersistenceTest.MANIFEST)
public class EntityServiceWithRealSQLiteDatabaseTest extends BasePersistenceTest {

    private static final String ONE_STRING = "asdf";

    @Test
    public void saveAndRetrieveSimple() {
        EntityService<Simple> entityService = entityService(Simple.class);
        Simple data = new Simple(null, ONE_STRING);
        long id = entityService.save(data);

        Simple savedSimple = entityService.get(id);

        assertThat(savedSimple).isNotNull();
        assertThat(savedSimple.getBigDouble()).isNull();
        assertThat(savedSimple.getBigFloat()).isNull();
        assertThat(savedSimple.getBigLong()).isNull();
        assertThat(savedSimple.getSomeBytes()).isNull();
        assertThat(savedSimple.getMyDate()).isNull();
        assertThat(savedSimple.getMyDouble()).isEqualTo(0d);
        assertThat(savedSimple.getMyFloat()).isEqualTo(0f);
        assertThat(savedSimple.getMyInt()).isEqualTo(0);
        assertThat(savedSimple.getSoLong()).isEqualTo(0l);
        assertThat(savedSimple).isNotSameAs(data);
        assertThat(savedSimple.getId()).isEqualTo((int) id);
        assertThat(savedSimple.getMyString()).isEqualTo(ONE_STRING);
        assertInsertsAndUpdatesAmountToDB(1, 0);
    }

    @Test
    public void saveAndRetrieveSimpleWithAllAttributes() {
        EntityService<Simple> entityService = entityService(Simple.class);
        Simple data = new Simple(null, ONE_STRING);
        data.setBigDouble(00d);
        data.setBigFloat(2.3f);
        data.setBigLong(4l);
        data.setBigBoolean(true);
        data.setMyBoolean(true);
        data.setMyDate(new Date());
        data.setMyDouble(4d);
        data.setMyFloat(5.4f);
        data.setMyInt(3);
        data.setSoLong(567l);
        data.setSomeBytes("someBytes".getBytes());

        long id = entityService.save(data);
        assertInsertsAndUpdatesAmountToDB(1, 0);

        Simple savedSimple = entityService.get(id);
        assertThat(savedSimple).isNotNull();
        assertThat(savedSimple).isNotSameAs(data);
        assertSameFields(data, savedSimple);

        entityService.resolveAssociations(savedSimple, 0);
        entityService.resolveAssociations(savedSimple, Integer.MAX_VALUE);
        entityService.resolveAssociations(savedSimple, Integer.MIN_VALUE);
        entityService.resolveAssociations(savedSimple);
    }

    @Test
    public void savingAndDeleteSingleNewEntity() {
        Simple testEntity = new Simple(null, "Test");

        EntityService<Simple> entityService = entityService(Simple.class);
        long id = entityService.save(testEntity);

        assertThat(id).isEqualTo(1);
        assertThat(entityService.get()).hasSize(1);
        assertThat(entityService.delete(testEntity)).isTrue();
        assertThat(entityService.get()).hasSize(0);
    }

    @Test
    public void deleteUnknownIdIsFalse() {
        assertThat(entityService(Simple.class).delete(123)).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNoIdFails() {
        assertThat(entityService(Simple.class).delete(new Simple(null, "Test"))).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void entityServiceOnlyForEntities() {
        entityService(Non.class);
    }

    @Test
    public void savingObjectWithDepthOfOne() {
        Simple simple = new Simple(null, "test");
        StageTwo two = new StageTwo(null, "two");
        two.setEntity(simple);
        StageOne one = new StageOne(null, "one");
        one.setEntity(two);

        EntityService<StageOne> entityService = entityService(StageOne.class);
        long id = entityService.save(one, 1);

        assertInsertsAndUpdatesAmountToDB(2, 0);

        StageOne savedStageOne = entityService.get(id);
        assertSameFields(savedStageOne, one);
        assertThat(savedStageOne.getId()).isEqualTo((int) id);
        assertThat(savedStageOne.getEntity()).isNull();

        entityService.resolveAssociations(savedStageOne, 0);
        assertThat(savedStageOne.getId()).isEqualTo((int) id);
        assertThat(savedStageOne.getEntity()).isNull();

        entityService.resolveAssociations(savedStageOne, 1);
        assertThat(savedStageOne.getEntity()).isNotNull();
        assertSameFields(savedStageOne.getEntity(), one.getEntity());

        entityService.resolveAssociations(savedStageOne);
        assertThat(savedStageOne.getEntity()).isNotNull();
        assertSameFields(savedStageOne.getEntity(), one.getEntity());

    }
}
