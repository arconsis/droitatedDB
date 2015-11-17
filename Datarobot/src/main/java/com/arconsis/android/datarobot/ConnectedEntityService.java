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
package com.arconsis.android.datarobot;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.arconsis.android.datarobot.entity.Entity;

import java.io.Closeable;

/**
 * Provides a version of the {@link EntityService} with an open connection to the {@link SQLiteDatabase}. Make sure to
 * close the connection with {@link ConnectedEntityService#close()} when you finished using the service.
 *
 * @param <E> Entity, for which this service will be used
 * @author Falk Appel
 * @author Alexander Frank
 */
public class ConnectedEntityService<E> extends EntityService<E> implements Closeable {
	private final SQLiteDatabase database;

	/**
	 * Creates a {@link ConnectedEntityService} for the given {@link Entity}
	 *
	 * @param context     Android context
	 * @param entityClass Class of the {@link Entity}, the service should be used for
	 * @throws IllegalArgumentException When the given {@link #entityClass} is no {@link Entity}
	 */
	public ConnectedEntityService(final Context context, final Class<E> entityClass) {
		super(context, entityClass);
		this.database = dbCreator.getDatabaseConnection();
	}

	/*
	 * Provides test access to constructor
	 */
	ConnectedEntityService(final Context context, final Class<E> entityClass, SQLiteDatabase database) {
		super(context, entityClass);
		this.database = database;
	}

	@Override
	protected void closeDB(SQLiteDatabase database) {
		// nothing todo
	}

	@Override
	protected SQLiteDatabase openDB() {
		return database;
	}

	@Override
	public void close() {
		dbCreator.reduceDatabaseConnection();
	}

}
