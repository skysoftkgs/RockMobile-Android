package org.church.rockmobile.adapter;

import java.util.Date;
import java.util.List;

import org.church.rockmobile.FeedFragment;
import org.church.rockmobile.GroupDetailActivity;
import org.church.rockmobile.LiveStreamActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.SeriesMessageDetailActivity;
import org.church.rockmobile.StoryDetailActivity;
import org.church.rockmobile.ThreadMessagesActivity;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.LiveStreamModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.SeriesMessageModel;
import org.church.rockmobile.model.StoryModel;
import org.church.rockmobile.model.ThreadModel;
import org.church.rockmobile.model.UserModel;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.newrelic.com.google.gson.Gson;
import com.parse.ParseUser;
 
public class FeedListAdapter extends BaseAdapter {
 
	private static final int TYPE_EMPTY = 6;
	private static final int TYPE_GROUP = 0;
    private static final int TYPE_STORY = 1;
    private static final int TYPE_SERIES_MESSAGE = 2;
    private static final int TYPE_LIVESTREAM = 3;
    private static final int TYPE_THREAD = 4;
    private static final int TYPE_FOOTER_LOADING = 5;
    
    // Declare Variables
    FeedFragment mFragment;
    LayoutInflater mInflater;
    private List<RequestModel> mFeedList;
    
    public FeedListAdapter(FeedFragment fragment,
            List<RequestModel> feedList) {
        this.mFragment = fragment;
        this.mFeedList = feedList;
        mInflater = LayoutInflater.from(mFragment.getActivity());
    }
 
    public class ViewHolder {
    	TextView contentTextView;
    	ImageView iconImageView;
    	ImageView coverPhotoImageView;
    	TextView livestreamTimeTextView;
    	TextView livestreamFeedTextView;
    	TextView threadTitleTextView;
    }
 
    @Override
    public int getItemViewType(int position) {
    	
    	if(mFeedList == null || mFeedList.size() == 0)
    		return TYPE_EMPTY;
    	
    	if (position == mFeedList.size())
    		return TYPE_FOOTER_LOADING;
    	
    	RequestModel model = (RequestModel)getItem(position);
    	if(model.getType().equalsIgnoreCase(Constants.REQUEST_TYPE_GROUP_JOIN) ||
    			model.getType().equalsIgnoreCase(Constants.REQUEST_TYPE_EVENT_ADD) ||
    			model.getType().equalsIgnoreCase(Constants.REQUEST_TYPE_EVENT_CHANGE)){
    		return TYPE_GROUP;
    	
    	}else if(model.getType().equalsIgnoreCase(Constants.REQUEST_TYPE_STORY_ADD)){
    		return TYPE_STORY;
    		
    	}else if(model.getType().equalsIgnoreCase(Constants.REQUEST_TYPE_SERIES_MESSAGE_ADD)){
    		return TYPE_SERIES_MESSAGE;
    		
    	}else if(model.getType().equalsIgnoreCase(Constants.REQUEST_TYPE_LIVESTREAM)){
    		return TYPE_LIVESTREAM;
    		
    	}else if(model.getType().equalsIgnoreCase(Constants.REQUEST_TYPE_THREAD_ADD)){
    		return TYPE_THREAD;
    	}
    	
    	return 0;
    }
     
    @Override
    public int getViewTypeCount(){
    	if(mFeedList == null || mFeedList.size() == 0){
    		return 1;
    		
    	}else{
    		return 6;
    	}
    }
    
    @Override
    public int getCount() {
    	if(mFeedList == null || mFeedList.size() == 0){
    		return 1;
    		
    	}else if(mFragment.isAllLoaded){
    		return mFeedList.size();
    	
    	}else{
    		return mFeedList.size() + 1;
    	}
    }
 
    @Override
    public Object getItem(int position) {
    	if(mFeedList == null || mFeedList.size() == 0){
    		return null;
    	}else{
    		return mFeedList.get(position);
    	}
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }

