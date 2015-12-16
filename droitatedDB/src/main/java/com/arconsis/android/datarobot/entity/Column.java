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
package com.arconsis.android.datarobot.entity;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field as a column of a database table.<br>
 * This annotation can be placed to a field within an @Entity. Every annotated
 * field will become a column in the database table of the entity. <br>
 * Field without a @Column annotation within an @Entities are  transient.
 * 
 * @author Alexander Frank
 * @author Falk Appel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(FIELD)
public @interface Column {
	// marker
}
