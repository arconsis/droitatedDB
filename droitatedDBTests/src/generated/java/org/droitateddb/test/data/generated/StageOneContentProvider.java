/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;
import org.droitateddb.*;
public class StageOneContentProvider extends BaseContentProvider {

    @Override
    protected String getEntityURIPart() {
        return "stageone";
    }

    @Override
    protected String getAuthority() {
    return "org.droitateddb.test.data.generated.provider.stageone";
    }

    @Override
    protected EntityInfo getEntityInfo() {
        return DB.StageOneInfo;
    }

    @Override
    protected AbstractAttribute getIdAttribute() {
        return DB.StageOneTable._ID;
    }
}