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

import android.database.Cursor;

/**
 * @param <T> Entity type represented within the Cursor
 * @author Falk Appel
 * @author Alexander Frank
 */
public interface CombinedCursor<T> extends Cursor, ObjectCursor<T> {
	// combines interfaces
}
