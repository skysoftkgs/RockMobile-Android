package org.church.rockmobile.adapter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.church.rockmobile.R;
import org.church.rockmobile.SeriesMessageDetailActivity;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.SeriesMessageModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeriesMessageListAdapter extends BaseAdapter {

	// Declare Variables
	Context mContext;
	LayoutInflater mInflater;
	private List<SeriesMessageModel> mMessageList;
	private boolean isPartHidden;

	public SeriesMessageListAdapter(Context context, List<SeriesMessageModel> messageList) {
		this.mContext = context;
		this.mMessageList = messageList;
		mInflater = LayoutInflater.from(mContext);
		isPartHidden = false;
	}

	public SeriesMessageListAdapter(Context context, List<SeriesMessageModel> messageList, boolean hidePart) {
		this.mContext = context;
		this.mMessageList = messageList;
		mInflater = LayoutInflater.from(mContext);
		isPartHidden = hidePart;
	}

	public class ViewHolder {
		TextView partTextView;
		TextView titleTextView;
		TextView dateTextView;
	}

	@Override
	public int getCount() {
		return mMessageList.size();
	}

	@Override
	public Object getItem(int position) {
		return mMessageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("SimpleDateFormat")
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.item_series_message, null);
			holder.partTextView = (TextView) view.findViewById(R.id.textView_part);
			holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
			holder.dateTextView = (TextView) view.findViewById(R.id.textView_date);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		if (isPartHidden) {
			holder.partTextView.setVisibility(View.GONE);
		} else {
			holder.partTextView.setVisibility(View.VISIBLE);
		}
		final SeriesMessageModel message = (SeriesMessageModel) getItem(position);
		holder.partTextView.setText("PART " + String.valueOf(position + 1));
		holder.titleTextView.setText(message.getTitle());
		SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
		String date = sdf.format(message.getStartDate());
		holder.dateTextView.setText(date);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SeriesMessageDetailActivity.class);
				if (isPartHidden) {
					intent.putExtra(Constants.BUNDLE_MESSAGE_POSITION, extractPartFromTitle(message.getTitle()));
					intent.putExtra(Constants.BUNDLE_IS_EXTRACT_ORIGINAL_TITLE, true);
					SeriesMessageDetailActivity.mSeriesMessage = message;
				} else {
					intent.putExtra(Constants.BUNDLE_MESSAGE_POSITION, position);
					SeriesMessageDetailActivity.mSeriesMessage = message;
				}
				
				mContext.startActivity(intent);
			}
		});

		return view;
	}

	private int extractPartFromTitle(String title) {
		Pattern pattern = Pattern.compile("- Part (\\d+),");
		Matcher matcher = pattern.matcher(title);
		if (matcher.find()) {
			String partNumber = matcher.group(1).trim();
			if (partNumber != null && !TextUtils.isEmpty(partNumber) && TextUtils.isDigitsOnly(partNumber)) {
				return Integer.parseInt(partNumber) - 1;
			}
		}
		return 0;
	}
}