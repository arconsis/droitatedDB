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
package org.droitateddb.builder;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class Constants {
	public static final String TAB = "    ";
	public static final String CONSTANT_PREFIX = "public static final ";
	public static final String CONSTANT_STRING = CONSTANT_PREFIX + "String %s = \"%s\";\n";
	public static final String CONSTANT_STRING_ARRAY = CONSTANT_PREFIX + "String[] %s = new String[]{%s};\n";
	public static final String CONSTANT_INT = CONSTANT_PREFIX + "int %s = %s;\n";
	public static final String CONSTANT_ATTRIBUTE_ARRAY = CONSTANT_PREFIX + "AbstractAttribute[] %s = new AbstractAttribute[]{%s};\n";
	public static final String GENERATED_COMMENT = "/** Automatically generated file. DO NOT MODIFY */\n";
}
