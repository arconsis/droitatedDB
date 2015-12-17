package org.droitateddb.builder.schema.reader;

import org.droitateddb.builder.schema.data.ColumnValidation;
import org.droitateddb.builder.schema.data.ValidatorInfo;
import org.droitateddb.builder.schema.visitor.ColumnDeclaredTypeRetrievalVisitor;
import org.droitateddb.builder.schema.visitor.DefaultTypeVisitor;
import org.droitateddb.builder.schema.visitor.ValidatorAllowedTypeRetrievalVisitor;
import org.droitateddb.validation.Validator;

import javax.annotation.processing.Messager;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class ColumnValidationReader implements Reader<ColumnValidation> {

    public static final String VALIDATOR_VALUE_METHOD_NAME = "value";

    private VariableElement column;
    private Elements elements;
    private final Messager messager;

    public ColumnValidationReader(final VariableElement column, final Elements elements, final Messager messager) {
        this.column = column;
        this.elements = elements;
        this.messager = messager;
    }

    @Override
    public ColumnValidation read() {
        ColumnValidation columnValidation = new ColumnValidation();

        List<? extends AnnotationMirror> annotationMirrors = column.getAnnotationMirrors();
        for (AnnotationMirror variableAnnotationMirror : annotationMirrors) {
            Element element = variableAnnotationMirror.getAnnotationType().asElement();
            for (AnnotationMirror annotationTypeMirror : element.getAnnotationMirrors()) {
                if (isValidatorAnnotation(annotationTypeMirror)) {
                    String validatorClass = getValidatorClass(annotationTypeMirror, element);
                    String validatorAnnotation = element.toString();
                    Map<String, Object> parameter = getValidationParameter(variableAnnotationMirror);
                    if (isParameterTypesValid(parameter)) {
                        columnValidation.add(new ValidatorInfo(validatorClass, validatorAnnotation, parameter));
                    } else {
                        messager.printMessage(Diagnostic.Kind.ERROR, "Validator annotation " + validatorAnnotation + " has invalid parameters types. Allowed are numbers (primitive and boxed) and String", element);
                    }
                }
            }
        }

        return columnValidation;
    }

    private boolean isParameterTypesValid(Map<String, Object> parameter) {
        for (Object paramValue : parameter.values()) {
            if (!isValid(paramValue)) {
                return false;
            }
        }
        return true;
    }

    private boolean isValid(Object value) {
        Class<?> paramType = value.getClass();
        return paramType.equals(java.lang.String.class)  //
                || paramType.equals(java.lang.Integer.class) //
                || paramType.equals(java.lang.Float.class) //
                || paramType.equals(java.lang.Double.class) //
                || paramType.equals(java.lang.Long.class) //
                || paramType.equals(java.lang.Short.class) //
                || paramType.equals(java.lang.Byte.class);
    }

    private boolean isValidatorAnnotation(AnnotationMirror mirror) {
        return mirror.getAnnotationType().asElement().toString().equals(Validator.class.getCanonicalName());
    }

    private String getValidatorClass(AnnotationMirror mirror, Element element) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = mirror.getElementValues();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValues.entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals(VALIDATOR_VALUE_METHOD_NAME)) {
                AnnotationValue value = entry.getValue();
                TypeElement acceptedType = value.accept(new ValidatorAllowedTypeRetrievalVisitor(column, messager), null);
                TypeElement actualType = column.accept(new ColumnDeclaredTypeRetrievalVisitor(), null);
                TypeKind columnTypeKind = column.asType().getKind();

                if (columnTypeKind.isPrimitive() && compareTypeWithPrimitive(acceptedType, columnTypeKind)) {
                    return value.getValue().toString();
                } else if (!columnTypeKind.isPrimitive() && compareTypeWithDeclared(acceptedType, actualType)) {
                    return value.getValue().toString();
                } else {
                    messager.printMessage(Diagnostic.Kind.ERROR, "The validator annotation " + element.toString() + " is not allowed on this type of column. Accepted is " + acceptedType.toString(), column);
                    return null;

                }
            }
        }
        messager.printMessage(Diagnostic.Kind.ERROR, "No value set for Validator annotation", mirror.getAnnotationType().asElement());
        return null;
    }

    private boolean compareTypeWithPrimitive(TypeElement acceptedType, TypeKind columnTypeKind) {
        return acceptedType.getQualifiedName().toString().equals(getBoxedClassName(columnTypeKind)) || acceptedType.getQualifiedName().toString().equals(Number.class.getCanonicalName());
    }

    private boolean compareTypeWithDeclared(TypeElement acceptedType, TypeElement actualType) {
        if (acceptedType.getQualifiedName().equals(actualType.getQualifiedName())) {
            return true;
        }

        TypeMirror superclass = actualType.getSuperclass();
        if (superclass.getKind().equals(TypeKind.NONE)) {
            return false;
        } else {
            TypeElement superTypeElement = superclass.accept(new DefaultTypeVisitor<TypeElement, Void>() {
                @Override
                public TypeElement visitDeclared(DeclaredType t, Void aVoid) {
                    return (TypeElement) t.asElement();
                }
            }, null);
            return compareTypeWithDeclared(acceptedType, superTypeElement);
        }
    }

    private String getBoxedClassName(TypeKind kind) {
        switch (kind) {
            case LONG:
                return Long.class.getCanonicalName();
            case INT:
                return Integer.class.getCanonicalName();
            case BYTE:
                return Byte.class.getCanonicalName();
            case SHORT:
                return Short.class.getCanonicalName();
            case FLOAT:
                return Float.class.getCanonicalName();
            case DOUBLE:
                return Double.class.getCanonicalName();
            default:
                return "Unknown";
        }
    }

    private Map<String, Object> getValidationParameter(AnnotationMirror mirror) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = elements.getElementValuesWithDefaults(mirror);
        Map<String, Object> parameter = new HashMap<String, Object>();
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> elem : elementValues.entrySet()) {
            parameter.put(elem.getKey().getSimpleName().toString(), elem.getValue().getValue());
        }
        return parameter;
    }

}
