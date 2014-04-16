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
package com.arconsis.android.datarobot.builder.schema.data;

import com.arconsis.android.datarobot.schema.ColumnType;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class Column {

	private final String nameInEntity;
	private final String typeInEntity;
	private final ColumnType typeInDb;
	private final boolean isPrimary;
	private final boolean isAutoincrementing;

	public Column(final String nameInEntity, final String typeInEntity, final ColumnType typeInDb, final boolean isPrimary, final boolean isAutoincrementing) {
		this.nameInEntity = nameInEntity;
		this.typeInEntity = typeInEntity;
		this.typeInDb = typeInDb;
		this.isPrimary = isPrimary;
		this.isAutoincrementing = isAutoincrementing;
	}

	public String getNameInEntity() {
		return nameInEntity;
	}

	public String getTypeInEntity() {
		return typeInEntity;
	}

	public ColumnType getTypeInDb() {
		return typeInDb;
	}

	public boolean isPrimary() {
		return isPrimary;
	}

	public boolean isAutoincrementing() {
		return isAutoincrementing;
	}

}
