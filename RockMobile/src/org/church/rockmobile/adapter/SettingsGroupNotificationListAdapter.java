package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;
 
public class SettingsGroupNotificationListAdapter extends BaseAdapter {
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    List<GroupModel> mAllGroupList;
    List<String> mNotificationGroupList;
    UserModel mCurrentUser;
    
    public SettingsGroupNotificationListAdapter(Context context, List<GroupModel> groupList) {
        this.mContext = context;
        this.mAllGroupList = groupList;
        mInflater = LayoutInflater.from(mContext);
        mCurrentUser = (UserModel) UserModel.getCurrentUser();
        mNotificationGroupList = mCurrentUser.getNotificationOffGroupList();
    }
 
    public class ViewHolder {
    	TextView groupNameTextView;
    	Switch groupSwitch;
    }
 
    @Override
    public int getCount() {
        return mAllGroupList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mAllGroupList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }

	@SuppressLint("SimpleDateFormat")
	public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_settings_group_notifications, null);
            holder.groupNameTextView = (TextView) view.findViewById(R.id.textView_groupName);
            holder.groupSwitch = (Switch)view.findViewById(R.id.switch_group);
            view.setTag(holder);
            
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final GroupModel group = (GroupModel) getItem(position);
        holder.groupNameTextView.setText(group.getTitle());
               
        if(mNotificationGroupList != null && mNotificationGroupList.contains(group.getObjectId())){
        	holder.groupSwitch.setChecked(false);
        	
        }else{
        	holder.groupSwitch.setChecked(true);
        }
        
        holder.groupSwitch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(holder.groupSwitch.isChecked()){
					mCurrentUser.removeNotificationOffGroup(group.getObjectId());
					
				}else{
					mCurrentUser.addNotificationOffGroup(group.getObjectId());
				}
				
				mCurrentUser.saveInBackground();
				
				PushNotificationService.getInstance().sendGroupRefresh(group, null);
			}
		});
               
        return view;
    }
}