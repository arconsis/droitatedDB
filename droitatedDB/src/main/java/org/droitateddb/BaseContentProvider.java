/*
 * Copyright (C) 2014 The droitated DB Authors
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
package org.droitateddb;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import org.droitateddb.cursor.CombinedCursor;
import org.droitateddb.cursor.CombinedCursorImpl;
import org.droitateddb.entity.Entity;
import org.droitateddb.schema.AbstractAttribute;
import org.droitateddb.schema.EntityInfo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link BaseContentProvider} is the base class of all generated {@link ContentProvider} for {@link Entity} classes.<br>
 * <br>
 * For each {@link Entity} which allows {@link ContentProvider} generation a separate {@link ContentProvider} will be generated. The generated
 * {@link ContentProvider} is also referenced in the AndroidManifest.xml to use an own Implementation remove the attribute
 * <code>droitateddb:generated="true"</code> in the <code>provider</code> element of the AndroidManifest.xml<br>
 * The authority of the {@link ContentProvider} is therefore defined within the {@link Entity} annotation.<br>
 * <br>
 * The base {@link Uri} for calling the {@link ContentProvider} is build following this schema: content://<b>[authority]</b>/<b>[
 * {@link BaseContentProvider#getEntityURIPart}]</b>. <br>
 * If not overridden {@link BaseContentProvider#getEntityURIPart} returns <b>entity</b><br>
 * <br>
 * For easier use the {@link BaseContentProvider} provides the methods {@link BaseContentProvider#uri(String)} and
 * {@link BaseContentProvider#uriForItem(String, long)}. These methods allow getting the {@link Uri} referencing the Provider with the table name of the
 * {@link Entity} and the primary key. <br>
 * <br>
 * {@link BaseContentProvider#uri(String)}: Returns a {@link Uri} for accessing multiple {@link Entity} elements.<br>
 * <br>
 * {@link BaseContentProvider#uriForItem(String, long)}: Returns a {@link Uri} for accessing a specific {@link Entity} element<br>
 *
 * @author Falk Appel
 * @author Alexander Frank
 */

public abstract class BaseContentProvider extends ContentProvider {

	private static final int         MATCH_DIR  = 0;
	private static final int         MATCH_ITEM = 1;
	private static final String      PROTOCOL   = "content://";
	private static final UriRegistry REGISTRY   = new UriRegistry();

	private final String    contentUri;
	private final String    dirContentType;
	private final String    itemContentType;
	private       DbCreator dbCreator;

	private final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	protected BaseContentProvider() {
		String authority = getAuthority();
		String base = getEntityURIPart();
		String typeDescription = "/vnd." + authority + "." + base;

		contentUri = PROTOCOL + authority + "/" + base;
		dirContentType = ContentResolver.CURSOR_DIR_BASE_TYPE + typeDescription;
		itemContentType = ContentResolver.CURSOR_ITEM_BASE_TYPE + typeDescription;

		uriMatcher.addURI(authority, base, MATCH_DIR);
		uriMatcher.addURI(authority, base + "/#", MATCH_ITEM);

		REGISTRY.add(getTableName(), contentUri);
	}

	/**
	 * Returns a {@link Uri} for the using it with a {@link ContentResolver} to access this {@link ContentProvider}. The {@link Uri} allows CRUD operations on
	 * multiple {@link Entity} elements.
	 *
	 * @param tableName Name of the Table (simple {@link Entity} name) the data should be retrieved from.
	 * @return Uri to access the tables data
	 */
	public static Uri uri(final String tableName) {
		return REGISTRY.getDictionaryUri(tableName);
	}

	/**
	 * Returns a {@link Uri} for the using it with a {@link ContentResolver} to access this {@link ContentProvider}. The {@link Uri} allows CRUD operations
	 * on a
	 * single {@link Entity} element, qualified by its primary key.
	 *
	 * @param tableName Name of the Table (simple {@link Entity} name) the data should be retrieved from.
	 * @param id        Primary key of the {@link Entity} to be accessed
	 * @return Uri to access the specific item in the table
	 */
	public static Uri uriForItem(final String tableName, final long id) {
		return REGISTRY.getItemUri(tableName, Long.toString(id));
	}

	/**
	 * Has to return the authority to be used for this {@link ContentProvider}.<br>
	 * If generated this is the authority defined in the {@link Entity} class
	 *
	 * @return The authority used for the ContentProvider
	 */
	protected abstract String getAuthority();

	/**
	 * Has to return the attribute describing the primary key of the {@link Entity}.<br>
	 * Id generated this is the {@link org.droitateddb.entity.PrimaryKey} annotated {@link org.droitateddb.entity.Column}.
	 *
	 * @return The description of the primary key of the table access by this ContentProvider
	 */
	protected abstract AbstractAttribute getIdAttribute();

	/**
	 * Has to return metadata of the {@link Entity} this {@link ContentProvider} is for.
	 *
	 * @return Metadata to the {@link Entity} being accessed
	 */
	protected abstract EntityInfo getEntityInfo();

