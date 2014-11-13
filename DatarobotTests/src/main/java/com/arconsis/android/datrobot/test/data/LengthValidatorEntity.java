package com.arconsis.android.datrobot.test.data;

import com.arconsis.android.datarobot.entity.AutoIncrement;
import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;
import com.arconsis.android.datarobot.validation.Length;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity(contentProvider = false)
public class LengthValidatorEntity {
    @Column
    @PrimaryKey
    @AutoIncrement
    private Integer _id;
    @Column
    @Length(min = 0, max = 10)
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
