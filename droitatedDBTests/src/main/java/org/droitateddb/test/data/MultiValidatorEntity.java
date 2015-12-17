package org.droitateddb.test.data;

import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;
import org.droitateddb.validation.Max;
import org.droitateddb.validation.Min;
import org.droitateddb.validation.NotNull;

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
