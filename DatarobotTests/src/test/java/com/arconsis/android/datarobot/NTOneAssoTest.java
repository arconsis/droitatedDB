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

import com.arconsis.android.datrobot.test.data.Author;
import com.arconsis.android.datrobot.test.data.Comment;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collection;

import static com.google.common.collect.Lists.newArrayList;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = BasePersistenceTest.MANIFEST)
public class NTOneAssoTest extends BasePersistenceTest {
	@Test
	public void testSaveAndLoadToOneWithDepth1() {

		Comment c1 = new Comment("asdf");
		EntityService<Comment> entityService = entityService(Comment.class);

		entityService.save(newArrayList(c1));
		assertInsertsAndUpdatesAmountToDB(1, 0);

		Comment comment = entityService.get(c1.getId());
		assertThat(comment.getAuthor()).isNull();

		Author a = new Author();
		a.setName("aasfewgrq");
		Comment c2 = new Comment("eb2rgbrtb");

		a.setComments(newArrayList(c1, c2));
		c1.setAuthor(a);
		c2.setAuthor(a);

		entityService.save(c1, 1);
		// ATTENTION: one update to Comment with fk and one insert for Author
		assertInsertsAndUpdatesAmountToDB(1, 1);
		assertThat(entityService.get()).hasSize(1);

		Integer id = c1.getId();
		Comment savedComment = entityService.get(id);
		assertThat(savedComment.getAuthor()).isNull();
		// check that save with depth 0 dosnt wipe the foreign key
		entityService.save(savedComment, 0);
		savedComment = entityService.get(id);
		assertThat(savedComment.getAuthor()).isNull();
		entityService.resolveAssociations(savedComment, 1);
		Author savedAuthor = savedComment.getAuthor();
		assertThat(savedAuthor).isNotNull();
		assertThat(savedAuthor.getComments()).isNull();
		assertThat(savedAuthor.getTexts()).isNull();
	}

	@Test
	public void testSaveAndLoadToManyWithDepth1() {

		Comment c1 = new Comment("asdf");
		EntityService<Comment> entityService = entityService(Comment.class);

		entityService.save(c1);
		assertInsertsAndUpdatesAmountToDB(1, 0);

		Author a = new Author();
		a.setName("aasfewgrq");
		Comment c2 = new Comment("eb2rgbrtb");

		a.setComments(newArrayList(c1, c2));
		c1.setAuthor(a);
		c2.setAuthor(a);

		EntityService<Author> authorService = entityService(Author.class);

		authorService.save(newArrayList(a), 1);
		int authorId = a.getId();
		// ATTENTION: one update to Comment and one insert for Author one insert for other comment and two inserts for
		// linktable
		assertInsertsAndUpdatesAmountToDB(4, 1);
		assertThat(entityService.get()).hasSize(2);
		assertThat(authorService.get()).hasSize(1);

		Author savedAuthor = authorService.get(authorId);
		assertThat(savedAuthor.getComments()).isNull();

		authorService.resolveAssociations(savedAuthor);
		Collection<Comment> comments=savedAuthor.getComments();
		assertThat(comments).hasSize(2);
		assertThat(comments.iterator().next().getAuthor()).isNull();
	}

	@Test
	public void saveObjectGraphBeginningFromComment() {

		Comment c1 = new Comment("asdf");
		Comment c2 = new Comment("eb2rgbrtb");

		Author a = new Author();
		a.setName("aasfewgrq");

		a.setComments(newArrayList(c1, c2));
		c1.setAuthor(a);
		c2.setAuthor(a);

		EntityService<Comment> entityService = entityService(Comment.class);
		long id = entityService.save(c1);
		assertInsertsAndUpdatesAmountToDB(5, 0);
		Comment fromDb = entityService.get(id);
		entityService.resolveAssociations(fromDb);
		assertSameFields(fromDb, c1);

		Author author = fromDb.getAuthor();
		assertSameFields(author, a);
		Collection<Comment> comments = author.getComments();
		assertThat(comments).hasSize(2);
		comments.remove(fromDb);
		assertSameFields(comments.iterator().next(), c2);

	}

	@Test
	public void saveObjectGraphBeginningFromAuthor() {

		Comment c1 = new Comment("asdf");
		Comment c2 = new Comment("eb2rgbrtb");

		Author a = new Author();
		a.setName("aasfewgrq");

		a.setComments(newArrayList(c1, c2));
		c1.setAuthor(a);
		c2.setAuthor(a);

		EntityService<Author> entityService = entityService(Author.class);
		long id = entityService.save(a);
		assertInsertsAndUpdatesAmountToDB(5, 0);
		Author author = entityService.get(id);
		entityService.resolveAssociations(author);
		assertSameFields(author, a);
		Collection<Comment> comments = author.getComments();
		assertThat(comments).hasSize(2);

		assertThat(comments.iterator().next().getAuthor()).isEqualTo(author);
		assertThat(comments.iterator().next().getAuthor()).isEqualTo(author);

	}

}
