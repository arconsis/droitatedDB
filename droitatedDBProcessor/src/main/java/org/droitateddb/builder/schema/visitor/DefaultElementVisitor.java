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

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;

/**
 * A default implementation if {@link ElementVisitor} to reduce boilerplate code for the users.
 * 
 * @author Alexander Frank
 * @author Falk Appel
 */
public class DefaultElementVisitor<R, P> implements ElementVisitor<R, P> {

	@Override
	public R visit(final Element e, final P p) {
		// default
		return null;
	}

	@Override
	public R visit(final Element e) {
		// default
		return null;
	}

	@Override
	public R visitPackage(final PackageElement e, final P p) {
		// default
		return null;
	}

	@Override
	public R visitType(final TypeElement e, final P p) {
		// default
		return null;
	}

	@Override
	public R visitVariable(final VariableElement e, final P p) {
		// default
		return null;
	}

	@Override
	public R visitExecutable(final ExecutableElement e, final P p) {
		// default
		return null;
	}

	@Override
	public R visitTypeParameter(final TypeParameterElement e, final P p) {
		// default
		return null;
	}

	@Override
	public R visitUnknown(final Element e, final P p) {
		// default
		return null;
	}

}
