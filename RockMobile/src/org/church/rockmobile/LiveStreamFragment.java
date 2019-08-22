package org.church.rockmobile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.LiveStreamListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.LiveStreamModel;
import org.church.rockmobile.model.UserModel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

public class LiveStreamFragment extends BaseFragment{

	MainActivity mActivity;
	LinearLayout mHeaderLayout;
	RelativeLayout mInfoLayout;
	TextView mInfoTextView;
	ListView mListView;
	TextView mTitleTextView;
	TextView mDescriptionTextView;
	TextView mRemainTimeTextView;
	Button mPlayButton;
	ImageView mImageView;
	ProgressBar mProgressBar;
	
	boolean mIsLiveStreamLoading;
	LiveStreamModel mCurrentLiveStream;
	

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_live_stream, container, false);      
		mActivity = (MainActivity) getActivity();
						
		mListView = (ListView) rootView.findViewById(R.id.listView_liveStream);
		View header = View.inflate(mActivity, R.layout.header_live_stream, null);
		mListView.addHeaderView(header);

		mHeaderLayout = (LinearLayout) header.findViewById(R.id.layout_header);
		mInfoLayout = (RelativeLayout) header.findViewById(R.id.layout_availableLiveStream);
		mInfoTextView = (TextView) header.findViewById(R.id.textView_noLiveStream);
		mImageView = (ImageView) header.findViewById(R.id.imageView_livestream);
		
		mTitleTextView = (TextView) rootView.findViewById(R.id.textView_title);
		mDescriptionTextView = (TextView) rootView.findViewById(R.id.textView_description);
		mRemainTimeTextView = (TextView) rootView.findViewById(R.id.textView_remainTime);
		mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		
		mPlayButton = (Button) header.findViewById(R.id.button_play);
		mPlayButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(mCurrentLiveStream != null){
					Intent intent = new Intent(mActivity, FullScreenVideoActivity.class);
					intent.putExtra(Constants.BUNDLE_VIDEO_URL, mCurrentLiveStream.getVideoLink());
					startActivity(intent);
				}
			}
		});
		
		loadLiveStream();
		
		//track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Livestream", params);
		
        return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		super.onCreateOptionsMenu(menu, inflater);
    	initActionBar();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
//		if(mCurrentLiveStream != null && mCurrentLiveStream.getVideoLink() != null){
//			mWebView.loadUrl(mCurrentLiveStream.getVideoLink());
//		}
	}
	
	public void initActionBar(){
		//customize actionbar
	    LayoutInflater inflator = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View v = inflator.inflate(R.layout.actionbar_livestream, null);

	    mActivity.mActionBar.setCustomView(v);
	}
	
	public void loadLiveStream(){
		if(mIsLiveStreamLoading) return;

		mIsLiveStreamLoading = true;
		
		mListView.setVisibility(View.INVISIBLE);
		final Date today = new Date();
		ParseQuery<LiveStreamModel> query = new ParseQuery<LiveStreamModel>("Livestream");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereGreaterThanOrEqualTo("endTime", today);
//		query.whereGreaterThanOrEqualTo("startTime", today);
		query.orderByAscending("startTime");
		query.findInBackground(new FindCallback<LiveStreamModel>() {
			
			@Override
			public void done(List<LiveStreamModel> list, ParseException err) {
				// TODO Auto-generated method stub
				mHeaderLayout.setVisibility(View.GONE);
				mInfoLayout.setVisibility(View.INVISIBLE);
				mInfoTextView.setVisibility(View.VISIBLE);
				mListView.setAdapter(null);
				
				if(list != null && list.size()>0){
										
					if(list.get(0).getStartTime().compareTo(today) < 0){		//if livestream is available
						mHeaderLayout.setVisibility(View.VISIBLE);
						mInfoLayout.setVisibility(View.VISIBLE);
						mInfoTextView.setVisibility(View.INVISIBLE);
						
						mCurrentLiveStream = list.get(0);
						if(mCurrentLiveStream.getVideoLink() == null || mCurrentLiveStream.getVideoLink().isEmpty()){
							mPlayButton.setVisibility(View.INVISIBLE);
							
						}else{
							mPlayButton.setVisibility(View.VISIBLE);
						}
						
						if(mCurrentLiveStream.getPhotoFile() != null){
							AppManager.getInstance().mImageLoader.displayImage(mCurrentLiveStream.getPhotoFile().getUrl(),
									mImageView, AppManager.getInstance().options, null, null);
						}
						mTitleTextView.setText(mCurrentLiveStream.getTitle());
						
						long diff = mCurrentLiveStream.getEndTime().getTime() - today.getTime();
						long seconds = diff / 1000;
						long minutes = (seconds / 60) %60;
						long hours = seconds / 3600;
						mRemainTimeTextView.setText(String.format("%dh %dm", hours, minutes));
						mDescriptionTextView.setText(mCurrentLiveStream.getDescription());
						
						list.remove(0);
					}
										
				    LiveStreamListAdapter adapter = new LiveStreamListAdapter(mActivity, list);
				    mListView.setAdapter(adapter);
				}
				
				mIsLiveStreamLoading = false;
				mProgressBar.setVisibility(View.INVISIBLE);
		        mListView.setVisibility(View.VISIBLE);
			}
		});
	}
		
}