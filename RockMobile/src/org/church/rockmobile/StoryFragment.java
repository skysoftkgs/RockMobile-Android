package org.church.rockmobile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.StoryListAdapter;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.model.StoryModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.widget.SegmentedGroup;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class StoryFragment extends BaseFragment implements OnCheckedChangeListener{

	static final int CATEGORY_NEWEST = 0;	//All
//	static final int CATEGORY_FEATURED = 1;
	static final int CATEGORY_BOOKMARK = 2;
	
	MainActivity mActivity;
	
	PullToRefreshGridView mPullRefreshNewestGridView;
//	PullToRefreshGridView mPullRefreshFeaturedGridView;
	PullToRefreshGridView mPullRefreshBookmarkGridView;
	GridView mNewestGridView;
//	GridView mFeaturedGridView;
	GridView mBookmarkGridView;
	ProgressBar mProgressBar;
	RadioButton mNewestRadioButton;
	SegmentedGroup mCategorySegment;
	
	StoryListAdapter mNewestAdapter;
//	StoryListAdapter mFeaturedAdapter;
	StoryListAdapter mBookmarkAdapter;
	
	boolean mIsNewestStoryLoading;
//	boolean mIsFeaturedStoryLoading;
	boolean mIsBookmarkStoryLoading;
	public static boolean mIsDataChanged;
	
	int mCategory;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    		Bundle savedInstanceState) {		
		View rootView = inflater.inflate(R.layout.fragment_story, container, false);      
		mActivity = (MainActivity) getActivity();
		
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_story);
     
        mCategorySegment = (SegmentedGroup) rootView.findViewById(R.id.segment_category);
        mCategorySegment.setOnCheckedChangeListener(this);
        mNewestRadioButton = (RadioButton) rootView.findViewById(R.id.btn_newest);
        
        mPullRefreshNewestGridView = (PullToRefreshGridView) rootView.findViewById(R.id.gridView_newest);
		mNewestGridView = mPullRefreshNewestGridView.getRefreshableView();
		mPullRefreshNewestGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

 			@Override
 			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
	 			loadNewestStories();
 			}

 			@Override
 			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
 				mPullRefreshNewestGridView.onRefreshComplete();
 			}

        });
          
//		mPullRefreshFeaturedGridView = (PullToRefreshGridView) rootView.findViewById(R.id.gridView_featured);
//		mFeaturedGridView = mPullRefreshFeaturedGridView.getRefreshableView();
//		mPullRefreshFeaturedGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {
//
// 			@Override
// 			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
//	 			loadFeaturedStories();
// 			}
//
// 			@Override
// 			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
// 				mPullRefreshFeaturedGridView.onRefreshComplete();
// 			}
//
//        });
		
		mPullRefreshBookmarkGridView = (PullToRefreshGridView) rootView.findViewById(R.id.gridView_bookmark);
		mBookmarkGridView = mPullRefreshBookmarkGridView.getRefreshableView();
		mPullRefreshBookmarkGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

 			@Override
 			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
	 			loadBookmarkStories();
 			}

 			@Override
 			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
 				mPullRefreshBookmarkGridView.onRefreshComplete();
 			}

        });
		
        mCategory = CATEGORY_NEWEST;
        showNewestStories(false);
        
        //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Stories", params); 
      		
        return rootView;
	}
	 
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mCategory == CATEGORY_BOOKMARK && mBookmarkAdapter != null){
			mBookmarkAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
    	initActionBar();
	}
	
	public void showNewestStories(boolean isReloading){
		if(AppManager.mNewestStoryList.size() == 0 || isReloading == true)
			loadNewestStories();
		else{
			mProgressBar.setVisibility(View.GONE);
			mPullRefreshNewestGridView.setVisibility(View.VISIBLE);
//			mPullRefreshFeaturedGridView.setVisibility(View.INVISIBLE);
			mPullRefreshBookmarkGridView.setVisibility(View.INVISIBLE);
			if(mNewestAdapter == null)
				mNewestAdapter = new StoryListAdapter(mActivity, AppManager.mNewestStoryList);
			mNewestGridView.setAdapter(mNewestAdapter);
		}
	}
	
