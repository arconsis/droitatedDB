package com.arconsis.android.datarobot.performance.tasks;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;

import com.arconsis.android.datarobot.EntityService;
import com.arconsis.android.datarobot.performance.db.Simple;

public final class BulkSimpleCreationTask extends AbstractCreationTask {
	private final EntityService<Simple> simpleService;
	private static final int SIMPLE_PER_BULK = 100000;

	public BulkSimpleCreationTask(final Context context, final CreationFinishedListener listener) {
		super(context, listener);
		simpleService = new EntityService<Simple>(context, Simple.class);
	}

	@Override
	protected void createSingle(final int iter) {
		Collection<Simple> bulk = new ArrayList<Simple>(SIMPLE_PER_BULK);
		for (int i = 0; i < SIMPLE_PER_BULK; i++) {
			Simple simple = new Simple("Simple #" + i, Long.MAX_VALUE - i, "STATIC");
			bulk.add(simple);
		}
		simpleService.save(bulk);
	}

	@Override
	protected int times() {
		return 1;
	}

	@Override
	protected String dialogMsg() {
		return "Creating " + SIMPLE_PER_BULK + " simple entities with a bulk operation. The simple entity has no relationships.";
	}

}