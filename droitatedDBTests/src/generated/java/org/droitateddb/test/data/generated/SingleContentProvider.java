/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;
import org.droitateddb.*;
public class SingleContentProvider extends BaseContentProvider {

    @Override
    protected String getEntityURIPart() {
        return "single";
    }

    @Override
    protected String getAuthority() {
    return "org.droitateddb.test.data.generated.provider.single";
    }

    @Override
    protected EntityInfo getEntityInfo() {
        return DB.SingleInfo;
    }

    @Override
    protected AbstractAttribute getIdAttribute() {
        return DB.SingleTable._ID;
    }
}