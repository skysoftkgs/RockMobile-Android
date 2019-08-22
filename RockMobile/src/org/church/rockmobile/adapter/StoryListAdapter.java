package org.church.rockmobile.adapter;

import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.StoryDetailActivity;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.model.StoryModel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class StoryListAdapter extends BaseAdapter {
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<StoryModel> mStoryList;

    public StoryListAdapter(Context context,
            List<StoryModel> storyList) {
        this.mContext = context;
        this.mStoryList = storyList;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	TextView titleTextView;
    	TextView userNameTextView;
    	ImageView imageView;
    }
 
    @Override
    public int getCount() {
        return mStoryList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mStoryList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }
 
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.item_story, null);
            holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
			holder.userNameTextView = (TextView) view.findViewById(R.id.textView_userName);
			holder.imageView = (ImageView) view.findViewById(R.id.imageView);

			ViewGroup.LayoutParams imageViewParams = holder.imageView.getLayoutParams();
  	        imageViewParams.width = AppManager.getInstance().mScreenWidth / 2;
  	        imageViewParams.height = AppManager.getInstance().mScreenWidth / 2;
  	        holder.imageView.setLayoutParams(imageViewParams);
  	        
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final StoryModel story = mStoryList.get(position);
        holder.titleTextView.setText(story.getTitle());
        String author = story.getAuthor();
        if(author != null)
        	holder.userNameTextView.setText("by " + author);
        else
        	holder.userNameTextView.setText("");
        
        if(story.getPhotoFile() != null){        
	        // display cover photo image
	        AppManager.getInstance().mImageLoader.displayImage(story.getPhotoFile().getUrl(),
	        		holder.imageView, AppManager.getInstance().options, null, null);
	        
        }else if(story.getThumbnailImageLink() != null){
        	AppManager.getInstance().mImageLoader.displayImage(story.getThumbnailImageLink(),
	        		holder.imageView, AppManager.getInstance().options, null, null);
        	
        }else{
        	holder.imageView.setImageBitmap(null);
        }
        
        view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
	        	Intent intent = new Intent(mContext, StoryDetailActivity.class);
	        	StoryDetailActivity.mStory = story;
	        	mContext.startActivity(intent);
			}
		});
        
        return view;
    }
}