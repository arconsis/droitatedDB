package com.arconsis.android.datarobot.performance.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.arconsis.android.datarobot.performance.R;
import com.arconsis.android.datarobot.performance.db.Operation;
import com.arconsis.android.datarobot.performance.tasks.AbstractCreationTask.CreationFinishedListener;
import com.arconsis.android.datarobot.performance.tasks.BulkNoteCreation;
import com.arconsis.android.datarobot.performance.tasks.BulkSimpleCreationTask;
import com.arconsis.android.datarobot.performance.tasks.NoteCreationTask;
import com.arconsis.android.datarobot.performance.tasks.SimpleCreationTask;

import org.droitateddb.BaseContentProvider;
import org.droitateddb.EntityService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MainActivity extends ListActivity {

	private OperationsAdapter operationsAdapter;

	private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 4, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	private EntityService<Operation> service;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		service = new EntityService<Operation>(this, Operation.class);
		operationsAdapter = new OperationsAdapter(this, new ArrayList<Operation>(service.get()));
		setListAdapter(operationsAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		service.close();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		if (R.id.dump_db == item.getItemId()) {
			File sd = Environment.getExternalStorageDirectory();
			File data = Environment.getDataDirectory();
			FileChannel source = null;
			FileChannel destination = null;
			String currentDBPath = "/data/com.arconsis.android.datarobot.performance/databases/performance.db";
			File currentDB = new File(data, currentDBPath);
			File backupDB = new File(sd, "performance.db");
			try {
				source = new FileInputStream(currentDB).getChannel();
				destination = new FileOutputStream(backupDB).getChannel();
				destination.transferFrom(source, 0, source.size());
				source.close();
				destination.close();
				Toast.makeText(this, R.string.db_exported, Toast.LENGTH_LONG).show();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else if (R.id.delete_all == item.getItemId()) {
			getContentResolver().delete(BaseContentProvider.uri(Operation.class.getSimpleName()), null, null);

			operationsAdapter.clear();
			operationsAdapter.notifyDataSetChanged();
			return true;
		} else if (R.id.bulk_creation == item.getItemId()) {
			Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Choose batch");
			builder.setSingleChoiceItems(new String[]{"Simple (Single)", "Note (Single)", "Simple (Bulk)", "Note (Bulk)"}, 0, new OnClickListener() {
				@Override
				public void onClick(final DialogInterface dialog, final int which) {
					if (which == 0) {
						new SimpleCreationTask(MainActivity.this, new SavePerformance("Simple (Single)")).executeOnExecutor(executor);
					} else if (which == 1) {
						new NoteCreationTask(MainActivity.this, new SavePerformance("Note (Single)")).executeOnExecutor(executor);
					} else if (which == 2) {
						new BulkSimpleCreationTask(MainActivity.this, new SavePerformance("Simple (Bulk)")).executeOnExecutor(executor);
					} else if (which == 3) {
						new BulkNoteCreation(MainActivity.this, new SavePerformance("Note (Bulk)")).executeOnExecutor(executor);
					}
					dialog.dismiss();
				}
			});
			builder.show();
		}
		return super.onOptionsItemSelected(item);
	}

	private final class SavePerformance implements CreationFinishedListener {
		private final String type;

		public SavePerformance(final String type) {
			this.type = type;
		}

		@Override
		public void onCompleted(final long time) {
			Operation operation = new Operation(type, time);
			service.save(operation);
			operationsAdapter.add(operation);
			operationsAdapter.notifyDataSetChanged();
		}
	}
}
