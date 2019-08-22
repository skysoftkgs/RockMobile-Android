package org.church.rockmobile.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.SeriesMessagesActivity;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.model.SeriesModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SeriesListAdapter extends BaseAdapter {

	// Declare Variables
	Context mContext;
	LayoutInflater mInflater;
	private List<SeriesModel> mSeriesList;

	public SeriesListAdapter(Context context, List<SeriesModel> seriesList) {
		this.mContext = context;
		this.mSeriesList = seriesList;
		mInflater = LayoutInflater.from(mContext);
	}

	public class ViewHolder {
		TextView nameTextView;
		TextView dateTextView;
		ImageView imageView;
	}

	@Override
	public int getCount() {
		return mSeriesList.size();
	}

	@Override
	public Object getItem(int position) {
		return mSeriesList.get(position);
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
			view = mInflater.inflate(R.layout.item_series, null);
			holder.nameTextView = (TextView) view.findViewById(R.id.textView_name);
			holder.dateTextView = (TextView) view.findViewById(R.id.textView_date);
			holder.imageView = (ImageView) view.findViewById(R.id.imageView_photo);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final SeriesModel series = (SeriesModel) getItem(position);
		holder.nameTextView.setText(series.getName());

		SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
		if (series.getEndDate() != null) {
			String startDate = sdf.format(series.getStartDate());
			String endDate = sdf.format(series.getEndDate());
			if (startDate.trim().equalsIgnoreCase(endDate.trim())) {
				holder.dateTextView.setText(startDate);
			} else {
				String date = startDate + " - " + endDate;
				holder.dateTextView.setText(date);
			}

		} else {
			String date = sdf.format(series.getStartDate());
			holder.dateTextView.setText(date);
		}

		if (series.getPhotoFile() != null) {
			// display cover photo image
			AppManager.getInstance().mImageLoader.displayImage(series.getPhotoFile().getUrl(), holder.imageView,
					AppManager.getInstance().options, null, null);

		} else if (series.getImageLink() != null) {
			// display cover photo image
			AppManager.getInstance().mImageLoader.displayImage(series.getImageLink(), holder.imageView,
					AppManager.getInstance().options, null, null);

		} else {
			holder.imageView.setImageBitmap(null);
		}

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, SeriesMessagesActivity.class);
				SeriesMessagesActivity.mSeries = series;
				mContext.startActivity(intent);
			}
		});

		return view;
	}
}