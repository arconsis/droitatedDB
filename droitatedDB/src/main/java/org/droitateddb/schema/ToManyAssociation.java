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
package org.droitateddb.schema;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class ToManyAssociation {

	private final String nameInEntity;
	private final Class<?> associatedType;
	private final Class<?> linkTableSchema;

	public ToManyAssociation(final String nameInEntity, final Class<?> associatedType, final Class<?> linkTableSchema) {
		this.nameInEntity = nameInEntity;
		this.associatedType = associatedType;
		this.linkTableSchema = linkTableSchema;
	}

	public String getNameInEntity() {
		return nameInEntity;
	}

	public Class<?> getAssociatedType() {
		return associatedType;
	}

	public Class<?> getLinkTableSchema() {
		return linkTableSchema;
	}

	public AssociationType getType() {
		return AssociationType.TO_MANY;
	}
}
