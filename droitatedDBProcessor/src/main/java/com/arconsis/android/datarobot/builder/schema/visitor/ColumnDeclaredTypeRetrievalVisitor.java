package com.arconsis.android.datarobot.builder.schema.visitor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;

/**
 * Visitor to get the type of a column.
 *
 * @author Alexander Frank
 * @author Falk Appel
 */
public class ColumnDeclaredTypeRetrievalVisitor extends DefaultElementVisitor<TypeElement, Void> {
    @Override
    public TypeElement visitVariable(VariableElement e, Void aVoid) {
        return e.asType().accept(new DefaultTypeVisitor<TypeElement, Void>() {
            @Override
            public TypeElement visitDeclared(DeclaredType t, Void aVoid) {
                return (TypeElement) t.asElement();
            }
        }, null);
    }
}
