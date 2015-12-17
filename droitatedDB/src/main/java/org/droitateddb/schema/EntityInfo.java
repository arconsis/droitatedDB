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
 * Description of an Entity.
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class EntityInfo {
    private final String tableName;
    private final String className;
    private final Class<?> definition;
    private final boolean validation;

    public EntityInfo(final String className, final String tableName, final Class<?> definition) {
        this.className = className;
        this.tableName = tableName;
        this.definition = definition;
        this.validation = false;
    }

    public EntityInfo(final String className, final String tableName, final Class<?> definition, final boolean validation) {
        this.className = className;
        this.tableName = tableName;
        this.definition = definition;
        this.validation = validation;
    }

    public String className() {
        return className;
    }

    public String table() {
        return tableName;
    }

    public Class<?> definition() {
        return definition;
    }

    public boolean hasValidation() {
        return validation;
    }
}