	public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        if(view == null){
        	holder = new ViewHolder();
        	int type = getItemViewType(position);
        	switch(type){
	        	case TYPE_EMPTY:
	        		view = mInflater.inflate(R.layout.item_no_feeds, parent, false);
	        		return view;
        		
	        	case TYPE_GROUP:
	        		view = mInflater.inflate(R.layout.item_feed_group, parent, false);
	                holder.contentTextView = (TextView) view.findViewById(R.id.textView_feedContent);
	                holder.iconImageView = (ImageView) view.findViewById(R.id.imageView_feedIcon);
	        		break;
	        		
	        	case TYPE_STORY:
	        		view = mInflater.inflate(R.layout.item_feed_story, parent, false);
	                holder.contentTextView = (TextView) view.findViewById(R.id.textView_feedContent);
	                holder.coverPhotoImageView = (ImageView) view.findViewById(R.id.imageView_coverPhoto);
	        		break;
	        		
	        	case TYPE_SERIES_MESSAGE:
	        		view = mInflater.inflate(R.layout.item_feed_series_message, parent, false);
	                holder.contentTextView = (TextView) view.findViewById(R.id.textView_feedContent);
	                holder.coverPhotoImageView = (ImageView) view.findViewById(R.id.imageView_coverPhoto);
	        		break;
	        		
	        	case TYPE_LIVESTREAM:
	        		view = mInflater.inflate(R.layout.item_feed_livestream, parent, false);
	                holder.livestreamTimeTextView = (TextView) view.findViewById(R.id.textView_time);
	                holder.livestreamFeedTextView = (TextView) view.findViewById(R.id.textView_feedContent);
	        		break;
	        		
	        	case TYPE_THREAD:
	        		view = mInflater.inflate(R.layout.item_feed_thread, parent, false);
	        		holder.contentTextView = (TextView) view.findViewById(R.id.textView_feedContent);
	                holder.iconImageView = (ImageView) view.findViewById(R.id.imageView_feedIcon);
	                holder.threadTitleTextView = (TextView) view.findViewById(R.id.textView_threadTitle);
	        		break;
	        		
	        	case TYPE_FOOTER_LOADING:
	        		view = mInflater.inflate(R.layout.footer_loading, parent, false);
	        		break;
        	}
         	         	
         	view.setTag(holder);
         	
        } else {
            holder = (ViewHolder) view.getTag();
        }
        
        if(mFeedList == null || mFeedList.size() == 0){
    		return view;
    	}
        
        if (getItemViewType(position) == TYPE_FOOTER_LOADING){
        	mFragment.loadFeeds();
    		return view;
        }
        
        final RequestModel feed = mFeedList.get(position);
        
