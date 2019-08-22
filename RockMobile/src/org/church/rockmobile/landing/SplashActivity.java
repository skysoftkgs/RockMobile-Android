package org.church.rockmobile.landing;

import org.church.rockmobile.MainActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.DateUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;

public class SplashActivity extends Activity {
	
	TextView splashTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);
		splashTextView = (TextView) findViewById(R.id.copyrightTextView);
		splashTextView.setText(getResources().getString(R.string.copyright, DateUtils.getCurrentYear()));
				
		new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
            	if(AppManager.getInstance().getIsTutorialPassed(getApplicationContext())){
            		if(AppManager.getInstance().getIsLoggedIn(getApplicationContext())){
                		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
    	            	AppManager.mAnonymousLogin = false;
    		            startActivity(intent);
                	}else{
    		            Intent intent = new Intent(SplashActivity.this, LandingActivity.class);
    		            startActivity(intent);
                	}
            	
            	}else{
            		Intent intent = new Intent(SplashActivity.this, TutorialActivity.class);
		            startActivity(intent);
            	}
            	
            	
            	finish();
            }
		}, 2000);
	}
}
