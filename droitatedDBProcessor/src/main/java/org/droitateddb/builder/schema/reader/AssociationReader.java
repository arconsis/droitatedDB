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

import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import org.droitateddb.builder.schema.data.Association;
import org.droitateddb.schema.AssociationType;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class AssociationReader implements Reader<Association> {

	private final VariableElement association;
	private final Set<String> entityNames;
	private final Messager messager;

	public AssociationReader(final VariableElement association, final Set<String> entityNames, final Messager messager) {
		this.association = association;
		this.entityNames = entityNames;
		this.messager = messager;
	}

	@Override
	public Association read() {
		String nameInEntity = association.getSimpleName().toString();
		String typeInEntity = association.asType().toString();
		boolean isCollectionType = typeInEntity.startsWith(Collection.class.getCanonicalName());
		String canonicalType = getUsedType(association, typeInEntity, isCollectionType);

		if ("".equals(canonicalType)) {
			return null;
		}

		int lastDot = canonicalType.lastIndexOf(".");
		String simpleType = canonicalType.substring(lastDot + 1, canonicalType.length());

		AssociationType cardinality = (isCollectionType) ? AssociationType.TO_MANY : AssociationType.TO_ONE;
		return new Association(nameInEntity, canonicalType, simpleType, cardinality);
	}

	private String getUsedType(final VariableElement association, final String fieldType, final boolean isCollectionType) {
		if (!isCollectionType) {
			if (entityNames.contains(fieldType)) {
				return fieldType;
			} else {
				messager.printMessage(Kind.ERROR, "The @Relationship has to be another @Entity or Collection of another @Entity", association);
				return "";
			}
		} else {
			Matcher matcher = Pattern.compile("<(.*?)>").matcher(fieldType);
			if (matcher.find()) {
				String type = matcher.group(1);
				if (!entityNames.contains(type)) {
					messager.printMessage(Kind.ERROR, "The collection type has to be an @Entity", association);
					return "";
				}
				return type;
			} else {
				messager.printMessage(Kind.ERROR, "No raw type is allowed when using @Relationship on a Collection", association);
				return "";
			}
		}
	}

}
