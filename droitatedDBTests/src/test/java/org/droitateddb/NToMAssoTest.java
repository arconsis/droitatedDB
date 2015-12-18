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

import org.droitateddb.test.data.Author;
import org.droitateddb.test.data.Text;
import org.fest.assertions.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = BasePersistenceTest.MANIFEST)
public class NToMAssoTest extends BasePersistenceTest {
    @Test
    public void simple() throws Exception {
        Text text1 = new Text("Text1");
        Author author = new Author("author", newArrayList(text1));
        text1.setAuthors(newArrayList(author));

        EntityService<Author> entityService = entityService(Author.class);

        entityService.save(author);
        assertInsertsAndUpdatesAmountToDB(4, 0);

    }

    @Test
    public void saveDepth1() throws Exception {
        Text text1 = new Text("Text1");
        Author author = new Author("author", newArrayList(text1));
        text1.setAuthors(newArrayList(author));

        EntityService<Author> entityService = entityService(Author.class);

        long id = entityService.save(author, 1);
        assertInsertsAndUpdatesAmountToDB(3, 0);
        Author authorFromDb = entityService.get(id);
        assertThat(authorFromDb.getTexts()).isNull();
        entityService.resolveAssociations(authorFromDb);
        Collection<Text> texts = authorFromDb.getTexts();
        assertThat(texts).hasSize(1);
        Text textFromDb = texts.iterator().next();
        assertThat(textFromDb.getAuthors()).isEmpty();
    }

    @Test
    public void resolveDepth1() throws Exception {
        Text text1 = new Text("Text1");
        Author author = new Author("author", newArrayList(text1));
        text1.setAuthors(newArrayList(author));

        EntityService<Author> entityService = entityService(Author.class);

        long id = entityService.save(author);
        assertInsertsAndUpdatesAmountToDB(4, 0);
        Author authorFromDb = entityService.get(id);
        assertThat(authorFromDb.getTexts()).isNull();
        entityService.resolveAssociations(authorFromDb, 1);
        Collection<Text> texts = authorFromDb.getTexts();
        assertThat(texts).hasSize(1);
        Text textFromDb = texts.iterator().next();
        assertThat(textFromDb.getAuthors()).isNull();
    }

    @Test
    public void createAndRead() throws Exception {
        Text text1 = new Text("Text1");
        Text text2 = new Text("Text2");
        Author author = new Author("author", newArrayList(text1, text2));
        text1.setAuthors(newArrayList(author));
        text2.setAuthors(newArrayList(author));

        EntityService<Author> entityService = entityService(Author.class);
        List<Author> list = entityService.get();
        assertThat(list).hasSize(0);

        entityService.save(author);
        assertInsertsAndUpdatesAmountToDB(7, 0);

        list = entityService.get();
        Assertions.assertThat(list).hasSize(1);
        Author fromDB = list.get(0);
        assertSameFields(author, fromDB);
        Collection<Text> textsFromDB = fromDB.getTexts();
        assertThat(textsFromDB).isNull();

        entityService.resolveAssociations(fromDB, 1);
        textsFromDB = fromDB.getTexts();
        assertThat(textsFromDB).hasSize(2);
        Text textFromDB1 = textsFromDB.iterator().next();
        Text textFromDB2 = textsFromDB.iterator().next();

        assertThat(textFromDB1.getAuthors()).isNull();
        assertThat(textFromDB2.getAuthors()).isNull();

        entityService.resolveAssociations(fromDB);
        textsFromDB = fromDB.getTexts();
        assertThat(textsFromDB).hasSize(2);
        Text textFromDB1Loaded2 = textsFromDB.iterator().next();
        Text textFromDB2Loaded2 = textsFromDB.iterator().next();
        assertThat(textFromDB1).isEqualTo(textFromDB1Loaded2);
        assertThat(textFromDB2).isEqualTo(textFromDB2Loaded2);

        assertThat(textFromDB1Loaded2.getAuthors()).isEqualTo(newArrayList(fromDB));
        assertThat(textFromDB2Loaded2.getAuthors()).isEqualTo(newArrayList(fromDB));

    }

    @Test
    public void createAndUpdate() throws Exception {
        Text text1 = new Text("Text1");
        Text text2 = new Text("Text2");
        Author author = new Author("author", newArrayList(text1, text2));
        text1.setAuthors(newArrayList(author));
        text2.setAuthors(newArrayList(author));

        EntityService<Author> entityService = entityService(Author.class);
        List<Author> list = entityService.get();
        assertThat(list).hasSize(0);

        long authorId = entityService.save(author);
        assertInsertsAndUpdatesAmountToDB(7, 0);
        Text text3 = new Text("Text3");
        author.getTexts().add(text3);
        text3.setAuthors(newArrayList(author));

        EntityService<Text> entityServiceText = entityService(Text.class);
        entityServiceText.save(text3);
        assertInsertsAndUpdatesAmountToDB(3, 3);

        Author fromDB = entityService.get(authorId);
        assertSameFields(author, fromDB);
        Collection<Text> textsFromDB = fromDB.getTexts();
        assertThat(textsFromDB).isNull();
        entityService.resolveAssociations(fromDB);
        textsFromDB = fromDB.getTexts();
        assertThat(textsFromDB).hasSize(3);

    }

    @Test
    public void createAndUpdateExtended() throws Exception {
        Text text1 = new Text("Text1");
        Text text2 = new Text("Text2");
        Author author = new Author("author", newArrayList(text1, text2));
        text1.setAuthors(newArrayList(author));
        text2.setAuthors(newArrayList(author));

        EntityService<Author> entityService = entityService(Author.class);
        List<Author> list = entityService.get();
        assertThat(list).hasSize(0);

        entityService.save(author);
        assertInsertsAndUpdatesAmountToDB(7, 0);
        author.getTexts().remove(text2);
        text2.getAuthors().remove(author);
        long authorId = entityService.save(author);
        assertInsertsAndUpdatesAmountToDB(0, 2, 1);

        Author fromDB = entityService.get(authorId);
        assertSameFields(author, fromDB);
        Collection<Text> textsFromDB = fromDB.getTexts();
        assertThat(textsFromDB).isNull();

        entityService.resolveAssociations(fromDB);
        textsFromDB = fromDB.getTexts();
        assertThat(textsFromDB).hasSize(1);
        Text textFromDB1 = textsFromDB.iterator().next();

        assertThat(textFromDB1.getAuthors()).isEqualTo(newArrayList(fromDB));

    }

}
