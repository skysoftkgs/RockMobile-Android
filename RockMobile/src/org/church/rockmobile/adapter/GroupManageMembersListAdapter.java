package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.GroupManageMembersActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.AdminRemoveRequestModel;
import org.church.rockmobile.model.GroupManageMembersModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.widget.RoundedImageView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class GroupManageMembersListAdapter extends BaseExpandableListAdapter {

	private GroupManageMembersActivity mActivity;
	private List<GroupManageMembersModel> mUserList;

	public GroupManageMembersListAdapter(GroupManageMembersActivity activity, List<GroupManageMembersModel> userList) {
		this.mActivity = activity;
		this.mUserList = userList;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition,  int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
		LayoutInflater infalInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.item_manage_members_child, null);
		Button removeUserButton = (Button) convertView.findViewById(R.id.button_remove_user);
		ImageView removeUserImageView = (ImageView) convertView.findViewById(R.id.imageView_remove_user);
		
		Button adminButton = (Button) convertView.findViewById(R.id.button_admin);
		ImageButton adminStatusImageButton = (ImageButton) convertView.findViewById(R.id.imageButton_adminStatus);
		
		final GroupManageMembersModel model = mUserList.get(groupPosition);

		if(model.getIsAdmin() == true){
			adminButton.setText(mActivity.getResources().getString(R.string.remove_admin));
			adminStatusImageButton.setImageResource(R.drawable.btn_make_admin);
			removeUserButton.setVisibility(View.INVISIBLE);
			removeUserImageView.setVisibility(View.INVISIBLE);
			
		}else{
			adminButton.setText(mActivity.getResources().getString(R.string.make_admin));
			adminStatusImageButton.setImageResource(R.drawable.btn_make_admin);
			removeUserButton.setVisibility(View.VISIBLE);
			removeUserImageView.setVisibility(View.VISIBLE);
		}
		
		adminButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				if(model.getIsAdmin() == true){
					removeAdminUser(model);
				}else{
					addAdminUser(model);
				}
			}
		});
		
		removeUserButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				removeUser(model);
			}
		});
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mUserList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mUserList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
		GroupManageMembersModel model = (GroupManageMembersModel) getGroup(groupPosition);
		LayoutInflater infalInflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = infalInflater.inflate(R.layout.item_manage_members_group, null);
		
		UserModel user = model.getUser();
		TextView nameTextView = (TextView) convertView.findViewById(R.id.textView_name);
		TextView adminTextView = (TextView) convertView.findViewById(R.id.textView_admin);
		RoundedImageView avatarImageView = (RoundedImageView) convertView.findViewById(R.id.imageView_photo);
		
		nameTextView.setText(user.getRealUsername());
		if(model.getIsAdmin() == true){
			adminTextView.setText("Admin");
		}else{
			adminTextView.setText("");
		}
		
		if(user.getParseFile("avatar") != null){
	        AppManager.getInstance().mImageLoader.displayImage(user.getParseFile("avatar").getUrl(),
	        		avatarImageView, AppManager.getInstance().options, null);
        }else{
        	avatarImageView.setImageResource(R.drawable.icon_default_user);
        }
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	public void removeAdminUser(final GroupManageMembersModel model){
		if(GroupManageMembersActivity.mGroup.getAdminUserList() != null &&
				GroupManageMembersActivity.mGroup.getAdminUserList().size() <= 1){
			RockMobileApplication.getInstance().showToast(mActivity, R.string.can_not_remove_admin, Toast.LENGTH_SHORT);
			return;
		}
		
		AdminRemoveRequestModel request = new AdminRemoveRequestModel();
		request.setChurchId(Constants.CHURCH_ID);
		request.setFromUser(ParseUser.getCurrentUser());
		request.setToUser(model.getUser());
		request.setGroup(GroupManageMembersActivity.mGroup);
		request.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException arg0) {
				// TODO Auto-generated method stub
				model.setIsAdmin(false);
				GroupManageMembersListAdapter.this.notifyDataSetChanged();
				
				RockMobileApplication.getInstance().showToast(mActivity, "Remove Request is Pending", Toast.LENGTH_SHORT);
			}
		});
		
//		GroupManageMembersActivity.mGroup.addUserToJoinedUserList(model.getUser());
//		GroupManageMembersActivity.mGroup.removeUserFromAdminUserList(model.getUser());
//		GroupManageMembersActivity.mGroup.saveInBackground(new SaveCallback(){
//
//			@Override
//			public void done(ParseException err) {
//				// TODO Auto-generated method stub
//				model.setIsAdmin(false);
//				GroupManageMembersListAdapter.this.notifyDataSetChanged();
//			}
//			
//		});
	}
	
	public void addAdminUser(final GroupManageMembersModel model){
		GroupManageMembersActivity.mGroup.addUserToAdminUserList(model.getUser());
		GroupManageMembersActivity.mGroup.removeUserFromJoinedUserList(model.getUser());
		GroupManageMembersActivity.mGroup.saveInBackground(new SaveCallback(){

			@Override
			public void done(ParseException err) {
				// TODO Auto-generated method stub
				model.setIsAdmin(true);
				GroupManageMembersListAdapter.this.notifyDataSetChanged();
			}
			
		});
	}
	
	public void removeUser(final GroupManageMembersModel model){
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);
		alertDialog.setTitle(mActivity.getResources().getString(R.string.comfirm));
		alertDialog.setMessage(mActivity.getResources().getString(R.string.are_you_sure_delete_user));

		// Setting Positive "Yes" Btn
		alertDialog.setPositiveButton("YES",
		        new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	
		            	RockMobileApplication.getInstance().showProgressFullScreenDialog(mActivity);
		            	GroupManageMembersActivity.mGroup.removeUserFromJoinedUserList(model.getUser());
		            	GroupManageMembersActivity.mGroup.saveInBackground(new SaveCallback() {
							
							@Override
							public void done(ParseException err) {
								// TODO Auto-generated method stub
								if(err == null){
									mUserList.remove(model);
									notifyDataSetChanged();
								}
								
								RockMobileApplication.getInstance().hideProgressDialog();
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
