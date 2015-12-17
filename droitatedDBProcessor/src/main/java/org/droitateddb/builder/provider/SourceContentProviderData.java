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

import org.droitateddb.processor.ContentProviderData;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class SourceContentProviderData extends ContentProviderData {

	private final String className;
	private final String source;

	public SourceContentProviderData(final String packageName, final String className, final String source, final String authority, final boolean exported) {
		super(packageName + "." + className, authority, exported);
		this.className = className;
		this.source = source;
	}

	public String getSimpleName() {
		return className;
	}

	public String getSource() {
		return source;
	}

}
