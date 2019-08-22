package org.church.rockmobile.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class SeriesMessageSearchListAdapter extends BaseAdapter implements Filterable {

	// Declare Variables
	Context mContext;
	LayoutInflater mInflater;
	private List<SeriesMessageModel> mMessageList;
	private List<SeriesMessageModel> mOriginalValues;
	private boolean isPartHidden;

	public SeriesMessageSearchListAdapter(Context context, List<SeriesMessageModel> messageList) {
		this.mContext = context;
		this.mMessageList = messageList;
		mInflater = LayoutInflater.from(mContext);
		isPartHidden = false;
	}

	public SeriesMessageSearchListAdapter(Context context, List<SeriesMessageModel> messageList, boolean hidePart) {
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

	private String extractOriginalTitleFromModifiedTitle(String title) {
		Pattern pattern = Pattern.compile("- Part \\d+, (.*)");
		Matcher matcher = pattern.matcher(title);
		if (matcher.find()) {
			String originalTitle = matcher.group(1).trim();
			if (title != null && !TextUtils.isEmpty(title)) {
				return originalTitle;
			}
		}
		return title;
	}

	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {

				mMessageList = (List<SeriesMessageModel>) results.values; // has
																			// the
																			// filtered
																			// values
				notifyDataSetChanged(); // notifies the data with new filtered
										// values
			}

			@SuppressLint("DefaultLocale")
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				FilterResults results = new FilterResults(); // Holds the
																// results of a
																// filtering
																// operation in
																// values
				List<SeriesMessageModel> FilteredArrList = new ArrayList<SeriesMessageModel>();

				if (mOriginalValues == null) {
					mOriginalValues = new ArrayList<SeriesMessageModel>(mMessageList); // saves
																						// the
																						// original
																						// data
																						// in
																						// mOriginalValues
				}

				/********
				 * 
				 * If constraint(CharSequence that is received) is null returns
				 * the mOriginalValues(Original) values else does the Filtering
				 * and returns FilteredArrList(Filtered)
				 *
				 ********/
				if (constraint == null || constraint.length() == 0) {

					// set the Original result to return
					results.count = mOriginalValues.size();
					results.values = mOriginalValues;
				} else {
					constraint = constraint.toString().toLowerCase();
					for (int i = 0; i < mOriginalValues.size(); i++) {
						SeriesMessageModel record = mOriginalValues.get(i);
						String name = record.getTitle();
						if (name.toLowerCase().contains(constraint.toString())) {
							FilteredArrList.add(record);
						}
					}
					// set the Filtered result to return
					results.count = FilteredArrList.size();
					results.values = FilteredArrList;
				}
				return results;
			}
		};
		return filter;
	}
}