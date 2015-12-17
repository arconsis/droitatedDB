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
import static org.droitateddb.schema.SchemaConstants.FOREIGN_KEY;
import static org.droitateddb.schema.SchemaConstants.LINK;

import java.util.Locale;

import org.droitateddb.builder.schema.data.Association;
import org.droitateddb.builder.schema.data.Table;
import org.droitateddb.schema.AssociationType;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class AssociationsInterfaceWriter implements Writer {

	private final String indent;
	private final Table table;
	private final Association association;

	public AssociationsInterfaceWriter(final String indent, final Table table, final Association association) {
		this.indent = indent;
		this.table = table;
		this.association = association;
	}

	@Override
	public String write() {
		StringBuilder builder = new StringBuilder();

		if (AssociationType.TO_ONE == association.getCardinality()) {
			String attributeRef = (FOREIGN_KEY + association.getNameInEntity()).toUpperCase(Locale.getDefault());
			builder.append(indent).append(CONSTANT_PREFIX).append("ToOneAssociation ").append(association.getNameInEntity().toUpperCase(Locale.getDefault()))
			.append(" = new ToOneAssociation(") //
			.append("\"").append(association.getNameInEntity()).append("\", ") //
			.append(association.getCanonicalTypeInEntity()).append(".class, ") //
			.append(attributeRef).append(");\n");
		} else if (AssociationType.TO_MANY == association.getCardinality()) {
			builder.append(indent).append(CONSTANT_PREFIX).append("ToManyAssociation ").append(association.getNameInEntity().toUpperCase(Locale.getDefault()))
			.append(" = new ToManyAssociation(") //
			.append("\"").append(association.getNameInEntity()).append("\", ") //
			.append(association.getCanonicalTypeInEntity()).append(".class, ") //
					.append(table.getName() + association.getSimpleTypeInEntity() + LINK).append(".class);\n");
		}

		return builder.toString();
	}

}
