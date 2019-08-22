package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.CategoryGroupsActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.common.Constants;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
 
public class GroupsCategoryListAdapter extends BaseAdapter {
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<String> mCategoryList;

    public GroupsCategoryListAdapter(Context context,
            List<String> categoryList) {
        this.mContext = context;
        this.mCategoryList = categoryList;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	TextView titleTextView;
    }
 
    @Override
    public int getCount() {
        return mCategoryList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mCategoryList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_groups_category, null);
            holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);  	        
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final String category = mCategoryList.get(position);
        holder.titleTextView.setText(category);
    
        view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, CategoryGroupsActivity.class);
				intent.putExtra(Constants.BUNDLE_CATEGORY, category);
				mContext.startActivity(intent);
			}
		});
        
        return view;
    }
}