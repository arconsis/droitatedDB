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
 public class Text {

	 @Relationship
	 private Collection<Author> authors;

	 @Column
	 @PrimaryKey
	 @AutoIncrement
	 private Integer id;

	 @Column
	 private String name;

	 public Text() {
		 // default
	 }

	 public Text(Integer id, String name, Collection<Author> authors) {
		 super();
		 this.id = id;
		 this.name = name;
		 this.authors = authors;
	 }

	 public Text(String name) {
		 super();
		 this.name = name;
	 }

	 public Collection<Author> getAuthors() {
		 return this.authors;
	 }

	 public Integer getId() {
		 return this.id;
	 }

	 public String getName() {
		 return this.name;
	 }

	 public void setAuthors(Collection<Author> authors) {
		 this.authors = authors;
	 }

	 public void setId(Integer id) {
		 this.id = id;
	 }

	 public void setName(String name) {
		 this.name = name;
	 }
 }
