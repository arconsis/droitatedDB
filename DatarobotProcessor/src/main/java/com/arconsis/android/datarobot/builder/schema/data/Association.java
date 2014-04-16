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

import com.arconsis.android.datarobot.schema.AssociationType;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class Association {

	private final String nameInEntity;
	private final String canonicalTypeInEntity;
	private final AssociationType cardinality;
	private final String simpleTypeInEntity;

	public Association(final String nameInEntity, final String canonicalTypeInEntity, final String simpleTypeInEntity, final AssociationType cardinality) {
		this.nameInEntity = nameInEntity;
		this.canonicalTypeInEntity = canonicalTypeInEntity;
		this.simpleTypeInEntity = simpleTypeInEntity;
		this.cardinality = cardinality;
	}

	public String getNameInEntity() {
		return nameInEntity;
	}

	public String getCanonicalTypeInEntity() {
		return canonicalTypeInEntity;
	}

	public String getSimpleTypeInEntity() {
		return simpleTypeInEntity;
	}

	public AssociationType getCardinality() {
		return cardinality;
	}

}
