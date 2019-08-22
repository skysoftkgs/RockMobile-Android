package org.church.rockmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.InviteUsersListAdapter;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.InviteUserModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class InviteUsersActivity extends Activity implements OnClickListener{

	public final String TAG = "InviteUsersActivity";
	
	ImageButton mBackImageButton;
	ImageButton mSearchImageButton;
	PullToRefreshGridView mPullRefreshGridView;
	GridView mGroupUsersGridView;
	RelativeLayout mTopBarLayout;
	LinearLayout mSearchBarLayout;
	
	public static GroupModel mGroup;
	public InviteUsersListAdapter mAdapter;
	List<UserModel> mGroupUsers = new ArrayList<UserModel>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_group_invite_users);
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Invite Users", params); 
	  		
	    initUI();
	}
	 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){	
			case R.id.imageButton_back:
				super.onBackPressed();
				break;
				
			case R.id.imageButton_search:
				 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
				 {
					 mTopBarLayout.animate().x(-mTopBarLayout.getWidth());
					 mSearchBarLayout.setVisibility(View.VISIBLE);
				 
				 }else{
                	 mTopBarLayout.setVisibility(View.GONE);
				 }
//					TranslateAnimation animation = new TranslateAnimation(0, -mTopBarLayout.getWidth(), 0, 0);
//					animation.setDuration(1000);
//					mTopBarLayout.startAnimation(animation);
//					mTopBarLayout.setVisibility(View.GONE);
				break;
				
			case R.id.button_done:
				 if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1){
                    mTopBarLayout.animate().x(0);
                    UtilityMethods.hideKeyboard(this);
                    mSearchBarLayout.setVisibility(View.GONE);
				 
				 }else{
                	mTopBarLayout.setVisibility(View.VISIBLE);
				 }
				break;
		}
	}
	
	public void initUI(){		
		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);
		
		mSearchImageButton = (ImageButton) findViewById(R.id.imageButton_search);
		mSearchImageButton.setOnClickListener(this);
		
		Button doneButton = (Button) findViewById(R.id.button_done);
		doneButton.setOnClickListener(this);
		
		EditText searchEditText = (EditText) findViewById(R.id.editText_search);
		searchEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(mAdapter != null)
					mAdapter.getFilter().filter(s.toString());
			}
		});
		
		mPullRefreshGridView = (PullToRefreshGridView) findViewById(R.id.gridView_inviteUsers);
		mGroupUsersGridView = mPullRefreshGridView.getRefreshableView();
		mPullRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

 			@Override
 			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
 				loadGroupUsers();
 			}

 			@Override
 			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
 				mPullRefreshGridView.onRefreshComplete();
 			}

        });
		
		mTopBarLayout = (RelativeLayout) findViewById(R.id.layout_topbar);
		mSearchBarLayout = (LinearLayout) findViewById(R.id.layout_searchbar);
		mSearchBarLayout.setVisibility(View.GONE);
		loadGroupUsers();
	}
	
	public void loadGroupUsers(){
		if(!mPullRefreshGridView.isRefreshing())
			RockMobileApplication.getInstance().showProgressFullScreenDialog(InviteUsersActivity.this);
		
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereNotEqualTo("isSuperAdmin", true);
		query.setLimit(Constants.PARSE_QUERY_MAX_LIMIT_COUNT);
		query.orderByAscending("firstName");
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> list, ParseException err) {
				// TODO Auto-generated method stub
				if(list != null){
					mGroupUsers = new ArrayList<UserModel>();
					for(int i= 0;i<list.size();i++){
						UserModel user = (UserModel) list.get(i);
						if(mGroup.getAdminUserList() != null && UtilityMethods.containsParseUser(mGroup.getAdminUserList(), user) ||
								mGroup.getJoinedUserList() != null && UtilityMethods.containsParseUser(mGroup.getJoinedUserList(), user) ||
								user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
							continue;
						
						mGroupUsers.add(user);
					}
					
					ParseQuery<RequestModel> query = new ParseQuery<RequestModel>("Feed");
					query.whereEqualTo("churchId", Constants.CHURCH_ID);
					query.whereEqualTo("group", mGroup);
					query.whereEqualTo("type", Constants.REQUEST_TYPE_GROUP_INVITATION);
//					query.whereNotEqualTo("groupInviteManager", null);
					query.setLimit(Constants.PARSE_QUERY_MAX_LIMIT_COUNT);
					query.include("fromUser");
					query.include("toUsers");
					query.findInBackground(new FindCallback<RequestModel>() {

						@Override
						public void done(List<RequestModel> requestList,
								ParseException arg1) {
							// TODO Auto-generated method stub
							List<InviteUserModel> userList = new ArrayList<InviteUserModel>();
							if(requestList != null){
								for(int i=0;i<mGroupUsers.size();i++){
									InviteUserModel  inviteUserModel = new InviteUserModel();
									inviteUserModel.setUser(mGroupUsers.get(i));
									inviteUserModel.setInvitationStatus(Constants.INVITATION_STATUS_NO);
									for(int j=0;j<requestList.size();j++){
										RequestModel requestModel = requestList.get(j);
										if(requestModel.getGroupInviteManager() == null) continue;
										
										if(UtilityMethods.containsParseUser(requestModel.getToUserList(), mGroupUsers.get(i))){
											if(requestModel.getGroupInviteManager().equals(Constants.REQUEST_GROUP_INVITE_MANAGER_ADMIN)){
												inviteUserModel.setInvitationStatus(Constants.INVITATION_STATUS_ADMIN_APPROVAL);
												break;
											}else if(requestModel.getGroupInviteManager().equals(Constants.REQUEST_GROUP_INVITE_MANAGER_OWNER)){
												inviteUserModel.setInvitationStatus(Constants.INVITATION_STATUS_OWNER_APPROVAL);
												break;
											}
										}
									}
									userList.add(inviteUserModel);
								}
							}
							
							mPullRefreshGridView.onRefreshComplete();
							mAdapter = new InviteUsersListAdapter(InviteUsersActivity.this, userList);
							mGroupUsersGridView.setAdapter(mAdapter);
							
							RockMobileApplication.getInstance().hideProgressDialog();
						}
						
					});
					
				}else{
					RockMobileApplication.getInstance().hideProgressDialog();
				}
			}
		});
	}

}
