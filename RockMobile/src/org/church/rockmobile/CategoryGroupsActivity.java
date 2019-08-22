package org.church.rockmobile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.CategoryGroupsListAdapter;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class CategoryGroupsActivity extends Activity implements OnClickListener{

	public final String TAG = "CategoryGroupsActivity";
	
	ImageButton mSearchImageButton;
	ImageButton mBackImageButton;
	ListView mGroupsListView;
	
	String mCategoryName;
	boolean mIsGroupsLoading;
	
	public static boolean mIsDataChanged = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_category_groups);
	    
	    initUI();
	    
	    //track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Category Groups", params); 
	}
	 
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mIsDataChanged){
			loadGroupsForCategory(mCategoryName);
			mIsDataChanged = false;
		}
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_search:
				Intent intent = new Intent(this, GroupSearchActivity.class);
				startActivity(intent);
				break;
				
			case R.id.imageButton_back:
				super.onBackPressed();
				break;
		}
	}
	
	public void initUI(){
		TextView titleTextView = (TextView) findViewById(R.id.textView_title);
		Bundle bundle = getIntent().getExtras();
		mCategoryName = bundle.getString(Constants.BUNDLE_CATEGORY);
		titleTextView.setText(mCategoryName);
		
		mSearchImageButton = (ImageButton) findViewById(R.id.imageButton_search);
		mSearchImageButton.setOnClickListener(this);
		
		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);
		
		mGroupsListView = (ListView) findViewById(R.id.listView_groups);
		loadGroupsForCategory(mCategoryName);
	}
	
	public void loadGroupsForCategory(String category){
		if(mIsGroupsLoading) return;
		
		mIsGroupsLoading = true;
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Group");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("category", category);
		query.orderByAscending("title");
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
					CategoryGroupsListAdapter categoryGroupsListAdapter = new CategoryGroupsListAdapter(CategoryGroupsActivity.this, newList);
					mGroupsListView.setAdapter(categoryGroupsListAdapter);
				}
				
		        mIsGroupsLoading = false;
		        RockMobileApplication.getInstance().hideProgressDialog();
			}
		});
	}
	
	private void sortGroups(List<GroupModel> list) {
		List<GroupModel> positiveSortingList = new ArrayList<GroupModel>();
		List<GroupModel> zeroOrNullSortingList = new ArrayList<GroupModel>();
		List<GroupModel> negativeSortingList = new ArrayList<GroupModel>();

		for (GroupModel groupModel : list) {
			int sorting = groupModel.getSorting();
			if (sorting == 0) {
				zeroOrNullSortingList.add(groupModel);
			} else if (sorting > 0) {
				positiveSortingList.add(groupModel);
			} else {
				negativeSortingList.add(groupModel);
			}
		}

		Collections.sort(positiveSortingList, new Comparator<GroupModel>() {

			@Override
			public int compare(GroupModel lhs, GroupModel rhs) {
				// TODO Auto-generated method stub
				return rhs.getSorting().compareTo(lhs.getSorting());
			}
		});

		Collections.sort(negativeSortingList, new Comparator<GroupModel>() {

			@Override
			public int compare(GroupModel lhs, GroupModel rhs) {
				// TODO Auto-generated method stub
				return rhs.getSorting().compareTo(lhs.getSorting());
			}
		});

		Collections.sort(zeroOrNullSortingList, new Comparator<GroupModel>() {

			@Override
			public int compare(GroupModel lhs, GroupModel rhs) {
				// TODO Auto-generated method stub
				return lhs.getTitle().compareTo(rhs.getTitle());
			}
		});

		list.clear();
		list.addAll(positiveSortingList);
		list.addAll(zeroOrNullSortingList);
		list.addAll(negativeSortingList);
	}
}
