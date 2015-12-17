package org.droitateddb.test.data;

import org.droitateddb.entity.*;
import org.droitateddb.validation.Length;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity(contentProvider = false)
public class RelatedToValidatingEntity {
    @Column
    @PrimaryKey
    @AutoIncrement
    private Integer _id;

    @Column
    @Length(min = 5)
    private String data;

    @Relationship
    private MultiValidatorEntity related;

    public void setData(String data) {
        this.data = data;
    }

    public void setRelated(MultiValidatorEntity related) {
        this.related = related;
    }
}
