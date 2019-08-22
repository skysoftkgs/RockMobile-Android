package org.church.rockmobile.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.model.ThreadMessageModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.widget.RoundedImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseUser;
 
public class ThreadMessageListAdapter extends BaseAdapter {
 
	private static final int TYPE_FRIEND_MESSAGE = 0;
    private static final int TYPE_MY_MESSAGE = 1;
    private static final int TYPE_MAX_COUNT = TYPE_MY_MESSAGE + 1;
    
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<ThreadMessageModel> mThreadMessageList;

    public ThreadMessageListAdapter(Context context,
            List<ThreadMessageModel> threadMessageList) {
        this.mContext = context;
        this.mThreadMessageList = threadMessageList;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	RoundedImageView userPhotoImageView;
    	TextView userNameTextView;
    	TextView timeTextView;
    	TextView messageTextView;    	
    }
 
    @Override
    public int getItemViewType(int position) {
    	ParseUser author = mThreadMessageList.get(position).getUser();
    	if(author.getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
    		return TYPE_MY_MESSAGE;
    	else
    		return TYPE_FRIEND_MESSAGE;
    }
    
    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }
    
    @Override
    public int getCount() {
        return mThreadMessageList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mThreadMessageList.get(position);
    }
 
    @Override
    public long getItemId(int position) {
        return position;
    }

	@SuppressLint("SimpleDateFormat")
	public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder holder;
        
        int type = getItemViewType(position);
        if(view == null){
        	holder = new ViewHolder();
         	if(type == TYPE_MY_MESSAGE){                   
	            view = mInflater.inflate(R.layout.item_thread_message_me, null);
	            holder.timeTextView = (TextView) view.findViewById(R.id.textView_time);
	            holder.messageTextView = (TextView) view.findViewById(R.id.textView_message);
	            
         	}else{
         		 view = mInflater.inflate(R.layout.item_thread_message_friend, null);
                 holder.userPhotoImageView = (RoundedImageView) view.findViewById(R.id.imageView_userPhoto);
                 holder.userNameTextView = (TextView) view.findViewById(R.id.textView_userName);
                 holder.timeTextView = (TextView) view.findViewById(R.id.textView_time);
                 holder.messageTextView = (TextView) view.findViewById(R.id.textView_message);
         	}
         	
         	view.setTag(holder);
        }else{
        	holder = (ViewHolder) view.getTag();
        }
       
        ThreadMessageModel model = (ThreadMessageModel) getItem(position);
        if(type == TYPE_MY_MESSAGE){                   
        	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, h:mm a");
            holder.timeTextView.setText(sdf.format(model.getPostTime()));
            holder.messageTextView.setText(model.getMessage());
            
     	}else{
     		UserModel user = model.getUser();
            ParseFile userPhotoFile = user.getParseFile("avatar");
            if(userPhotoFile != null){        
            	// display post image
     	        AppManager.getInstance().mImageLoader.displayImage(userPhotoFile.getUrl(),
     	        		holder.userPhotoImageView, AppManager.getInstance().options, null, null);
            }else{
            	holder.userPhotoImageView.setImageResource(R.drawable.icon_default_user);
            }
             
            holder.userNameTextView.setText(user.getRealUsername());
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy, h:mm a");
            holder.timeTextView.setText(sdf.format(model.getPostTime()));
            holder.messageTextView.setText(model.getMessage());
     	}
                
        return view;
    }
}