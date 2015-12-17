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
package org.droitateddb.builder.schema.data;

import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class Table {

	private final String name;
	private final String entityClassName;

	private final Set<Column> columns = new TreeSet<Column>(new Comparator<Column>() {
		@Override
		public int compare(final Column c1, final Column c2) {
			return c1.getNameInEntity().compareTo(c2.getNameInEntity());
		}
	});
	private final Set<Association> associations = new TreeSet<Association>(new Comparator<Association>() {
		@Override
		public int compare(final Association a1, final Association a2) {
			return a1.getNameInEntity().compareTo(a2.getNameInEntity());
		}
	});

	public Table(final String name, final String entityClassName) {
		this.name = name;
		this.entityClassName = entityClassName;
	}

	public String getName() {
		return name;
	}

	public String getEntityClassName() {
		return entityClassName;
	}

	public Set<Column> getColumns() {
		return Collections.unmodifiableSet(columns);
	}

	public void addColumn(final Column column) {
		columns.add(column);
	}

	public Set<Association> getAssociations() {
		return Collections.unmodifiableSet(associations);
	}

	public void addAssociation(final Association association) {
		associations.add(association);
	}

}
