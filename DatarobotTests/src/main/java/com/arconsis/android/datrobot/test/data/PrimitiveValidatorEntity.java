package com.arconsis.android.datrobot.test.data;

import com.arconsis.android.datarobot.entity.AutoIncrement;
import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;
import com.arconsis.android.datarobot.validation.Min;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity(contentProvider = false)
public class PrimitiveValidatorEntity {
    @PrimaryKey
    @AutoIncrement
    @Column
    private Integer _id;

    @Column
    @Min(5)
    private int foo;

    public void setFoo(int foo) {
        this.foo = foo;
    }
}
