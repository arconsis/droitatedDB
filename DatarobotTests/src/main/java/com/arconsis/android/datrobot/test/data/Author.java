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
package com.arconsis.android.datrobot.test.data;

import java.util.Collection;

import com.arconsis.android.datarobot.entity.AutoIncrement;
import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;
import com.arconsis.android.datarobot.entity.Relationship;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity
public class Author {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Integer id;
	@Column
	private String name;

	@Relationship
	private Collection<Comment> comments;

	@Relationship
	private Collection<Text> texts;


	public Collection<Comment> getComments() {
		return this.comments;
	}

	public void setComments(Collection<Comment> comments) {
		this.comments = comments;
	}

	public Author() {
		// default
	}

	public Author(String name, Collection<Text> texts) {
		super();
		this.name = name;
		this.texts = texts;
	}

	public Author(String name) {
		this.name = name;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Collection<Text> getTexts() {
		return this.texts;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setTexts(Collection<Text> texts) {
		this.texts = texts;
	}
}