	protected String getEntityURIPart() {
		return "entity";
	}

	private String getTableName() {
		return getEntityInfo().table();
	}

	private String getIdName() {
		return getIdAttribute().fieldName();
	}

	@Override
	public boolean onCreate() {
		dbCreator = DbCreator.getInstance(getContext());
		return true;
	}

	@Override
	public String getType(final Uri uri) {
		switch (uriMatcher.match(uri)) {
			case MATCH_DIR:
				return dirContentType;
			case MATCH_ITEM:
				return itemContentType;
			default:
				throw new UnsupportedOperationException("No type for Uri " + uri + " present");
		}
	}

	@Override
	public Uri insert(final Uri uri, final ContentValues values) {
		return dbCreator.functionOnDatabase(new DbFunction<Uri>() {
			@Override
			public Uri apply(SQLiteDatabase db) {
				switch (uriMatcher.match(uri)) {
					case MATCH_DIR:
						long id = db.insertWithOnConflict(getTableName(), null, values, SQLiteDatabase.CONFLICT_REPLACE);
						getContext().getContentResolver().notifyChange(uri, null);
						return Uri.parse(contentUri + "/" + id);
					default:
						throw new UnsupportedOperationException("Insert not allowed with specific note uri");
				}
			}
		});
	}

	@Override
	public Cursor query(final Uri uri, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
		SQLiteDatabase db = dbCreator.getDatabaseConnection();
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(getTableName());
		switch (uriMatcher.match(uri)) {
			case MATCH_DIR:
				return wrap(builder.query(db, projection, selection, selectionArgs, null, null, sortOrder), uri);
			case MATCH_ITEM:
				int id = Integer.parseInt(uri.getLastPathSegment());
				builder.appendWhere(getIdName() + " = " + id);
				return wrap(builder.query(db, projection, selection, selectionArgs, null, null, sortOrder), uri);
			default:
				throw new UnsupportedOperationException("Unsupported query Uri " + uri);
		}
	}

	@Override
	public int update(final Uri uri, final ContentValues values, final String selection, final String[] selectionArgs) {
		return dbCreator.functionOnDatabase(new DbFunction<Integer>() {
			@Override
			public Integer apply(SQLiteDatabase db) {
				switch (uriMatcher.match(uri)) {
					case MATCH_DIR:
						return db.update(getTableName(), values, selection, selectionArgs);
					case MATCH_ITEM:
						String selectId = getIdName() + " = " + Integer.parseInt(uri.getLastPathSegment());
						return db.update(getTableName(), values, getEffectivSelection(selection, selectId), selectionArgs);
					default:
						throw new UnsupportedOperationException(
								"Update not supported for the given Uri + " + uri + ". Only single item updates are allowed right " +
										"now");
				}
			}
		});
	}

	@Override
	public int delete(final Uri uri, final String selection, final String[] selectionArgs) {
		return dbCreator.functionOnDatabase(new DbFunction<Integer>() {
			@Override
			public Integer apply(SQLiteDatabase db) {
				switch (uriMatcher.match(uri)) {
					case MATCH_DIR:
						return db.delete(getTableName(), selection, selectionArgs);
					case MATCH_ITEM:
						String selectId = getIdName() + " = " + Integer.parseInt(uri.getLastPathSegment());
						return db.delete(getTableName(), getEffectivSelection(selection, selectId), selectionArgs);
					default:
						throw new UnsupportedOperationException(
								"Delete not supported for the given Uri + " + uri + ". Only single item deletes are allowed right now");
				}
			}
		});
	}

	private Cursor wrap(final Cursor cursor, final Uri uri) {
		try {
			EntityInfo entityInfo = getEntityInfo();
			CombinedCursor<?> magicCursor = CombinedCursorImpl.create(getContext(), cursor, entityInfo, Class.forName(entityInfo.className()));
			magicCursor.setNotificationUri(getContext().getContentResolver(), uri);
			return magicCursor;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private String getEffectivSelection(final String submitedSelection, final String idSelection) {
		String effectivSelection;
		if (submitedSelection == null || submitedSelection.equals("")) {
			effectivSelection = idSelection;
		} else {
			effectivSelection = submitedSelection + " AND " + idSelection;
		}
		return effectivSelection;
	}

	private static class UriRegistry {
		private final Map<String, String> registry = new ConcurrentHashMap<String, String>();

		public void add(final String tableName, final String uri) {
			registry.put(tableName, uri);
		}

		public Uri getDictionaryUri(final String tableName) {
			return Uri.parse(getRegisteredUri(tableName));
		}

		public Uri getItemUri(final String tableName, final String id) {
			return Uri.parse(getRegisteredUri(tableName) + "/" + id);
		}

		private String getRegisteredUri(final String tableName) {
			String uri = registry.get(tableName);
			if (uri == null) {
				throw new IllegalStateException("No ContentProvider Uri was registered for table " + tableName +
						". Did you forget to set \"contentProvider=true\" on the corresponding @Entity annotation?");
			}
			return uri;
		}
	}

}
