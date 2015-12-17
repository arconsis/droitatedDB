/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;
import org.droitateddb.*;
public class TextContentProvider extends BaseContentProvider {

    @Override
    protected String getEntityURIPart() {
        return "text";
    }

    @Override
    protected String getAuthority() {
    return "org.droitateddb.test.data.generated.provider.text";
    }

    @Override
    protected EntityInfo getEntityInfo() {
        return DB.TextInfo;
    }

    @Override
    protected AbstractAttribute getIdAttribute() {
        return DB.TextTable.ID;
    }
}