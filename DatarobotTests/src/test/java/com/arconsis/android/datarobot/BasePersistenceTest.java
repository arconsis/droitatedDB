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

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.arconsis.android.datrobot.test.data.generated.DB;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.robolectric.Robolectric;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class BasePersistenceTest {

    public static final String MANIFEST = "/src/test/resources/AndroidManifest.xml";
    private static final String DROP = "DROP TABLE IF EXISTS ";

    private SQLiteDatabase database;
    protected Context context;
    private final Collection<ConnectedEntityService<?>> services = new ArrayList<ConnectedEntityService<?>>();
    protected DbCreator dbCreator;

    @Before
    public void setUp() throws Exception {
        context = Robolectric.getShadowApplication().getApplicationContext();
        dbCreator = spy(DbCreator.getInstance(context));
        database = spy(dbCreator.getWritableDatabase());
        database.execSQL(DROP + DB.AuthorTable.TABLE_NAME);
        database.execSQL(DROP + DB.BidirectionalOneTable.TABLE_NAME);
        database.execSQL(DROP + DB.StageOneTable.TABLE_NAME);
        database.execSQL(DROP + DB.CommentTable.TABLE_NAME);
        database.execSQL(DROP + DB.WithAssociationTable.TABLE_NAME);
        database.execSQL(DROP + DB.TextTable.TABLE_NAME);
        database.execSQL(DROP + DB.BidirectionalTwoTable.TABLE_NAME);
        database.execSQL(DROP + DB.SimpleTable.TABLE_NAME);
        database.execSQL(DROP + DB.StageTwoTable.TABLE_NAME);
        database.execSQL(DROP + DB.TextAuthorAssociation.TABLE_NAME);
        database.execSQL(DROP + DB.AuthorTextAssociation.TABLE_NAME);
        database.execSQL(DROP + DB.AuthorCommentAssociation.TABLE_NAME);
        database.execSQL(DROP + DB.SingleTable.TABLE_NAME);
        database.execSQL(DROP + DB.SimpleWithoutAutoIncrementTable.TABLE_NAME);
        database.execSQL(DROP + DB.LengthValidatorEntityTable.TABLE_NAME);
        database.execSQL(DROP + DB.MultiValidatorEntityTable.TABLE_NAME);
        database.execSQL(DROP + DB.PatternValidatorEntityTable.TABLE_NAME);
        database.execSQL(DROP + DB.PrimitiveValidatorEntityTable.TABLE_NAME);
        database.execSQL(DROP + DB.RelatedToValidatingEntityTable.TABLE_NAME);
        database.execSQL(DROP + DB.OneCircularValidationEntityTable.TABLE_NAME);
        database.execSQL(DROP + DB.TwoCircularValidationEntityTable.TABLE_NAME);
        database.execSQL(DROP + DB.CollectionRelatedValidatorEntityTable.TABLE_NAME);
        database.execSQL(DROP + DB.CollectionRelatedValidatorEntityLengthValidatorEntityAssociation.TABLE_NAME);

        doReturn(database).when(dbCreator).getReadableDatabase();
        doReturn(database).when(dbCreator).getWritableDatabase();
        dbCreator.onCreate(database);

    }

    @After
    public void tearDown() throws Exception {
        for (ConnectedEntityService<?> service : services) {
            service.close();
        }
        database.close();
    }

    protected <T> EntityService<T> entityService(Class<T> entityClass) {
        ConnectedEntityService<T> entityService = new ConnectedEntityService<T>(context, entityClass, database);
        services.add(entityService);
        return entityService;
    }

    static void assertSameFields(Object one, Object other) {
        Collection<String> assoFieldNames = getAllFieldNames(EntityData.getEntityData(one));

        boolean fieldsAreEqual = EqualsBuilder.reflectionEquals(one, other, assoFieldNames);
        if (!fieldsAreEqual) {
            Assert.fail("Expected fields to be equal but they were :\n" + ToStringBuilder.reflectionToString(one) + "\nand :\n" +
                    ToStringBuilder.reflectionToString(other));
        }
    }


    private static Collection<String> getAllFieldNames(EntityData entityData) {
        Collection<String> assoFieldNames = Collections2.transform(entityData.allAssociations, new Function<Field, String>() {

            @Override
            public String apply(Field field) {
                return field.getName();
            }
        });
        return assoFieldNames;
    }

    protected void assertInsertsAndUpdatesAmountToDB(int expectedInserts, int expectedUpdates) {
        assertInsertsAndUpdatesAmountToDB(expectedInserts, expectedUpdates, 0);
    }

    protected void assertInsertsAndUpdatesAmountToDB(int expectedInserts, int expectedUpdates, int expectedDeletions) {
        verify(database, times(expectedInserts)).insertOrThrow(anyString(), anyString(), any(ContentValues.class));
        verify(database, times(expectedUpdates)).update(anyString(), any(ContentValues.class), anyString(), any(String[].class));
        verify(database, times(expectedDeletions)).delete(anyString(), anyString(), any(String[].class));
        reset(database);
    }

}