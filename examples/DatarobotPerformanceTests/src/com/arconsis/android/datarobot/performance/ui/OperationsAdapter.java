package com.arconsis.android.datarobot.performance.ui;

import java.text.NumberFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.arconsis.android.datarobot.performance.R;
import com.arconsis.android.datarobot.performance.db.Operation;

public class OperationsAdapter extends ArrayAdapter<Operation> {

	public OperationsAdapter(final Context context, final List<Operation> operations) {
		super(context, R.layout.item_operation, operations);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = LayoutInflater.from(getContext()).inflate(R.layout.item_operation, null);
		}

		Operation operation = getItem(position);

		((TextView) view.findViewById(R.id.type)).setText(operation.getType());
		((TextView) view.findViewById(R.id.duration)).setText(toSeconds(operation.getDuration()));

		return view;
	}

	private String toSeconds(final long millis) {
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(2);
		return numberFormat.format(millis / 1000d);
	}

}
