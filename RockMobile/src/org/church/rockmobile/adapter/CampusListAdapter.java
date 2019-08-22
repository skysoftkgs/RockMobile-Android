package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.CampusFragment;
import org.church.rockmobile.CategoryGroupsActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.CampusModel;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;
import org.church.rockmobile.service.PushNotificationService.ParseFunctionCallback;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
 
public class CampusListAdapter extends BaseAdapter {
 
    // Declare Variables
    CampusFragment mFragment;
    LayoutInflater mInflater;
    private List<CampusModel> mCampuseList;

    public CampusListAdapter(CampusFragment fragment,
            List<CampusModel> campuseList) {
        this.mFragment = fragment;
        this.mCampuseList = campuseList;
        mInflater = LayoutInflater.from(mFragment.getActivity());
    }
 
    public class ViewHolder {
    	TextView nameTextView;
    	TextView addressTextView;
    	TextView serviceTimesTextView;
    	ImageButton phoneImageButton;
    	ImageButton emailImageButton;
    	ImageButton earthImageButton;
    	ImageButton groupJoinImageButton;
    	ImageView mapImageView;
    }
 
    @Override
    public int getCount() {
        return mCampuseList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mCampuseList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }

	public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_campuses, null);
            holder.nameTextView = (TextView) view.findViewById(R.id.textView_name);
            holder.serviceTimesTextView = (TextView) view.findViewById(R.id.textView_serviceTimes);
            holder.addressTextView = (TextView) view.findViewById(R.id.textView_address);
            holder.phoneImageButton = (ImageButton) view.findViewById(R.id.imageButton_phone);
            holder.emailImageButton = (ImageButton) view.findViewById(R.id.imageButton_email);
            holder.earthImageButton = (ImageButton) view.findViewById(R.id.imageButton_earth);
            holder.groupJoinImageButton = (ImageButton) view.findViewById(R.id.imageButton_addMember);
            holder.mapImageView = (ImageView) view.findViewById(R.id.imageView_map);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final CampusModel campus = mCampuseList.get(position);
        ParseGeoPoint location = campus.getGeoLocation();
        if(location != null){
	        String getMapURL = "http://maps.googleapis.com/maps/api/staticmap?zoom=15&size=600x300&&maptype=roadmap&markers=size:mid|color:red|"  
	        		+ String.valueOf(location.getLatitude())
	        		+ "," 
	        		+ String.valueOf(location.getLongitude())
	        		+ "&sensor=false";
	        if(AppManager.getInstance().mImageLoader != null)
	        	AppManager.getInstance().mImageLoader.displayImage(getMapURL,
	        		holder.mapImageView, AppManager.getInstance().options, null, null);
        }
        
        holder.nameTextView.setText(campus.getName());
        holder.addressTextView.setText(campus.getAddress());
        String times = "";
        List<String> timesList = campus.getServiceTimes();
        if(timesList != null){
        	for(int i= 0;i<timesList.size()-1;i++)
        		times += timesList.get(i) + ", ";
        	
        	if(timesList.size()>0)
        		times += timesList.get(timesList.size()-1);
        }
        holder.serviceTimesTextView.setText(times);
        
        if (!UtilityMethods.isValidPhoneNumber(campus.getPhoneNumber())){
        	holder.phoneImageButton.setEnabled(false);
        	holder.phoneImageButton.setAlpha(0.7f);
        }
        
        holder.phoneImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(campus.getPhoneNumber() != null){
					Intent intent = new Intent(Intent.ACTION_DIAL);
					intent.setData(Uri.parse("tel:" + campus.getPhoneNumber()));
					mFragment.startActivity(intent);
				}
			}
		});
        
        holder.emailImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(campus.getEmail() != null){
					Intent emailIntent = new Intent(Intent.ACTION_SEND);
					emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{campus.getEmail()});
					emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
					emailIntent.putExtra(Intent.EXTRA_TEXT, "");
					emailIntent.setType("message/rfc822");
					mFragment.startActivity(Intent.createChooser(emailIntent, "Choose an Email client"));
				}
			}
		});

		holder.earthImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(campus.getWebsiteUrl() != null){
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(campus.getWebsiteUrl()));
					mFragment.startActivity(intent);
				}
			}
		});

		final GroupModel group = campus.getGroup();
		final ParseUser currentUser = ParseUser.getCurrentUser();
		if(group == null){
			holder.groupJoinImageButton.setImageBitmap(null);
		
		}else if(!UtilityMethods.containsParseUser(group.getAdminUserList(), currentUser) &&
			!UtilityMethods.containsParseUser(group.getJoinedUserList(), currentUser) &&
			!UtilityMethods.containsParseUser(group.getPendingUserList(), currentUser)){
			holder.groupJoinImageButton.setImageResource(R.drawable.btn_add_member_selector);
		
		}else if(UtilityMethods.containsParseUser(group.getPendingUserList(), currentUser)){
			holder.groupJoinImageButton.setImageResource(R.drawable.btn_event_remove_selector);
		
		}else{
			holder.groupJoinImageButton.setImageBitmap(null);
		}
		
		holder.groupJoinImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!UtilityMethods.containsParseUser(group.getAdminUserList(), currentUser) &&
					!UtilityMethods.containsParseUser(group.getJoinedUserList(), currentUser) &&
					!UtilityMethods.containsParseUser(group.getPendingUserList(), currentUser)){
					if(group.getIsPublic() == false)
						requestJoinToGroup(group, holder.groupJoinImageButton);
					else
						joinToGroup(group, holder.groupJoinImageButton);
					
				}else if(UtilityMethods.containsParseUser(group.getPendingUserList(), currentUser)){
					cancelJoinToGroup(group, holder.groupJoinImageButton);
				}
				
			}
		});
        return view;
    }
	
	public void joinToGroup(final GroupModel group, final ImageButton joinButton){
		RockMobileApplication.getInstance().showProgressFullScreenDialog(mFragment.getActivity());
		final UserModel currentUser = (UserModel)ParseUser.getCurrentUser();
		PushNotificationService.getInstance().joinGroup(group, currentUser, new  ParseFunctionCallback() {
			
			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(err == null){
					RockMobileApplication.getInstance().showToast(mFragment.getActivity(), "Added to group.", Toast.LENGTH_SHORT);
					joinButton.setImageBitmap(null);
					CategoryGroupsActivity.mIsDataChanged = true;
					AppManager.mMyGroupsList.add(0, group);
					AppManager.mAllGroupsList.remove(group);
					
				}else{
					RockMobileApplication.getInstance().showToast(mFragment.getActivity(),
							mFragment.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}
	
	public void requestJoinToGroup(final GroupModel group, final ImageButton joinButton){
		RockMobileApplication.getInstance().showProgressFullScreenDialog(mFragment.getActivity());
		final UserModel currentUser = (UserModel)ParseUser.getCurrentUser();
		PushNotificationService.getInstance().requestJoinGroup(group, currentUser, new  ParseFunctionCallback() {
			
			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(err == null){
					RockMobileApplication.getInstance().showToast(mFragment.getActivity(), R.string.group_request_pending, Toast.LENGTH_SHORT);
					joinButton.setImageResource(R.drawable.btn_event_remove_selector);
					CategoryGroupsActivity.mIsDataChanged = true;
					
				}else{
					RockMobileApplication.getInstance().showToast(mFragment.getActivity(),
							mFragment.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}
	
	public void cancelJoinToGroup(final GroupModel group, final ImageButton joinButton){
				
		final UserModel currentUser = (UserModel)ParseUser.getCurrentUser();
		if(!group.removeUserFromPendingUserList(currentUser)) return;
		
		RockMobileApplication.getInstance().showProgressFullScreenDialog(mFragment.getActivity());
		PushNotificationService.getInstance().cancelJoinGroup(group, new  ParseFunctionCallback() {
			
			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();
				if(err == null){
					RockMobileApplication.getInstance().showToast(mFragment.getActivity(), R.string.group_request_canceled, Toast.LENGTH_SHORT);
					joinButton.setImageResource(R.drawable.btn_add_member_selector);
					CategoryGroupsActivity.mIsDataChanged = true;
					
				}else{
					RockMobileApplication.getInstance().showToast(mFragment.getActivity(),
							mFragment.getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});
	}
}