        switch(getItemViewType(position)){
	    	case TYPE_GROUP:
	    		GroupModel group = feed.getGroup();
	            UserModel user = feed.getFromUser();
	            if(feed.getType() != null){
	    	        if(feed.getType().equals(Constants.REQUEST_TYPE_GROUP_JOIN)){
	    	        	if(user.getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
	    	        		holder.contentTextView.setText(Html.fromHtml("<b>Your</b> request to join <b>" + group.getTitle() + "</b> group has been accepted."));
	    	        		
	    	        	}else{
	    	        		holder.contentTextView.setText(Html.fromHtml("<b>" + user.getRealUsername() + "</b> has been added to <b>" + group.getTitle() + "</b> group."));
	    	        	}
	    		       
	    		        holder.iconImageView.setImageResource(R.drawable.icon_group_join);
	    		        
	    	        }else if(feed.getType().equals(Constants.REQUEST_TYPE_EVENT_ADD)){
	    		        holder.contentTextView.setText(Html.fromHtml("<b>" + user.getRealUsername() +
	    						"</b> has added <b>" + feed.getEvent().getTitle() + "</b> event to the <b>" + group.getTitle() + "</b> group."));
	    		        holder.iconImageView.setImageResource(R.drawable.icon_event_add);
	    		        
	    	        }else if(feed.getType().equals(Constants.REQUEST_TYPE_EVENT_CHANGE)){
	    		        holder.contentTextView.setText(Html.fromHtml(String.format("<b>%s</b> changed the <b>%s</b> event in <b>%s</b> gruop", 
	    		        		user.getRealUsername(),
	    		        		feed.getEvent().getTitle(), 
	    		        		group.getTitle())));
	    		        holder.iconImageView.setImageResource(R.drawable.icon_event_add);
	    	        }
	            }
	    		break;
	    		
	    	case TYPE_STORY:
	    		StoryModel story = feed.getStory();
	    		holder.contentTextView.setText(Html.fromHtml(String.format("<b>%s</b> posted a new article in <b>Stories.</b>", story.getAuthor())));
	    		if(story.getPhotoFile() != null){        
	    	        // display cover photo image
	    	        AppManager.getInstance().mImageLoader.displayImage(story.getPhotoFile().getUrl(),
	    	        		holder.coverPhotoImageView, AppManager.getInstance().options, null, null);
	    	        
	            }else if(story.getImageLink() != null){
	            	AppManager.getInstance().mImageLoader.displayImage(story.getImageLink(),
	    	        		holder.coverPhotoImageView, AppManager.getInstance().options, null, null);
	            
	            }else{
	            	holder.coverPhotoImageView.setImageBitmap(null);
	            }
	    		break;
	    		
	    	case TYPE_SERIES_MESSAGE:
	    		SeriesMessageModel seriesMessage = feed.getSeriesMessage();
	    		holder.contentTextView.setText(seriesMessage.getTitle());
	    		if(seriesMessage.getPhotoFile() != null){        
	    	        // display cover photo image
	    	        AppManager.getInstance().mImageLoader.displayImage(seriesMessage.getPhotoFile().getUrl(),
	    	        		holder.coverPhotoImageView, AppManager.getInstance().options, null, null);
	    	        
	            }else if(seriesMessage.getImageLink() != null){
	            	AppManager.getInstance().mImageLoader.displayImage(seriesMessage.getImageLink(),
	    	        		holder.coverPhotoImageView, AppManager.getInstance().options, null, null);
	            
	            }else{
	            	holder.coverPhotoImageView.setImageBitmap(null);
	            }
	    		break;
	    		
	    	case TYPE_LIVESTREAM:
	    		Date today = new Date();
	    		LiveStreamModel livestream = feed.getLiveStream();
	    		if(livestream.getStartTime().compareTo(today) > 0){
	    			long diff = livestream.getStartTime().getTime() - today.getTime();
					long seconds = diff / 1000;
					long minutes = (seconds / 60) %60;
					long hours = (seconds / 3600) % 24;
					long days = seconds / 3600 / 24;
					holder.livestreamTimeTextView.setText(String.format("%dd %dh %dm", days, hours, minutes));
					holder.livestreamFeedTextView.setText("Livestream is not currently available.");
					
	    		}else{
	    			long diff = livestream.getEndTime().getTime() - today.getTime();
					long seconds = diff / 1000;
					long minutes = (seconds / 60) %60;
					long hours = (seconds / 3600) % 24;
					long days = seconds / 3600 / 24;
					holder.livestreamTimeTextView.setText(String.format("%dd %dh %dm", days, hours, minutes));
					holder.livestreamFeedTextView.setText("Livestream is available now.");
	    		}
	    		
	    		break;
	    		
	    	case TYPE_THREAD:
	    		GroupModel group1 = feed.getGroup();
	            UserModel user1 = (UserModel) feed.getFromUser();
	    		holder.contentTextView.setText(Html.fromHtml("<b>" + user1.getRealUsername() + "</b> posted a new topic in <b>" + group1.getTitle() + "</b> group."));
	    		if(user1.getAvatarFile() != null){
		    		AppManager.getInstance().mImageLoader.displayImage(user1.getAvatarFile().getUrl(),
			        		holder.iconImageView, AppManager.getInstance().options, null, null);
	    		}else{
	    			holder.iconImageView.setImageResource(R.drawable.icon_default_user);
	    		}
	    		
	    		ThreadModel thread = feed.getThread();
	    		holder.threadTitleTextView.setText(thread.getTitle());
	    		break;
	    		
		}
        
        view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch(getItemViewType(position)){
		    	case TYPE_GROUP:
		    		GroupModel group = feed.getGroup();
		            Intent intent;
		            intent = new Intent(mFragment.getActivity(), GroupDetailActivity.class);
    	        	GroupDetailActivity.mGroup = group;
    	        	mFragment.startActivity(intent);
		    		break;
		    		
		    	case TYPE_STORY:
		    		intent = new Intent(mFragment.getActivity(), StoryDetailActivity.class);
		        	StoryDetailActivity.mStory = feed.getStory();
		        	mFragment.startActivity(intent);
		    		break;
		    		
		    	case TYPE_SERIES_MESSAGE:
		    		intent = new Intent(mFragment.getActivity(), SeriesMessageDetailActivity.class);
		    		intent.putExtra(Constants.BUNDLE_MESSAGE_POSITION, 0);
		    		String seriesMessageJson = new Gson().toJson(feed.getSeriesMessage());
		    		intent.putExtra("Test", seriesMessageJson);
					SeriesMessageDetailActivity.mSeriesMessage = feed.getSeriesMessage(); 
					mFragment.startActivity(intent);
		    		break;
		    		
		    	case TYPE_LIVESTREAM:
//		    		MainActivity activity = (MainActivity) mContext;
//		    		activity.setTab(3);
		    		intent = new Intent(mFragment.getActivity(), LiveStreamActivity.class);
		    		mFragment.startActivity(intent);
		    		break;
		    		
		    	case TYPE_THREAD:
		    		intent = new Intent(mFragment.getActivity(), ThreadMessagesActivity.class);
					ThreadMessagesActivity.mThread = feed.getThread();
					ThreadMessagesActivity.mGroup = feed.getGroup();
					mFragment.startActivity(intent);
		    		break;
				}
			}
		});
        
        return view;
    }
}