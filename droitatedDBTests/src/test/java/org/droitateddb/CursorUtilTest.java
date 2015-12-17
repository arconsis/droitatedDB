package org.droitateddb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

@RunWith(RobolectricTestRunner.class)
public class CursorUtilTest {


	@Test(expected = NullPointerException.class)
	public void thrwosNPEForNull() throws Exception {
		CursorUtil.getObjectCursor(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void throwsIllegalArgumentExceptionForWrongCursor() throws Exception {
		CursorUtil.getObjectCursor(new CursorDummy());
	}

	@Test(expected = IllegalStateException.class)
	public void throwsIllegalArgumentExceptionForWrongWrappedCursor() throws Exception {
		CursorUtil.getObjectCursor(new CursorWrapper(new CursorDummy()));
	}

	private final class CursorDummy implements Cursor {
		@Override
		public int getCount() {
			// DUMMY
			return 0;
		}

		@Override
		public int getPosition() {
			// DUMMY
			return 0;
		}

		@Override
		public boolean move(int offset) {
			// DUMMY
			return false;
		}

		@Override
		public boolean moveToPosition(int position) {
			// DUMMY
			return false;
		}

		@Override
		public boolean moveToFirst() {
			// DUMMY
			return false;
		}

		@Override
		public boolean moveToLast() {
			// DUMMY
			return false;
		}

		@Override
		public boolean moveToNext() {
			// DUMMY
			return false;
		}

		@Override
		public boolean moveToPrevious() {
			// DUMMY
			return false;
		}

		@Override
		public boolean isFirst() {
			// DUMMY
			return false;
		}

		@Override
		public boolean isLast() {
			// DUMMY
			return false;
		}

		@Override
		public boolean isBeforeFirst() {
			// DUMMY
			return false;
		}

		@Override
		public boolean isAfterLast() {
			// DUMMY
			return false;
		}

		@Override
		public int getColumnIndex(String columnName) {
			// DUMMY
			return 0;
		}

		@Override
		public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
			// DUMMY
			return 0;
		}

		@Override
		public String getColumnName(int columnIndex) {
			// DUMMY
			return null;
		}

		@Override
		public String[] getColumnNames() {
			// DUMMY
			return null;
		}

		@Override
		public int getColumnCount() {
			// DUMMY
			return 0;
		}

		@Override
		public byte[] getBlob(int columnIndex) {
			// DUMMY
			return null;
		}

		@Override
		public String getString(int columnIndex) {
			// DUMMY
			return null;
		}

		@Override
		public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
			// DUMMY

		}

		@Override
		public short getShort(int columnIndex) {
			// DUMMY
			return 0;
		}

		@Override
		public int getInt(int columnIndex) {
			// DUMMY
			return 0;
		}

		@Override
		public long getLong(int columnIndex) {
			// DUMMY
			return 0;
		}

		@Override
		public float getFloat(int columnIndex) {
			// DUMMY
			return 0;
		}

		@Override
		public double getDouble(int columnIndex) {
			// DUMMY
			return 0;
		}

		@Override
		public int getType(int columnIndex) {
			// DUMMY
			return 0;
		}

		@Override
		public boolean isNull(int columnIndex) {
			// DUMMY
			return false;
		}

		@Override
		public void deactivate() {
			// DUMMY

		}

		@Override
		public boolean requery() {
			// DUMMY
			return false;
		}

		@Override
		public void close() {
			// DUMMY

		}

		@Override
		public boolean isClosed() {
			// DUMMY
			return false;
		}

		@Override
		public void registerContentObserver(ContentObserver observer) {
			// DUMMY

		}

		@Override
		public void unregisterContentObserver(ContentObserver observer) {
			// DUMMY

		}

		@Override
		public void registerDataSetObserver(DataSetObserver observer) {
			// DUMMY

		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			// DUMMY

		}

		@Override
		public void setNotificationUri(ContentResolver cr, Uri uri) {
			// DUMMY

		}

		@Override
		public boolean getWantsAllOnMoveCalls() {
			// DUMMY
			return false;
		}

		@Override
		public Bundle getExtras() {
			// DUMMY
			return null;
		}

		@Override
		public Bundle respond(Bundle extras) {
			// DUMMY
			return null;
		}
	}

}
