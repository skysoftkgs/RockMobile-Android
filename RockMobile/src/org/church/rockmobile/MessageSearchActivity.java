package org.church.rockmobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.church.rockmobile.adapter.SeriesMessageSearchListAdapter;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.SeriesMessageModel;
import org.church.rockmobile.model.SeriesModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.parse.ParseQuery;

public class MessageSearchActivity extends Activity implements OnClickListener{

	public final String TAG = "MessageSearchActivity";
	
	Button mDoneButton;
	ListView mMessageListView;
	EditText mSearchEditText;
	
	SeriesMessageSearchListAdapter mMessageSearchListAdapter;
	boolean mIsMessageLoading;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_group_search);
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Message Search", params); 
	  		
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
				if(mMessageSearchListAdapter != null)
					mMessageSearchListAdapter.getFilter().filter(s.toString());
			}
		});
		
		mMessageListView = (ListView) findViewById(R.id.listView_groups);
		loadAllMessages();
	}
	
	public void loadAllMessages() {
		if (mIsMessageLoading)
			return;

		mIsMessageLoading = true;
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
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
				mMessageSearchListAdapter = new SeriesMessageSearchListAdapter(MessageSearchActivity.this, AppManager.mSeriesMessagesList, true);
				mMessageListView.setAdapter(mMessageSearchListAdapter);
				RockMobileApplication.getInstance().hideProgressDialog();
				mIsMessageLoading = false;
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
}
