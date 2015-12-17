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
package org.droitateddb.processor;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class ContentProviderData {

	 private final String canonicalName;
	 private final String authority;
	 private final boolean exported;

	 public ContentProviderData(final String canonicalName, final String authority, final boolean exported) {
		 this.canonicalName = canonicalName;
		 this.authority = authority;
		 this.exported = exported;
	 }

	 public String getCanonicalName() {
		 return canonicalName;
	 }

	 public String getAuthority() {
		 return authority;
	 }

	 public boolean isExported() {
		 return exported;
	 }

 }