package com.arconsis.android.datrobot.test.data;

import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;
import com.arconsis.android.datarobot.validation.Max;
import com.arconsis.android.datarobot.validation.Min;
import com.arconsis.android.datarobot.validation.NotNull;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity(contentProvider = false)
public class MultiValidatorEntity {
    @Column
    @PrimaryKey
    private Integer _id;

    @Column
    @Min(4)
    @Max(25)
    @NotNull
    private Integer foo;

    public void setFoo(int foo) {
        this.foo = foo;
    }
}
