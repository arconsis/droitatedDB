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
package org.droitateddb.builder.provider;

import static org.droitateddb.builder.Constants.GENERATED_COMMENT;
import static org.droitateddb.builder.Constants.TAB;
import static org.droitateddb.schema.SchemaConstants.DB;
import static org.droitateddb.schema.SchemaConstants.INFO_SUFFIX;
import static org.droitateddb.schema.SchemaConstants.TABLE;

import java.util.Locale;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import org.droitateddb.entity.Entity;

/**
 * Generates Java source file content a ContentProvider depending on a {@link Entity} annotated class.
 * 
 * @author Alexander Frank
 * @author Falk Appel
 */
public class ContentProviderBuilder {

	private final Element entityElement;
	private final Entity entity;
	private final String packageName;
	private final String entityName;
	private final VariableElement primaryKey;
	private final Messager messager;

	public static final String CONTENT_PROVIDER_SUFFIX = "ContentProvider";

	public ContentProviderBuilder(final String packageName, final Element entityElement, final Messager messager) {
		this.packageName = packageName;
		this.entityElement = entityElement;
		this.messager = messager;
		entityName = entityElement.getSimpleName().toString();
		entity = entityElement.getAnnotation(Entity.class);
		primaryKey = getPrimaryKey();
	}

	private VariableElement getPrimaryKey() {
		for (Element element : entityElement.getEnclosedElements()) {
			VariableElement variableElement = element.accept(new PrimaryKeyVisitor(), null);
			if (variableElement != null) {
				return variableElement;
			}
		}
		String error = "The @Entity " + entityName + " has no @PrimaryKey. @PrimaryKey is required";
		messager.printMessage(Kind.ERROR, error, entityElement);
		throw new IllegalStateException(error);
	}

	public SourceContentProviderData build() {

		String providerName = entityName + CONTENT_PROVIDER_SUFFIX;
		String authority = getAuthority();

		StringBuilder builder = new StringBuilder();
		addSignature(builder, providerName);
		overrideEntityUriPart(builder);
		overrideGetAuthority(builder, authority);
		overrideGetEntityInfo(builder);
		overrideGetIdAttribute(builder);
		addEnd(builder);
		String source = builder.toString();
		return new SourceContentProviderData(packageName, providerName, source, authority, entity.exported());
	}

	private void addSignature(final StringBuilder builder, final String providerName) {
		builder.append(GENERATED_COMMENT);
		builder.append("package ").append(packageName).append(";\n\n") //
		.append("import ").append("com.arconsis.android.datarobot.schema.*;\n") //
		.append("import ").append("com.arconsis.android.datarobot.*;\n") //
		.append("public class ").append(providerName).append(" extends BaseContentProvider {\n\n");
	}

	private void overrideEntityUriPart(final StringBuilder builder) {
		builder.append(TAB).append("@Override\n") //
		.append(TAB).append("protected String getEntityURIPart() {\n") //
		.append(TAB).append(TAB).append("return \"").append(entityName.toLowerCase(Locale.getDefault())).append("\";\n") //
		.append(TAB).append("}\n\n");
	}

	private void overrideGetAuthority(final StringBuilder builder, final String authority) {
		builder.append(TAB).append("@Override\n") //
		.append(TAB).append("protected String getAuthority() {\n") //
		.append(TAB).append("return \"").append(authority).append("\";\n") //
		.append(TAB).append("}\n\n");
	}

	private void overrideGetEntityInfo(final StringBuilder builder) {
		builder.append(TAB).append("@Override\n");
		builder.append(TAB).append("protected EntityInfo getEntityInfo() {\n");
		builder.append(TAB).append(TAB).append("return ").append(DB).append(".").append(entityName).append(INFO_SUFFIX).append(";\n");
		builder.append(TAB).append("}\n\n");
	}

	private void overrideGetIdAttribute(final StringBuilder builder) {
		builder.append(TAB).append("@Override\n");
		builder.append(TAB).append("protected AbstractAttribute getIdAttribute() {\n");
		builder.append(TAB).append(TAB).append("return ").append(DB).append(".").append(entityName).append(TABLE).append(".")
		.append(primaryKey.getSimpleName().toString().toUpperCase(Locale.getDefault())).append(";\n");
		builder.append(TAB).append("}\n");

	}

	private void addEnd(final StringBuilder builder) {
		builder.append("}");
	}

	private String getAuthority() {
		String authority = entity.authority();
		if (authority == null || authority.equals("")) {
			return packageName + ".provider." + entityName.toLowerCase(Locale.getDefault());
		}
		return authority;
	}

}
