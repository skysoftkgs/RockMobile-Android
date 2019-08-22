package org.church.rockmobile;

import java.util.HashMap;
import java.util.Map;

import org.church.rockmobile.model.UserModel;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

public class SettingsAboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_settings_about);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Settingg About", params); 
	}
	
	public void initUI(){
//		TextView aboutTextView = (TextView)findViewById(R.id.textView_about);
//		try {
//			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//			aboutTextView.setText("Version: #" + versionName);
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		ImageButton backImageButton = (ImageButton)findViewById(R.id.imageButton_back);
		backImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingsAboutActivity.this.onBackPressed();
			}
		});
	}
}
