package org.church.rockmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.GroupManageMembersListAdapter;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupManageMembersModel;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.ImageButton;

import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class GroupManageMembersActivity extends Activity implements OnClickListener{

	public final String TAG = "InviteUsersActivity";
	
	ImageButton mBackImageButton;
	ExpandableListView mGroupUsersListView;
	
	public static GroupModel mGroup;
	public GroupManageMembersListAdapter mAdapter;
	List<GroupManageMembersModel> mGroupUsers = new ArrayList<GroupManageMembersModel>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_group_manage_members);
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Group Manage Members", params); 
	  		
	    initUI();
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
		
		mGroupUsersListView = (ExpandableListView) findViewById(R.id.expandableListView_manageMembers);
		mGroupUsersListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			
			@Override
			public void onGroupExpand(int groupPosition) {
				// TODO Auto-generated method stub
				for(int i = 0;i < mAdapter.getGroupCount();i++){
					if(i != groupPosition)
						mGroupUsersListView.collapseGroup(i);
				}
			}
		});
		
		loadGroupUsers();
	}
	
	public void loadGroupUsers(){
			
		RockMobileApplication.getInstance().showProgressFullScreenDialog(GroupManageMembersActivity.this);
		
		ParseQuery<ParseUser> query = UserModel.getQuery();
		query.setLimit(1000);
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereNotEqualTo("isSuperAdmin", true);
		query.orderByAscending("firstName");
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> arg0, ParseException arg1) {
				// TODO Auto-generated method stub
				if(arg0 != null){
					mGroupUsers = new ArrayList<GroupManageMembersModel>();
					for (ParseUser user: arg0){
						if(UtilityMethods.containsParseUser(mGroup.getJoinedUserList(), user)){
							GroupManageMembersModel model = new GroupManageMembersModel();
							model.setUser((UserModel) user);
							model.setIsAdmin(false);
							mGroupUsers.add(model);
							
						}else if(UtilityMethods.containsParseUser(mGroup.getAdminUserList(), user)){
							GroupManageMembersModel model = new GroupManageMembersModel();
							model.setUser((UserModel) user);
							model.setIsAdmin(true);
							mGroupUsers.add(model);
						}
					}
					
					mAdapter = new GroupManageMembersListAdapter(GroupManageMembersActivity.this, mGroupUsers);
					mGroupUsersListView.setAdapter(mAdapter);
				}
				
				RockMobileApplication.getInstance().hideProgressDialog();
					
			}
		});	
	}

}
