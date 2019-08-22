package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.common.DateUtils;
import org.church.rockmobile.model.ThreadModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GroupThreadsListAdapter extends BaseAdapter {

	// Declare Variables
	Context mContext;
	LayoutInflater mInflater;
	private List<ThreadModel> mThreadList;

	public GroupThreadsListAdapter(Context context, List<ThreadModel> threadList) {
		this.mContext = context;
		this.mThreadList = threadList;
		mInflater = LayoutInflater.from(mContext);
	}

	public class ViewHolder {
		TextView titleTextView;
		TextView startTextView;
		TextView lastTextView;
		TextView threadMessageCount;
		TextView startDateTime;
		TextView endDateTime;
	}

	@Override
	public int getCount() {
		return mThreadList.size();
	}

	@Override
	public Object getItem(int position) {
		return mThreadList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.item_group_thread, null);
			holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
			holder.startTextView = (TextView) view.findViewById(R.id.textView_start);
			holder.lastTextView = (TextView) view.findViewById(R.id.textView_last);
			holder.threadMessageCount = (TextView) view.findViewById(R.id.threadMessageCount);
			holder.startDateTime = (TextView) view.findViewById(R.id.textView_start_datetime);
			holder.endDateTime = (TextView) view.findViewById(R.id.textView_last_datetime);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final ThreadModel thread = mThreadList.get(position);
		holder.titleTextView.setText(thread.getTitle());

		int count = thread.getMessageCount();
		holder.threadMessageCount.setText(String.valueOf(count < 0 ? 0 : count));

		if (thread.getCreatedAt() != null) {
			String startMessageTime = DateUtils.getFormattedDate(thread.getCreatedAt());
			holder.startDateTime.setText(startMessageTime);
		} else {
			holder.startDateTime.setVisibility(View.GONE);
		}

		if (thread.getUpdatedAt() != null) {
			String lastMessageTime = DateUtils.getFormattedDate(thread.getUpdatedAt());
			holder.endDateTime.setText(lastMessageTime);
		} else {
			holder.endDateTime.setVisibility(View.GONE);
		}

		holder.startTextView.setText(thread.getUser().getRealUsername());

		if (thread.getLastUser() != null)
			holder.lastTextView.setText(thread.getLastUser());
		else
			holder.lastTextView.setText("");

		return view;
	}
}