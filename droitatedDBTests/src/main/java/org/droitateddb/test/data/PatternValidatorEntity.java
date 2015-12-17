package org.droitateddb.test.data;

import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.validation.Pattern;

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
