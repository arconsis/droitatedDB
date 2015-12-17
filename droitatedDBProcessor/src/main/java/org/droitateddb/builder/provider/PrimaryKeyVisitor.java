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
package org.droitateddb.builder.provider;

import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic.Kind;

import org.droitateddb.builder.schema.visitor.DefaultElementVisitor;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.PrimaryKey;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class PrimaryKeyVisitor extends DefaultElementVisitor<VariableElement, Void> {
	private final Messager messager;

	public PrimaryKeyVisitor() {
		// default
		messager = null;
	}

	public PrimaryKeyVisitor(final Messager messager) {
		this.messager = messager;
	}

	@Override
	public VariableElement visitVariable(final VariableElement e, final Void p) {
		print("Working on variable " + e.getSimpleName().toString());
		if (e.getAnnotation(Column.class) != null && e.getAnnotation(PrimaryKey.class) != null) {
			return e;
		} else {
			return null;
		}
	}

	private void print(final String txt) {
		if (messager != null) {
			messager.printMessage(Kind.NOTE, txt);
		}
	}
}
