/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;
import org.droitateddb.*;
public class StageTwoContentProvider extends BaseContentProvider {

    @Override
    protected String getEntityURIPart() {
        return "stagetwo";
    }

    @Override
    protected String getAuthority() {
    return "org.droitateddb.test.data.generated.provider.stagetwo";
    }

    @Override
    protected EntityInfo getEntityInfo() {
        return DB.StageTwoInfo;
    }

    @Override
    protected AbstractAttribute getIdAttribute() {
        return DB.StageTwoTable._ID;
    }
}