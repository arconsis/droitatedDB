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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import android.os.Parcel;

import org.droitateddb.test.data.Simple;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = BasePersistenceTest.MANIFEST)
public class FlatEntityParcelableTets {
	@Test
	public void test() {

		Simple data = new Simple(1, "Test");
		data.setBigDouble(00d);
		data.setBigFloat(2.3f);
		data.setBigLong(4l);
		data.setMyDouble(4d);
		data.setMyFloat(5.4f);
		data.setMyInt(3);
		data.setSoLong(567l);
		data.setSomeBytes("someBytes".getBytes());

		FlatEntityParcelable<Simple> parcelable = new FlatEntityParcelable<Simple>(data);
		Parcel p = Parcel.obtain();
		parcelable.writeToParcel(p, 0);

		@SuppressWarnings("unchecked")
		FlatEntityParcelable<Simple> fromParcel = (FlatEntityParcelable<Simple>) FlatEntityParcelable.CREATOR.createFromParcel(p);

		Simple entity = fromParcel.getEntity();

		BasePersistenceTest.assertSameFields(data, entity);
	}

}
