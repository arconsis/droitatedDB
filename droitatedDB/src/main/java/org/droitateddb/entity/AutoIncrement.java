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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field within a entity class to be an auto incremented value.<br>
 * At the moment only a @PrimaryKey annotated field can be marked with @AutoIncrement.<br>
 * The database to automatically increments the primary key with each new saved entity.<br>
 * Do not set the value of an @PrimaryKey annotated field within your code.
 * 
 * @author Falk Appel
 * @author Alexander Frank
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface AutoIncrement {
	// marker
}
