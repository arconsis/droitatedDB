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
package org.droitateddb.builder.schema.reader;

import org.droitateddb.builder.schema.data.Schema;
import org.droitateddb.builder.schema.data.Table;
import org.droitateddb.builder.schema.visitor.TypeResolvingVisitor;
import org.droitateddb.config.Persistence;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class SchemaReader implements Reader<Schema> {
    private final Persistence persistence;
    private final String updateHookClassName;
    private final String createHookClassName;
    private final Set<? extends Element> entities;
    private final Elements elements;
    private final Set<String> entityNames = new TreeSet<String>();
    private final Messager messager;

    public SchemaReader(final Persistence persistence, final String updateHookClassName, final String createHookClassName,
                        final Set<? extends Element> entities, final Elements elements, final Messager messager) {
        this.persistence = persistence;
        this.updateHookClassName = updateHookClassName;
        this.createHookClassName = createHookClassName;
        this.entities = entities;
        this.elements = elements;
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
            TableReader tableReader = new TableReader(entity, entityNames, elements, messager);
            Table read = tableReader.read();
            if (read != null) {
                schema.addTable(read);
            }
        }
        return schema;
    }
}
