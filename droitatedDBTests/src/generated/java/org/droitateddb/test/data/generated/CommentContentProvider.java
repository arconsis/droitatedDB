/** Automatically generated file. DO NOT MODIFY */
package org.droitateddb.test.data.generated;

import org.droitateddb.schema.*;
import org.droitateddb.*;
public class CommentContentProvider extends BaseContentProvider {

    @Override
    protected String getEntityURIPart() {
        return "comment";
    }

    @Override
    protected String getAuthority() {
    return "org.droitateddb.test.data.generated.provider.comment";
    }

    @Override
    protected EntityInfo getEntityInfo() {
        return DB.CommentInfo;
    }

    @Override
    protected AbstractAttribute getIdAttribute() {
        return DB.CommentTable.ID;
    }
}