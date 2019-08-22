package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.GroupAboutActivity;
import org.church.rockmobile.GroupDetailActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;
import org.church.rockmobile.service.PushNotificationService.ParseFunctionCallback;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;

public class CategoryGroupsListAdapter extends BaseAdapter {

	// Declare Variables
	Context mContext;
	LayoutInflater mInflater;
	private List<GroupModel> mGroupList;

	public CategoryGroupsListAdapter(Context context, List<GroupModel> groupList) {
		this.mContext = context;
		this.mGroupList = groupList;
		mInflater = LayoutInflater.from(mContext);
	}

	public class ViewHolder {
		TextView titleTextView;
		TextView statusTextView;
		ImageView statusImageView;
	}

	@Override
	public int getCount() {
		return mGroupList.size();
	}

	@Override
	public Object getItem(int position) {
		return mGroupList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup parent) {
		final ViewHolder holder;
		if (view == null) {
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.item_category_group, null);
			holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
			holder.statusTextView = (TextView) view.findViewById(R.id.textView_groupStatus);
			holder.statusImageView = (ImageView) view.findViewById(R.id.imageView_groupStatus);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		final GroupModel group = mGroupList.get(position);
		final ParseUser currentUser = ParseUser.getCurrentUser();

		final List<UserModel> pendingUserList = group.getPendingUserList();

		holder.titleTextView.setText(group.getTitle());

		if (UtilityMethods.containsParseUser(group.getGroupUserList(), currentUser)) { // group
																						// joined
			holder.statusTextView.setText("Joined");
			holder.statusImageView.setImageResource(R.color.transparent);

		} else if (pendingUserList != null && UtilityMethods.containsParseUser(pendingUserList, currentUser)) { // group
																												// pending
			holder.statusTextView.setText("Pending");
			holder.statusImageView.setImageBitmap(
					BitmapFactory.decodeResource(mContext.getResources(), R.drawable.icon_group_pending));

		} else { // no group member
			holder.statusTextView.setText("");
			if (group.getIsPublic()) {
				holder.statusImageView.setImageBitmap(
						BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_add_member_normal));
			} else {
				holder.statusImageView.setImageBitmap(
						BitmapFactory.decodeResource(mContext.getResources(), R.drawable.btn_add_member_private));
			}
		}

		holder.statusImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				GroupModel group = mGroupList.get(position);
				ParseUser currentUser = ParseUser.getCurrentUser();
				if (!UtilityMethods.containsParseUser(group.getAdminUserList(), currentUser)
						&& !UtilityMethods.containsParseUser(group.getJoinedUserList(), currentUser)
						&& !UtilityMethods.containsParseUser(group.getPendingUserList(), currentUser)) {
					if (group.getIsPublic() == false)
						requestJoinToGroup(group, holder);
					else
						joinToGroup(group, holder);

				} else if (UtilityMethods.containsParseUser(group.getPendingUserList(), currentUser)) {
					cancelJoinToGroup(group, holder);
				}
			}
		});

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (UtilityMethods.containsParseUser(group.getGroupUserList(), currentUser)) { // group
																								// joined
					Intent intent = new Intent(mContext, GroupDetailActivity.class);
					GroupDetailActivity.mGroup = group;
					mContext.startActivity(intent);

				} else { // no group member
					Intent intent = new Intent(mContext, GroupAboutActivity.class);
					GroupAboutActivity.mGroup = group;
					mContext.startActivity(intent);
				}
			}
		});
		return view;
	}

	public void joinToGroup(final GroupModel group, final ViewHolder holder) {
		RockMobileApplication.getInstance().showProgressFullScreenDialog((Activity) mContext);
		final UserModel currentUser = (UserModel) UserModel.getCurrentUser();
		PushNotificationService.getInstance().joinGroup(group, currentUser, new ParseFunctionCallback() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if (err == null) {
					RockMobileApplication.getInstance().showToast(mContext, "Added to group successfully.",
							Toast.LENGTH_SHORT);
					holder.statusImageView.setVisibility(View.INVISIBLE);
					holder.statusTextView.setText("Joined");
					AppManager.mMyGroupsList.add(0, group);
					AppManager.mAllGroupsList.remove(group);

				} else {
					RockMobileApplication.getInstance().showToast(mContext,
							mContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}

	public void requestJoinToGroup(final GroupModel group, final ViewHolder holder) {
		RockMobileApplication.getInstance().showProgressFullScreenDialog((Activity) mContext);
		final UserModel currentUser = (UserModel) ParseUser.getCurrentUser();
		PushNotificationService.getInstance().requestJoinGroup(group, currentUser, new ParseFunctionCallback() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if (err == null) {
					RockMobileApplication.getInstance().showToast(mContext, R.string.group_request_pending,
							Toast.LENGTH_SHORT);
					holder.statusImageView.setImageResource(R.drawable.btn_event_remove_selector);
					holder.statusTextView.setText("Pending");

				} else {
					RockMobileApplication.getInstance().showToast(mContext,
							mContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}

	public void cancelJoinToGroup(final GroupModel group, final ViewHolder holder) {
		final UserModel currentUser = (UserModel) ParseUser.getCurrentUser();
		if (!group.removeUserFromPendingUserList(currentUser))
			return;

		RockMobileApplication.getInstance().showProgressFullScreenDialog((Activity) mContext);
		PushNotificationService.getInstance().cancelJoinGroup(group, new ParseFunctionCallback() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if (err == null) {
					RockMobileApplication.getInstance().showToast(mContext, R.string.group_request_canceled,
							Toast.LENGTH_SHORT);
					holder.statusImageView.setImageResource(R.drawable.btn_add_member_selector);
					holder.statusTextView.setText("");

				} else {
					RockMobileApplication.getInstance().showToast(mContext,
							mContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}
}