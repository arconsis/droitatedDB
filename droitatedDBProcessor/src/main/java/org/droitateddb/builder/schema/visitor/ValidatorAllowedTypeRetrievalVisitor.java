package org.droitateddb.builder.schema.visitor;

import org.droitateddb.validation.CustomValidator;

import javax.annotation.processing.Messager;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.List;

/**
 * Get the allowed type of a validator
 *
 * @author Alexander Frank
 * @author Falk Appel
 */
public class ValidatorAllowedTypeRetrievalVisitor extends DefaultAnnotationValueVisitor<TypeElement, Void> {

    public static final int CUSTOM_VALIDATOR_ACCEPTED_TYPE_INDEX = 1;
    private VariableElement column;
    private Messager messager;

    public ValidatorAllowedTypeRetrievalVisitor(VariableElement column, Messager messager) {
        this.column = column;

        this.messager = messager;
    }

    @Override
    public TypeElement visitType(TypeMirror t, Void aVoid) {
        return t.accept(new DefaultTypeVisitor<TypeElement, Void>() {
            @Override
            public TypeElement visitDeclared(DeclaredType t, Void o) {
                return t.asElement().accept(new DefaultElementVisitor<TypeElement, Void>() {
                    @Override
                    public TypeElement visitType(TypeElement e, Void aVoid) {
                        List<? extends TypeMirror> interfaces = e.getInterfaces();
                        for (TypeMirror mirror : interfaces) {
                            TypeElement classType = getTypeFromMirror(mirror);
                            if (isCustomValidator(classType)) {
                                return mirror.accept(new DefaultTypeVisitor<TypeElement, Void>() {
                                    @Override
                                    public TypeElement visitDeclared(DeclaredType t, Void aVoid) {
                                        return t.getTypeArguments().get(CUSTOM_VALIDATOR_ACCEPTED_TYPE_INDEX).accept(new DefaultTypeVisitor<TypeElement, Void>() {
                                            @Override
                                            public TypeElement visitDeclared(DeclaredType t, Void aVoid) {
                                                return ((TypeElement) t.asElement());
                                            }
                                        }, null);
                                    }
                                }, null);
                            }
                        }
                        return null;
                    }
                }, null);
            }
        }, null);
    }

    private TypeElement getTypeFromMirror(TypeMirror mirror) {
        return mirror.accept(new DefaultTypeVisitor<TypeElement, Void>() {
            @Override
            public TypeElement visitDeclared(DeclaredType t, Void aVoid) {
                return (TypeElement) t.asElement();
            }
        }, null);
    }

    private boolean isCustomValidator(TypeElement classType) {
        return classType != null && classType.getQualifiedName().toString().equals(CustomValidator.class.getCanonicalName());
    }
}
