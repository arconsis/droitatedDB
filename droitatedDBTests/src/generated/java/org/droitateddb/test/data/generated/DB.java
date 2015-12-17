/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;

public interface DB {

    public static final String DB_NAME = "test.db";
    public static final String DB_CREATE_HOOK = "org.droitateddb.test.data.UpdateHook";
    public static final String DB_UPDATE_HOOK = "org.droitateddb.test.data.UpdateHook";
    public static final int DB_VERSION = 1;

    public static final EntityInfo BidirectionalOneInfo = new EntityInfo("org.droitateddb.test.data.BidirectionalOne", "BidirectionalOne", BidirectionalOneTable.class, false);
    public static final EntityInfo CollectionRelatedValidatorEntityInfo = new EntityInfo("org.droitateddb.test.data.CollectionRelatedValidatorEntity", "CollectionRelatedValidatorEntity", CollectionRelatedValidatorEntityTable.class, false);
    public static final EntityInfo TextInfo = new EntityInfo("org.droitateddb.test.data.Text", "Text", TextTable.class, false);
    public static final EntityInfo SimpleInfo = new EntityInfo("org.droitateddb.test.data.Simple", "Simple", SimpleTable.class, false);
    public static final EntityInfo LengthValidatorEntityInfo = new EntityInfo("org.droitateddb.test.data.LengthValidatorEntity", "LengthValidatorEntity", LengthValidatorEntityTable.class, true);
    public static final EntityInfo SimpleWithoutAutoIncrementInfo = new EntityInfo("org.droitateddb.test.data.SimpleWithoutAutoIncrement", "SimpleWithoutAutoIncrement", SimpleWithoutAutoIncrementTable.class, false);
    public static final EntityInfo SingleInfo = new EntityInfo("org.droitateddb.test.data.Single", "Single", SingleTable.class, false);
    public static final EntityInfo BidirectionalTwoInfo = new EntityInfo("org.droitateddb.test.data.BidirectionalTwo", "BidirectionalTwo", BidirectionalTwoTable.class, false);
    public static final EntityInfo PatternValidatorEntityInfo = new EntityInfo("org.droitateddb.test.data.PatternValidatorEntity", "PatternValidatorEntity", PatternValidatorEntityTable.class, true);
    public static final EntityInfo TwoCircularValidationEntityInfo = new EntityInfo("org.droitateddb.test.data.TwoCircularValidationEntity", "TwoCircularValidationEntity", TwoCircularValidationEntityTable.class, true);
    public static final EntityInfo StageTwoInfo = new EntityInfo("org.droitateddb.test.data.StageTwo", "StageTwo", StageTwoTable.class, false);
    public static final EntityInfo OneCircularValidationEntityInfo = new EntityInfo("org.droitateddb.test.data.OneCircularValidationEntity", "OneCircularValidationEntity", OneCircularValidationEntityTable.class, true);
    public static final EntityInfo RelatedToValidatingEntityInfo = new EntityInfo("org.droitateddb.test.data.RelatedToValidatingEntity", "RelatedToValidatingEntity", RelatedToValidatingEntityTable.class, true);
    public static final EntityInfo AuthorInfo = new EntityInfo("org.droitateddb.test.data.Author", "Author", AuthorTable.class, false);
    public static final EntityInfo CommentInfo = new EntityInfo("org.droitateddb.test.data.Comment", "Comment", CommentTable.class, false);
    public static final EntityInfo WithAssociationInfo = new EntityInfo("org.droitateddb.test.data.WithAssociation", "WithAssociation", WithAssociationTable.class, false);
    public static final EntityInfo StageOneInfo = new EntityInfo("org.droitateddb.test.data.StageOne", "StageOne", StageOneTable.class, false);
    public static final EntityInfo MultiValidatorEntityInfo = new EntityInfo("org.droitateddb.test.data.MultiValidatorEntity", "MultiValidatorEntity", MultiValidatorEntityTable.class, true);
    public static final EntityInfo PrimitiveValidatorEntityInfo = new EntityInfo("org.droitateddb.test.data.PrimitiveValidatorEntity", "PrimitiveValidatorEntity", PrimitiveValidatorEntityTable.class, true);

