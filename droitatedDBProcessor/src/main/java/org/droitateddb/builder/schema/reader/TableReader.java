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
package org.droitateddb.builder.schema.reader;

import org.droitateddb.builder.schema.data.Association;
import org.droitateddb.builder.schema.data.Column;
import org.droitateddb.builder.schema.data.Table;
import org.droitateddb.builder.schema.visitor.AssociationElementResolvingTypeVisitor;
import org.droitateddb.builder.schema.visitor.ColumnElementResolvingTypeVisitor;
import org.droitateddb.builder.schema.visitor.EmptyContructorVisitor;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.entity.Relationship;
import org.droitateddb.schema.AssociationType;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Alexander Frank
 * @author Falk Appel
 */
public class TableReader implements Reader<Table> {

    private final TypeElement entity;
    private final Set<String> entityNames;
    private final Elements elements;
    private final Messager messager;

    public TableReader(final TypeElement entity, final Set<String> entityNames, final Elements elements, final Messager messager) {
        this.entity = entity;
        this.entityNames = entityNames;
        this.elements = elements;
        this.messager = messager;
    }

    @Override
    public Table read() {
        int primaryKeyCount = 0;
        boolean noArgsConstructor = false;

        Table table = new Table(entity.getSimpleName().toString(), entity.toString());

        Map<String, AtomicInteger> countedToManyAssociations = new HashMap<String, AtomicInteger>();

        for (Element child : entity.getEnclosedElements()) {
            if (child.accept(new EmptyContructorVisitor(), null) != null) {
                noArgsConstructor = true;
            }

            VariableElement column = child.accept(new ColumnElementResolvingTypeVisitor(), null);
            if (column != null) {
                if (hasAmbiguosAssociationDeclaration(column)) {
                    return null;
                }
                if (column.getAnnotation(PrimaryKey.class) != null && ++primaryKeyCount > 1) {
                    messager.printMessage(Kind.ERROR, "Only one @PrimaryKey is allowed within an @Entity", entity);
                    return null;
                }
                ColumnReader columnReader = new ColumnReader(column, elements, messager);
                Column read = columnReader.read();
                if (read != null) {
                    table.addColumn(read);
                }
            }

            VariableElement association = child.accept(new AssociationElementResolvingTypeVisitor(), null);
            if (association != null) {
                AssociationReader associationReader = new AssociationReader(association, entityNames, messager);
                Association read = associationReader.read();
                if (read != null) {
                    table.addAssociation(read);

                    if (AssociationType.TO_MANY == read.getCardinality()) {
                        if (countedToManyAssociations.containsKey(read.getCanonicalTypeInEntity())) {
                            countedToManyAssociations.get(read.getCanonicalTypeInEntity()).incrementAndGet();
                        } else {
                            countedToManyAssociations.put(read.getCanonicalTypeInEntity(), new AtomicInteger(1));
                        }
                    }
                }
            }
        }

        if (primaryKeyCount == 0) {
            messager.printMessage(Kind.ERROR, "No @PrimaryKey was set for this @Entity", entity);
            return null;
        }
        if (!noArgsConstructor) {
            messager.printMessage(Kind.ERROR, "An @Entity needs to have a no-args constructor", entity);
            return null;
        }

        List<String> multipeToManyAssocitionsInEntity = getMultipeToManyAssociations(countedToManyAssociations);
        if (multipeToManyAssocitionsInEntity.size() > 0) {
            messager.printMessage(Kind.ERROR,
                    "You can not have multipe To-Many associations of the same type within one @Entity at this moment. Conflicts are with Top-Many associtions of type  "
                            + join(multipeToManyAssocitionsInEntity, ", "), entity);
        }

        return table;
    }

    private boolean hasAmbiguosAssociationDeclaration(final VariableElement variableElement) {
        if (variableElement.getAnnotation(Relationship.class) != null) {
            messager.printMessage(Kind.ERROR, "Only @Column or @Relationship is allowed, but not both", variableElement);
            return true;
        }
        return false;
    }

    private List<String> getMultipeToManyAssociations(final Map<String, AtomicInteger> countedToManyAssociations) {
        List<String> multipe = new ArrayList<String>();
        for (Map.Entry<String, AtomicInteger> entry : countedToManyAssociations.entrySet()) {
            if (entry.getValue().get() > 1) {
                multipe.add(entry.getKey());
            }
        }
        return multipe;
    }

    private String join(final Collection<String> stringCollection, final String separator) {
        StringBuilder builder = new StringBuilder();
        int count = 0;
        int size = stringCollection.size();
        for (String entry : stringCollection) {
            builder.append(entry);
            if (count++ < size - 1) {
                builder.append(separator);
            }
        }
        return builder.toString();
    }
}
