package com.arconsis.android.datarobot.performance.db;

import com.arconsis.android.datarobot.entity.AutoIncrement;
import com.arconsis.android.datarobot.entity.Column;
import com.arconsis.android.datarobot.entity.Entity;
import com.arconsis.android.datarobot.entity.PrimaryKey;

@Entity
public class Simple {

	@PrimaryKey
	@AutoIncrement
	@Column
	private Integer _id;
	@Column
	private String entry1;
	@Column
	private long value1;
	@Column
	private String entry;

	public Simple() {
		// no-args
	}

	public Simple(final String entry1, final long value1, final String entry) {
		this.entry1 = entry1;
		this.value1 = value1;
		this.entry = entry;
	}

	public Integer getId() {
		return _id;
	}

	public void setId(final Integer id) {
		this._id = id;
	}

	public String getEntry1() {
		return entry1;
	}

	public void setEntry1(final String entry1) {
		this.entry1 = entry1;
	}

	public long getValue1() {
		return value1;
	}

	public void setValue1(final long value1) {
		this.value1 = value1;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(final String entry) {
		this.entry = entry;
	}

}
