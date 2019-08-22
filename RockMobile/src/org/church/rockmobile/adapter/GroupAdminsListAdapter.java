package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.UserModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
 
public class GroupAdminsListAdapter extends BaseAdapter {
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<UserModel> mGroupAdminsList;

    public GroupAdminsListAdapter(Context context,
            List<UserModel> groupAdminsList) {
        this.mContext = context;
        this.mGroupAdminsList = groupAdminsList;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	TextView nameTextView;
    	ImageView imageView;
    }
 
    @Override
    public int getCount() {
        return mGroupAdminsList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mGroupAdminsList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_group_admin, null);
            holder.nameTextView = (TextView) view.findViewById(R.id.textView_name);
            holder.imageView = (ImageView) view.findViewById(R.id.imageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ParseUser parseUser = mGroupAdminsList.get(position);
        parseUser.fetchIfNeededInBackground(new GetCallback<UserModel>() {

			@Override
			public void done(UserModel user, ParseException err) {
				// TODO Auto-generated method stub
				holder.nameTextView.setText(UtilityMethods.toCapitalizedString(user.getRealUsername()));
		        if(user.getParseFile("avatar") != null){
			        AppManager.getInstance().mImageLoader.displayImage(user.getParseFile("avatar").getUrl(),
			        		holder.imageView, AppManager.getInstance().options, null);
		        }else{
		        	holder.imageView.setImageResource(R.drawable.icon_default_user);
		        }
			}
		});
       
        return view;
    }
}