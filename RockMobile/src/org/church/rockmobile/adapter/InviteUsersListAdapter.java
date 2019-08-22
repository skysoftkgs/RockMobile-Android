package org.church.rockmobile.adapter;

import java.util.ArrayList;
import java.util.List;

import org.church.rockmobile.InviteUsersActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.InviteUserModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;
import org.church.rockmobile.service.PushNotificationService.ParseFunctionCallback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
 
public class InviteUsersListAdapter extends BaseAdapter implements Filterable{
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<InviteUserModel> mUserList;
    private List<InviteUserModel> mOriginalValues;
    
    public InviteUsersListAdapter(Context context,
            List<InviteUserModel> userList) {
        this.mContext = context;
        this.mUserList = userList;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	TextView nameTextView;
    	ImageView photoImageView;
    	ImageButton actionImageButton;
    }
 
    @Override
    public int getCount() {
        return mUserList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_invite_users, null);
            holder.nameTextView = (TextView) view.findViewById(R.id.textView_name);
            holder.photoImageView = (ImageView) view.findViewById(R.id.imageView_photo);
            holder.actionImageButton = (ImageButton) view.findViewById(R.id.imageButton_action);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final InviteUserModel userModel = mUserList.get(position);
        final UserModel user = userModel.getUser();
        holder.nameTextView.setText(user.getRealUsername());
                
        if(user.getParseFile("avatar") != null){
	        AppManager.getInstance().mImageLoader.displayImage(user.getParseFile("avatar").getUrl(),
	        		holder.photoImageView, AppManager.getInstance().options, null);
        }else{
        	holder.photoImageView.setImageResource(R.drawable.icon_default_user);
        }
        
        if(userModel.getInvitationStatus() == Constants.INVITATION_STATUS_NO){		//No invitation pending
        	holder.actionImageButton.setImageResource(R.drawable.btn_circle_plus_selector);
        	
        }else{		//Invitation pending
        	holder.actionImageButton.setImageResource(R.drawable.btn_event_remove_selector);
        }
            	
        holder.actionImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(userModel.getInvitationStatus() == Constants.INVITATION_STATUS_NO){		//No invitation pending
					if(InviteUsersActivity.mGroup.getIsPublic()){
		        		sendInvitation(userModel, holder.actionImageButton);
		        	}else if(UtilityMethods.containsParseUser(InviteUsersActivity.mGroup.getAdminUserList(), ParseUser.getCurrentUser())){
		        		sendInvitation(userModel, holder.actionImageButton);
		        	}else{
		        		requestJoinToGroup(userModel, holder.actionImageButton);
		        	}
		        	
		        }else{		//Invitation pending
		        	cancelInvitation(userModel, holder.actionImageButton);
		        }
			}
		});
        
        return view;
    }
    
    public void sendInvitation(final InviteUserModel userModel, final ImageButton imageButton){
    	RockMobileApplication.getInstance().showProgressFullScreenDialog((Activity)mContext);
    	
    	final UserModel user = userModel.getUser();
		PushNotificationService.getInstance().inviteToGroup(InviteUsersActivity.mGroup, user, new ParseFunctionCallback() {
			
			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				
				if(err == null){
					RockMobileApplication.getInstance().showToast(mContext, "Invitation request is in pending.", Toast.LENGTH_SHORT);
					imageButton.setImageResource(R.drawable.btn_event_remove_selector);
					userModel.setInvitationStatus(Constants.INVITATION_STATUS_OWNER_APPROVAL);
					
				}else{
					RockMobileApplication.getInstance().showToast(mContext,
							mContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}
    
    public void requestJoinToGroup(final InviteUserModel userModel, final ImageButton imageButton){
    	final UserModel user = userModel.getUser();
    	
    	RockMobileApplication.getInstance().showProgressFullScreenDialog((Activity)mContext);
    	PushNotificationService.getInstance().requestJoinGroup(InviteUsersActivity.mGroup, user, new  ParseFunctionCallback() {
			
			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(err == null){
					RockMobileApplication.getInstance().showToast(mContext, "Invitation request is sent to Admin users.", Toast.LENGTH_SHORT);
					imageButton.setImageResource(R.drawable.btn_event_remove_selector);
					userModel.setInvitationStatus(Constants.INVITATION_STATUS_ADMIN_APPROVAL);
					
				}else{
					RockMobileApplication.getInstance().showToast(mContext,
							mContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}
    
    public void cancelInvitation(final InviteUserModel userModel, final ImageButton imageButton){
    	final GroupModel group = InviteUsersActivity.mGroup;
    	final UserModel user = (UserModel)userModel.getUser();
    			
    	RockMobileApplication.getInstance().showProgressFullScreenDialog((Activity)mContext);
				
		//send push notification
		if(userModel.getInvitationStatus() == Constants.INVITATION_STATUS_OWNER_APPROVAL){
			PushNotificationService.getInstance().cancelInviteUserToGroup(group, user, new ParseFunctionCallback() {
				
				@Override
				public void done(String result, ParseException err) {
					RockMobileApplication.getInstance().hideProgressDialog();
					
					// TODO Auto-generated method stub
					if(err == null){
						RockMobileApplication.getInstance().showToast(mContext, "Group join request was cancelled.", Toast.LENGTH_SHORT);
						imageButton.setImageResource(R.drawable.btn_circle_plus_selector);
						userModel.setInvitationStatus(Constants.INVITATION_STATUS_NO);
						
					}else{
						RockMobileApplication.getInstance().showToast(mContext,
								mContext.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
					}
				}
			});
		}
    }
        
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

            	mUserList = (List<InviteUserModel>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @SuppressLint("DefaultLocale")
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<InviteUserModel> FilteredArrList = new ArrayList<InviteUserModel>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<InviteUserModel>(mUserList); // saves the original data in mOriginalValues
                }

                /********
                 * 
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)  
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return  
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                	constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                    	InviteUserModel userModel = mOriginalValues.get(i);
                    	UserModel record = userModel.getUser();
                    	String name = record.getRealUsername();
                        if (name.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(userModel);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }
}