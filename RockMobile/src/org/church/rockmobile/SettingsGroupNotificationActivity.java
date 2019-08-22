package org.church.rockmobile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.SettingsGroupNotificationListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class SettingsGroupNotificationActivity extends Activity {

	SettingsGroupNotificationListAdapter mAdapter;
	ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_settings_group_notification);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Settings group notification", params); 
	}
	
	public void initUI(){
		ImageButton backImageButton = (ImageButton)findViewById(R.id.imageButton_back);
		backImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingsGroupNotificationActivity.this.onBackPressed();
			}
		});
		
		mListView = (ListView)findViewById(R.id.listView_group_notification);
		
		showMyGroups();
	}
	
	public void showMyGroups(){
		if(AppManager.mMyGroupsList.size() == 0)
			loadMyGroups();
		
		else{
			mAdapter = new SettingsGroupNotificationListAdapter(this, AppManager.mMyGroupsList);
			mListView.setAdapter(mAdapter);
		}
	}
	
	public void loadMyGroups(){
		
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		
		ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Group");
		query1.whereContainsAll("adminUsers", Arrays.asList(ParseUser.getCurrentUser()));
		
		ParseQuery<ParseObject> query2 = new ParseQuery<ParseObject>("Group");
		query2.whereContainsAll("joinedUsers", Arrays.asList(ParseUser.getCurrentUser()));
		
		
		List <ParseQuery<ParseObject>> query0 = new ArrayList<ParseQuery<ParseObject>>();
		query0.add(query1);
		query0.add(query2);
		
		ParseQuery<ParseObject> query = ParseQuery.or(query0);
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.orderByAscending("title");
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> list, ParseException err) {
				// TODO Auto-generated method stub
				if(list != null){
					AppManager.mMyGroupsList.clear();
					List<ParseObject> sortedList = UtilityMethods.sortBySortingValue(list);
					for (ParseObject object : sortedList){
						AppManager.mMyGroupsList.add((GroupModel) object);
					}
				}

				RockMobileApplication.getInstance().hideProgressDialog();
				mAdapter = new SettingsGroupNotificationListAdapter(SettingsGroupNotificationActivity.this, AppManager.mMyGroupsList);
				mListView.setAdapter(mAdapter);
			}
		});
	}
}
