package org.church.rockmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.church.rockmobile.adapter.GroupAdminsListAdapter;
import org.church.rockmobile.adapter.GroupEventsListAdapter;
import org.church.rockmobile.adapter.GroupThreadsListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.NotificationService;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.EventModel;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.ThreadModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;
import org.church.rockmobile.widget.ExpandableHeightGridView;
import org.church.rockmobile.widget.SegmentedGroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class GroupDetailActivity extends Activity implements OnClickListener, OnCheckedChangeListener {

	public final String TAG = "GroupDetailActivity";
	public static final int REQUEST_ADD_NEW_EVENT = 3000;
	public static final int REQUEST_ADD_NEW_THREAD = 3001;

	private final int TAB_EVENT = 1;
	private final int TAB_FORUM = 0;
	private final int TAB_ABOUT = 2;

	int selectedTab;

	ExpandableHeightGridView mAdminsGridView;
	PullToRefreshListView mPullRefreshListView;
	SwipeMenuListView mListView;

	ImageButton mSettingsImageButton;
	Button mLeaveButton;
	ImageButton mBackImageButton;
	ImageView mGroupPictureImageView;
	TextView mTitleTextView;
	TextView mCategoryTextView;
	TextView mDetailTextView;
	SegmentedGroup mDetailSegment;
	ScrollView mAboutScrollView;
	Button mAddNewButton;
	RadioButton mEventRadioButton;
	RadioButton mForumRadioButton;
	ProgressBar mProgressBar;

	public static List<EventModel> mEventsList = new ArrayList<EventModel>();
	public static List<ThreadModel> mThreadsList = new ArrayList<ThreadModel>();

	public static GroupModel mGroup;

	GroupEventsListAdapter mGroupEventsListAdapter;
	GroupThreadsListAdapter mGroupThreadsListAdapter;
	GroupAdminsListAdapter mGroupAdminsListAdapter;
	boolean mIsGroupAdminsLoading;
	boolean mIsGroupEventsLoading;
	boolean mIsGroupThreadLoading;

	boolean mIsEventsLoaded;
	boolean mIsThreadsLoaded;

	private final Observer refreshNotification = new Observer() {
		public void update(Observable observable, Object data) {
			String groupId = (String) data;
			if (groupId.equals(mGroup.getObjectId())) {
				mGroup.fetchInBackground(new GetCallback<GroupModel>() {

					@Override
					public void done(GroupModel arg0, ParseException arg1) {
						// TODO Auto-generated method stub
						if (mDetailSegment.getCheckedRadioButtonId() == R.id.btn_about)
							displayGroupDetail();
					}
				});
			}
		}
	};

	private final Observer refreshThread = new Observer() {
		public void update(Observable observable, Object data) {
			String threadId = (String) data;
			for (ThreadModel thread : mThreadsList) {
				if (thread.getObjectId().equals(threadId)) {
					thread.getGroup().fetchInBackground(new GetCallback<GroupModel>() {

						@Override
						public void done(GroupModel arg0, ParseException arg1) {
							// TODO Auto-generated method stub
							if (selectedTab == TAB_FORUM && mGroupThreadsListAdapter != null) {
								mGroupThreadsListAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_group_detail);

		initUI();

		// track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Group Detail", params);

		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_GROUP_REFRESH,
				refreshNotification);
		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_REFRESH, refreshThread);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.imageButton_setting:
			intent = new Intent(this, GroupSettingsActivity.class);
			GroupSettingsActivity.mGroup = mGroup;
			startActivity(intent);
			break;
		case R.id.button_group_leave:
			leaveFromGroup();
			break;

		case R.id.imageButton_back:
			super.onBackPressed();
			break;

		case R.id.button_add_new:
			if (mGroup.getIsOrganization() == true && UtilityMethods.containsParseUser(mGroup.getAdminUserList(),
					ParseUser.getCurrentUser()) == false) {
				RockMobileApplication.getInstance().showErrorDialog(this,
						"In organization groups like this one only admins can create events and threads");
				return;
			}

			if (selectedTab == TAB_EVENT) {
				intent = new Intent(GroupDetailActivity.this, NewEventActivity.class);
				NewEventActivity.mGroup = mGroup;
				NewEventActivity.mEvent = null;
				startActivityForResult(intent, REQUEST_ADD_NEW_EVENT);
			} else if (selectedTab == TAB_FORUM) {
				intent = new Intent(GroupDetailActivity.this, NewThreadActivity.class);
				NewThreadActivity.mGroup = mGroup;
				NewThreadActivity.mThread = null;
				startActivityForResult(intent, REQUEST_ADD_NEW_THREAD);
			}
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_GROUP_REFRESH,
				refreshNotification);
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_REFRESH,
				refreshThread);
	}

	public void initUI() {
		TextView titleTextView = (TextView) findViewById(R.id.textView_title);
		titleTextView.setText(mGroup.getTitle().toUpperCase());

		mAboutScrollView = (ScrollView) findViewById(R.id.scrollView_groupAbout);

		mDetailSegment = (SegmentedGroup) findViewById(R.id.segment_category);
		mDetailSegment.setOnCheckedChangeListener(this);

		mEventRadioButton = (RadioButton) findViewById(R.id.btn_events);
		mForumRadioButton = (RadioButton) findViewById(R.id.btn_forum);

		mSettingsImageButton = (ImageButton) findViewById(R.id.imageButton_setting);
		mLeaveButton = (Button) findViewById(R.id.button_group_leave);
		if (UtilityMethods.containsParseUser(mGroup.getAdminUserList(), ParseUser.getCurrentUser())) {
			mSettingsImageButton.setOnClickListener(this);
			mLeaveButton.setVisibility(View.INVISIBLE);

		} else {
			mSettingsImageButton.setVisibility(View.INVISIBLE);
			mLeaveButton.setOnClickListener(this);
			mLeaveButton.setVisibility(View.VISIBLE);
		}

		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);

		mAddNewButton = (Button) findViewById(R.id.button_add_new);
		mAddNewButton.setOnClickListener(this);

		mGroupPictureImageView = (ImageView) findViewById(R.id.imageView_groupPicture);
		mGroupPictureImageView.setAlpha(0.4f);
		if (mGroup.getPhotoFile() != null) {
			AppManager.getInstance().mImageLoader.displayImage(mGroup.getPhotoFile().getUrl(), mGroupPictureImageView,
					AppManager.getInstance().options, null);
		}

		mTitleTextView = (TextView) findViewById(R.id.textView_name);
		mCategoryTextView = (TextView) findViewById(R.id.textView_kind);
		mDetailTextView = (TextView) findViewById(R.id.textView_about);
		mAdminsGridView = (ExpandableHeightGridView) findViewById(R.id.gridView_admins);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.listView_event);
		mListView = mPullRefreshListView.getRefreshableView();
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<SwipeMenuListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
				// TODO Auto-generated method stub
				if (selectedTab == TAB_EVENT) {
					loadGroupEvents(false);

				} else if (selectedTab == TAB_FORUM) {
					loadGroupForums(false);
				}

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
				// TODO Auto-generated method stub
				mPullRefreshListView.onRefreshComplete();
			}
		});

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar_groupDetail);

