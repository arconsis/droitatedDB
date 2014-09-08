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
package com.arconsis.android.datarobot.builder.schema.reader;

import com.arconsis.android.datarobot.builder.schema.data.Schema;
import com.arconsis.android.datarobot.builder.schema.data.Table;
import com.arconsis.android.datarobot.builder.schema.visitor.TypeResolvingVisitor;
import com.arconsis.android.datarobot.config.Persistence;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class SchemaReader implements Reader<Schema> {
	private final Persistence            persistence;
	private final String                 updateHookClassName;
	private final String                 createHookClassName;
	private final Set<? extends Element> entities;
	private final Set<String> entityNames = new TreeSet<String>();
	private final Messager messager;

	public SchemaReader(final Persistence persistence, final String updateHookClassName, final String createHookClassName,
						final Set<? extends Element> entities, final Messager messager) {
		this.persistence = persistence;
		this.updateHookClassName = updateHookClassName;
		this.createHookClassName = createHookClassName;
		this.entities = entities;
		for (Element entity : entities) {
			entityNames.add(entity.toString());
		}
		this.messager = messager;
	}

	@Override
	public Schema read() {
		Schema schema = new Schema(persistence.dbName(), persistence.dbVersion(), updateHookClassName, createHookClassName);
		for (Element element : entities) {
			TypeElement entity = element.accept(new TypeResolvingVisitor(), null);
			TableReader tableReader = new TableReader(entity, entityNames, messager);
			Table read = tableReader.read();
			if (read != null) {
				schema.addTable(read);
			}
		}
		return schema;
	}
}
