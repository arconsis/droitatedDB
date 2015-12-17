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

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import android.content.ContentProvider;

/**
 * Declares a class to be an entity.<br>
 * Entities can be stored within the database. Default a not exported
 * {@link ContentProvider} will be generated. <br>
 * Set <code>contentProvider=false</code> if you don`t need a {@link ContentProvider}.<br>
 * Set <code>exported=true</code> if the {@link ContentProvider} should be exported.<br>
 * Use <code>authority="xxx"</code> to define the authority if necessary.<br>
 * <br>
 * Each class annotated with Entity need at least:<br>
 * - a default constructor<br>
 * - exact one {@link Integer} or {@link Long} field annotated with @PrimaryKey and @Column<br>
 * 
 * @author Alexander Frank
 * @author Falk Appel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(TYPE)
public @interface Entity {
	boolean contentProvider() default true;

	String authority() default "";

	boolean exported() default false;
}
