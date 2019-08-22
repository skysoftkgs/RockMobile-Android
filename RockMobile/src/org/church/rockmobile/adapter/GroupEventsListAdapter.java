package org.church.rockmobile.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.church.rockmobile.GroupDetailActivity;
import org.church.rockmobile.NewEventActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.EventModel;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
 
public class GroupEventsListAdapter extends BaseAdapter {
 
    // Declare Variables
    GroupDetailActivity mActivity;
    LayoutInflater mInflater;
    private List<EventModel> mEventList;

    boolean mTenMinChecked = false;
    boolean mThirtyMinChecked = false;
    boolean mOneHourChecked = false;
    boolean mOneDayChecked = false;
    
    public GroupEventsListAdapter(GroupDetailActivity activity,
            List<EventModel> eventList) {
        this.mActivity = activity;
        this.mEventList = eventList;
        mInflater = LayoutInflater.from(mActivity);
    }
 
    public class ViewHolder {
    	TextView titleTextView;
    	TextView timeTextView;
    	TextView placeTextView;
    	ImageButton reminderImageButton;
    	ImageButton editEventImageButton;
    }
 
    @Override
    public int getCount() {
        return mEventList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mEventList.get(position);
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
            view = mInflater.inflate(R.layout.item_group_event, null);
            holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
            holder.timeTextView = (TextView) view.findViewById(R.id.textView_time); 
            holder.placeTextView = (TextView) view.findViewById(R.id.textView_place);
            holder.reminderImageButton = (ImageButton) view.findViewById(R.id.imageButton_reminder);
            holder.editEventImageButton = (ImageButton) view.findViewById(R.id.imageButton_edit_event);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
                
        final EventModel event = mEventList.get(position);
        holder.titleTextView.setText(event.getTitle());
        Date eventTime = event.getEventTime();
        SimpleDateFormat df = new SimpleDateFormat("h:mm a; MM/dd/yyyy");
        holder.timeTextView.setText(df.format(eventTime));
        holder.placeTextView.setText(event.getAddress());
        
        holder.reminderImageButton.setImageResource(R.drawable.btn_event_notify_normal);
        final int id = UtilityMethods.convertStringToUniqueInt(event.getObjectId());
         for(int i=0;i<4;i++){
        	int newId = id * 10 + i;
        	if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mActivity, newId) == true){
        		holder.reminderImageButton.setImageResource(R.drawable.btn_event_notify_set);
        		break;
			}
        }
        
        holder.reminderImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				final Dialog dialog = new Dialog(mActivity);
				dialog.setContentView(R.layout.dialog_notify);
				dialog.setTitle(mActivity.getResources().getString(R.string.notify_event_dialog_title));
				final ImageView tenMinImageView = (ImageView) dialog.findViewById(R.id.imageView_10min);
				final ImageView thirtyMinImageView = (ImageView) dialog.findViewById(R.id.imageView_30min);
				final ImageView oneHourImageView = (ImageView) dialog.findViewById(R.id.imageView_1hour);
				final ImageView oneDayImageView = (ImageView) dialog.findViewById(R.id.imageView_1day);
				
				//show registered notification
				if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mActivity, id * 10) == true){
					tenMinImageView.setVisibility(View.VISIBLE);
					mTenMinChecked = true;
					
				}else{
					tenMinImageView.setVisibility(View.INVISIBLE);
					mTenMinChecked = false;
				}
				
				if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mActivity, id * 10 + 1) == true){
					thirtyMinImageView.setVisibility(View.VISIBLE);
					mThirtyMinChecked = true;
				
				}else{
					thirtyMinImageView.setVisibility(View.INVISIBLE);
					mThirtyMinChecked = false;
				}
				
				if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mActivity, id * 10 + 2) == true){
					oneHourImageView.setVisibility(View.VISIBLE);
					mOneHourChecked = true;
				
				}else{
					oneHourImageView.setVisibility(View.INVISIBLE);
					mOneHourChecked = false;
				}
				
				if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mActivity, id * 10 + 3) == true){
					oneDayImageView.setVisibility(View.VISIBLE);
					mOneDayChecked = true;
				
				}else{
					oneDayImageView.setVisibility(View.INVISIBLE);
					mOneDayChecked = false;
				}
				
				RelativeLayout tenMinLayout = (RelativeLayout) dialog.findViewById(R.id.layout_10min);
				tenMinLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mTenMinChecked){
							tenMinImageView.setVisibility(View.INVISIBLE);
							mTenMinChecked = false;
						}else{
							tenMinImageView.setVisibility(View.VISIBLE);
							mTenMinChecked = true;
						}
					}
				});
				
				RelativeLayout thirtyMinLayout = (RelativeLayout) dialog.findViewById(R.id.layout_30min);
				thirtyMinLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mThirtyMinChecked){
							thirtyMinImageView.setVisibility(View.INVISIBLE);
							mThirtyMinChecked = false;
						}else{
							thirtyMinImageView.setVisibility(View.VISIBLE);
							mThirtyMinChecked = true;
						}
					}
				});
				
				RelativeLayout oneHourLayout = (RelativeLayout) dialog.findViewById(R.id.layout_1hour);
				oneHourLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mOneHourChecked){
							oneHourImageView.setVisibility(View.INVISIBLE);
							mOneHourChecked = false;
						}else{
							oneHourImageView.setVisibility(View.VISIBLE);
							mOneHourChecked = true;
						}
					}
				});
				
				RelativeLayout oneDayLayout = (RelativeLayout) dialog.findViewById(R.id.layout_1day);
				oneDayLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if(mOneDayChecked){
							oneDayImageView.setVisibility(View.INVISIBLE);
							mOneDayChecked = false;
						}else{
							oneDayImageView.setVisibility(View.VISIBLE);
							mOneDayChecked = true;
						}
					}
				});
				
	 
				Button notifyButton = (Button) dialog.findViewById(R.id.button_notify);
				notifyButton.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						ArrayList<Integer> reminderMinutes = new ArrayList<Integer>();
						if(mTenMinChecked){
							reminderMinutes.add(10);
						}
						if(mThirtyMinChecked){
							reminderMinutes.add(30);
						}
						if(mOneHourChecked){
							reminderMinutes.add(60);
						}
						if(mOneDayChecked){
							reminderMinutes.add(60*24);
						}
						
//						UtilityMethods.addEventToCalendar(mActivity, event.getTitle(), "", 
//								event.getEventTime().getTime(), event.getEventTime().getTime(), event.getAddress(), reminderMinutes);
						AppManager.getInstance().notifyEvent(mActivity, 
								event.getObjectId(), event.getTitle(),
								event.getEventTime(), reminderMinutes);
						dialog.dismiss();
						
						GroupEventsListAdapter.this.notifyDataSetChanged();
					}
				});
	 
				dialog.show();
			}
		});
        
        holder.editEventImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub				
				Intent intent = new Intent(mActivity, NewEventActivity.class);
				NewEventActivity.mGroup = GroupDetailActivity.mGroup;
				NewEventActivity.mEvent = event;
				mActivity.startActivityForResult(intent, GroupDetailActivity.REQUEST_ADD_NEW_EVENT);
			}
		});
        
        return view;
    }
}