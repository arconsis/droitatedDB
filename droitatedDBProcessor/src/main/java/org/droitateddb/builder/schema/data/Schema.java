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
import java.util.LinkedList;
import java.util.List;


/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class Schema {

	private final String dbName;
	private final int    dbVersion;
	private final String updateHookClassName;
	private final String createHookClassName;
	private final List<Table> tables = new LinkedList<Table>();

	public Schema(final String dbName, final int dbVersion, final String updateHookClassName, final String createHookClassName) {
		this.dbName = dbName;
		this.dbVersion = dbVersion;
		this.updateHookClassName = updateHookClassName;
		this.createHookClassName = createHookClassName;
	}

	public String getDbName() {
		return dbName;
	}

	public int getDbVersion() {
		return dbVersion;
	}

	public String getUpdateHookClassName() {
		return updateHookClassName;
	}

	public String getCreateHookClassName() {
		return createHookClassName;
	}

	public void addTable(final Table table) {
		tables.add(table);
	}

	public List<Table> getTables() {
		return Collections.unmodifiableList(tables);
	}

}