//		Bundle bundle = getIntent().getExtras();
//		if (bundle != null && bundle.getBoolean(Constants.BUNDLE_JOIN_FROM_GROUP_ABOUT, false) == true) {
//			mForumRadioButton.setChecked(true);
//
//		} else {
			selectedTab = TAB_FORUM;
			displayForms();
//		}

		setupSwipeListView();
	}

	public void setupSwipeListView() {
		// step 1. create a MenuCreator
		SwipeMenuCreator creator = new SwipeMenuCreator() {

			@Override
			public void create(SwipeMenu menu) {
				// create "open" item
				SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
				// set item background
				openItem.setBackground(new ColorDrawable(getResources().getColor(R.color.red_color)));
				// set item width
				openItem.setWidth(UtilityMethods.dp2px(GroupDetailActivity.this, 90));
				// set item title
				if(UtilityMethods.containsParseUser(mGroup.getAdminUserList(), ParseUser.getCurrentUser())){
					openItem.setTitle("Delete");
				}else{
					openItem.setTitle("Flag");
				}
				// set item title fontsize
				openItem.setTitleSize(18);
				// set item title font color
				openItem.setTitleColor(Color.WHITE);
				// add to menu
				menu.addMenuItem(openItem);
			}
		};
		// set creator
		mListView.setMenuCreator(creator);

		// step 2. listener item click event
		mListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

				switch (index) {
				case 0:
					// delete
					if (selectedTab == TAB_EVENT) {
						deleteEvent(position);
						mEventsList.remove(position);
						mGroupEventsListAdapter.notifyDataSetChanged();

					} else if (selectedTab == TAB_FORUM) {
						ThreadModel thread = mThreadsList.get(position);
						if(UtilityMethods.containsParseUser(mGroup.getAdminUserList(), ParseUser.getCurrentUser())){
							deleteThread(position);
							mThreadsList.remove(position);
							mGroupThreadsListAdapter.notifyDataSetChanged();
							
						} else{
							RequestModel request = new RequestModel();
							request.setChurchId(Constants.CHURCH_ID);
							request.setType(Constants.REQUEST_TYPE_THREAD_FLAG);
							request.setFromUser((UserModel) ParseUser.getCurrentUser());
							request.setGroup(mGroup);
							request.setToUserList(mGroup.getAdminUserList());
							request.setRequestStatus(Constants.REQUEST_STATUS_REQUEST);
							request.setThread(thread);
							request.saveInBackground(new SaveCallback() {
								
								@Override
								public void done(ParseException arg0) {
									// TODO Auto-generated method stub
									if (arg0 == null){
										RockMobileApplication.getInstance().showToast(GroupDetailActivity.this, "Thread Flagged", Toast.LENGTH_SHORT);
										PushNotificationService.getInstance().sendPendingRequest(mGroup, null);
									}
								}
							});
						}
					}
					break;
				}
				return false;
			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (selectedTab == TAB_FORUM) {
					Intent intent = new Intent(GroupDetailActivity.this, ThreadMessagesActivity.class);
					ThreadMessagesActivity.mThread = mThreadsList.get(position - 1);
					ThreadMessagesActivity.mGroup = mGroup;
					startActivity(intent);
				}
			}
		});
	}

	public void displayEvents() {
		selectedTab = TAB_EVENT;
		mAddNewButton.setText(getResources().getString(R.string.add_new_event));
		mAddNewButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_event_add, 0, 0, 0);
		mAboutScrollView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
		if (mIsEventsLoaded == true) {
			mGroupEventsListAdapter = new GroupEventsListAdapter(this, mEventsList);
			mListView.setAdapter(mGroupEventsListAdapter);

		} else {
			loadGroupEvents(true);
		}
	}

	public void displayForms() {
		selectedTab = TAB_FORUM;
		mAddNewButton.setText(getResources().getString(R.string.add_new_thread));
		mAddNewButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_thread_add, 0, 0, 0);
		mAboutScrollView.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.GONE);
		if (mIsThreadsLoaded == true) {
			mGroupThreadsListAdapter = new GroupThreadsListAdapter(this, mThreadsList);
			mListView.setAdapter(mGroupThreadsListAdapter);

		} else {
			loadGroupForums(true);
		}
	}

	public void displayGroupDetail() {
		selectedTab = TAB_ABOUT;
		mAboutScrollView.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		if (mGroup != null) {

			mTitleTextView.setText(UtilityMethods.toCapitalizedString(mGroup.getTitle()));
			mCategoryTextView.setText(mGroup.getCategory());
			mDetailTextView.setText(mGroup.getDescription());
			TextView typeTextView = (TextView) findViewById(R.id.textView_type);
			typeTextView.setText(mGroup.getIsPublic() ? "Public" : "Private");
			List<UserModel> userList = UtilityMethods.removeDuplicateUsers(mGroup.getAdminUserList());
			if (userList != null) {
				mGroupAdminsListAdapter = new GroupAdminsListAdapter(GroupDetailActivity.this, userList);
				mAdminsGridView.setAdapter(mGroupAdminsListAdapter);
				mAdminsGridView.setExpanded(true);
			}
		}
	}

	public void loadGroupEvents(boolean isProgressBarShow) {
		if (mIsGroupEventsLoading)
			return;

		mIsGroupEventsLoading = true;
		ParseQuery<EventModel> query = new ParseQuery<EventModel>("GroupEvent");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("group", mGroup);
		query.whereEqualTo("isPending", false);
		query.orderByAscending("timeStamp");
		if (isProgressBarShow) {
			mProgressBar.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
		query.findInBackground(new FindCallback<EventModel>() {

			@Override
			public void done(List<EventModel> list, ParseException err) {
				// TODO Auto-generated method stub
				if (list != null) {
					mEventsList = list;
					mGroupEventsListAdapter = new GroupEventsListAdapter(GroupDetailActivity.this, mEventsList);
					if (selectedTab == TAB_EVENT) {
						mListView.setAdapter(mGroupEventsListAdapter);
						mProgressBar.setVisibility(View.GONE);
						mListView.setVisibility(View.VISIBLE);
					}
					mIsEventsLoaded = true;
				}

				mIsGroupEventsLoading = false;
				mPullRefreshListView.onRefreshComplete();
			}
		});
	}

	public void loadGroupForums(boolean isProgressBarShow) {
		if (mIsGroupThreadLoading)
			return;

		mIsGroupThreadLoading = true;
		ParseQuery<ThreadModel> query = new ParseQuery<ThreadModel>("Thread");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("group", mGroup);
		query.include("group");
		query.include("creator");
		query.orderByDescending("updatedAt");
		if (isProgressBarShow) {
			mProgressBar.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
		query.findInBackground(new FindCallback<ThreadModel>() {

			@Override
			public void done(List<ThreadModel> list, ParseException err) {
				// TODO Auto-generated method stub
				if (list != null) {
					mThreadsList = list;
					mGroupThreadsListAdapter = new GroupThreadsListAdapter(GroupDetailActivity.this, mThreadsList);
					if (selectedTab == TAB_FORUM) {
						mListView.setAdapter(mGroupThreadsListAdapter);
						mProgressBar.setVisibility(View.GONE);
						mListView.setVisibility(View.VISIBLE);
					}
					mIsThreadsLoaded = true;
				}

				mIsGroupThreadLoading = false;
				mPullRefreshListView.onRefreshComplete();
			}
		});
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
		case R.id.btn_events:
			displayEvents();
			break;

		case R.id.btn_forum:
			displayForms();
			break;

		case R.id.btn_about:
			displayGroupDetail();
			break;
		}
	}

	public void leaveFromGroup() {

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupDetailActivity.this);
		alertDialog.setTitle(getResources().getString(R.string.comfirm));
		alertDialog.setMessage(getResources().getString(R.string.are_you_sure_leave));

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				RockMobileApplication.getInstance().showProgressFullScreenDialog(GroupDetailActivity.this);
				mGroup.removeUserFromJoinedUserList((UserModel) ParseUser.getCurrentUser());
				mGroup.saveInBackground(new SaveCallback() {

					@Override
					public void done(ParseException err) {
						// TODO Auto-generated method stub
						if (err == null) {
							RockMobileApplication.getInstance().showToast(GroupDetailActivity.this,
									R.string.group_leave_success, Toast.LENGTH_SHORT);
							CategoryGroupsActivity.mIsDataChanged = true;
							AppManager.mMyGroupsList.remove(mGroup);
							if (mGroup.getIsFeatured() && mGroup.getChurchId() == Constants.CHURCH_ID) {
								AppManager.mAllGroupsList.add(0, mGroup);
							}
							PushNotificationService.getInstance().sendGroupRefresh(mGroup, null);
							GroupDetailActivity.this.onBackPressed();

						} else {
							RockMobileApplication.getInstance().showToast(GroupDetailActivity.this,
									getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
						}

						RockMobileApplication.getInstance().hideProgressDialog();
					}
				});

			}
		});
		// Setting Negative "NO" Btn
		alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				dialog.cancel();
			}
		});

		// Showing Alert Dialog
		alertDialog.show();
	}

	public void deleteEvent(int position) {
		final EventModel event = mEventsList.get(position);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Feed");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("event", event);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException arg1) {
				// TODO Auto-generated method stub
				ParseObject.deleteAllInBackground(list, new DeleteCallback() {

					@Override
					public void done(ParseException arg0) {
						event.deleteInBackground();
					}
				});
			}
		});
	}

	public void deleteThread(int position) {
		final ThreadModel thread = mThreadsList.get(position);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Feed");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("thread", thread);
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> list, ParseException arg1) {
				// TODO Auto-generated method stub
				ParseObject.deleteAllInBackground(list, new DeleteCallback() {

					@Override
					public void done(ParseException arg0) {
						thread.deleteInBackground();
					}
				});
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null && resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_ADD_NEW_EVENT) {
				Bundle extra = data.getExtras();
				if (extra != null && extra.getBoolean(Constants.BUNDLE_NEW_EVENT_REFRESH) == true) {
					displayEvents();
				}
			} else if (requestCode == REQUEST_ADD_NEW_THREAD) {
				displayForms();
			}
		}
	}

}