//	public void showFeaturedStories(boolean isReloading){
//		if(AppManager.mFeaturedStoryList.size() == 0 || isReloading == true)
//			loadFeaturedStories();
//		else{
//			mProgressBar.setVisibility(View.GONE);
//			mPullRefreshNewestGridView.setVisibility(View.INVISIBLE);
//			mPullRefreshFeaturedGridView.setVisibility(View.VISIBLE);
//			mPullRefreshBookmarkGridView.setVisibility(View.INVISIBLE);
//			if(mFeaturedAdapter == null)
//				mFeaturedAdapter = new StoryListAdapter(mActivity, AppManager.mFeaturedStoryList);
//			mFeaturedGridView.setAdapter(mFeaturedAdapter);
//		}
//	}
	
	public void showBookmarkStories(boolean isReloading){
		if(AppManager.mBookmarkStoryList.size() == 0 || isReloading == true)
			loadBookmarkStories();
		else{
			mProgressBar.setVisibility(View.GONE);
			mPullRefreshNewestGridView.setVisibility(View.INVISIBLE);
//			mPullRefreshFeaturedGridView.setVisibility(View.INVISIBLE);
			mPullRefreshBookmarkGridView.setVisibility(View.VISIBLE);
			if(mBookmarkAdapter == null)
				mBookmarkAdapter = new StoryListAdapter(mActivity, AppManager.mBookmarkStoryList);
			mBookmarkGridView.setAdapter(mBookmarkAdapter);
		}
	}
	
	public void loadNewestStories(){
		if(!mPullRefreshNewestGridView.isRefreshing()){
			mProgressBar.setVisibility(View.VISIBLE);
			mPullRefreshNewestGridView.setVisibility(View.INVISIBLE);
//			mPullRefreshFeaturedGridView.setVisibility(View.INVISIBLE);
			mPullRefreshBookmarkGridView.setVisibility(View.INVISIBLE);
		}
		
		if(mIsNewestStoryLoading){
			return;
		}
		
		mIsNewestStoryLoading = true;
		ParseQuery<StoryModel> query = new ParseQuery<StoryModel>("Story");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.orderByDescending("storyDate");
		query.findInBackground(new FindCallback<StoryModel>() {
			
			@Override
			public void done(List<StoryModel> list, ParseException err) {
				// TODO Auto-generated method stub
				if(list != null)
					AppManager.mNewestStoryList = list;
					
				mNewestAdapter = new StoryListAdapter(mActivity, AppManager.mNewestStoryList);
				if(mCategory == CATEGORY_NEWEST){
					mPullRefreshNewestGridView.setVisibility(View.VISIBLE);
			        mNewestGridView.setAdapter(mNewestAdapter);
			        mProgressBar.setVisibility(View.GONE);
				}
				
		        mIsNewestStoryLoading = false;
		        mIsDataChanged = false;
		        
		        mPullRefreshNewestGridView.onRefreshComplete();
			}
		});
	}
		
//	public void loadFeaturedStories(){
//		if(!mPullRefreshFeaturedGridView.isRefreshing()){
//			mProgressBar.setVisibility(View.VISIBLE);
//			mPullRefreshNewestGridView.setVisibility(View.INVISIBLE);
//			mPullRefreshFeaturedGridView.setVisibility(View.INVISIBLE);
//			mPullRefreshBookmarkGridView.setVisibility(View.INVISIBLE);
//		}
//		
//		if(mIsFeaturedStoryLoading){
//			return;
//		}
//		
//		mIsFeaturedStoryLoading = true;
//		ParseQuery<StoryModel> query = new ParseQuery<StoryModel>("Story");
//		query.whereEqualTo("churchId", Constants.CHURCH_ID);
//		query.whereEqualTo("isFeatured", true);
//		query.orderByDescending("createdAt");
//		query.findInBackground(new FindCallback<StoryModel>() {
//			
//			@Override
//			public void done(List<StoryModel> list, ParseException err) {
//				// TODO Auto-generated method stub
//				if(list != null)
//					AppManager.mFeaturedStoryList = list;
//					
//				mFeaturedAdapter = new StoryListAdapter(mActivity, AppManager.mFeaturedStoryList);
//				if(mCategory == CATEGORY_FEATURED){
//					mPullRefreshFeaturedGridView.setVisibility(View.VISIBLE);
//			        mFeaturedGridView.setAdapter(mFeaturedAdapter);
//			        mProgressBar.setVisibility(View.GONE);
//				}
//				
//		        mIsFeaturedStoryLoading = false;
//		        mIsDataChanged = false;
//		        
//		        mPullRefreshFeaturedGridView.onRefreshComplete();
//			}
//		});
//	}
	
	public void loadBookmarkStories(){
		if(!mPullRefreshBookmarkGridView.isRefreshing()){
			mProgressBar.setVisibility(View.VISIBLE);
			mPullRefreshNewestGridView.setVisibility(View.INVISIBLE);
//			mPullRefreshFeaturedGridView.setVisibility(View.INVISIBLE);
			mPullRefreshBookmarkGridView.setVisibility(View.INVISIBLE);
		}
		
		if(mIsBookmarkStoryLoading){	
			return;
		}
				
		mIsBookmarkStoryLoading = true;
		ParseQuery<StoryModel> query = new ParseQuery<StoryModel>("Story");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereContainsAll("bookMarkUsers", Arrays.asList(ParseUser.getCurrentUser().getObjectId()));
		query.orderByDescending("createdAt");
		query.findInBackground(new FindCallback<StoryModel>() {
			
			@Override
			public void done(List<StoryModel> list, ParseException err) {
				// TODO Auto-generated method stub
				if(list != null)
					AppManager.mBookmarkStoryList = list;
					
				mBookmarkAdapter = new StoryListAdapter(mActivity, AppManager.mBookmarkStoryList);
				if(mCategory == CATEGORY_BOOKMARK){
					mPullRefreshBookmarkGridView.setVisibility(View.VISIBLE);
			        mBookmarkGridView.setAdapter(mBookmarkAdapter);
			        mProgressBar.setVisibility(View.GONE);
				}
				
		        mIsBookmarkStoryLoading = false;
		        mIsDataChanged = false;
		        
		        mPullRefreshBookmarkGridView.onRefreshComplete();
			}
		});
	}
	
	public void initActionBar(){
		//customize actionbar
	    LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = inflator.inflate(R.layout.actionbar_story, null);
	    
	    mActivity.mActionBar.setCustomView(v);	  
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		// TODO Auto-generated method stub
		switch (checkedId) {
			case R.id.btn_newest:
				mCategory = CATEGORY_NEWEST;
				showNewestStories(false);
				break;
				
//			case R.id.btn_featured:
//				mCategory = CATEGORY_FEATURED;
//				showFeaturedStories(false);
//				break;
				
			case R.id.btn_bookmarks:
				mCategory = CATEGORY_BOOKMARK;
				showBookmarkStories(false);
				break;
		}
	}
}
