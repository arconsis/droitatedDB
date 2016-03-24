/*
 * Copyright (C) 2016 The droitated DB Authors
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
package org.droitateddb.builder.schema.writer;

import org.droitateddb.builder.Constants;
import org.droitateddb.schema.SchemaConstants;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class BasePackageConstantsWriter implements Writer {
    private final String basePackage;

    public BasePackageConstantsWriter(String basePackage) {

        this.basePackage = basePackage;
    }

    @Override
    public String write() {
        StringBuilder builder = new StringBuilder();
        builder.append(Constants.GENERATED_COMMENT);
        builder.append("package ").append(SchemaConstants.BASE_PACKAGE_FILE_PACKAGE).append(";\n\n");
        builder.append("public interface ").append(SchemaConstants.BASE_PACKAGE_FILE_NAME).append(" {\n\n");
        builder.append(Constants.TAB).append(String.format(Constants.CONSTANT_STRING,SchemaConstants.BASE_PACKAGE_CONSTANT_NAME,basePackage )).append('}');
        return builder.toString();
    }
}
