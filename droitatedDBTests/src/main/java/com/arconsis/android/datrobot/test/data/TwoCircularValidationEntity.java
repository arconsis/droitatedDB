package com.arconsis.android.datrobot.test.data;

import com.arconsis.android.datarobot.entity.*;
import com.arconsis.android.datarobot.validation.Max;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity(contentProvider = false)
public class TwoCircularValidationEntity {
    @Column
    @PrimaryKey
    @AutoIncrement
    private Integer _id;

    @Column
    @Max(10)
    private int data;

    @Relationship
    private OneCircularValidationEntity circle;

    public void setData(int data) {
        this.data = data;
    }

    public void setCircle(OneCircularValidationEntity circle) {
        this.circle = circle;
    }
}
