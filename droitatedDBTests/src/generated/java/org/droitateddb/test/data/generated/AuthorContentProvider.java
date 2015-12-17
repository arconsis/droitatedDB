/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;
import org.droitateddb.*;
public class AuthorContentProvider extends BaseContentProvider {

    @Override
    protected String getEntityURIPart() {
        return "author";
    }

    @Override
    protected String getAuthority() {
    return "org.droitateddb.test.data.generated.provider.author";
    }

    @Override
    protected EntityInfo getEntityInfo() {
        return DB.AuthorInfo;
    }

    @Override
    protected AbstractAttribute getIdAttribute() {
        return DB.AuthorTable.ID;
    }
}