    public interface BidirectionalOneTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.BidirectionalOne";
        public static final String TABLE_NAME = "BidirectionalOne";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute NAME = new TextAttribute("name", java.lang.String.class, 1);
        public static final IntegerAttribute FK_ENTITY = new IntegerAttribute("entity", "fk_entity", org.droitateddb.test.data.BidirectionalTwo.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE BidirectionalOne (_id Integer PRIMARY KEY AUTOINCREMENT, name Text, fk_entity Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX bidirectionalone__id_idx on BidirectionalOne (_id)";
        public static final String SQL_INDEX_FK_ENTITY = "CREATE INDEX bidirectionalone_fk_entity_idx on BidirectionalOne (fk_entity)";
        public static final String[] PROJECTION = new String[]{"_id", "name", "fk_entity"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, NAME};

        public interface Associations {
            public static final ToOneAssociation ENTITY = new ToOneAssociation("entity", org.droitateddb.test.data.BidirectionalTwo.class, FK_ENTITY);
        }
    }

    public interface CollectionRelatedValidatorEntityTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.CollectionRelatedValidatorEntity";
        public static final String TABLE_NAME = "CollectionRelatedValidatorEntity";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);

        public static final String SQL_CREATION = "CREATE TABLE CollectionRelatedValidatorEntity (_id Integer PRIMARY KEY AUTOINCREMENT)";
        public static final String SQL_INDEX__ID = "CREATE INDEX collectionrelatedvalidatorentity__id_idx on CollectionRelatedValidatorEntity (_id)";
        public static final String[] PROJECTION = new String[]{"_id"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID};

