package org.droitateddb.test.data;

import org.droitateddb.entity.*;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity(contentProvider = false)
public class CollectionRelatedValidatorEntity {
    @PrimaryKey
    @AutoIncrement
    @Column
    private Integer _id;

    @Relationship
    private Collection<LengthValidatorEntity> related = new LinkedList<LengthValidatorEntity>();

    public void addRelated(LengthValidatorEntity entity) {
        related.add(entity);
    }
}
