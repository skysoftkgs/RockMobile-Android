package org.church.rockmobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class GroupSettingsActivity extends Activity implements OnClickListener{

	public final String TAG = "GroupSettingsActivity";
	public static final int REQUEST_EDIT_NEW_GROUP = 2005;
	ImageButton mBackImageButton;
		
	public static GroupModel mGroup;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_group_setting);
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Group Setting", params); 
	  		
	    initUI();
	}
	 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()){
			case R.id.layout_manage_members:
				intent = new Intent(this, GroupManageMembersActivity.class);
				GroupManageMembersActivity.mGroup = mGroup;
				startActivity(intent);
				break;
				
			case R.id.layout_invite_users:
				intent = new Intent(this, InviteUsersActivity.class);
				InviteUsersActivity.mGroup = mGroup;
				startActivity(intent);
				break;
				
			case R.id.layout_edit_group:
				intent = new Intent(this, NewGroupActivity.class);
				NewGroupActivity.mGroup = mGroup;
				startActivityForResult(intent, REQUEST_EDIT_NEW_GROUP);
				break;
				
			case R.id.layout_delete_group:
				deleteGroup();
				break;
				
			case R.id.layout_clear_chat_history:
				clearChatHistory();
				break;
	
			case R.id.imageButton_back:
				super.onBackPressed();
				break;
		}
	}
	
	public void initUI(){
		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);
		
		RelativeLayout managerMembersLayout = (RelativeLayout) findViewById(R.id.layout_manage_members);
		managerMembersLayout.setOnClickListener(this);
		
		RelativeLayout inviteUsersLayout = (RelativeLayout) findViewById(R.id.layout_invite_users);
		inviteUsersLayout.setOnClickListener(this);
		
		RelativeLayout editGroupLayout = (RelativeLayout) findViewById(R.id.layout_edit_group);
		editGroupLayout.setOnClickListener(this);
		
		RelativeLayout deleteGroupLayout = (RelativeLayout) findViewById(R.id.layout_delete_group);
		deleteGroupLayout.setOnClickListener(this);
		
		RelativeLayout clearChatHistoryLayout = (RelativeLayout) findViewById(R.id.layout_clear_chat_history);
		clearChatHistoryLayout.setOnClickListener(this);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(data != null && resultCode == Activity.RESULT_OK){
			if(requestCode == REQUEST_EDIT_NEW_GROUP){
				mGroup = NewGroupActivity.mGroup;
			}
		}
	}
	
	public void deleteGroup(){
		if(UtilityMethods.containsParseUser(mGroup.getAdminUserList(), ParseUser.getCurrentUser()) &&
				mGroup.getAdminUserList() != null &&
				mGroup.getAdminUserList().size() <= 1){
			RockMobileApplication.getInstance().showToast(this, R.string.can_not_remove_admin, Toast.LENGTH_SHORT);
			return;
		}
		
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		mGroup.removeUserFromAdminUserList((UserModel)ParseUser.getCurrentUser());
		mGroup.removeUserFromJoinedUserList((UserModel)ParseUser.getCurrentUser());
		mGroup.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException err) {
				// TODO Auto-generated method stub
				if(err == null){
					RockMobileApplication.getInstance().showToast(GroupSettingsActivity.this, R.string.deleted_successfully, Toast.LENGTH_SHORT);
//					Intent intent = new Intent(GroupSettingsActivity.this, MainActivity.class);
//					intent.putExtra(
//			        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
//					startActivity(intent);
				}
				
				RockMobileApplication.getInstance().hideProgressDialog();
			}
		});
	}
	
	public void clearChatHistory(){
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupSettingsActivity.this);
		alertDialog.setTitle(getResources().getString(R.string.comfirm));
		alertDialog.setMessage(getResources().getString(R.string.are_you_sure_clear_chat_history));

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("YES",
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            	RockMobileApplication.getInstance().showProgressFullScreenDialog(GroupSettingsActivity.this);
		            	ParseQuery<ParseObject> query0 = new ParseQuery<ParseObject>("Feed");
		            	query0.whereEqualTo("type", Constants.REQUEST_TYPE_THREAD_ADD);
		            	query0.whereEqualTo("group", mGroup);
		            	query0.findInBackground(new FindCallback<ParseObject>() {

							@Override
							public void done(List<ParseObject> list0,
									ParseException arg1) {
								// TODO Auto-generated method stub
								if(list0 != null){
									ParseObject.deleteAllInBackground(list0, new DeleteCallback() {
										
										@Override
										public void done(ParseException arg0) {
											// TODO Auto-generated method stub
											ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Thread");
							        		query.whereEqualTo("churchId", Constants.CHURCH_ID);
							        		query.whereEqualTo("group", mGroup);
							        		query.findInBackground(new FindCallback<ParseObject>() {
												
												@Override
												public void done(List<ParseObject> list, ParseException err) {
													// TODO Auto-generated method stub
													
													if(list != null){
														ParseObject.deleteAllInBackground(list, new DeleteCallback() {

															@Override
															public void done(
																	ParseException arg0) {
																// TODO Auto-generated method stub
																RockMobileApplication.getInstance().hideProgressDialog();
																GroupDetailActivity.mThreadsList.clear();
															}});
														
													}else{
														RockMobileApplication.getInstance().hideProgressDialog();
													}
												}
											});
										}
									});
									
								}else{
									RockMobileApplication.getInstance().hideProgressDialog();
								}
							}
						});
		            }
		        });
		// Setting Negative "NO" Btn
		alertDialog.setNegativeButton("NO",
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		              				                
		                dialog.cancel();
		            }
		        });

		// Showing Alert Dialog
		alertDialog.show();
	}
}
