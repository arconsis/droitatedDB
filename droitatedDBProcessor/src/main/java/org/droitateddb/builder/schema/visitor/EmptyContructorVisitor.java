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
package org.droitateddb.builder.schema.visitor;

import javax.lang.model.element.ExecutableElement;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class EmptyContructorVisitor extends DefaultElementVisitor<ExecutableElement, Void> {
	@Override
	public ExecutableElement visitExecutable(final ExecutableElement e, final Void p) {
		if ("<init>".equals(e.getSimpleName().toString()) && e.getParameters().size() == 0) {
			return e;
		}
		return null;
	}
}
