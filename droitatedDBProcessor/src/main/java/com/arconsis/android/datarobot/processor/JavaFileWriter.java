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
package com.arconsis.android.datarobot.processor;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class JavaFileWriter {

	 private final String packageName;
	 private final String className;
	 private final ProcessingEnvironment processingEnv;

	 public JavaFileWriter(final String packageName, final String className, final ProcessingEnvironment processingEnv) {
		 this.packageName = packageName;
		 this.className = className;
		 this.processingEnv = processingEnv;

	 }

	 public void write(final String data) {
		 Writer writer = null;
		 try {
			 clearOldVersions(packageName, className);
			 JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + className);
			 writer = file.openWriter();
			 writer.append(data).flush();
		 } catch (IOException e) {
			 throw new IllegalStateException(e);
		 } finally {
			 if (writer != null) {
				 try {
					 writer.close();
				 } catch (IOException e) {
					 // don't care
				 }
			 }
		 }
	 }

	 private void clearOldVersions(final String packageName, final String fileName) {
		 try {
			 FileObject schema = processingEnv.getFiler().getResource(StandardLocation.SOURCE_OUTPUT, packageName, fileName);
			 String schemaFile = schema.toUri().toASCIIString();
			 String withoutFile = schemaFile.replace("file:", "") + ".java";
			 if (new File(withoutFile).exists()) {
				 new File(withoutFile).delete();
			 }
		 } catch (IOException e) {
			 // ignore
		 }
	 }
 }
