package org.church.rockmobile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.common.Constants;
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
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewThreadActivity extends Activity implements OnClickListener{

	public final String TAG = "NewThreadActivity";

	ImageButton mSendImageButton;
	ImageButton mCloseImageButton;
	EditText mNameEditText;
	EditText mMessageEditText;
	TextView mTitleTextView;
	ImageButton mPushSettingImageButton;
	int mMsgId;
	boolean mIsPushSet;
	
	public static GroupModel mGroup;
	public static ThreadModel mThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_new_thread);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("New thread", params); 
	}
	 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_sendMessage:
				addNewThread();
				break;
				
			case R.id.imageButton_close:
				super.onBackPressed();
				break;
		}
	}
	
	public void initUI(){
		mTitleTextView = (TextView) findViewById(R.id.textView_title);
		
		mSendImageButton = (ImageButton) findViewById(R.id.imageButton_sendMessage);
		mSendImageButton.setOnClickListener(this);
		
		mCloseImageButton = (ImageButton) findViewById(R.id.imageButton_close);
		mCloseImageButton.setOnClickListener(this);
		
		mNameEditText = (EditText) findViewById(R.id.editText_name);
		mMessageEditText = (EditText) findViewById(R.id.editText_message);
		
		mPushSettingImageButton = (ImageButton) findViewById(R.id.imageButton_pushSetting);
		if(UtilityMethods.containsParseUser(mGroup.getAdminUserList(), ParseUser.getCurrentUser())){
			mPushSettingImageButton.setVisibility(View.VISIBLE);
		}else{
			mPushSettingImageButton.setVisibility(View.GONE);
		}
		
		if(mThread != null){
			mTitleTextView.setText("EDIT THREAD");
			mNameEditText.setText(mThread.getTitle());
			RelativeLayout messageInputLayout = (RelativeLayout) findViewById(R.id.layout_messageInput);
			messageInputLayout.setVisibility(View.INVISIBLE);
			
			setPushChecked(mThread.getIsMessageEnabled());
		}
		
		mPushSettingImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				setPushChecked(!mIsPushSet);
				
				if(mThread != null){
					if(mIsPushSet)
						mThread.setIsMessageEnabled(true);
					else
						mThread.setIsMessageEnabled(false);
					
					mThread.saveInBackground();
					
					PushNotificationService.getInstance().sendThreadRefresh(mThread, null);
				}
			}
		});
		
		mNameEditText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
					(actionId == EditorInfo.IME_ACTION_DONE)){
					addNewThread();
				}
				return false;
			}
		});
	}

	public void setPushChecked(boolean checked){
		mIsPushSet = checked;
		if(checked){
			mPushSettingImageButton.setImageResource(R.drawable.switch_thread_admin_on);
			
		}else{
			mPushSettingImageButton.setImageResource(R.drawable.switch_thread_admin_off);
		}
	}
	
	
	public void addNewThread(){
		if(!UtilityMethods.checkIsEmptyForEditText(NewThreadActivity.this, mNameEditText, "Thread Name "))
			return;
		
		UtilityMethods.hideKeyboard(this);
		RockMobileApplication.getInstance().showProgressFullScreenDialog(NewThreadActivity.this);
		
		mMsgId = R.string.thread_changed; 
		if(mThread == null){
			mThread = new ThreadModel();
			mMsgId = R.string.new_thread_saved;
		}
		
		mThread.setChurchId(Constants.CHURCH_ID);
		mThread.setTitle(mNameEditText.getText().toString());
		mThread.setUser(ParseUser.getCurrentUser());
		mThread.setGroup(mGroup);
		if(mIsPushSet){
			mThread.setIsMessageEnabled(true);
		}else{
			mThread.setIsMessageEnabled(false);
		}
		mThread.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException object) {
				
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(object == null){
					RockMobileApplication.getInstance().showToast(NewThreadActivity.this, mMsgId, Toast.LENGTH_SHORT);
					
					if(mTitleTextView.getText().toString().equals("NEW THREAD")){
						GroupDetailActivity.mThreadsList.add(0, mThread);
						addToFeed();
						
						if(mMessageEditText.getText().toString().length() > 0){
							addThreadMessage(mMessageEditText.getText().toString(), mThread);
						}
						
						sendNewThreadNotification(mThread);
						
						Intent returnIntent = new Intent();
						returnIntent.putExtra(Constants.BUNDLE_NEW_THREAD_REFRESH, true);
						setResult(RESULT_OK, returnIntent);
					}
					
					NewThreadActivity.this.finish();
					
				}
			}
		});
	}
	
	public void sendNewThreadNotification(final ThreadModel thread){
		//send push notification
		PushNotificationService.getInstance().sendNewThread(mThread, null);			
		
		//for group notification
		ParseQuery<GroupNotificationModel> query = new ParseQuery<GroupNotificationModel>("GroupNotification");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("groupId", thread.getGroup().getObjectId());
		query.findInBackground(new FindCallback<GroupNotificationModel>() {
			
			@Override
			public void done(List<GroupNotificationModel> list, ParseException arg1) {
				// TODO Auto-generated method stub
				List<UserModel> groupUserList = thread.getGroup().getGroupUserList();
				ParseUser currentUser = ParseUser.getCurrentUser();
				for(int i=0;i<groupUserList.size();i++){
					UserModel user = groupUserList.get(i);
					if(currentUser.getObjectId().equals(user.getObjectId())){
						continue;
					}
					
					int index = UtilityMethods.getIndexOfGroupNotificationArray(user, list);
					if(index >= 0){
						GroupNotificationModel model = list.get(index);
						model.setNotificationCount(model.getNotificationCount() + 1);
						model.saveEventually();
						
					}else{
						GroupNotificationModel model = new GroupNotificationModel();
						model.setChurchId(Constants.CHURCH_ID);
						model.setGroupId(thread.getGroup().getObjectId());
						model.setUserId(user.getObjectId());
						model.setNotificationCount(1);
						model.saveEventually();
					}
				}
			}
		});
	}
	
	public void addToFeed(){
		RequestModel feed = new RequestModel();
		feed.setChurchId(Constants.CHURCH_ID);
		feed.setFromUser((UserModel)ParseUser.getCurrentUser());
		feed.setGroup(mGroup);
		feed.setThread(mThread);
		feed.setType(Constants.REQUEST_TYPE_THREAD_ADD);
		feed.setToUserList(mGroup.getGroupUserListExceptCurrentUser());
		feed.saveInBackground();
	}
	
	public void addThreadMessage(String message, final ThreadModel thread){
		final ThreadMessageModel model = new ThreadMessageModel();
		model.setChurchId(Constants.CHURCH_ID);
		model.setUser(ParseUser.getCurrentUser());
		model.setThread(thread);
		model.setMessage(message);
		model.setPostTime(new Date());
		model.saveInBackground();
	}
}
