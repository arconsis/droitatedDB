/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;
import org.droitateddb.*;
public class SimpleWithoutAutoIncrementContentProvider extends BaseContentProvider {

    @Override
    protected String getEntityURIPart() {
        return "simplewithoutautoincrement";
    }

    @Override
    protected String getAuthority() {
    return "org.droitateddb.test.data.generated.provider.simplewithoutautoincrement";
    }

    @Override
    protected EntityInfo getEntityInfo() {
        return DB.SimpleWithoutAutoIncrementInfo;
    }

    @Override
    protected AbstractAttribute getIdAttribute() {
        return DB.SimpleWithoutAutoIncrementTable._ID;
    }
}