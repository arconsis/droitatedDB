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

import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;
import com.arconsis.android.datarobot.entity.Relationship;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity
public class SimpleWithoutAutoIncrement {

	@Column
	@PrimaryKey
	private Integer _id;

	@Column
	private String myString;
	@Relationship
	private Comment comment;


	public Comment getComment() {
		return this.comment;
	}

	public void setComment(Comment comment) {
		this.comment = comment;
	}

	public SimpleWithoutAutoIncrement() {
		// no-args
	}

	public SimpleWithoutAutoIncrement(final Integer id, final String myString) {
		this._id = id;
		this.myString = myString;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(Integer _id) {
		this._id = _id;
	}

	public String getMyString() {
		return myString;
	}

	public void setMyString(String myString) {
		this.myString = myString;
	}



}
