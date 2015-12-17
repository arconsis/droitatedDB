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
package org.droitateddb.manifest;

import org.droitateddb.manifest.AndroidManifestProcessor.ManifestBuilder;
import org.droitateddb.processor.ContentProviderData;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.Diagnostic.Kind;
import java.io.File;
import java.util.List;

/**
 * Provides access to the content of the AndroidManifest.xml and allows modifying data within it
 *
 * @author Alexander Frank
 * @author Falk Appel
 */
public class AndroidManifestAccess {

    private final ProcessingEnvironment env;

    public AndroidManifestAccess(final ProcessingEnvironment env) {
        this.env = env;
    }

    public final AndroidManifest load() throws Exception {
        return new AndroidManifestProcessor(openManifest()).parse();
    }

    public void addProviders(final List<ContentProviderData> data) throws Exception {
        AndroidManifestProcessor manifestParser = new AndroidManifestProcessor(openManifest());
        ManifestBuilder builder = manifestParser.change();
        for (ContentProviderData provider : data) {
            builder.addProviderIfNotExists(provider);
        }
        builder.commit();
    }

    private File openManifest() {
        String manifestFileName = env.getOptions().get("manifest");
        if (manifestFileName == null || "".equals(manifestFileName)) {
            env.getMessager().printMessage(Kind.MANDATORY_WARNING, "Please configure the annotation processor option: manifest");
            throw new IllegalStateException("Provide a file path to your manifest file");
        }
        env.getMessager().printMessage(Kind.NOTE, "Working with Android manifest: " + manifestFileName);
        File manifest = new File(manifestFileName);
        if (!manifest.exists()) {
            throw new IllegalStateException("Manifest file " + manifest.getAbsolutePath() + " does not exist!");
        }
        return manifest;
    }
}
