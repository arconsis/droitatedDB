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
package org.droitateddb.builder.schema.writer;

import static org.droitateddb.builder.Constants.CONSTANT_PREFIX;
import static org.droitateddb.builder.Constants.TAB;

import java.util.Locale;

import org.droitateddb.builder.schema.data.Association;
import org.droitateddb.schema.SchemaConstants;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class ToOneAssociationAttributeWriter implements Writer {

	private final String indent;
	private final Association association;
	private final int columnIdx;

	public ToOneAssociationAttributeWriter(final String indent, final Association association, final int columnIdx) {
		this.indent = indent;
		this.association = association;
		this.columnIdx = columnIdx;
	}

	@Override
	public String write() {
		StringBuilder builder = new StringBuilder();
		String nameToUpper = (SchemaConstants.FOREIGN_KEY + association.getNameInEntity()).toUpperCase(Locale.getDefault());
		builder.append(indent).append(TAB).append(CONSTANT_PREFIX).append("IntegerAttribute ").append(nameToUpper) //
				.append(" = new IntegerAttribute(\"").append(association.getNameInEntity()).append("\", \"") //
		.append(SchemaConstants.FOREIGN_KEY + association.getNameInEntity()).append("\", ")//
		.append(association.getCanonicalTypeInEntity()).append(".class, ") //
		.append(columnIdx).append(");\n");
		return builder.toString();
	}

	public String getSql() {
		return SchemaConstants.FOREIGN_KEY + association.getNameInEntity() + " Integer";
	}

}
