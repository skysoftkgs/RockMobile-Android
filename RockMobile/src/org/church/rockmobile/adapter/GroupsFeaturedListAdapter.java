package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.model.GroupModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class GroupsFeaturedListAdapter extends BaseAdapter {
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<GroupModel> mGroupsFeaturedList;

    public GroupsFeaturedListAdapter(Context context,
            List<GroupModel> groupsFeaturedList) {
        this.mContext = context;
        this.mGroupsFeaturedList = groupsFeaturedList;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	TextView titleTextView;
    	TextView categoryTextView;
    	ImageView imageView;
    }
 
    @Override
    public int getCount() {
        return mGroupsFeaturedList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mGroupsFeaturedList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_groups_featured, null);
            holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
			holder.categoryTextView = (TextView) view.findViewById(R.id.textView_category);
			holder.imageView = (ImageView) view.findViewById(R.id.imageView);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
         
        final GroupModel group = mGroupsFeaturedList.get(position);
        holder.titleTextView.setText(group.getTitle());
        holder.categoryTextView.setText(group.getCategory());
                	
        if(group.getPhotoFile() != null){        
	        // display post image
	        AppManager.getInstance().mImageLoader.displayImage(group.getPhotoFile().getUrl(),
	        		holder.imageView, AppManager.getInstance().options, null, null);
        }else{
        	holder.imageView.setImageBitmap(null);
        }
        
        return view;
    }
}