package org.church.rockmobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.SeriesListAdapter;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.model.SeriesModel;
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
import com.parse.ParseQuery;


public class SeriesFragment extends BaseFragment{
	
	MainActivity mActivity;
	boolean mIsSeriesLoading;
	PullToRefreshGridView mPullRefreshSeriesGridView;
	GridView mSeriesGridView;
	ProgressBar mProgressBar;
	SeriesListAdapter mAdapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_series, container, false);      
		mActivity = (MainActivity) getActivity();
				
		mPullRefreshSeriesGridView = (PullToRefreshGridView) rootView.findViewById(R.id.gridView_series);
		mSeriesGridView = mPullRefreshSeriesGridView.getRefreshableView();
		mPullRefreshSeriesGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

 			@Override
 			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
 				loadSeries();
 			}

 			@Override
 			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
 				mPullRefreshSeriesGridView.onRefreshComplete();
 			}

        });
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_series);
		
		showSeries();

		//track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Series", params); 
				
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
	    View v = inflator.inflate(R.layout.actionbar_series, null);
	    mActivity.mActionBar.setCustomView(v);
	}
	
	public void showSeries(){
		if(AppManager.mSeriesList.size() <= 0)
			loadSeries();
		else{
			if(mAdapter == null)
				mAdapter = new SeriesListAdapter(mActivity, AppManager.mSeriesList);
			mSeriesGridView.setAdapter(mAdapter);
			mProgressBar.setVisibility(View.GONE);
		}
	}
	
	public void loadSeries(){
		if(mIsSeriesLoading) return;

		mIsSeriesLoading = true;
		if(!mPullRefreshSeriesGridView.isRefreshing())
			mProgressBar.setVisibility(View.VISIBLE);
		ParseQuery<SeriesModel> query = new ParseQuery<SeriesModel>("Series");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.orderByDescending("startDate");
		query.findInBackground(new FindCallback<SeriesModel>() {
			
			@Override
			public void done(List<SeriesModel> list, ParseException err) {
				// TODO Auto-generated method stub				
				if(list != null){
					AppManager.mSeriesList = list;
					mAdapter = new SeriesListAdapter(mActivity, list);
					mSeriesGridView.setAdapter(mAdapter);
				}
				
				mIsSeriesLoading = false;
		        mProgressBar.setVisibility(View.GONE);
		        
		        mPullRefreshSeriesGridView.onRefreshComplete();
			}
		});
	}
}
