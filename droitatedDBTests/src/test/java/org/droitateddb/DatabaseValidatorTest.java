package org.droitateddb;

import android.content.Context;
import org.droitateddb.test.data.*;
import org.droitateddb.validation.AccumulatedValidationResult;
import org.junit.Before;
import org.junit.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
public class DatabaseValidatorTest {

    private Context context;

    @Before
    public void setup() {
        context = mock(Context.class);
        when(context.getPackageName()).thenReturn("org.droitateddb.test.data");
    }

    @Test
    public void entityWithoutValidators() {
        DatabaseValidator<Simple> validator = new DatabaseValidator<Simple>(context);
        AccumulatedValidationResult result = validator.validate(new Simple());
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void failingLengthValidation() {
        LengthValidatorEntity lengthValidator = new LengthValidatorEntity();
        lengthValidator.setData("12345678901");

        DatabaseValidator<LengthValidatorEntity> validator = new DatabaseValidator<LengthValidatorEntity>(context);
        AccumulatedValidationResult result = validator.validate(lengthValidator);
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void successfulLengthValidation() {
        LengthValidatorEntity lengthValidator = new LengthValidatorEntity();
        lengthValidator.setData("1234567");

        DatabaseValidator<LengthValidatorEntity> validator = new DatabaseValidator<LengthValidatorEntity>(context);
        AccumulatedValidationResult result = validator.validate(lengthValidator);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void successfulMultipleValidation() {
        MultiValidatorEntity multiValidatorEntity = new MultiValidatorEntity();
        multiValidatorEntity.setFoo(15);

        DatabaseValidator<MultiValidatorEntity> validator = new DatabaseValidator<MultiValidatorEntity>(context);
        AccumulatedValidationResult result = validator.validate(multiValidatorEntity);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void failingOnMultipleValidation() {
        DatabaseValidator<MultiValidatorEntity> validator = new DatabaseValidator<MultiValidatorEntity>(context);

        MultiValidatorEntity multiValidatorEntity = new MultiValidatorEntity();
        assertThat(validator.validate(multiValidatorEntity).isValid()).isFalse();

        multiValidatorEntity.setFoo(-5);
        assertThat(validator.validate(multiValidatorEntity).isValid()).isFalse();

        multiValidatorEntity.setFoo(26);
        assertThat(validator.validate(multiValidatorEntity).isValid()).isFalse();
    }

    @Test
    public void successfulPatternValidation() {
        PatternValidatorEntity entity = new PatternValidatorEntity();
        entity.setData("1234");

        DatabaseValidator<PatternValidatorEntity> validator = new DatabaseValidator<PatternValidatorEntity>(context);
        AccumulatedValidationResult result = validator.validate(entity);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void failingPatternValidation() {
        PatternValidatorEntity entity = new PatternValidatorEntity();
        entity.setData("123asd4");

        DatabaseValidator<PatternValidatorEntity> validator = new DatabaseValidator<PatternValidatorEntity>(context);
        AccumulatedValidationResult result = validator.validate(entity);
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void successfulPrimitiveValidation() {
        PrimitiveValidatorEntity entity = new PrimitiveValidatorEntity();
        entity.setFoo(10);

        DatabaseValidator<PrimitiveValidatorEntity> validator = new DatabaseValidator<PrimitiveValidatorEntity>(context);
        AccumulatedValidationResult result = validator.validate(entity);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void failingPrimitiveValidation() {
        PrimitiveValidatorEntity entity = new PrimitiveValidatorEntity();

        DatabaseValidator<PrimitiveValidatorEntity> validator = new DatabaseValidator<PrimitiveValidatorEntity>(context);
        AccumulatedValidationResult result = validator.validate(entity);
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void failingOnRelatedEntity() {
        MultiValidatorEntity invalid = new MultiValidatorEntity();
        invalid.setFoo(125);
        RelatedToValidatingEntity relatedTo = new RelatedToValidatingEntity();
        relatedTo.setData("12345");
        relatedTo.setRelated(invalid);

        DatabaseValidator<RelatedToValidatingEntity> validator = new DatabaseValidator<RelatedToValidatingEntity>(context);
        AccumulatedValidationResult result = validator.validate(relatedTo);
        assertThat(result.isValid()).isFalse();
    }

    @Test
    public void successfulCircularRelatedEntities() {
        OneCircularValidationEntity one = new OneCircularValidationEntity();
        one.setData(5);
        TwoCircularValidationEntity two = new TwoCircularValidationEntity();
        two.setData(4);

        one.setCircle(two);
        two.setCircle(one);

        DatabaseValidator<OneCircularValidationEntity> validator = new DatabaseValidator<OneCircularValidationEntity>(context);
        AccumulatedValidationResult result = validator.validate(one);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void considerDepth() {
        MultiValidatorEntity invalid = new MultiValidatorEntity();
        invalid.setFoo(125);
        RelatedToValidatingEntity relatedTo = new RelatedToValidatingEntity();
        relatedTo.setData("12345");
        relatedTo.setRelated(invalid);

        DatabaseValidator<RelatedToValidatingEntity> validator = new DatabaseValidator<RelatedToValidatingEntity>(context);
        AccumulatedValidationResult result = validator.validate(relatedTo, 0);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    public void collectionRelatedEntities() {
        LengthValidatorEntity entity1 = new LengthValidatorEntity();
        entity1.setData("213f");
        LengthValidatorEntity entity2 = new LengthValidatorEntity();
        entity2.setData("asd3f");
        LengthValidatorEntity entity3 = new LengthValidatorEntity();
        entity3.setData("sdjfn");
        LengthValidatorEntity entity4 = new LengthValidatorEntity();
        entity4.setData("efuweu9fn9ashef79");

        CollectionRelatedValidatorEntity collection = new CollectionRelatedValidatorEntity();
        collection.addRelated(entity1);
        collection.addRelated(entity2);
        collection.addRelated(entity3);
        collection.addRelated(entity4);

        DatabaseValidator<CollectionRelatedValidatorEntity> validator = new DatabaseValidator<CollectionRelatedValidatorEntity>(context);
        AccumulatedValidationResult result = validator.validate(collection);
        assertThat(result.isValid()).isFalse();
    }
}
