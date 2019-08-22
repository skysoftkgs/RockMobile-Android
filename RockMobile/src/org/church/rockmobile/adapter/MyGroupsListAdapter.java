package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.GroupAboutActivity;
import org.church.rockmobile.GroupDetailActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.GroupNotificationModel;
import org.church.rockmobile.model.UserModel;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.parse.ParseUser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class MyGroupsListAdapter extends BaseAdapter {
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<GroupModel> mMyGroupsList;
    private List<GroupNotificationModel> mGroupNotificationList;
    
    public MyGroupsListAdapter(Context context,
            List<GroupModel> myGroupsList, List<GroupNotificationModel> groupNotificationList) {
        this.mContext = context;
        this.mMyGroupsList = myGroupsList;
        this.mGroupNotificationList = groupNotificationList;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	TextView titleTextView;
    	TextView categoryTextView;
    	TextView notificationTextView;
    	ImageView imageView;
    }
 
    @Override
    public int getCount() {
        return mMyGroupsList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mMyGroupsList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_my_groups, null);
            holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
			holder.categoryTextView = (TextView) view.findViewById(R.id.textView_category);
			holder.notificationTextView = (TextView) view.findViewById(R.id.textView_notification);
			holder.imageView = (ImageView) view.findViewById(R.id.imageView);

			ViewGroup.LayoutParams imageViewParams = holder.imageView.getLayoutParams();
  	        imageViewParams.width = AppManager.getInstance().mScreenWidth / 2;
  	        imageViewParams.height = AppManager.getInstance().mScreenWidth / 2;
  	        holder.imageView.setLayoutParams(imageViewParams);
  	        
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final GroupModel group = mMyGroupsList.get(position);
		holder.titleTextView.setText(group.getTitle());
        holder.categoryTextView.setText(group.getCategory());
        
        if(mGroupNotificationList != null){
	        GroupNotificationModel notificationModel = AppManager.getInstance().getGroupNotification(group.getObjectId());
	        if(notificationModel != null && notificationModel.getNotificationCount() > 0){
	        	holder.notificationTextView.setText(String.valueOf(notificationModel.getNotificationCount()));
	        	holder.notificationTextView.setVisibility(View.VISIBLE);
	        	
	        }else{
	        	holder.notificationTextView.setVisibility(View.INVISIBLE);
	        }
        }else{
        	holder.notificationTextView.setVisibility(View.INVISIBLE);
        }
        
        if(group.getPhotoFile() != null){        
	        // display post image
	        AppManager.getInstance().mImageLoader.displayImage(group.getPhotoFile().getUrl(),
	        		holder.imageView, AppManager.getInstance().options, new SimpleImageLoadingListener() {
				 @Override
				 public void onLoadingStarted(String imageUri, View view) {
	//				 holder.progressBar.setProgress(0);
	//				 holder.progressBar.setVisibility(View.VISIBLE);
				 }
	
				 @Override
				 public void onLoadingFailed(String imageUri, View view,
						 FailReason failReason) {
	//				 holder.progressBar.setVisibility(View.GONE);
				 }
	
				 @Override
				 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
	//				 holder.progressBar.setVisibility(View.GONE);
				 }
			 }, new ImageLoadingProgressListener() {
				 @Override
				 public void onProgressUpdate(String imageUri, View view, int current,
						 int total) {
	//				 holder.progressBar.setProgress(Math.round(100.0f * current / total));
				 }
			});
        }else{
        	holder.imageView.setImageBitmap(null);
        }
        
        view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				List<UserModel> adminUsersList = group.getAdminUserList();
		        List<UserModel> joinedUsersList = group.getJoinedUserList();
			        
				if(joinedUsersList != null && UtilityMethods.containsParseUser(joinedUsersList, ParseUser.getCurrentUser()) ||
		        		adminUsersList != null && UtilityMethods.containsParseUser(adminUsersList, ParseUser.getCurrentUser())){		//group joined
					Intent intent = new Intent(mContext, GroupDetailActivity.class);
		        	GroupDetailActivity.mGroup = group;
		        	
		        	//set notification count to 0
		        	holder.notificationTextView.setVisibility(View.INVISIBLE);
		        	GroupNotificationModel notificationModel = AppManager.getInstance().getGroupNotification(group.getObjectId());
		        	if(notificationModel != null){
		        		notificationModel.setNotificationCount(0);
		        		notificationModel.saveEventually();
		        	}
		        	
		        	mContext.startActivity(intent);
	   
		        }else{		//no group member
		        	Intent intent = new Intent(mContext, GroupAboutActivity.class);
		        	GroupAboutActivity.mGroup = group;
		        	mContext.startActivity(intent);
		        }
			}
		});
        
        return view;
    }
}