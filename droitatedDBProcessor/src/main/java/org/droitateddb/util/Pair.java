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
package org.droitateddb.util;

/**
 * 
 * @author Alexander Frank
 * @author Falk Appel
 */
public class Pair<K, V> {

	private final K first;

	private final V second;

	public Pair(K first, V second) {
		this.first = first;
		this.second = second;
	}

	public K getFirst() {
		return this.first;
	}

	public V getSecond() {
		return this.second;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Pair)) {
			return false;
		}
		return hashCode() == o.hashCode();
	}

	@Override
	public int hashCode() {
		return (first.toString() + second.toString()).hashCode();
	}

}