package org.church.rockmobile.adapter;

import java.util.ArrayList;
import java.util.List;

import org.church.rockmobile.GroupAboutActivity;
import org.church.rockmobile.GroupDetailActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.parse.ParseUser;
 
public class GroupSearchListAdapter extends BaseAdapter implements Filterable{
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<GroupModel> mGroupList;
    private List<GroupModel> mOriginalValues;

    public GroupSearchListAdapter(Context context,
            List<GroupModel> groupList) {
        this.mContext = context;
        this.mGroupList = groupList;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	TextView titleTextView;
    	TextView detailTextView;
    }
 
    @Override
    public int getCount() {
        return mGroupList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mGroupList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_group_search, null);
            holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
            holder.detailTextView = (TextView) view.findViewById(R.id.textView_detail);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final GroupModel group = mGroupList.get(position);
        final List<UserModel> adminUsersList = group.getList("adminUsers");
        final List<UserModel> joinedUsersList = group.getList("joinedUsers");
        final UserModel currentUser = (UserModel) ParseUser.getCurrentUser();
        
        holder.titleTextView.setText(group.getString("title"));
        holder.detailTextView.setText(group.getString("detail"));
        
        view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(joinedUsersList != null && UtilityMethods.containsParseUser(joinedUsersList, currentUser) ||
		        		adminUsersList != null && UtilityMethods.containsParseUser(adminUsersList, currentUser)){		//group joined
					Intent intent = new Intent(mContext, GroupDetailActivity.class);
		        	GroupDetailActivity.mGroup = group;
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
    
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,FilterResults results) {

            	mGroupList = (List<GroupModel>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @SuppressLint("DefaultLocale")
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<GroupModel> FilteredArrList = new ArrayList<GroupModel>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<GroupModel>(mGroupList); // saves the original data in mOriginalValues
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
                     GroupModel record = mOriginalValues.get(i);
                     String name = record.getTitle();
                        if (name.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(record);
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