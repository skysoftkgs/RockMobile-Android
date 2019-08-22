package org.church.rockmobile;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.RequestNotificationListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageButton;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class RequestNotificationActivity extends Activity implements OnClickListener{

	public final String TAG = "RequestNotificationActivity";
	public final static int REQUEST_JOIN_GROUP = 0;
	public final static int REQUEST_ADD_EVENT = 1;
	
	ImageButton mBackImageButton;
	PullToRefreshGridView mPullRefreshRequestGridView;
	GridView mRequestGridView;

	boolean mIsRequestsLoading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_request);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Request screen", params); 
	}
	 

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_back:
				super.onBackPressed();
				break;
		}
	}
	
	public void initUI(){
		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);
		
		mPullRefreshRequestGridView = (PullToRefreshGridView) findViewById(R.id.gridView_request);
		mRequestGridView = mPullRefreshRequestGridView.getRefreshableView();
		mPullRefreshRequestGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

 			@Override
 			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
 				loadRequests();
 			}

 			@Override
 			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
 				mPullRefreshRequestGridView.onRefreshComplete();
 			}

        });
		
		if(AppManager.mRequestsList.isEmpty() || AppManager.mIsRequestsDataChanged == true){
			loadRequests();
			
		}else{
			RequestNotificationListAdapter requestNotificationListAdapter = new RequestNotificationListAdapter(RequestNotificationActivity.this, AppManager.mRequestsList);
			mRequestGridView.setAdapter(requestNotificationListAdapter);
		}
	}
	
	public void loadRequests(){
		if(mIsRequestsLoading) return;
		
		final ParseUser currentUser = ParseUser.getCurrentUser();
		mIsRequestsLoading = true;
		if(!mPullRefreshRequestGridView.isRefreshing())
			RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		ParseQuery<RequestModel> query = new ParseQuery<RequestModel>("Feed");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("requestStatus", Constants.REQUEST_STATUS_REQUEST);
		query.whereContainsAll("toUsers", Arrays.asList(currentUser));
		query.orderByDescending("createdAt");
		query.include("fromUser");
		query.include("groupInvitationUser");
		query.include("group");
		query.include("event");
		query.include("oldEvent");
		query.include("thread");
		query.include("threadMessage");
		query.findInBackground(new FindCallback<RequestModel>() {
			
			@Override
			public void done(List<RequestModel> list, ParseException err) {
				// TODO Auto-generated method stub				
				if(list != null){
					AppManager.mRequestsList.clear();
					
					for(RequestModel model : list){
						if(model.getType().equals(Constants.REQUEST_TYPE_GROUP_JOIN)){
							if(model.getGroup() != null)
								AppManager.mRequestsList.add(model);
							
						}if(model.getType().equals(Constants.REQUEST_TYPE_GROUP_INVITATION)){
							if(model.getGroup() != null && model.getGroupInvitationUser() != null)
								AppManager.mRequestsList.add(model);
							
						}else if(model.getType().equals(Constants.REQUEST_TYPE_EVENT_ADD) ||
								model.getType().equals(Constants.REQUEST_TYPE_EVENT_CHANGE)){
							if(model.getGroup() != null && model.getEvent() != null)
								AppManager.mRequestsList.add(model);
							
						}else if(model.getType().equals(Constants.REQUEST_TYPE_THREAD_ADD)){
							if(model.getGroup() != null && model.getThread() != null)
								AppManager.mRequestsList.add(model);
							
						}else if(model.getType().equals(Constants.REQUEST_TYPE_MESSAGE_FLAG)){
							if(model.getGroup() != null && model.getThreadMessage() != null)
								AppManager.mRequestsList.add(model);
							
						}else if(model.getType().equals(Constants.REQUEST_TYPE_THREAD_FLAG)){
							if(model.getGroup() != null && model.getThread() != null)
								AppManager.mRequestsList.add(model);
							
						}else{
							AppManager.mRequestsList.add(model);
						}
					
					}
					RequestNotificationListAdapter requestNotificationListAdapter = new RequestNotificationListAdapter(RequestNotificationActivity.this, AppManager.mRequestsList);
					mRequestGridView.setAdapter(requestNotificationListAdapter);
					
					AppManager.mRequestNotificationCount = 0;
					AppManager.mIsRequestsDataChanged = false;
				}
				
				currentUser.put("lastRequestVisitTime", new Date());
				currentUser.saveInBackground();
				mIsRequestsLoading = false;
		        RockMobileApplication.getInstance().hideProgressDialog();
		        
		        mPullRefreshRequestGridView.onRefreshComplete();
			}
		});
	}
}
