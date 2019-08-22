package org.church.rockmobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.church.rockmobile.adapter.GroupAdminsListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.NotificationService;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;
import org.church.rockmobile.service.PushNotificationService.ParseFunctionCallback;
import org.church.rockmobile.widget.ExpandableHeightGridView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class GroupAboutActivity extends Activity implements OnClickListener {

	public final String TAG = "GroupAboutActivity";

	ExpandableHeightGridView mAdminsGridView;
	ImageButton mAddMemberImageButton;
	ImageButton mBackImageButton;
	ImageView mGroupPictureImageView;
	TextView mTitleTextView;
	TextView mCategoryTextView;
	TextView mDetailTextView;

	public static GroupModel mGroup;

	GroupAdminsListAdapter mGroupAdminsListAdapter;
	boolean mIsGroupAdminsLoading;
	UserModel mCurrentUser;

	private final Observer refreshNotification = new Observer() {
		public void update(Observable observable, Object data) {
			String groupId = (String) data;
			if (groupId.equals(mGroup.getObjectId())) {
				mGroup.fetchInBackground(new GetCallback<GroupModel>() {

					@Override
					public void done(GroupModel arg0, ParseException arg1) {
						// TODO Auto-generated method stub
						initUI();
					}
				});
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_no_group_about);

		initUI();

		// track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Group About", params);

		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_GROUP_REFRESH,
				refreshNotification);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.imageButton_add_member:
			if (!UtilityMethods.containsParseUser(mGroup.getAdminUserList(), mCurrentUser)
					&& !UtilityMethods.containsParseUser(mGroup.getJoinedUserList(), mCurrentUser)
					&& !UtilityMethods.containsParseUser(mGroup.getPendingUserList(), mCurrentUser)) {
				if (mGroup.getIsPublic() == false)
					requestJoinToGroup();
				else
					joinToGroup();

			} else if (UtilityMethods.containsParseUser(mGroup.getPendingUserList(), mCurrentUser)) {
				cancelJoinToGroup();
			}
			break;

		case R.id.imageButton_back:
			super.onBackPressed();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_GROUP_REFRESH,
				refreshNotification);
	}

	public void initUI() {
		mCurrentUser = (UserModel) ParseUser.getCurrentUser();

		mAddMemberImageButton = (ImageButton) findViewById(R.id.imageButton_add_member);
		mAddMemberImageButton.setOnClickListener(this);

		if (!UtilityMethods.containsParseUser(mGroup.getAdminUserList(), mCurrentUser)
				&& !UtilityMethods.containsParseUser(mGroup.getJoinedUserList(), mCurrentUser)
				&& !UtilityMethods.containsParseUser(mGroup.getPendingUserList(), mCurrentUser)) {
			if (!mGroup.getIsPublic()) {
				mAddMemberImageButton.setImageResource(R.drawable.btn_add_member_selector_private);
			} else {
				mAddMemberImageButton.setImageResource(R.drawable.btn_add_member_selector);
			}
		} else if (UtilityMethods.containsParseUser(mGroup.getPendingUserList(), mCurrentUser)) {
			mAddMemberImageButton.setImageResource(R.drawable.btn_event_remove_selector);
		}

		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);

		mGroupPictureImageView = (ImageView) findViewById(R.id.imageView_groupPicture);
		mTitleTextView = (TextView) findViewById(R.id.textView_groupTitle);
		mCategoryTextView = (TextView) findViewById(R.id.textView_groupCategory);
		mDetailTextView = (TextView) findViewById(R.id.textView_groupDetail);
		mAdminsGridView = (ExpandableHeightGridView) findViewById(R.id.gridView_admins);

		if (mGroup != null) {
			mTitleTextView.setText(mGroup.getTitle());
			mCategoryTextView.setText(mGroup.getCategory());
			mDetailTextView.setText(mGroup.getDescription());
			if (mGroup.getPhotoFile() != null)
				AppManager.getInstance().mImageLoader.displayImage(mGroup.getPhotoFile().getUrl(),
						mGroupPictureImageView, AppManager.getInstance().options, null);

			List<UserModel> userList = UtilityMethods.removeDuplicateUsers(mGroup.getAdminUserList());
			if (userList != null) {
				mGroupAdminsListAdapter = new GroupAdminsListAdapter(GroupAboutActivity.this, userList);
				mAdminsGridView.setAdapter(mGroupAdminsListAdapter);
				mAdminsGridView.setExpanded(true);
			}
		}
	}

	public void joinToGroup() {
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		PushNotificationService.getInstance().joinGroup(mGroup, mCurrentUser, new ParseFunctionCallback() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if (err == null) {
					RockMobileApplication.getInstance().showToast(GroupAboutActivity.this, "Added to group.",
							Toast.LENGTH_SHORT);
					mAddMemberImageButton.setImageResource(R.drawable.btn_event_remove_selector);
					CategoryGroupsActivity.mIsDataChanged = true;
					AppManager.mMyGroupsList.add(0, mGroup);
					AppManager.mAllGroupsList.remove(mGroup);

					// go to group detail page.
					Intent intent = new Intent(GroupAboutActivity.this, GroupDetailActivity.class);
					GroupDetailActivity.mGroup = mGroup;
//					intent.putExtra(Constants.BUNDLE_JOIN_FROM_GROUP_ABOUT, true);
					startActivity(intent);
					finish();

				} else {
					RockMobileApplication.getInstance().showToast(GroupAboutActivity.this,
							getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}

	public void requestJoinToGroup() {
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		PushNotificationService.getInstance().requestJoinGroup(mGroup, mCurrentUser, new ParseFunctionCallback() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if (err == null) {
					RockMobileApplication.getInstance().showToast(GroupAboutActivity.this,
							R.string.group_request_pending, Toast.LENGTH_SHORT);
					mAddMemberImageButton.setImageResource(R.drawable.btn_event_remove_selector);
					CategoryGroupsActivity.mIsDataChanged = true;

				} else {
					RockMobileApplication.getInstance().showToast(GroupAboutActivity.this,
							getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}

	public void cancelJoinToGroup() {

		if (!mGroup.removeUserFromPendingUserList(mCurrentUser))
			return;

		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		PushNotificationService.getInstance().cancelJoinGroup(mGroup, new ParseFunctionCallback() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if (err == null) {
					RockMobileApplication.getInstance().showToast(GroupAboutActivity.this,
							R.string.group_request_canceled, Toast.LENGTH_SHORT);
					if (!mGroup.getIsPublic()) {
						mAddMemberImageButton.setImageResource(R.drawable.btn_add_member_selector_private);
					} else {
						mAddMemberImageButton.setImageResource(R.drawable.btn_add_member_selector);
					}
					CategoryGroupsActivity.mIsDataChanged = true;

				} else {
					RockMobileApplication.getInstance().showToast(GroupAboutActivity.this,
							getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}
}
