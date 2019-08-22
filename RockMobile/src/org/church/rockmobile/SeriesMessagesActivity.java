package org.church.rockmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.church.rockmobile.adapter.SeriesMessageListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.SeriesMessageModel;
import org.church.rockmobile.model.SeriesModel;
import org.church.rockmobile.model.UserModel;

import com.flurry.android.FlurryAgent;
import com.newrelic.com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

public class SeriesMessagesActivity extends Activity implements OnClickListener {

	public final String TAG = "SeriesMessagesActivity";

	ImageButton mBackImageButton;
	ImageView mSeriesImageView;
	ListView mListView;

	public static SeriesModel mSeries;
	public SeriesMessageListAdapter mAdapter;
	List<SeriesMessageModel> mSeriesMessages = new ArrayList<SeriesMessageModel>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_series_message);
		if (savedInstanceState != null) {
			// mSeriesMessages = new
			// Gson().fromJson(savedInstanceState.getString(TAG), new
			// TypeToken<List<SeriesMessageModel>>(){}.getType());
			mSeries = new Gson().fromJson(savedInstanceState.getString(TAG), SeriesModel.class);
		}

		initUI();

		// track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Series Message", params);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageButton_back:
			super.onBackPressed();
			break;
		}
	}

	public void initUI() {
		mBackImageButton = (ImageButton) findViewById(R.id.imageButton_back);
		mBackImageButton.setOnClickListener(this);

		mSeriesImageView = (ImageView) findViewById(R.id.imageView_series);
		if (mSeries != null && mSeries.getPhotoFile() != null) {
			// display cover photo image
			AppManager.getInstance().mImageLoader.displayImage(mSeries.getPhotoFile().getUrl(), mSeriesImageView,
					AppManager.getInstance().options, null, null);

		} else if (mSeries != null && mSeries.getImageLink() != null) {
			// display cover photo image
			AppManager.getInstance().mImageLoader.displayImage(mSeries.getImageLink(), mSeriesImageView,
					AppManager.getInstance().options, null, null);

		} else {
			mSeriesImageView.setImageBitmap(null);
		}

		mListView = (ListView) findViewById(R.id.listView_seriesMessage);
		loadSeriesMessages();
	}

	public void loadSeriesMessages() {

		RockMobileApplication.getInstance().showProgressFullScreenDialog(SeriesMessagesActivity.this);
		mSeriesMessages = new ArrayList<SeriesMessageModel>();

		ParseQuery<SeriesMessageModel> query = new ParseQuery<SeriesMessageModel>("SeriesMessage");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.whereEqualTo("series", mSeries);
		query.orderByAscending("startDate");
		query.findInBackground(new FindCallback<SeriesMessageModel>() {

			@Override
			public void done(List<SeriesMessageModel> list, ParseException err) {
				// TODO Auto-generated method stub
				if (list != null) {
					mSeriesMessages.addAll(list);
					mAdapter = new SeriesMessageListAdapter(SeriesMessagesActivity.this, mSeriesMessages);
					mListView.setAdapter(mAdapter);
				}

				RockMobileApplication.getInstance().hideProgressDialog();
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(TAG, new Gson().toJson(mSeries));
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (mSeries == null) {
			mSeries = new Gson().fromJson(savedInstanceState.getString(TAG), SeriesModel.class);
		}
	}

}
