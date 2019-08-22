package org.church.rockmobile.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.LiveStreamModel;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
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
 
public class LiveStreamListAdapter extends BaseAdapter {
 
    // Declare Variables
    Context mContext;
    LayoutInflater mInflater;
    private List<LiveStreamModel> mlivestreamList;

    boolean mTenMinChecked = false;
    boolean mThirtyMinChecked = false;
    boolean mOneHourChecked = false;
    boolean mOneDayChecked = false;
    
    public LiveStreamListAdapter(Context context,
            List<LiveStreamModel> list) {
        this.mContext = context;
        this.mlivestreamList = list;
        mInflater = LayoutInflater.from(mContext);
    }
 
    public class ViewHolder {
    	TextView titleTextView;
    	TextView timeTextView;
    	ImageButton reminderImageButton;
    }
 
    @Override
    public int getCount() {
        return mlivestreamList.size();
    }
 
    @Override
    public Object getItem(int position) {
        return mlivestreamList.get(position);
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
            view = mInflater.inflate(R.layout.item_live_stream, null);
            holder.titleTextView = (TextView) view.findViewById(R.id.textView_title);
            holder.timeTextView = (TextView) view.findViewById(R.id.textView_time);
            holder.reminderImageButton = (ImageButton) view.findViewById(R.id.imageButton_notify);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final LiveStreamModel livestream = mlivestreamList.get(position);
        holder.titleTextView.setText(livestream.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a MM/dd/yyyy");
        holder.timeTextView.setText(sdf.format(livestream.getStartTime()));
              
        holder.reminderImageButton.setImageResource(R.drawable.btn_event_notify_normal);
        final int id = UtilityMethods.convertStringToUniqueInt(livestream.getObjectId());
        for(int i=0;i<4;i++){
        	int newId = id * 10 + i;
        	if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mContext, newId) == true){
        		holder.reminderImageButton.setImageResource(R.drawable.btn_event_notify_set);
        		break;
			}
        }
        
        holder.reminderImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(mContext);
				dialog.setContentView(R.layout.dialog_notify);
				dialog.setTitle(mContext.getResources().getString(R.string.notify_event_dialog_title));
				final ImageView tenMinImageView = (ImageView) dialog.findViewById(R.id.imageView_10min);
				final ImageView thirtyMinImageView = (ImageView) dialog.findViewById(R.id.imageView_30min);
				final ImageView oneHourImageView = (ImageView) dialog.findViewById(R.id.imageView_1hour);
				final ImageView oneDayImageView = (ImageView) dialog.findViewById(R.id.imageView_1day);
				
				//show registered notification
				if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mContext, id * 10) == true){
					tenMinImageView.setVisibility(View.VISIBLE);
					mTenMinChecked = true;
					
				}else{
					tenMinImageView.setVisibility(View.INVISIBLE);
					mTenMinChecked = false;
				}
				
				if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mContext, id * 10 + 1) == true){
					thirtyMinImageView.setVisibility(View.VISIBLE);
					mThirtyMinChecked = true;
				
				}else{
					thirtyMinImageView.setVisibility(View.INVISIBLE);
					mThirtyMinChecked = false;
				}
				
				if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mContext, id * 10 + 2) == true){
					oneHourImageView.setVisibility(View.VISIBLE);
					mOneHourChecked = true;
				
				}else{
					oneHourImageView.setVisibility(View.INVISIBLE);
					mOneHourChecked = false;
				}
				
				if(AppManager.getInstance().checkIfpendingIntentIsRegistered(mContext, id * 10 + 3) == true){
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
						List<Integer> reminderMinutes = new ArrayList<Integer>();
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
						
//						AppManager.getInstance().notifyEvent(mContext, livestream.getObjectId(), livestream.getTitle(),
//								livestream.getStartTime(), reminderMinutes);
						dialog.dismiss();
						
						LiveStreamListAdapter.this.notifyDataSetChanged();
					}
				});
	 
				dialog.show();
			}
		});

        return view;
    }
}