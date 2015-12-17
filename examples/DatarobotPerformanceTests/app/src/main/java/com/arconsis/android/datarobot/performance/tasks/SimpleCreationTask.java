package com.arconsis.android.datarobot.performance.tasks;

import android.content.Context;

import com.arconsis.android.datarobot.performance.db.Simple;

import org.droitateddb.EntityService;

public final class SimpleCreationTask extends AbstractCreationTask {

	private final EntityService<Simple> simpleService;

	public SimpleCreationTask(final Context context, final CreationFinishedListener listener) {
		super(context, listener);
		simpleService = new EntityService<Simple>(context, Simple.class);
	}

	@Override
	protected void createSingle(final int iter) {
		Simple simple = new Simple("Simple #" + iter, Long.MAX_VALUE - iter, "STATIC");
		simpleService.save(simple);
	}

	@Override
	protected int times() {
		return 1000;
	}

	@Override
	protected String dialogMsg() {
		return "Creating " + times() + " simple entities, which are saved individually. The simple entity has no relationships.";
	}
}