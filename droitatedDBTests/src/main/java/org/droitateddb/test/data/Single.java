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
package org.droitateddb.test.data;

import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity
public class Single {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Integer _id;


	@Column
	private String myString;


	public Single() {
		super();
	}

	public Single(String myString) {
		super();
		this.myString = myString;
	}

	public Integer get_id() {
		return this._id;
	}

	public String getMyString() {
		return this.myString;
	}

	public void set_id(Integer _id) {
		this._id = _id;
	}

	public void setMyString(String myString) {
		this.myString = myString;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Single)) {
			return false;
		}
		return hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this._id == null) ? 0 : this._id.intValue());
		result = prime * result + ((this.myString == null) ? 0 : this.myString.hashCode());
		return result;
	}

}