        public interface Associations {
            public static final ToManyAssociation RELATED = new ToManyAssociation("related", org.droitateddb.test.data.LengthValidatorEntity.class, CollectionRelatedValidatorEntityLengthValidatorEntityAssociation.class);
        }
    }

    public interface TextTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.Text";
        public static final String TABLE_NAME = "Text";

        public static final IntegerAttribute ID = new IntegerAttribute("id", java.lang.Integer.class, 0);
        public static final TextAttribute NAME = new TextAttribute("name", java.lang.String.class, 1);

        public static final String SQL_CREATION = "CREATE TABLE Text (id Integer PRIMARY KEY AUTOINCREMENT, name Text)";
        public static final String SQL_INDEX_ID = "CREATE INDEX text_id_idx on Text (id)";
        public static final String[] PROJECTION = new String[]{"id", "name"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{ID, NAME};

        public interface Associations {
            public static final ToManyAssociation AUTHORS = new ToManyAssociation("authors", org.droitateddb.test.data.Author.class, TextAuthorAssociation.class);
        }
    }

    public interface SimpleTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.Simple";
        public static final String TABLE_NAME = "Simple";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final IntegerAttribute BIGBOOLEAN = new IntegerAttribute("bigBoolean", java.lang.Boolean.class, 1);
        public static final RealAttribute BIGDOUBLE = new RealAttribute("bigDouble", java.lang.Double.class, 2);
        public static final RealAttribute BIGFLOAT = new RealAttribute("bigFloat", java.lang.Float.class, 3);
        public static final IntegerAttribute BIGLONG = new IntegerAttribute("bigLong", java.lang.Long.class, 4);
        public static final IntegerAttribute MYBOOLEAN = new IntegerAttribute("myBoolean", boolean.class, 5);
        public static final IntegerAttribute MYDATE = new IntegerAttribute("myDate", java.util.Date.class, 6);
        public static final RealAttribute MYDOUBLE = new RealAttribute("myDouble", double.class, 7);
        public static final RealAttribute MYFLOAT = new RealAttribute("myFloat", float.class, 8);
        public static final IntegerAttribute MYINT = new IntegerAttribute("myInt", int.class, 9);
        public static final TextAttribute MYSTRING = new TextAttribute("myString", java.lang.String.class, 10);
        public static final IntegerAttribute SOLONG = new IntegerAttribute("soLong", long.class, 11);
        public static final BlobAttribute SOMEBYTES = new BlobAttribute("someBytes", byte[].class, 12);

        public static final String SQL_CREATION = "CREATE TABLE Simple (_id Integer PRIMARY KEY AUTOINCREMENT, bigBoolean Integer, bigDouble Real, bigFloat Real, bigLong Integer, myBoolean Integer, myDate Integer, myDouble Real, myFloat Real, myInt Integer, myString Text, soLong Integer, someBytes Blob)";
        public static final String SQL_INDEX__ID = "CREATE INDEX simple__id_idx on Simple (_id)";
        public static final String[] PROJECTION = new String[]{"_id", "bigBoolean", "bigDouble", "bigFloat", "bigLong", "myBoolean", "myDate", "myDouble", "myFloat", "myInt", "myString", "soLong", "someBytes"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, BIGBOOLEAN, BIGDOUBLE, BIGFLOAT, BIGLONG, MYBOOLEAN, MYDATE, MYDOUBLE, MYFLOAT, MYINT, MYSTRING, SOLONG, SOMEBYTES};

    }

    public interface LengthValidatorEntityTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.LengthValidatorEntity";
        public static final String TABLE_NAME = "LengthValidatorEntity";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute DATA = new TextAttribute("data", java.lang.String.class, 1, new ColumnValidator(org.droitateddb.validation.Length.class, org.droitateddb.validation.LengthValidator.class, "min", 0, "max", 10));

        public static final String SQL_CREATION = "CREATE TABLE LengthValidatorEntity (_id Integer PRIMARY KEY AUTOINCREMENT, data Text)";
        public static final String SQL_INDEX__ID = "CREATE INDEX lengthvalidatorentity__id_idx on LengthValidatorEntity (_id)";
        public static final String[] PROJECTION = new String[]{"_id", "data"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, DATA};

    }

    public interface SimpleWithoutAutoIncrementTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.SimpleWithoutAutoIncrement";
        public static final String TABLE_NAME = "SimpleWithoutAutoIncrement";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute MYSTRING = new TextAttribute("myString", java.lang.String.class, 1);
        public static final IntegerAttribute FK_COMMENT = new IntegerAttribute("comment", "fk_comment", org.droitateddb.test.data.Comment.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE SimpleWithoutAutoIncrement (_id Integer PRIMARY KEY, myString Text, fk_comment Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX simplewithoutautoincrement__id_idx on SimpleWithoutAutoIncrement (_id)";
        public static final String SQL_INDEX_FK_COMMENT = "CREATE INDEX simplewithoutautoincrement_fk_comment_idx on SimpleWithoutAutoIncrement (fk_comment)";
        public static final String[] PROJECTION = new String[]{"_id", "myString", "fk_comment"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, MYSTRING};

        public interface Associations {
            public static final ToOneAssociation COMMENT = new ToOneAssociation("comment", org.droitateddb.test.data.Comment.class, FK_COMMENT);
        }
    }

    public interface SingleTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.Single";
        public static final String TABLE_NAME = "Single";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute MYSTRING = new TextAttribute("myString", java.lang.String.class, 1);

        public static final String SQL_CREATION = "CREATE TABLE Single (_id Integer PRIMARY KEY AUTOINCREMENT, myString Text)";
        public static final String SQL_INDEX__ID = "CREATE INDEX single__id_idx on Single (_id)";
        public static final String[] PROJECTION = new String[]{"_id", "myString"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, MYSTRING};

    }

    public interface BidirectionalTwoTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.BidirectionalTwo";
        public static final String TABLE_NAME = "BidirectionalTwo";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute NAME = new TextAttribute("name", java.lang.String.class, 1);
        public static final IntegerAttribute FK_ENTITY = new IntegerAttribute("entity", "fk_entity", org.droitateddb.test.data.BidirectionalOne.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE BidirectionalTwo (_id Integer PRIMARY KEY AUTOINCREMENT, name Text, fk_entity Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX bidirectionaltwo__id_idx on BidirectionalTwo (_id)";
        public static final String SQL_INDEX_FK_ENTITY = "CREATE INDEX bidirectionaltwo_fk_entity_idx on BidirectionalTwo (fk_entity)";
        public static final String[] PROJECTION = new String[]{"_id", "name", "fk_entity"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, NAME};

        public interface Associations {
            public static final ToOneAssociation ENTITY = new ToOneAssociation("entity", org.droitateddb.test.data.BidirectionalOne.class, FK_ENTITY);
        }
    }

    public interface PatternValidatorEntityTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.PatternValidatorEntity";
        public static final String TABLE_NAME = "PatternValidatorEntity";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute DATA = new TextAttribute("data", java.lang.String.class, 1, new ColumnValidator(org.droitateddb.validation.Pattern.class, org.droitateddb.validation.PatternValidator.class, "value", "[0-9]{1,}"));

        public static final String SQL_CREATION = "CREATE TABLE PatternValidatorEntity (_id Integer PRIMARY KEY AUTOINCREMENT, data Text)";
        public static final String SQL_INDEX__ID = "CREATE INDEX patternvalidatorentity__id_idx on PatternValidatorEntity (_id)";
        public static final String[] PROJECTION = new String[]{"_id", "data"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, DATA};

    }

    public interface TwoCircularValidationEntityTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.TwoCircularValidationEntity";
        public static final String TABLE_NAME = "TwoCircularValidationEntity";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final IntegerAttribute DATA = new IntegerAttribute("data", int.class, 1, new ColumnValidator(org.droitateddb.validation.Max.class, org.droitateddb.validation.MaxValidator.class, "value", 10));
        public static final IntegerAttribute FK_CIRCLE = new IntegerAttribute("circle", "fk_circle", org.droitateddb.test.data.OneCircularValidationEntity.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE TwoCircularValidationEntity (_id Integer PRIMARY KEY AUTOINCREMENT, data Integer, fk_circle Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX twocircularvalidationentity__id_idx on TwoCircularValidationEntity (_id)";
        public static final String SQL_INDEX_FK_CIRCLE = "CREATE INDEX twocircularvalidationentity_fk_circle_idx on TwoCircularValidationEntity (fk_circle)";
        public static final String[] PROJECTION = new String[]{"_id", "data", "fk_circle"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, DATA};

        public interface Associations {
            public static final ToOneAssociation CIRCLE = new ToOneAssociation("circle", org.droitateddb.test.data.OneCircularValidationEntity.class, FK_CIRCLE);
        }
    }

    public interface StageTwoTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.StageTwo";
        public static final String TABLE_NAME = "StageTwo";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute NAME = new TextAttribute("name", java.lang.String.class, 1);
        public static final IntegerAttribute FK_ENTITY = new IntegerAttribute("entity", "fk_entity", org.droitateddb.test.data.Simple.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE StageTwo (_id Integer PRIMARY KEY AUTOINCREMENT, name Text, fk_entity Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX stagetwo__id_idx on StageTwo (_id)";
        public static final String SQL_INDEX_FK_ENTITY = "CREATE INDEX stagetwo_fk_entity_idx on StageTwo (fk_entity)";
        public static final String[] PROJECTION = new String[]{"_id", "name", "fk_entity"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, NAME};

        public interface Associations {
            public static final ToOneAssociation ENTITY = new ToOneAssociation("entity", org.droitateddb.test.data.Simple.class, FK_ENTITY);
        }
    }

    public interface OneCircularValidationEntityTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.OneCircularValidationEntity";
        public static final String TABLE_NAME = "OneCircularValidationEntity";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final IntegerAttribute DATA = new IntegerAttribute("data", int.class, 1, new ColumnValidator(org.droitateddb.validation.Max.class, org.droitateddb.validation.MaxValidator.class, "value", 10));
        public static final IntegerAttribute FK_CIRCLE = new IntegerAttribute("circle", "fk_circle", org.droitateddb.test.data.TwoCircularValidationEntity.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE OneCircularValidationEntity (_id Integer PRIMARY KEY AUTOINCREMENT, data Integer, fk_circle Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX onecircularvalidationentity__id_idx on OneCircularValidationEntity (_id)";
        public static final String SQL_INDEX_FK_CIRCLE = "CREATE INDEX onecircularvalidationentity_fk_circle_idx on OneCircularValidationEntity (fk_circle)";
        public static final String[] PROJECTION = new String[]{"_id", "data", "fk_circle"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, DATA};

        public interface Associations {
            public static final ToOneAssociation CIRCLE = new ToOneAssociation("circle", org.droitateddb.test.data.TwoCircularValidationEntity.class, FK_CIRCLE);
        }
    }

    public interface RelatedToValidatingEntityTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.RelatedToValidatingEntity";
        public static final String TABLE_NAME = "RelatedToValidatingEntity";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute DATA = new TextAttribute("data", java.lang.String.class, 1, new ColumnValidator(org.droitateddb.validation.Length.class, org.droitateddb.validation.LengthValidator.class, "min", 5, "max", -1));
        public static final IntegerAttribute FK_RELATED = new IntegerAttribute("related", "fk_related", org.droitateddb.test.data.MultiValidatorEntity.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE RelatedToValidatingEntity (_id Integer PRIMARY KEY AUTOINCREMENT, data Text, fk_related Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX relatedtovalidatingentity__id_idx on RelatedToValidatingEntity (_id)";
        public static final String SQL_INDEX_FK_RELATED = "CREATE INDEX relatedtovalidatingentity_fk_related_idx on RelatedToValidatingEntity (fk_related)";
        public static final String[] PROJECTION = new String[]{"_id", "data", "fk_related"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, DATA};

        public interface Associations {
            public static final ToOneAssociation RELATED = new ToOneAssociation("related", org.droitateddb.test.data.MultiValidatorEntity.class, FK_RELATED);
        }
    }

    public interface AuthorTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.Author";
        public static final String TABLE_NAME = "Author";

        public static final IntegerAttribute ID = new IntegerAttribute("id", java.lang.Integer.class, 0);
        public static final TextAttribute NAME = new TextAttribute("name", java.lang.String.class, 1);

        public static final String SQL_CREATION = "CREATE TABLE Author (id Integer PRIMARY KEY AUTOINCREMENT, name Text)";
        public static final String SQL_INDEX_ID = "CREATE INDEX author_id_idx on Author (id)";
        public static final String[] PROJECTION = new String[]{"id", "name"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{ID, NAME};

        public interface Associations {
            public static final ToManyAssociation COMMENTS = new ToManyAssociation("comments", org.droitateddb.test.data.Comment.class, AuthorCommentAssociation.class);
            public static final ToManyAssociation TEXTS = new ToManyAssociation("texts", org.droitateddb.test.data.Text.class, AuthorTextAssociation.class);
        }
    }

    public interface CommentTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.Comment";
        public static final String TABLE_NAME = "Comment";

        public static final IntegerAttribute ID = new IntegerAttribute("id", java.lang.Integer.class, 0);
        public static final TextAttribute NAME = new TextAttribute("name", java.lang.String.class, 1);
        public static final IntegerAttribute FK_AUTHOR = new IntegerAttribute("author", "fk_author", org.droitateddb.test.data.Author.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE Comment (id Integer PRIMARY KEY AUTOINCREMENT, name Text, fk_author Integer)";
        public static final String SQL_INDEX_ID = "CREATE INDEX comment_id_idx on Comment (id)";
        public static final String SQL_INDEX_FK_AUTHOR = "CREATE INDEX comment_fk_author_idx on Comment (fk_author)";
        public static final String[] PROJECTION = new String[]{"id", "name", "fk_author"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{ID, NAME};

        public interface Associations {
            public static final ToOneAssociation AUTHOR = new ToOneAssociation("author", org.droitateddb.test.data.Author.class, FK_AUTHOR);
        }
    }

    public interface WithAssociationTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.WithAssociation";
        public static final String TABLE_NAME = "WithAssociation";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute NAME = new TextAttribute("name", java.lang.String.class, 1);
        public static final IntegerAttribute FK_ENTITY = new IntegerAttribute("entity", "fk_entity", org.droitateddb.test.data.Simple.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE WithAssociation (_id Integer PRIMARY KEY AUTOINCREMENT, name Text, fk_entity Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX withassociation__id_idx on WithAssociation (_id)";
        public static final String SQL_INDEX_FK_ENTITY = "CREATE INDEX withassociation_fk_entity_idx on WithAssociation (fk_entity)";
        public static final String[] PROJECTION = new String[]{"_id", "name", "fk_entity"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, NAME};

        public interface Associations {
            public static final ToOneAssociation ENTITY = new ToOneAssociation("entity", org.droitateddb.test.data.Simple.class, FK_ENTITY);
        }
    }

    public interface StageOneTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.StageOne";
        public static final String TABLE_NAME = "StageOne";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final TextAttribute NAME = new TextAttribute("name", java.lang.String.class, 1);
        public static final IntegerAttribute FK_ENTITY = new IntegerAttribute("entity", "fk_entity", org.droitateddb.test.data.StageTwo.class, 2);

        public static final String SQL_CREATION = "CREATE TABLE StageOne (_id Integer PRIMARY KEY AUTOINCREMENT, name Text, fk_entity Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX stageone__id_idx on StageOne (_id)";
        public static final String SQL_INDEX_FK_ENTITY = "CREATE INDEX stageone_fk_entity_idx on StageOne (fk_entity)";
        public static final String[] PROJECTION = new String[]{"_id", "name", "fk_entity"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, NAME};

        public interface Associations {
            public static final ToOneAssociation ENTITY = new ToOneAssociation("entity", org.droitateddb.test.data.StageTwo.class, FK_ENTITY);
        }
    }

    public interface MultiValidatorEntityTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.MultiValidatorEntity";
        public static final String TABLE_NAME = "MultiValidatorEntity";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final IntegerAttribute FOO = new IntegerAttribute("foo", java.lang.Integer.class, 1, new ColumnValidator(org.droitateddb.validation.Min.class, org.droitateddb.validation.MinValidator.class, "value", 4), new ColumnValidator(org.droitateddb.validation.Max.class, org.droitateddb.validation.MaxValidator.class, "value", 25), new ColumnValidator(org.droitateddb.validation.NotNull.class, org.droitateddb.validation.NotNullValidator.class));

        public static final String SQL_CREATION = "CREATE TABLE MultiValidatorEntity (_id Integer PRIMARY KEY, foo Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX multivalidatorentity__id_idx on MultiValidatorEntity (_id)";
        public static final String[] PROJECTION = new String[]{"_id", "foo"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, FOO};

    }

    public interface PrimitiveValidatorEntityTable {
        public static final String CLASS_NAME = "org.droitateddb.test.data.PrimitiveValidatorEntity";
        public static final String TABLE_NAME = "PrimitiveValidatorEntity";

        public static final IntegerAttribute _ID = new IntegerAttribute("_id", java.lang.Integer.class, 0);
        public static final IntegerAttribute FOO = new IntegerAttribute("foo", int.class, 1, new ColumnValidator(org.droitateddb.validation.Min.class, org.droitateddb.validation.MinValidator.class, "value", 5));

        public static final String SQL_CREATION = "CREATE TABLE PrimitiveValidatorEntity (_id Integer PRIMARY KEY AUTOINCREMENT, foo Integer)";
        public static final String SQL_INDEX__ID = "CREATE INDEX primitivevalidatorentity__id_idx on PrimitiveValidatorEntity (_id)";
        public static final String[] PROJECTION = new String[]{"_id", "foo"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{_ID, FOO};

    }

    public interface CollectionRelatedValidatorEntityLengthValidatorEntityAssociation {
        public static final String TABLE_NAME = "CollectionRelatedValidatorEntityLengthValidatorEntityAssociation";

        public static final IntegerAttribute FK_COLLECTIONRELATEDVALIDATORENTITY_FROM = new IntegerAttribute("", "fk_collectionrelatedvalidatorentity_from", org.droitateddb.test.data.CollectionRelatedValidatorEntity.class, 0);
        public static final IntegerAttribute FK_LENGTHVALIDATORENTITY_TO = new IntegerAttribute("", "fk_lengthvalidatorentity_to", org.droitateddb.test.data.LengthValidatorEntity.class, 1);

        public static final String SQL_CREATION = "CREATE TABLE CollectionRelatedValidatorEntityLengthValidatorEntityAssociation(fk_collectionrelatedvalidatorentity_from Integer, fk_lengthvalidatorentity_to Integer, UNIQUE(fk_collectionrelatedvalidatorentity_from, fk_lengthvalidatorentity_to) ON CONFLICT IGNORE)";
        public static final String SQL_INDEX_fk_collectionrelatedvalidatorentity_from = "CREATE INDEX collectionrelatedvalidatorentitylengthvalidatorentityassociation_fk_collectionrelatedvalidatorentity_from_idx on CollectionRelatedValidatorEntityLengthValidatorEntityAssociation (fk_collectionrelatedvalidatorentity_from)";
        public static final String SQL_INDEX_fk_lengthvalidatorentity_to = "CREATE INDEX collectionrelatedvalidatorentitylengthvalidatorentityassociation_fk_lengthvalidatorentity_to_idx on CollectionRelatedValidatorEntityLengthValidatorEntityAssociation (fk_lengthvalidatorentity_to)";
        public static final String[] PROJECTION = new String[]{"fk_collectionrelatedvalidatorentity_from", "fk_lengthvalidatorentity_to"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{FK_COLLECTIONRELATEDVALIDATORENTITY_FROM, FK_LENGTHVALIDATORENTITY_TO};
    }
    public interface AuthorCommentAssociation {
        public static final String TABLE_NAME = "AuthorCommentAssociation";

        public static final IntegerAttribute FK_AUTHOR_FROM = new IntegerAttribute("", "fk_author_from", org.droitateddb.test.data.Author.class, 0);
        public static final IntegerAttribute FK_COMMENT_TO = new IntegerAttribute("", "fk_comment_to", org.droitateddb.test.data.Comment.class, 1);

        public static final String SQL_CREATION = "CREATE TABLE AuthorCommentAssociation(fk_author_from Integer, fk_comment_to Integer, UNIQUE(fk_author_from, fk_comment_to) ON CONFLICT IGNORE)";
        public static final String SQL_INDEX_fk_author_from = "CREATE INDEX authorcommentassociation_fk_author_from_idx on AuthorCommentAssociation (fk_author_from)";
        public static final String SQL_INDEX_fk_comment_to = "CREATE INDEX authorcommentassociation_fk_comment_to_idx on AuthorCommentAssociation (fk_comment_to)";
        public static final String[] PROJECTION = new String[]{"fk_author_from", "fk_comment_to"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{FK_AUTHOR_FROM, FK_COMMENT_TO};
    }
    public interface AuthorTextAssociation {
        public static final String TABLE_NAME = "AuthorTextAssociation";

        public static final IntegerAttribute FK_AUTHOR_FROM = new IntegerAttribute("", "fk_author_from", org.droitateddb.test.data.Author.class, 0);
        public static final IntegerAttribute FK_TEXT_TO = new IntegerAttribute("", "fk_text_to", org.droitateddb.test.data.Text.class, 1);

        public static final String SQL_CREATION = "CREATE TABLE AuthorTextAssociation(fk_author_from Integer, fk_text_to Integer, UNIQUE(fk_author_from, fk_text_to) ON CONFLICT IGNORE)";
        public static final String SQL_INDEX_fk_author_from = "CREATE INDEX authortextassociation_fk_author_from_idx on AuthorTextAssociation (fk_author_from)";
        public static final String SQL_INDEX_fk_text_to = "CREATE INDEX authortextassociation_fk_text_to_idx on AuthorTextAssociation (fk_text_to)";
        public static final String[] PROJECTION = new String[]{"fk_author_from", "fk_text_to"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{FK_AUTHOR_FROM, FK_TEXT_TO};
    }
    public interface TextAuthorAssociation {
        public static final String TABLE_NAME = "TextAuthorAssociation";

        public static final IntegerAttribute FK_TEXT_FROM = new IntegerAttribute("", "fk_text_from", org.droitateddb.test.data.Text.class, 0);
        public static final IntegerAttribute FK_AUTHOR_TO = new IntegerAttribute("", "fk_author_to", org.droitateddb.test.data.Author.class, 1);

        public static final String SQL_CREATION = "CREATE TABLE TextAuthorAssociation(fk_text_from Integer, fk_author_to Integer, UNIQUE(fk_text_from, fk_author_to) ON CONFLICT IGNORE)";
        public static final String SQL_INDEX_fk_text_from = "CREATE INDEX textauthorassociation_fk_text_from_idx on TextAuthorAssociation (fk_text_from)";
        public static final String SQL_INDEX_fk_author_to = "CREATE INDEX textauthorassociation_fk_author_to_idx on TextAuthorAssociation (fk_author_to)";
        public static final String[] PROJECTION = new String[]{"fk_text_from", "fk_author_to"};
        public static final AbstractAttribute[] ATTRIBUTES = new AbstractAttribute[]{FK_TEXT_FROM, FK_AUTHOR_TO};
    }
}
