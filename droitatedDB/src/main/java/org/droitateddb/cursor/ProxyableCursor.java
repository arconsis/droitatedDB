/*
 * Copyright (C) 2014 The Datarobot Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.droitateddb.cursor;

import android.content.ContentResolver;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;

/**
 * Abstract class to make {@link Cursor} proxyable.
 * 
 * @author Falk Appel
 * @author Alexander Frank
 * 
 */
abstract class ProxyableCursor implements Cursor {

	@Override
	public int getCount() {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public int getPosition() {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public boolean move(int offset) {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean moveToPosition(int position) {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean moveToFirst() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean moveToLast() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean moveToNext() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean moveToPrevious() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean isFirst() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean isLast() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean isBeforeFirst() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public boolean isAfterLast() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public int getColumnIndex(String columnName) {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public int getColumnIndexOrThrow(String columnName) throws IllegalArgumentException {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public String getColumnName(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return null;
	}

	@Override
	public String[] getColumnNames() {
		// no implementation needed, proxy will request the original cursor
		return null;
	}

	@Override
	public int getColumnCount() {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public byte[] getBlob(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return null;
	}

	@Override
	public String getString(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return null;
	}

	@Override
	public void copyStringToBuffer(int columnIndex, CharArrayBuffer buffer) {
		// no implementation needed, proxy will request the original cursor

	}

	@Override
	public short getShort(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public int getInt(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public long getLong(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public float getFloat(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public double getDouble(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public int getType(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return 0;
	}

	@Override
	public boolean isNull(int columnIndex) {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public void deactivate() {
		// no implementation needed, proxy will request the original cursor

	}

	@Override
	public boolean requery() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public void close() {
		// no implementation needed, proxy will request the original cursor

	}

	@Override
	public boolean isClosed() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public void registerContentObserver(ContentObserver observer) {
		// no implementation needed, proxy will request the original cursor

	}

	@Override
	public void unregisterContentObserver(ContentObserver observer) {
		// no implementation needed, proxy will request the original cursor

	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		// no implementation needed, proxy will request the original cursor

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		// no implementation needed, proxy will request the original cursor

	}

	@Override
	public void setNotificationUri(ContentResolver cr, Uri uri) {
		// no implementation needed, proxy will request the original cursor

	}


	public Uri getNotificationUri() {
		// no implementation needed, proxy will request the original cursor
		return null;
	}

	@Override
	public boolean getWantsAllOnMoveCalls() {
		// no implementation needed, proxy will request the original cursor
		return false;
	}

	@Override
	public Bundle getExtras() {
		// no implementation needed, proxy will request the original cursor
		return null;
	}

	@Override
	public Bundle respond(Bundle extras) {
		// no implementation needed, proxy will request the original cursor
		return null;
	}

}
