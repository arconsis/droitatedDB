package org.droitateddb;

import android.content.Context;
import org.droitateddb.schema.AbstractAttribute;
import org.droitateddb.schema.ColumnValidator;
import org.droitateddb.schema.EntityInfo;
import org.droitateddb.validation.AccumulatedValidationResult;
import org.droitateddb.validation.CustomValidator;
import org.droitateddb.validation.ValidationResult;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Validates entities by their schema definition
 *
 * @author Falk Appel
 * @author Alexander Frank
 */
public class DatabaseValidator<T> {

    private Context context;

    public DatabaseValidator(Context context) {
        this.context = context;
    }

    public AccumulatedValidationResult validate(Collection<T> toBeValidated) {
        return validate(toBeValidated, Integer.MAX_VALUE);
    }

    public AccumulatedValidationResult validate(Collection<T> toBeValidated, int maxDepth) {
        AccumulatedValidationResult validationResult = new AccumulatedValidationResult();
        Set<Object> alreadyValidated = new HashSet<Object>();
        for (T data : toBeValidated) {
            validate(data, validationResult, alreadyValidated, 0, maxDepth);
        }
        return validationResult;
    }

    public AccumulatedValidationResult validate(T toBeValidated) {
        return validate(toBeValidated, Integer.MAX_VALUE);
    }

    public AccumulatedValidationResult validate(T toBeValidated, int maxDepth) {
        AccumulatedValidationResult validationResult = new AccumulatedValidationResult();
        Set<Object> alreadyValidated = new HashSet<Object>();
        validate(toBeValidated, validationResult, alreadyValidated, 0, maxDepth);
        return validationResult;
    }

    private void validate(Object validatingObject, AccumulatedValidationResult validationResult, Set<Object> alreadyValidated, int currentDepth, int maxDepth) {
        try {
            EntityInfo entityInfo = SchemaUtil.getEntityInfo(validatingObject.getClass());
            Class<?> definition = entityInfo.definition();
            EntityData entityData = EntityData.getEntityData(validatingObject);

            List<Field> allAssociations = entityData.allAssociations;
            if (allAssociations.size() > 0) {
                validateRelationshipsOfEntity(validatingObject, allAssociations, validationResult, alreadyValidated, currentDepth, maxDepth);
            }

            if (entityInfo.hasValidation()) {
                validateColumnsInEntity(validatingObject, definition, entityData.columns, validationResult);
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    private void validateColumnsInEntity(Object validatingObject, Class<?> definition, List<Field> columns, AccumulatedValidationResult validationResult) throws NoSuchFieldException, IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        for (Field column : columns) {
            ValidationResult result = checkForValidatorsAndValidate(validatingObject, column, definition);
            if (!result.isValid()) {
                validationResult.addError(result);
            }
        }
    }

    private void validateRelationshipsOfEntity(Object validatingObject, List<Field> relationships, AccumulatedValidationResult validationResult, Set<Object> alreadyValidated, int currentDepth, int maxDepth) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        if (currentDepth < maxDepth) {
            for (Field relationship : relationships) {
                getRelatedEntityAndValidate(validatingObject, relationship, validationResult, currentDepth, maxDepth, alreadyValidated);
            }
        }
    }

    private void getRelatedEntityAndValidate(Object validatingObject, Field relationship, AccumulatedValidationResult validationResult, int currentDepth, int maxDepth, Set<Object> alreadyValidated) throws IllegalAccessException {
        relationship.setAccessible(true);
        Object relatedEntity = relationship.get(validatingObject);
        if (relatedEntity != null) {
            handleRelatedEntity(relatedEntity, validationResult, currentDepth, maxDepth, alreadyValidated);

        }
    }

    @SuppressWarnings("unchecked")
    private void handleRelatedEntity(Object relatedEntity, AccumulatedValidationResult validationResult, int currentDepth, int maxDepth, Set<Object> alreadyValidated) {
        if (Collection.class.isAssignableFrom(relatedEntity.getClass())) {
            handleCollectionRelationship((Collection<Object>) relatedEntity, validationResult, currentDepth, maxDepth, alreadyValidated);
        } else {
            checkAlreadyValidatedAndValidate(relatedEntity, validationResult, currentDepth, maxDepth, alreadyValidated);
        }
    }

    private void handleCollectionRelationship(Collection<Object> relatedEntity, AccumulatedValidationResult validationResult, int currentDepth, int maxDepth, Set<Object> alreadyValidated) {
        for (Object entity : relatedEntity) {
            checkAlreadyValidatedAndValidate(entity, validationResult, currentDepth, maxDepth, alreadyValidated);
        }
    }

    private void checkAlreadyValidatedAndValidate(Object entity, AccumulatedValidationResult validationResult, int currentDepth, int maxDepth, Set<Object> alreadyValidated) {
        if (!alreadyValidated.contains(entity)) {
            alreadyValidated.add(entity);
            validate(entity, validationResult, alreadyValidated, currentDepth + 1, maxDepth);
        }
    }

    private ValidationResult checkForValidatorsAndValidate(Object toBeValidated, Field column, Class<?> definition) throws NoSuchFieldException, IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Field columnDefinition = definition.getDeclaredField(column.getName().toUpperCase());
        AbstractAttribute attribute = (AbstractAttribute) columnDefinition.get(null);
        ColumnValidator[] columnValidators = attribute.getColumnValidators();
        if (columnValidators.length > 0) {
            return validateColumn(toBeValidated, attribute, columnValidators);
        }
        return ValidationResult.valid();
    }

    @SuppressWarnings("unchecked")
    private ValidationResult validateColumn(Object toBeValidated, AbstractAttribute attribute, ColumnValidator[] columnValidators) throws NoSuchFieldException, IllegalAccessException, InstantiationException, java.lang.reflect.InvocationTargetException, NoSuchMethodException {
        Field entityField = toBeValidated.getClass().getDeclaredField(attribute.fieldName());
        entityField.setAccessible(true);
        Object entityValue = entityField.get(toBeValidated);
        for (ColumnValidator columnValidator : columnValidators) {
            Class<? extends Annotation> validatorAnnotation = columnValidator.getValidatorAnnotation();

            Class<?> proxyClass = Proxy.getProxyClass(Thread.currentThread().getContextClassLoader(), validatorAnnotation);
            Annotation annotationInstance = (Annotation) proxyClass.getConstructor(new Class[]{InvocationHandler.class}).newInstance(new Object[]{new DatabaseValidatorAnnotationHandler(columnValidator.getParams())});

            Class<? extends CustomValidator<?, ?>> validatorClass = columnValidator.getValidatorClass();
            CustomValidator<Annotation, Object> customValidator = (CustomValidator<Annotation, Object>) validatorClass.getConstructor().newInstance();


            ValidationResult result = customValidator.onValidate(validatorAnnotation.cast(annotationInstance), entityValue);
            if (!result.isValid()) {
                return result;
            }
        }
        return ValidationResult.valid();
    }

}
