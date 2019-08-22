package org.church.rockmobile;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.church.rockmobile.adapter.ThreadMessageListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.NotificationService;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.GroupNotificationModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.ThreadMessageModel;
import org.church.rockmobile.model.ThreadModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class ThreadMessagesActivity extends Activity implements OnClickListener {

	public final String TAG = "ThreadMessagesActivity";

	ImageButton mBackImageButton;
	ImageButton mEditImageButton;
	PullToRefreshListView mPullRefreshListView;
	SwipeMenuListView mListView;
	EditText mMessageEditText;
	boolean isThreadMessageLoading;

	public static ThreadModel mThread;
	public static GroupModel mGroup;
	public ThreadMessageListAdapter mAdapter;
	List<ThreadMessageModel> mThreadMessages = new ArrayList<ThreadMessageModel>();

	private final Observer messageNotification = new Observer() {
		public void update(Observable observable, Object data) {
			String threadMessageId = (String) data;
			ParseQuery<ThreadMessageModel> query = new ParseQuery<ThreadMessageModel>("ThreadMessage");
			query.whereEqualTo("objectId", threadMessageId);
			query.include("author");
			query.getFirstInBackground(new GetCallback<ThreadMessageModel>() {

				@Override
				public void done(ThreadMessageModel model, ParseException err) {
					// TODO Auto-generated method stub
					if (model != null && err == null) {
						addThreadToListView(model);
					}
				}
			});
		}
	};

	private final Observer refreshThread = new Observer() {
		public void update(Observable observable, Object data) {
			String threadId = (String) data;
			if (mThread.getObjectId().equals(threadId)) {
				mThread.fetchInBackground(new GetCallback<ThreadModel>() {

					@Override
					public void done(ThreadModel arg0, ParseException arg1) {
						// TODO Auto-generated method stub
						mThread = arg0;
					}
				});
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_thread_message);

		initUI();

		// track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Thread message", params);

		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_MESSAGE,
				messageNotification);
		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_REFRESH, refreshThread);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageButton_back:
			super.onBackPressed();
			break;

		case R.id.imageButton_editThread:
			Intent intent = new Intent(this, NewThreadActivity.class);
			if (mThread == null) // new thread
				NewThreadActivity.mGroup = GroupDetailActivity.mGroup;
			else
				NewThreadActivity.mGroup = mGroup;
			NewThreadActivity.mThread = mThread;
			startActivity(intent);
			break;

		case R.id.imageButton_sendMessage:
			addThreadMessageToGroup();
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		TextView threadNameTextView = (TextView) findViewById(R.id.textView_threadName);
		threadNameTextView.setText(mThread.getTitle());

		if (AppManager.mIsThreadMessageDataChanged) {
			loadThreadMessages();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_MESSAGE,
				messageNotification);
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_REFRESH,
				refreshThread);
	}

	public void initUI() {
		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);

		mEditImageButton = (ImageButton) findViewById(R.id.imageButton_editThread);
		mEditImageButton.setOnClickListener(this);
		ParseUser currentUser = ParseUser.getCurrentUser();
		if(!mThread.getUser().getObjectId().equals(currentUser.getObjectId()) &&
				!UtilityMethods.containsParseUser(mGroup.getAdminUserList(), currentUser)){
			mEditImageButton.setVisibility(View.GONE);
		}
		
		mMessageEditText = (EditText) findViewById(R.id.editText_message);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.listView_messages);
		mListView = mPullRefreshListView.getRefreshableView();
		mPullRefreshListView.setOnRefreshListener(new OnRefreshListener2<SwipeMenuListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
				// TODO Auto-generated method stub
				loadThreadMessages();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
				// TODO Auto-generated method stub
				mPullRefreshListView.onRefreshComplete();
			}
		});
		
		ImageButton sendMessageImageButton = (ImageButton) findViewById(R.id.imageButton_sendMessage);
		sendMessageImageButton.setOnClickListener(this);

		AppManager.mIsThreadMessageDataChanged = true;
		
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
				openItem.setWidth(UtilityMethods.dp2px(ThreadMessagesActivity.this, 90));
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
					final ThreadMessageModel threadMessage = mThreadMessages.get(position);
					if(UtilityMethods.containsParseUser(mGroup.getAdminUserList(), ParseUser.getCurrentUser())){
						ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Feed");
						query.setLimit(1000);
						query.whereEqualTo("churchId", Constants.CHURCH_ID);
						query.whereEqualTo("threadMessage", mThreadMessages);
						query.findInBackground(new FindCallback<ParseObject>() {

							@Override
							public void done(List<ParseObject> list,
									ParseException error) {
								// TODO Auto-generated method stub
								if (error == null){
									ParseObject.deleteAllInBackground(list, new DeleteCallback() {
										
										@Override
										public void done(ParseException arg0) {
											// TODO Auto-generated method stub
											threadMessage.deleteInBackground();
											mThread.setMessageCount(mThread.getMessageCount() - 1);
											mThread.saveInBackground();
										}
									});
								}
							}
						});
						
						mThreadMessages.remove(position);
						mAdapter.notifyDataSetChanged();
						
					}else{
						RequestModel request = new RequestModel();
						request.setChurchId(Constants.CHURCH_ID);
						request.setType(Constants.REQUEST_TYPE_MESSAGE_FLAG);
						request.setFromUser((UserModel) ParseUser.getCurrentUser());
						request.setGroup(mGroup);
						request.setToUserList(mGroup.getAdminUserList());
						request.setRequestStatus(Constants.REQUEST_STATUS_REQUEST);
						request.setThread(mThread);
						request.setThreadMessage(threadMessage);
						request.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException arg0) {
								// TODO Auto-generated method stub
								if (arg0 == null){
									RockMobileApplication.getInstance().showToast(ThreadMessagesActivity.this, "Message Flagged", Toast.LENGTH_SHORT);
									PushNotificationService.getInstance().sendPendingRequest(mGroup, null);
								}
							}
						});
					}

					break;
				}
				return false;
			}
		});
	}
	
	public void loadThreadMessages() {
		if (isThreadMessageLoading) {
			return;
		}

		isThreadMessageLoading = true;
		RockMobileApplication.getInstance().showProgressFullScreenDialog(ThreadMessagesActivity.this);
		mThreadMessages = new ArrayList<ThreadMessageModel>();
		ParseQuery<ThreadMessageModel> query = new ParseQuery<ThreadMessageModel>("ThreadMessage");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("thread", mThread);
		query.orderByDescending("createdAt");
		query.include("author");
		query.setLimit(20);
		query.findInBackground(new FindCallback<ThreadMessageModel>() {

			@Override
			public void done(List<ThreadMessageModel> list, ParseException err) {
				// TODO Auto-generated method stub
				if (list != null) {
					for (int i = list.size() - 1; i >= 0; i--)
						mThreadMessages.add(list.get(i));
					mAdapter = new ThreadMessageListAdapter(ThreadMessagesActivity.this, mThreadMessages);
					mListView.setAdapter(mAdapter);
					if (mThreadMessages.size() > 0)
						mListView.setSelection(mThreadMessages.size() - 1);
				}

				AppManager.mIsThreadMessageDataChanged = false;
				isThreadMessageLoading = false;
				mPullRefreshListView.onRefreshComplete();
				RockMobileApplication.getInstance().hideProgressDialog();
			}
		});
	}

	public void addThreadMessageToGroup() {
		if (TextUtils.isEmpty(mMessageEditText.getText().toString().trim())) {
			RockMobileApplication.getInstance().showErrorDialog(this, "Thread left empty, please enter a message");
			mMessageEditText.setText("");
			return;
		}

		final ThreadMessageModel model = new ThreadMessageModel();
		model.setChurchId(Constants.CHURCH_ID);
		model.setUser(ParseUser.getCurrentUser());
		model.setThread(mThread);
		model.setMessage(mMessageEditText.getText().toString());
		model.setPostTime(new Date());
		mMessageEditText.setText("");
		model.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException err) {
				// TODO Auto-generated method stub
				if (err == null) {
					addThreadToListView(model);

					// send push notification
					if (mThread.getIsMessageEnabled() && UtilityMethods.containsParseUser(mGroup.getAdminUserList(),
							ParseUser.getCurrentUser())) {
						PushNotificationService.getInstance().sendThreadMessage(model, model.getMessage(), true, null);

					} else {
						PushNotificationService.getInstance().sendThreadMessage(model, model.getMessage(), false, null);
					}

					// set start user and end user and updating messageCount
					UserModel currentUser = (UserModel) ParseUser.getCurrentUser();
					mThread.setLastUser(currentUser.getRealUsername());
					int messageCount = mThread.getMessageCount() < 0 ? 1 : mThread.getMessageCount() + 1;
					mThread.setMessageCount(messageCount);
					mThread.saveInBackground();

					ParseQuery<GroupNotificationModel> query = new ParseQuery<GroupNotificationModel>(
							"GroupNotification");
					query.whereEqualTo("churchId", Constants.CHURCH_ID);
					query.whereEqualTo("groupId", mGroup.getObjectId());
					query.findInBackground(new FindCallback<GroupNotificationModel>() {

						@Override
						public void done(List<GroupNotificationModel> list, ParseException err) {
							// TODO Auto-generated method stub
							List<UserModel> groupUserList = mGroup.getGroupUserList();
							if (groupUserList != null) {
								for (int i = 0; i < groupUserList.size(); i++) {
									if (ParseUser.getCurrentUser().getObjectId()
											.equals(groupUserList.get(i).getObjectId()))
										continue;

									int index = getIndexOfGroupNotificationArray(groupUserList.get(i), list);
									if (index >= 0) {
										GroupNotificationModel model = list.get(index);
										model.setNotificationCount(model.getNotificationCount() + 1);
										model.saveEventually();

									} else {
										GroupNotificationModel model = new GroupNotificationModel();
										model.setChurchId(Constants.CHURCH_ID);
										model.setGroupId(mGroup.getObjectId());
										model.setUserId(groupUserList.get(i).getObjectId());
										model.setNotificationCount(1);
										model.saveEventually();
									}
								}
							}
						}
					});
				}
			}
		});

	}

	public void addThreadToListView(ThreadMessageModel model) {
		mThreadMessages.add(model);
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(mAdapter.getCount() - 1);
	}

	public int getIndexOfGroupNotificationArray(ParseUser user, List<GroupNotificationModel> list) {
		if (user == null || list == null)
			return -1;

		for (int i = 0; i < list.size(); i++) {
			GroupNotificationModel model = list.get(i);
			if (model.getUserId().equals(user.getObjectId())) {
				return i;
			}
		}

		return -1;
	}
}
