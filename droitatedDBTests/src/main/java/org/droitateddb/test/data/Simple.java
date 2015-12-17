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
package org.droitateddb.test.data;

import java.util.Date;

import org.droitateddb.entity.AutoIncrement;
import org.droitateddb.entity.Column;
import org.droitateddb.entity.Entity;
import org.droitateddb.entity.PrimaryKey;

/**
 * @author Falk Appel
 * @author Alexander Frank
 */
@Entity
public class Simple {

	@Column
	@PrimaryKey
	@AutoIncrement
	private Integer _id;
	@Column
	private Boolean bigBoolean;

	@Column
	private Double bigDouble;

	@Column
	private Float bigFloat;

	@Column
	private Long bigLong;

	@Column
	private Date myDate;
	@Column
	private boolean myBoolean;

	@Column
	private double myDouble;

	@Column
	private float myFloat;

	@Column
	private int myInt;

	@Column
	private String myString;

	@Column
	private long soLong;

	@Column
	private byte[] someBytes;

	public Simple() {
		// no-args
	}

	public Simple(final Integer id, final String myString) {
		this._id = id;
		this.myString = myString;
	}

	public Double getBigDouble() {
		return this.bigDouble;
	}

	public Float getBigFloat() {
		return this.bigFloat;
	}

	public Long getBigLong() {
		return this.bigLong;
	}

	public Integer getId() {
		return _id;
	}

	public Date getMyDate() {
		return this.myDate;
	}

	public double getMyDouble() {
		return this.myDouble;
	}

	public float getMyFloat() {
		return this.myFloat;
	}

	public int getMyInt() {
		return this.myInt;
	}

	public String getMyString() {
		return myString;
	}

	public long getSoLong() {
		return this.soLong;
	}

	public byte[] getSomeBytes() {
		return someBytes;
	}

	public void setBigDouble(final Double bigDouble) {
		this.bigDouble = bigDouble;
	}

	public void setBigFloat(final Float bigFloat) {
		this.bigFloat = bigFloat;
	}

	public void setBigLong(final Long bigLong) {
		this.bigLong = bigLong;
	}

	public void setId(final Integer _id) {
		this._id = _id;
	}

	public void setMyDate(final Date myDate) {
		this.myDate = myDate;
	}

	public void setMyDouble(final double myDouble) {
		this.myDouble = myDouble;
	}

	public void setMyFloat(final float myFloat) {
		this.myFloat = myFloat;
	}

	public void setMyInt(final int myInt) {
		this.myInt = myInt;
	}

	public void setMyString(final String myString) {
		this.myString = myString;
	}

	public void setSoLong(final long soLong) {
		this.soLong = soLong;
	}

	public void setSomeBytes(final byte[] someBytes) {
		this.someBytes = someBytes;
	}

	public Boolean getBigBoolean() {
		return this.bigBoolean;
	}

	public void setBigBoolean(Boolean bigBoolean) {
		this.bigBoolean = bigBoolean;
	}

	public boolean isMyBoolean() {
		return this.myBoolean;
	}

	public void setMyBoolean(boolean myBoolean) {
		this.myBoolean = myBoolean;
	}

}
