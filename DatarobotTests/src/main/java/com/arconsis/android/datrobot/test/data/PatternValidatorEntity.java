package com.arconsis.android.datrobot.test.data;

import com.arconsis.android.datarobot.entity.AutoIncrement;
import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;
import com.arconsis.android.datarobot.validation.Pattern;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity(contentProvider = false)
public class PatternValidatorEntity {
    @PrimaryKey
    @AutoIncrement
    @Column
    private Integer _id;

    @Column
    @Pattern("[0-9]{1,}")
    private String data;

    public void setData(String data) {
        this.data = data;
    }
}
