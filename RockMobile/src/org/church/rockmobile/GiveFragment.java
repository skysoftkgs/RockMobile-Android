package org.church.rockmobile;

import java.util.HashMap;
import java.util.Map;

import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.UserModel;

import com.flurry.android.FlurryAgent;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class GiveFragment extends BaseFragment {

	MainActivity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_give, container, false);
		Button buttonGive = (Button) rootView.findViewById(R.id.giveBtn);
		buttonGive.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				loadGiveUrl();
			}
		});

		mActivity = (MainActivity) getActivity();

		// track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Give", params);

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
		View v = inflator.inflate(R.layout.actionbar_give, null);
		mActivity.mActionBar.setCustomView(v);
	}

	public void loadGiveUrl() {
		RockMobileApplication.getInstance().showProgressFullScreenDialog(mActivity);
		ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("ExternalLink");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.getFirstInBackground(new GetCallback<ParseObject>() {

			@Override
			public void done(ParseObject arg0, ParseException arg1) {
				// TODO Auto-generated method stub
				RockMobileApplication.getInstance().hideProgressDialog();

				if (arg0 != null && arg0.getString("url") != null) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(arg0.getString("url")));
					startActivity(intent);
				}
			}
		});
	}
}
