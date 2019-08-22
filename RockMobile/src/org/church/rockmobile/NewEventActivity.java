package org.church.rockmobile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.EventModel;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.service.PushNotificationService;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class NewEventActivity extends Activity implements OnClickListener{

	public final String TAG = "NewEventActivity";

	ImageButton mApplyImageButton;
	ImageButton mCloseImageButton;
	TextView mHourTextView;
	TextView mDayTextView;
	EditText mNameEditText;
	EditText mPlaceEditText;
	RelativeLayout mHourLayout;
	RelativeLayout mDayLayout;
		
	String mEventDate;
	String mEventHour;
	boolean mIsAddNew;
	
	public static GroupModel mGroup;
	public static EventModel mEvent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_new_event);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("New event", params); 
	}
	 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_apply:
				addNewEvent();
				break;
				
			case R.id.imageButton_close:
				super.onBackPressed();
				break;
				
			case R.id.layout_hour:
				showTimePickerDialog();
				break;
				
			case R.id.layout_day:
				showDatePickerDialog();
				break;
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public void initUI(){
		TextView titleTextView = (TextView) findViewById(R.id.textView_title);
		
		mHourLayout = (RelativeLayout) findViewById(R.id.layout_hour);
		mHourLayout.setOnClickListener(this);
				
		mDayLayout = (RelativeLayout) findViewById(R.id.layout_day);
		mDayLayout.setOnClickListener(this);
		
		mHourTextView = (TextView) findViewById(R.id.textView_hour);
		mDayTextView = (TextView) findViewById(R.id.textView_day);
		
		mApplyImageButton = (ImageButton) findViewById(R.id.imageButton_apply);
		mApplyImageButton.setOnClickListener(this);
		
		mCloseImageButton = (ImageButton) findViewById(R.id.imageButton_close);
		mCloseImageButton.setOnClickListener(this);
		
		mNameEditText = (EditText) findViewById(R.id.editText_name);
		mPlaceEditText = (EditText) findViewById(R.id.editText_place);

		if(mEvent != null){
			titleTextView.setText("EDIT EVENT");
			Date date = mEvent.getEventTime();
			SimpleDateFormat sdfTime = new SimpleDateFormat("h:mm a");
			SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy");
			mNameEditText.setText(mEvent.getTitle());
			mEventHour = sdfTime.format(date);
			mEventDate = sdfDate.format(date);
			mHourTextView.setText(mEventHour);
			mDayTextView.setText(mEventDate);
			mPlaceEditText.setText(mEvent.getAddress());
		}
		
		UtilityMethods.hideKeyboard(this);
	}
	
	@SuppressLint("SimpleDateFormat")
	public void addNewEvent(){
		if(!UtilityMethods.checkIsEmptyForEditText(NewEventActivity.this, mNameEditText, "Event Name "))
			return;
		
		if(!UtilityMethods.checkIsEmptyForTextView(NewEventActivity.this, mHourTextView, "Event time "))
			return;
		if(!UtilityMethods.checkIsEmptyForTextView(NewEventActivity.this, mDayTextView, "Event date "))
			return;
		
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		Date date = null;
		try {
        	date = format.parse(mEventDate + " " + mEventHour);
    
        } catch (Exception e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
		
		if (date == null || date.compareTo(new Date()) < 0){
			RockMobileApplication.getInstance().showErrorDialog(this, "You can't set passed date.");
			return;
		}
		
		UtilityMethods.hideKeyboard(this);
		RockMobileApplication.getInstance().showProgressFullScreenDialog(NewEventActivity.this);
		
		if(mEvent == null){
			mEvent = new EventModel();
			mIsAddNew = true;
		}
		
		if(UtilityMethods.containsParseUser(mGroup.getAdminUserList(), ParseUser.getCurrentUser())){
	        mEvent.setEventTime(date);
	        mEvent.setChurchId(Constants.CHURCH_ID);
	        mEvent.setTitle(mNameEditText.getText().toString());
	        mEvent.setAddress(mPlaceEditText.getText().toString());
	        mEvent.setGroup(mGroup);
        	mEvent.setIsPending(false);
        	mEvent.saveInBackground(new SaveCallback() {
				
				@Override
				public void done(ParseException err) {
					// TODO Auto-generated method stub
					RockMobileApplication.getInstance().hideProgressDialog();
					if(err == null){
						addEventToGroup(mEvent);
					}
				}
			});
			
		}else{
			if(mIsAddNew == false){
				final EventModel event = new EventModel();
				event.setEventTime(date);
				event.setChurchId(Constants.CHURCH_ID);
		        event.setTitle(mNameEditText.getText().toString());
		        event.setAddress(mPlaceEditText.getText().toString());
		        event.setGroup(mGroup);
		        event.setEditedEvent(mEvent);
				event.setIsPending(true);
				event.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException object) {
						// TODO Auto-generated method stub
						RockMobileApplication.getInstance().hideProgressDialog();
						if(object == null){
							requestAddEventToGroup(event);
						}
					}
				});
				
			}else{
				mEvent.setEventTime(date);
		        mEvent.setChurchId(Constants.CHURCH_ID);
		        mEvent.setTitle(mNameEditText.getText().toString());
		        mEvent.setAddress(mPlaceEditText.getText().toString());
		        mEvent.setGroup(mGroup);
	        	mEvent.setIsPending(true);
	        	mEvent.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(ParseException err) {
						// TODO Auto-generated method stub
						RockMobileApplication.getInstance().hideProgressDialog();
						if(err == null){
							requestAddEventToGroup(mEvent);
						}
					}
				});
			}
			
		}
	}
	
	@SuppressLint("SimpleDateFormat")
	public void showDatePickerDialog()
	{
		Calendar c = Calendar.getInstance();
		int year;
		int month;
		int day;
	  
		year = c.get(Calendar.YEAR);
	    month = c.get(Calendar.MONTH);
	    day = c.get(Calendar.DAY_OF_MONTH);
	  
	    if(mDayTextView.getText().toString() != null && mDayTextView.getText().toString().length()>0){
	    	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
	        try {
	        	Date date = format.parse(mDayTextView.getText().toString());
	        	Calendar calendar = Calendar.getInstance();
	        	calendar.setTime(date);
	    
	        	year = calendar.get(Calendar.YEAR);
	        	month = calendar.get(Calendar.MONTH);
	        	day = calendar.get(Calendar.DAY_OF_MONTH);
	    
	        }catch(Exception e) {
	        	 // TODO Auto-generated catch block
	        	e.printStackTrace();
	        }
	    }
	  
	    try{
	    	DatePickerDialog dialog = new DatePickerDialog(NewEventActivity.this,
	               new DateSetListener(), year, month, day);
//	    	dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
	    	dialog.show();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
	  
	}
	  
	class DateSetListener implements DatePickerDialog.OnDateSetListener {

		@Override
	    public void onDateSet(DatePicker view, int year, int monthOfYear,
	              int dayOfMonth) {
	          // TODO Auto-generated method stub
			mEventDate = String.format("%d/%02d/%04d", monthOfYear+1, dayOfMonth, year);
			mDayTextView.setText(mEventDate); 
	         
	    }
	}
	  
	  
	@SuppressLint("SimpleDateFormat")
	public void showTimePickerDialog()
	{
		Calendar c = Calendar.getInstance();
		int hour;
		int minute;
	  
		hour = c.get(Calendar.HOUR_OF_DAY);
	    minute = c.get(Calendar.MINUTE);
	  
	    if(mHourTextView.getText().toString() != null && mHourTextView.getText().toString().length()>0){
	    	SimpleDateFormat format = new SimpleDateFormat("h:mm a");
	        try {
	        	Date date = format.parse(mHourTextView.getText().toString());
	        	Calendar calendar = Calendar.getInstance();
	        	calendar.setTime(date);
	    
	        	hour = calendar.get(Calendar.HOUR_OF_DAY);
	        	minute = calendar.get(Calendar.MINUTE);
	    
	        } catch (Exception e) {
	        	// TODO Auto-generated catch block
	        	e.printStackTrace();
	        }
	    }
	  
	    try{
	    	TimePickerDialog dialog = new TimePickerDialog(NewEventActivity.this,
	               new TimeSetListener(), hour, minute, false);
	      
	    	dialog.show();
	    }catch(Exception e){
	    	e.printStackTrace();
	    }  
	}
	  
	@SuppressLint("SimpleDateFormat")
	class TimeSetListener implements TimePickerDialog.OnTimeSetListener {

		@Override
		public void onTimeSet(TimePicker arg0, int hour, int min) {
			// TODO Auto-generated method stub
			try{
				DateFormat f1 = new SimpleDateFormat("HH:mm");
				Date d = f1.parse(String.format("%02d:%02d", hour, min));
				DateFormat f2 = new SimpleDateFormat("h:mm a");
				mEventHour = f2.format(d);
				mHourTextView.setText(mEventHour);
				
			}catch(Exception e){
				
			}
		}
	}
	
	public void addEventToGroup(final EventModel event){
		
		RequestModel requestModel = new RequestModel();
		requestModel.setChurchId(Constants.CHURCH_ID);
		requestModel.setGroup(mGroup);
		requestModel.setEvent(event);
		requestModel.setFromUser((UserModel)ParseUser.getCurrentUser());
		if(mIsAddNew){
			requestModel.setType(Constants.REQUEST_TYPE_EVENT_ADD);
		}else{
			requestModel.setType(Constants.REQUEST_TYPE_EVENT_CHANGE);
		}
		requestModel.setToUserList(mGroup.getGroupUserListExceptCurrentUser());
		requestModel.setRequestStatus(Constants.REQUEST_STATUS_FEED);
		requestModel.saveEventually(new SaveCallback() {
			
			@Override
			public void done(ParseException err) {
				// TODO Auto-generated method stub
				if(err == null){
					if(mIsAddNew){
						RockMobileApplication.getInstance().showToast(NewEventActivity.this, R.string.new_event_saved, Toast.LENGTH_SHORT);
					}else{
						RockMobileApplication.getInstance().showToast(NewEventActivity.this, R.string.event_changed, Toast.LENGTH_SHORT);
					}
					PushNotificationService.getInstance().sendFeedUpdate(mGroup, null);
					PushNotificationService.getInstance().sendEventRefresh(event, mGroup, null);
					
					Intent returnIntent = new Intent();
					returnIntent.putExtra(Constants.BUNDLE_NEW_EVENT_REFRESH, true);
					setResult(RESULT_OK, returnIntent);
					if(mIsAddNew){
						GroupDetailActivity.mEventsList.add(0, event);
					}
					finish();
					
				}else{
					RockMobileApplication.getInstance().showToast(NewEventActivity.this,
							getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});			
	}
	
	public void requestAddEventToGroup(final EventModel event){
		RequestModel requestModel = new RequestModel();
		requestModel.setChurchId(Constants.CHURCH_ID);
		requestModel.setGroup(mGroup);
		requestModel.setEvent(event);
		requestModel.setFromUser((UserModel)ParseUser.getCurrentUser());
		if(mIsAddNew){
			requestModel.setType(Constants.REQUEST_TYPE_EVENT_ADD);
		}else{
			requestModel.setType(Constants.REQUEST_TYPE_EVENT_CHANGE);
			requestModel.setOldEvent(mEvent);
		}
		requestModel.setToUserList(mGroup.getAdminUserList());
		requestModel.setRequestStatus(Constants.REQUEST_STATUS_REQUEST);
		requestModel.saveInBackground(new SaveCallback() {
			
			@Override
			public void done(ParseException err) {
				// TODO Auto-generated method stub
				if(err == null){
					if(mIsAddNew){
						RockMobileApplication.getInstance().showToast(NewEventActivity.this, R.string.new_event_is_pending, Toast.LENGTH_SHORT);
						
					}else{
						RockMobileApplication.getInstance().showToast(NewEventActivity.this, R.string.change_event_is_pending, Toast.LENGTH_SHORT);
					}

					PushNotificationService.getInstance().sendPendingRequest(mGroup, null);
					
					Intent returnIntent = new Intent();
					setResult(RESULT_OK, returnIntent);
					finish();
					
				}else{
					RockMobileApplication.getInstance().showToast(NewEventActivity.this,
							getResources().getString(R.string.server_error), Toast.LENGTH_SHORT);
				}
			}
		});				
	}
}
