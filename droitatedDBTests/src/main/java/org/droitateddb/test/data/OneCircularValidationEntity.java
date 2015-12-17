package org.droitateddb.test.data;

import org.droitateddb.entity.*;
import org.droitateddb.validation.Max;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity(contentProvider = false)
public class OneCircularValidationEntity {
    @Column
    @PrimaryKey
    @AutoIncrement
    private Integer _id;

    @Column
    @Max(10)
    private int data;

    @Relationship
    private TwoCircularValidationEntity circle;

    public void setData(int data) {
        this.data = data;
    }

    public void setCircle(TwoCircularValidationEntity circle) {
        this.circle = circle;
    }
}
