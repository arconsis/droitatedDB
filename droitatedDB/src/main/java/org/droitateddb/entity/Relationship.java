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
package org.droitateddb.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;

/**
 * Indicates an association between to {@link Entity} elements.<br>
 * Can be used on single {@link Entity} associations or on a {@link Collection} of an {@link Entity} type.<br><br>
 * At the moment the @Relationship annotation defines only an uni-directional relation between @Entities.<br>
 * For <b>to one</b> relationships there is a foreignkey column generated within the database,<br>
 * for <b>to many</b> relationships a link table is generated. The column and/or table name can be accessed via the generated DB schema.
 * 
 * @author Alexander Frank
 * @author Falk Appel
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface Relationship {
	// marker
}
