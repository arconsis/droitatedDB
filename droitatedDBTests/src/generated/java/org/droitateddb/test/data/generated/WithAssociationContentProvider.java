/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;
import org.droitateddb.*;
public class WithAssociationContentProvider extends BaseContentProvider {

    @Override
    protected String getEntityURIPart() {
        return "withassociation";
    }

    @Override
    protected String getAuthority() {
    return "org.droitateddb.test.data.generated.provider.withassociation";
    }

    @Override
    protected EntityInfo getEntityInfo() {
        return DB.WithAssociationInfo;
    }

    @Override
    protected AbstractAttribute getIdAttribute() {
        return DB.WithAssociationTable._ID;
    }
}