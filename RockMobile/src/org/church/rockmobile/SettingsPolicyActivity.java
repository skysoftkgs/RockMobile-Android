package org.church.rockmobile;

import java.util.HashMap;
import java.util.Map;

import org.church.rockmobile.model.UserModel;

import com.flurry.android.FlurryAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;

public class SettingsPolicyActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_settings_policy);
	    
	    initUI();
	    
	    //track event
  		Map<String, String> params = new HashMap<String, String>();
  		params.put("username", UserModel.getCurrentUser().getUsername());
  		FlurryAgent.logEvent("Privacy policy", params); 
	}
	
	public void initUI(){
		ImageButton backImageButton = (ImageButton)findViewById(R.id.imageButton_back);
		backImageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				SettingsPolicyActivity.this.onBackPressed();
			}
		});
	}
}
