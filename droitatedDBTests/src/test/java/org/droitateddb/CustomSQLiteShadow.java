package org.droitateddb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.CancellationSignal;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowSQLiteDatabase;

/**
 *
 * Hack for robolectric described here: http://stackoverflow.com/questions/16156711/android-robolectric-runtimeexception-instantiationexception
 */
@Implements(value = SQLiteDatabase.class, inheritImplementationMethods = true)
public class CustomSQLiteShadow extends ShadowSQLiteDatabase {

    @Implementation
    public Cursor rawQueryWithFactory(final SQLiteDatabase.CursorFactory cursorFactory, final String sql, final String[] selectionArgs, final String editTable,
                                      @SuppressWarnings("unused") final CancellationSignal cancellationSignal) {
        return rawQueryWithFactory(cursorFactory, sql, selectionArgs, editTable);
    }
}