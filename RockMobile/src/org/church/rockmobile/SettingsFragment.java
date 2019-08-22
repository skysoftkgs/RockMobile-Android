package org.church.rockmobile;

import java.util.HashMap;
import java.util.Map;

import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.DateUtils;
import org.church.rockmobile.landing.LandingActivity;
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
import android.support.v4.content.IntentCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends BaseFragment implements OnClickListener {

	MainActivity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
		mActivity = (MainActivity) getActivity();

		TextView copyrightTextView = (TextView) rootView.findViewById(R.id.textView2);
		copyrightTextView.setText(mActivity.getString(R.string.copyright, DateUtils.getCurrentYear()));

		RelativeLayout profileLayout = (RelativeLayout) rootView.findViewById(R.id.layout_profile);
		profileLayout.setOnClickListener(this);

		RelativeLayout groupNotificationsLayout = (RelativeLayout) rootView
				.findViewById(R.id.layout_group_notification);
		groupNotificationsLayout.setOnClickListener(this);

		RelativeLayout aboutLayout = (RelativeLayout) rootView.findViewById(R.id.layout_about_app);
		aboutLayout.setOnClickListener(this);

		RelativeLayout policyLayout = (RelativeLayout) rootView.findViewById(R.id.layout_privacy_policy);
		policyLayout.setOnClickListener(this);

		RelativeLayout giveLayout = (RelativeLayout) rootView.findViewById(R.id.layout_give);
		giveLayout.setOnClickListener(this);

		Button signOutButton = (Button) rootView.findViewById(R.id.button_sign_out);
		signOutButton.setOnClickListener(this);

		Button reportButton = (Button) rootView.findViewById(R.id.button_report_bug);
		reportButton.setOnClickListener(this);

		Button feedbackButton = (Button) rootView.findViewById(R.id.button_feedback);
		feedbackButton.setOnClickListener(this);

		// track event
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", UserModel.getCurrentUser().getUsername());
		FlurryAgent.logEvent("Settings", params);

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
		View v = inflator.inflate(R.layout.actionbar_settings, null);

		mActivity.mActionBar.setCustomView(v);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.layout_profile:
			intent = new Intent(mActivity, SettingsProfileActivity.class);
			startActivity(intent);
			break;

		case R.id.layout_group_notification:
			if (AppManager.getInstance().getIsLoggedIn(mActivity) == false) {
				RockMobileApplication.getInstance().showToast(mActivity, "You have no any group as guest.",
						Toast.LENGTH_SHORT);
				return;
			}
			intent = new Intent(mActivity, SettingsGroupNotificationActivity.class);
			startActivity(intent);
			break;

		case R.id.layout_about_app:
			intent = new Intent(mActivity, SettingsAboutActivity.class);
			startActivity(intent);
			break;

		case R.id.layout_privacy_policy:
			intent = new Intent(mActivity, SettingsPolicyActivity.class);
			startActivity(intent);
			break;

		case R.id.layout_give:
			loadGiveUrl();
			break;

		case R.id.button_sign_out:
			AppManager.getInstance().logout(mActivity);
			intent = new Intent(mActivity, LandingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
			startActivity(intent);
			mActivity.finish();
			break;

		case R.id.button_feedback:
			sendEmail();
			break;

		case R.id.button_report_bug:
			sendEmail();
			break;
		}
	}

	public void sendEmail() {
		Intent email = new Intent(Intent.ACTION_SEND);
		email.putExtra(Intent.EXTRA_EMAIL, new String[] { Constants.FEEDBACK_EMAIL });
		email.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
		email.putExtra(Intent.EXTRA_TEXT, "Email text");
		email.setType("message/rfc822");
		startActivity(Intent.createChooser(email, "Choose an Email client :"));
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
