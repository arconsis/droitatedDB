package com.arconsis.android.datarobot.performance.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.arconsis.android.datarobot.performance.util.DeleteUtil;

public abstract class AbstractCreationTask extends AsyncTask<Void, Integer, Void> {

	private final ProgressDialog progressDialog;
	private long start;
	private final CreationFinishedListener listener;
	private final Context context;

	public AbstractCreationTask(final Context context, final CreationFinishedListener listener) {
		this.context = context;
		this.listener = listener;

		progressDialog = new ProgressDialog(context, ProgressDialog.THEME_HOLO_DARK);
		progressDialog.setTitle("Batch creation");
		progressDialog.setMessage(dialogMsg());
		progressDialog.setCancelable(true);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setMax(times());
		progressDialog.setProgress(0);

		if (times() == 1) {
			progressDialog.setIndeterminate(true);
		} else {
			progressDialog.setIndeterminate(false);
		}
	}

	@Override
	protected final void onPreExecute() {
		progressDialog.show();
		start = System.currentTimeMillis();
	}

	@Override
	protected final Void doInBackground(final Void... paramArrayOfParams) {
		for (int i = 0; i < times(); i++) {
			createSingle(i);
			publishProgress(i);
		}
		return null;
	}

	protected abstract void createSingle(int iter);

	protected abstract int times();

	protected abstract String dialogMsg();

	@Override
	protected final void onProgressUpdate(final Integer... values) {
		progressDialog.setProgress(values[0]);
	}

	@Override
	protected final void onPostExecute(final Void result) {
		progressDialog.dismiss();
		long time = System.currentTimeMillis() - start;
		listener.onCompleted(time);
		DeleteUtil.deleteTestEntities(context);
	}

	public interface CreationFinishedListener {
		void onCompleted(long time);
	}

}