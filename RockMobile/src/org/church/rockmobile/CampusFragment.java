package org.church.rockmobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.CampusListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.CampusModel;
import org.church.rockmobile.model.UserModel;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class CampusFragment extends BaseFragment{
	
	MainActivity mActivity;
	boolean mIsCampusesLoading;
	
	PullToRefreshGridView mPullRefreshCampuseGridView;
	GridView mCampuseGridView;
	ProgressBar mProgressBar;
	CampusListAdapter mAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_campus, container, false);      
		mActivity = (MainActivity) getActivity();
				
		mPullRefreshCampuseGridView = (PullToRefreshGridView) rootView.findViewById(R.id.gridView_campuse);
		mCampuseGridView = mPullRefreshCampuseGridView.getRefreshableView();
		mPullRefreshCampuseGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

 			@Override
 			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
 				loadCampuses();
 			}

 			@Override
 			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
 				mPullRefreshCampuseGridView.onRefreshComplete();
 			}

        });
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_campuse);

		showCampuses();
	
		//track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Campus", params); 
				
        return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);		
    	initActionBar();
	}

	public void initActionBar(){
		//customize actionbar
	    LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = inflator.inflate(R.layout.actionbar_campuses, null);
	    mActivity.mActionBar.setCustomView(v);
	}
	
	public void showCampuses(){
		if(AppManager.mCampusesList.size() == 0)
			loadCampuses();
		else if(getActivity() != null){
			mCampuseGridView.setAdapter(mAdapter);
			mProgressBar.setVisibility(View.GONE);
		}
	}
	
	public void loadCampuses(){
		if(mIsCampusesLoading) return;
		
		mIsCampusesLoading = true;
		if(!mPullRefreshCampuseGridView.isRefreshing())
			mProgressBar.setVisibility(View.VISIBLE);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Campus");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.orderByAscending("name");
		query.include("group");
		query.findInBackground(new FindCallback<ParseObject>() {
			
			@Override
			public void done(List<ParseObject> list, ParseException err) {
				// TODO Auto-generated method stub				
				if(list != null){
					AppManager.mCampusesList.clear();
					List<ParseObject> sortedList = UtilityMethods.sortBySortingValue(list);
					for (ParseObject object : sortedList){
						AppManager.mCampusesList.add((CampusModel) object);
					}
					if(getActivity() != null){
						mAdapter = new CampusListAdapter(CampusFragment.this, AppManager.mCampusesList);
						mCampuseGridView.setAdapter(mAdapter);
						mPullRefreshCampuseGridView.onRefreshComplete();
					}
				}
				
				mIsCampusesLoading = false;
		        mProgressBar.setVisibility(View.GONE);
			}
		});
	}
}
