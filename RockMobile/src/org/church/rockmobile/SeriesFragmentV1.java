package org.church.rockmobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.SeriesListAdapter;
import org.church.rockmobile.adapter.SeriesMessageListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.SeriesMessageModel;
import org.church.rockmobile.model.SeriesModel;
import org.church.rockmobile.model.UserModel;
import org.church.rockmobile.widget.SegmentedGroup;

import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.flurry.android.FlurryAgent;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SeriesFragmentV1 extends BaseFragment implements OnCheckedChangeListener, OnClickListener {

	static final int CATEGORY_SERIES = 0;
	static final int CATEGORY_ALL = 1;

	MainActivity mActivity;

	PullToRefreshGridView mPullRefreshSeriesGridView;
	PullToRefreshListView mPullRefreshAllListView;

	GridView mSeriesGridView;
	ListView mAllGridView;

	ProgressBar mProgressBar;

	SeriesListAdapter mSeriesAdapter;
	SeriesMessageListAdapter mAllAdapter;

	SegmentedGroup mCategorySegment;

	RadioButton mSeriesRadioButton;

	boolean mIsSeriesLoading;
	boolean mIsAllLoading;

	public static boolean mIsDataChanged;
	
	ImageButton mSearchImageButton;

	int mCategory;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_series_v1, container, false);
		mActivity = (MainActivity) getActivity();
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar_series);

		mCategorySegment = (SegmentedGroup) rootView.findViewById(R.id.segment_category);
		mCategorySegment.setOnCheckedChangeListener(this);
		mSeriesRadioButton = (RadioButton) rootView.findViewById(R.id.btn_series);

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

		mPullRefreshAllListView = (PullToRefreshListView) rootView.findViewById(R.id.gridView_all);
		mAllGridView = mPullRefreshAllListView.getRefreshableView();
		mPullRefreshAllListView.setOnRefreshListener(new OnRefreshListener2<SwipeMenuListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
				// TODO Auto-generated method stub
				loadAllMessages();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<SwipeMenuListView> refreshView) {
				// TODO Auto-generated method stub
				mPullRefreshAllListView.onRefreshComplete();
			}
		});

		mCategory = CATEGORY_SERIES;
		showSeries(false);

		// track event
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

	public void initActionBar() {
		// customize actionbar
		LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.actionbar_series, null);
		
		mSearchImageButton = (ImageButton) v.findViewById(R.id.imageButton_search);
	    mSearchImageButton.setOnClickListener(this);
	    
		mActivity.mActionBar.setCustomView(v);
	}

	public void showSeries(boolean isReloading) {
		if (AppManager.mSeriesList.size() <= 0 || isReloading == true)
			loadSeries();
		else {
			mProgressBar.setVisibility(View.GONE);
			mPullRefreshSeriesGridView.setVisibility(View.VISIBLE);
			mPullRefreshAllListView.setVisibility(View.INVISIBLE);
			if (mSeriesAdapter == null)
				mSeriesAdapter = new SeriesListAdapter(mActivity, AppManager.mSeriesList);
			mSeriesGridView.setAdapter(mSeriesAdapter);
		}
	}

	public void loadSeries() {
		if (mIsSeriesLoading)
			return;

		mIsSeriesLoading = true;
		if (!mPullRefreshSeriesGridView.isRefreshing()) {
			mProgressBar.setVisibility(View.VISIBLE);
			mPullRefreshSeriesGridView.setVisibility(View.VISIBLE);
			mPullRefreshAllListView.setVisibility(View.INVISIBLE);
		}
		ParseQuery<SeriesModel> query = new ParseQuery<SeriesModel>("Series");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.orderByDescending("startDate");
		query.findInBackground(new FindCallback<SeriesModel>() {

			@Override
			public void done(List<SeriesModel> list, ParseException err) {
				if (list != null) {
					AppManager.mSeriesList = list;
				}

				mSeriesAdapter = new SeriesListAdapter(mActivity, AppManager.mSeriesList);
				if (mCategory == CATEGORY_SERIES) {
					mPullRefreshSeriesGridView.setVisibility(View.VISIBLE);
					mSeriesGridView.setAdapter(mSeriesAdapter);
					mProgressBar.setVisibility(View.GONE);
				}

				mIsSeriesLoading = false;
				mIsDataChanged = false;

				mPullRefreshSeriesGridView.onRefreshComplete();
			}
		});
	}

	public void showAllMessages(boolean isReloading) {
		if (AppManager.mSeriesMessagesList.size() <= 0 || isReloading == true)
			loadAllMessages();
		else {
			mProgressBar.setVisibility(View.GONE);
			mPullRefreshSeriesGridView.setVisibility(View.INVISIBLE);
			mPullRefreshAllListView.setVisibility(View.VISIBLE);
			if (mAllAdapter == null)
				mAllAdapter = new SeriesMessageListAdapter(mActivity, AppManager.mSeriesMessagesList, true);
			mAllGridView.setAdapter(mAllAdapter);
		}
	}

	public void loadAllMessages() {
		if (mIsAllLoading)
			return;

		mIsAllLoading = true;
		if (!mPullRefreshAllListView.isRefreshing()) {
			mProgressBar.setVisibility(View.VISIBLE);
			mPullRefreshSeriesGridView.setVisibility(View.INVISIBLE);
			mPullRefreshAllListView.setVisibility(View.INVISIBLE);
		}

		ParseQuery<SeriesMessageModel> query = new ParseQuery<SeriesMessageModel>("SeriesMessage");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.orderByDescending("startDate");
		query.findInBackground(new FindCallback<SeriesMessageModel>() {

			@Override
			public void done(List<SeriesMessageModel> list, ParseException err) {
				// TODO Auto-generated method stub
				if (list != null) {
					AppManager.mSeriesMessagesList = list;
				}
				modifyMessagesTitlesAccordingToSeries(AppManager.mSeriesList, AppManager.mSeriesMessagesList);
				mAllAdapter = new SeriesMessageListAdapter(mActivity, AppManager.mSeriesMessagesList, true);
				if (mCategory == CATEGORY_ALL) {
					mPullRefreshAllListView.setVisibility(View.VISIBLE);
					mAllGridView.setAdapter(mAllAdapter);
					mProgressBar.setVisibility(View.GONE);
				}

				mIsAllLoading = false;
				mIsDataChanged = false;

				mPullRefreshAllListView.onRefreshComplete();
			}
		});
	}

	private void modifyMessagesTitlesAccordingToSeries(List<SeriesModel> seriesList,
			List<SeriesMessageModel> messagesList) {

		if (seriesList != null && seriesList.size() > 0 && messagesList != null && messagesList.size() > 0) {
			HashMap<String, SeriesModel> seriesHash = new HashMap<>();
			HashMap<String, Integer> seriestPartCountHash = new HashMap<>();
			for (SeriesModel seriesModel : seriesList) {
				if (!seriesHash.containsKey(seriesModel.getObjectId())) {
					seriesHash.put(seriesModel.getObjectId(), seriesModel);
				}
			}

			for (SeriesMessageModel seriesMessageModel : messagesList) {
				if (seriesMessageModel.getSeries() != null) {
					String seriesObjectId = seriesMessageModel.getSeries().getObjectId();
					if (seriestPartCountHash.containsKey(seriesObjectId)) {
						int count = seriestPartCountHash.get(seriesObjectId) + 1;
						seriestPartCountHash.put(seriesObjectId, count);
					} else {
						seriestPartCountHash.put(seriesObjectId, 1);
					}
				}
			}

			String lastObjId = null;
			int partNumber = 1;
			for (int i = 0; i < messagesList.size(); i++) {
				if (messagesList.get(i).getSeries() != null) {
					String seriesModelObjectId = messagesList.get(i).getSeries().getObjectId();
					if (seriesHash.containsKey(seriesModelObjectId)) {
						SeriesModel seriesModel = seriesHash.get(seriesModelObjectId);
						if (lastObjId == null || TextUtils.isEmpty(lastObjId)
								|| !seriesModel.getObjectId().equals(lastObjId)) {
							partNumber = seriestPartCountHash.get(seriesModel.getObjectId());
							lastObjId = seriesModel.getObjectId();
						}

						String title = seriesModel.getName() + " - Part " + partNumber + ", "
								+ messagesList.get(i).getTitle();
						partNumber--;
						messagesList.get(i).setTitle(title);
					}
				}
			}
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.btn_series:
			mCategory = CATEGORY_SERIES;
			showSeries(false);
			break;

		case R.id.btn_all:
			mCategory = CATEGORY_ALL;
			showAllMessages(false);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()){
			case R.id.imageButton_search:
				intent = new Intent(mActivity, MessageSearchActivity.class);
				startActivity(intent);
				break;
		}
	}
}
