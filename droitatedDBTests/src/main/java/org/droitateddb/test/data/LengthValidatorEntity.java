package org.droitateddb.test.data;

import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.validation.Length;

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
