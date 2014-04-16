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

import static com.arconsis.android.datarobot.schema.SchemaConstants.GENERATED_SUFFIX;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic.Kind;

import com.arconsis.android.datarobot.builder.provider.ContentProviderBuilder;
import com.arconsis.android.datarobot.builder.provider.SourceContentProviderData;
import com.arconsis.android.datarobot.builder.schema.reader.SchemaReader;
import com.arconsis.android.datarobot.builder.schema.visitor.TypeResolvingVisitor;
import com.arconsis.android.datarobot.builder.schema.writer.SchemaWriter;
import com.arconsis.android.datarobot.config.Persistence;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.hooks.DbUpdate;
import com.arconsis.android.datarobot.hooks.Update;
import com.arconsis.android.datarobot.manifest.AndroidManifest;
import com.arconsis.android.datarobot.manifest.AndroidManifestAccess;
import com.arconsis.android.datarobot.schema.SchemaConstants;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
@SupportedAnnotationTypes({ "com.arconsis.android.datarobot.entity.Entity", "com.arconsis.android.datarobot.config.Persistence",
"com.arconsis.android.datarobot.hooks.Update" })
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class EntityAnnotationProcessor extends AbstractProcessor {

	private Messager messager;
	private AndroidManifestAccess androidManifestAccess;

	@Override
	public synchronized void init(final ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		messager = processingEnv.getMessager();
		androidManifestAccess = new AndroidManifestAccess(processingEnv);
	}

	@Override
	public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			try {
				Persistence persistence = checkPersistenceAnnotation(roundEnv);
				if (persistence == null) {
					return true;
				}

				AndroidManifest androidManifest = androidManifestAccess.load();
				String updateHookName = getUpdateHookName(roundEnv);
				Set<? extends Element> entityAnnotated = roundEnv.getElementsAnnotatedWith(Entity.class);

				String generatedPackage = androidManifest.getPackageName() + "." + GENERATED_SUFFIX;
				generateDbSchema(generatedPackage, entityAnnotated, persistence, updateHookName);
				generateContentProviderIfRequested(generatedPackage, entityAnnotated);
			} catch (RuntimeException e) {
				messager.printMessage(Kind.ERROR, "Not able to generate DB schema from the annotated entity classes " + e.getMessage());
				throw e;
			}
			catch (Exception e) {
				messager.printMessage(Kind.ERROR, "Not able to generate DB schema from the annotated entity classes " + e.getMessage());
				throw new IllegalStateException(e);
			}
		}
		return true;
	}

	@Override
	public java.util.Set<String> getSupportedOptions() {
		HashSet<String> validOptions = new HashSet<String>();
		validOptions.add("manifest");
		return validOptions;
	}

	private Persistence checkPersistenceAnnotation(final RoundEnvironment roundEnv) {
		Set<? extends Element> persistenceAnnotated = roundEnv.getElementsAnnotatedWith(Persistence.class);

		if (persistenceAnnotated.size() == 0) {
			return null;
		}

		if (persistenceAnnotated.size() > 1) {
			messager.printMessage(Kind.ERROR, "Only one @Persistence activator is allowed within the project", persistenceAnnotated.iterator().next());
			return null;
		}
		Element persistenceClass = persistenceAnnotated.iterator().next();
		Persistence persistence = persistenceClass.getAnnotation(Persistence.class);
		return persistence;
	}

	private String getUpdateHookName(final RoundEnvironment roundEnv) {
		Set<? extends Element> updateAnnotated = roundEnv.getElementsAnnotatedWith(Update.class);
		if (updateAnnotated.size() == 0) {
			return null;
		}
		if (updateAnnotated.size() > 1) {
			messager.printMessage(Kind.ERROR, "Only one @Update hook is allowed with the project", updateAnnotated.iterator().next());
			return null;
		}
		Element updateElement = updateAnnotated.iterator().next();
		TypeElement typeElement = updateElement.accept(new TypeResolvingVisitor(), null);
		boolean implementsDbUpdate = false;
		for (TypeMirror typeMirror : typeElement.getInterfaces()) {
			if (typeMirror.toString().equals(DbUpdate.class.getCanonicalName())) {
				implementsDbUpdate = true;
			}
		}
		if (!implementsDbUpdate) {
			messager.printMessage(Kind.ERROR, "The @Update hook has to implement the " + DbUpdate.class.getCanonicalName() + " interface", updateElement);
			return null;
		}
		return typeElement.getQualifiedName().toString();
	}

	private void generateDbSchema(final String packageName, final Set<? extends Element> entityAnnotated, final Persistence persistence,
			final String updateHookName) {
		SchemaReader schemaReader = new SchemaReader(persistence, updateHookName, entityAnnotated, messager);
		SchemaWriter schemaWriter = new SchemaWriter(packageName, SchemaConstants.DB, schemaReader.read());

		JavaFileWriter javaFileWriter = new JavaFileWriter(packageName, SchemaConstants.DB, processingEnv);
		javaFileWriter.write(schemaWriter.write());
	}

	private void generateContentProviderIfRequested(final String packageName, final Set<? extends Element> entityAnnotated) throws Exception {
		List<ContentProviderData> providers = new LinkedList<ContentProviderData>();
		for (Element entity : entityAnnotated) {
			Entity entityAnnotation = entity.getAnnotation(Entity.class);
			if (entityAnnotation.contentProvider()) {
				ContentProviderBuilder providerBuilder = new ContentProviderBuilder(packageName, entity, messager);
				SourceContentProviderData providerData = providerBuilder.build();
				JavaFileWriter javaFileWriter = new JavaFileWriter(packageName, providerData.getSimpleName(), processingEnv);
				javaFileWriter.write(providerData.getSource());
				providers.add(providerData);
			}
		}
		androidManifestAccess.addProviders(providers);
	}

	public static void main(final String[] args) {
		// keep eclipse happy
	}

}
