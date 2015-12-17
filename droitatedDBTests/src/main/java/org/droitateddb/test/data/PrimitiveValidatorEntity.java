package org.droitateddb.test.data;

import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.validation.Min;

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
