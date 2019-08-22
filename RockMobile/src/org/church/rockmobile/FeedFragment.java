package org.church.rockmobile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import org.church.rockmobile.adapter.FeedListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.NotificationService;
import org.church.rockmobile.landing.LoginActivity;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.UserModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class FeedFragment extends BaseFragment implements OnClickListener{
	
	MainActivity mActivity;
	boolean mIsFeedsLoading;
	boolean mIsRequestsLoading;
	public boolean isAllLoaded = false;
	int pageNo;
	
	public static boolean mIsDataChanged;
	
	PullToRefreshGridView mPullRefreshFeedGridView;
	GridView mFeedGridView;
	TextView mNotificationCountTextView;
	TextView mInvitationTextView;
	ProgressBar mFeedProgressBar;
	FeedListAdapter mAdapter;
	
	private final Observer pendingNotification = new Observer()
	{
		public void update(Observable observable, Object data)
		{
			showRequestCount();
		}
	};
	
	private final Observer feedNotification = new Observer()
	{
		public void update(Observable observable, Object data)
		{
			loadFeeds();
		}
	};
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_feed, container, false);      
		mActivity = (MainActivity) getActivity();
				
		mPullRefreshFeedGridView = (PullToRefreshGridView) rootView.findViewById(R.id.gridView_feed);
		mFeedGridView = mPullRefreshFeedGridView.getRefreshableView();
		mPullRefreshFeedGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

 			@Override
 			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
 				isAllLoaded = false;
 				pageNo = 0;
 				loadFeeds();
 			}

 			@Override
 			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
 				mPullRefreshFeedGridView.onRefreshComplete();
 			}

        });
		mFeedProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_feed);
		
		showFeeds();
		
		//track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Feed", params); 
		
		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_PENDING, pendingNotification);
		NotificationService.getInstance().addObserver(Constants.LOCAL_NOTIFICATION_TYPE_FEED, feedNotification);
        return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
		
		if(ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())){
			ParseUser.logOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
	        startActivity(intent);
	        return;
		}
		
    	initActionBar();
	}

	public void initActionBar(){
		//customize actionbar
	    LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = inflator.inflate(R.layout.actionbar_feed, null);
	    ImageButton feedNotificationImageButton = (ImageButton) v.findViewById(R.id.imageButton_feedNotification);
	    feedNotificationImageButton.setOnClickListener(this);
	    mNotificationCountTextView = (TextView) v.findViewById(R.id.textView_notificationCount);
	    mInvitationTextView = (TextView) v.findViewById(R.id.textView_invitation);
	    mActivity.mActionBar.setCustomView(v);
	    
	    showRequestCount();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mIsDataChanged == true){
			isAllLoaded = false;
			pageNo = 0;
			loadFeeds();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_feedNotification:
				AppManager.mRequestNotificationCount = 0;
				mNotificationCountTextView.setVisibility(View.GONE);
				Intent intent = new Intent(mActivity, RequestNotificationActivity.class);
				startActivity(intent);
				break;
		}
	}
	
	public void showFeeds(){
		if(AppManager.mFeedsList.size() == 0){
			isAllLoaded = false;
			pageNo = 0;
			loadFeeds();
		
		} else{
			mFeedGridView.setAdapter(mAdapter);
			mFeedProgressBar.setVisibility(View.GONE);
		}
	}
	
	public void loadFeeds(){
		if(mIsFeedsLoading) return;
		
		final ParseUser currentUser = ParseUser.getCurrentUser();
		mIsFeedsLoading = true;
		if(!mPullRefreshFeedGridView.isRefreshing() && pageNo == 0)
			mFeedProgressBar.setVisibility(View.VISIBLE);
				
		ParseQuery<RequestModel> query1 = new ParseQuery<RequestModel>("Feed");
		query1.whereEqualTo("requestStatus", Constants.REQUEST_STATUS_FEED);
		query1.whereContainsAll("toUsers", Arrays.asList(currentUser));
				
		ParseQuery<RequestModel> query2 = new ParseQuery<RequestModel>("Feed");
		query2.whereEqualTo("type", Constants.REQUEST_TYPE_STORY_ADD);
		
		Date today = new Date();
		ParseQuery<ParseObject> innerQuery = new ParseQuery<ParseObject>("Livestream");		
		innerQuery.whereGreaterThanOrEqualTo("endTime", today);
		ParseQuery<RequestModel> query3 = new ParseQuery<RequestModel>("Feed");
		query3.whereEqualTo("type", Constants.REQUEST_TYPE_LIVESTREAM);
		query3.whereMatchesQuery("livestream", innerQuery);
		
		ParseQuery<RequestModel> query4 = new ParseQuery<RequestModel>("Feed");
		query4.whereEqualTo("type", Constants.REQUEST_TYPE_THREAD_ADD);
		query4.whereContainsAll("toUsers", Arrays.asList(currentUser));
		
		ParseQuery<RequestModel> query5 = new ParseQuery<RequestModel>("Feed");
		query5.whereEqualTo("type", Constants.REQUEST_TYPE_SERIES_MESSAGE_ADD);
		
		List <ParseQuery<RequestModel>> query0 = new ArrayList<ParseQuery<RequestModel>>();
		query0.add(query1);
		query0.add(query2);
		query0.add(query3);
		query0.add(query4);
		query0.add(query5);
		
		ParseQuery<RequestModel> query = ParseQuery.or(query0);
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.include("fromUser");
		query.include("group");
		query.include("event");
		query.include("oldEvent");
		query.include("thread");
		query.include("story");
		query.include("livestream");
		query.include("seriesMessage");
		query.orderByDescending("updatedAt");
		query.setLimit(Constants.PARSE_QUERY_LIMIT_COUNT);
		query.setSkip(pageNo * Constants.PARSE_QUERY_LIMIT_COUNT);
		query.findInBackground(new FindCallback<RequestModel>() {
			
			@Override
			public void done(List<RequestModel> list, ParseException err) {
				// TODO Auto-generated method stub
				mPullRefreshFeedGridView.onRefreshComplete();
				
				if(list != null){
					if(pageNo == 0){
						AppManager.mFeedsList.clear();
					}
					
					if(list.size() == Constants.PARSE_QUERY_LIMIT_COUNT){
						pageNo++;
					
					}else{
						isAllLoaded = true;
					}
					
					for(RequestModel model : list){
						if(model.getType().equals(Constants.REQUEST_TYPE_GROUP_JOIN)){
							if(model.getGroup() != null)
								AppManager.mFeedsList.add(model);
							
						}else if(model.getType().equals(Constants.REQUEST_TYPE_EVENT_ADD) ||
								model.getType().equals(Constants.REQUEST_TYPE_EVENT_CHANGE)){
							if(model.getGroup() != null && model.getEvent() != null)
								AppManager.mFeedsList.add(model);
							
						}else if(model.getType().equals(Constants.REQUEST_TYPE_THREAD_ADD)){
							if(model.getGroup() != null && model.getThread() != null)
								AppManager.mFeedsList.add(model);
							
						}else{
							AppManager.mFeedsList.add(model);
						}
					}
					
					if(mAdapter == null && FeedFragment.this != null){
						mAdapter = new FeedListAdapter(FeedFragment.this, AppManager.mFeedsList);
						mFeedGridView.setAdapter(mAdapter);
					}else{
						mAdapter.notifyDataSetChanged();
					}
					
					mIsDataChanged = false;
				}
				
				mIsFeedsLoading = false;
		        mFeedProgressBar.setVisibility(View.GONE);
			}
		});
	}
	
	public void showRequestCount(){
	
		if(AppManager.mRequestNotificationCount <= 0)
			loadRequestCount();
		else{
			mNotificationCountTextView.setVisibility(View.VISIBLE);
			mNotificationCountTextView.setText(String.valueOf(AppManager.mRequestNotificationCount));
			if(AppManager.mInvitationCount < 0)
				AppManager.mInvitationCount = 0;
			mInvitationTextView.setText("You have " + String.valueOf(AppManager.mInvitationCount) + " invites.");
		}
	}
	
	public void loadRequestCount(){
		if(mIsRequestsLoading) return;
		
		mNotificationCountTextView.setVisibility(View.GONE);
		final ParseUser currentUser = ParseUser.getCurrentUser();
		mIsRequestsLoading = true;
		ParseQuery<RequestModel> query = new ParseQuery<RequestModel>("Feed");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("requestStatus", Constants.REQUEST_STATUS_REQUEST);
		query.whereContainsAll("toUsers", Arrays.asList(currentUser));
		query.whereGreaterThan("createdAt", currentUser.getDate("lastRequestVisitTime"));
//		query.include("fromUser");
//		query.include("group");
		query.findInBackground(new FindCallback<RequestModel>() {
			
			@Override
			public void done(List<RequestModel> list, ParseException err) {
				// TODO Auto-generated method stub				
				if(list != null && list.size()>0){
					AppManager.mInvitationCount = 0;
					for(RequestModel model : list){
						if(model.getType() != null && model.getType().equals(Constants.REQUEST_TYPE_GROUP_INVITATION)){
							AppManager.mInvitationCount++;
						}
					}
					AppManager.mRequestNotificationCount = list.size();
					mNotificationCountTextView.setText(String.valueOf(list.size()));
					mNotificationCountTextView.setVisibility(View.VISIBLE);
					
				}else{
					mNotificationCountTextView.setVisibility(View.GONE);
				}
				
				mInvitationTextView.setText("You have " + String.valueOf(AppManager.mInvitationCount) + " invites.");
				mIsRequestsLoading = false;
			}
		});
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_PENDING, pendingNotification);
		NotificationService.getInstance().removeObserver(Constants.LOCAL_NOTIFICATION_TYPE_FEED, feedNotification);
	}	
}
