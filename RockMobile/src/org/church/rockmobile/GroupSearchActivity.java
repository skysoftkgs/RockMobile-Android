package org.church.rockmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.GroupSearchListAdapter;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class GroupSearchActivity extends Activity implements OnClickListener{

	public final String TAG = "GroupSearchActivity";
	
	Button mDoneButton;
	ListView mGroupsListView;
	EditText mSearchEditText;
	
	GroupSearchListAdapter mGroupSearchListAdapter;
	boolean mIsGroupsLoading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_group_search);
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Group Search", params); 
	  		
	    initUI();
	}
	 
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.button_done:
				super.onBackPressed();
				break;
		}
	}
	
	public void initUI(){
		mDoneButton = (Button) findViewById(R.id.button_done);
		mDoneButton.setOnClickListener(this);		
		
		mSearchEditText = (EditText) findViewById(R.id.editText_search);
		mSearchEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(mGroupSearchListAdapter != null)
					mGroupSearchListAdapter.getFilter().filter(s.toString());
			}
		});
		
		mGroupsListView = (ListView) findViewById(R.id.listView_groups);
		loadAllGroups();
	}
	
	public void loadAllGroups(){
		if(mIsGroupsLoading) return;
		
		mIsGroupsLoading = true;
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Group");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.orderByAscending("title");
		query.include("adminUsers");
		query.include("joinedUsers");
		query.include("pendingUsers");
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> list, ParseException err) {
				// TODO Auto-generated method stub
				if(list != null){
					List<GroupModel> newList = new ArrayList<GroupModel>();
					List<ParseObject> sortedList = UtilityMethods.sortBySortingValue(list);
					for (ParseObject object : sortedList){
						newList.add((GroupModel) object);
					}
					mGroupSearchListAdapter = new GroupSearchListAdapter(GroupSearchActivity.this, newList);
					mGroupsListView.setAdapter(mGroupSearchListAdapter);
				}
				
		        mIsGroupsLoading = false;
		        RockMobileApplication.getInstance().hideProgressDialog();
			}
		});
	}
}
