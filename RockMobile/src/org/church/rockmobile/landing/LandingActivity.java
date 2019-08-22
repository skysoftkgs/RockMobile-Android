package org.church.rockmobile.landing;

import org.church.rockmobile.MainActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.UtilityMethods;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LandingActivity extends Activity implements OnClickListener{

	public static final String TAG = "LandingActivity";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_landing);
        
        Button loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(this);
        
        Button signupButton = (Button) findViewById(R.id.button_signup);
        signupButton.setOnClickListener(this);
        
        Button skipButton = (Button) findViewById(R.id.button_skip);
        skipButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(v.getId()){
			case R.id.button_login:
				intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				break;
				
			case R.id.button_signup:
				intent = new Intent(this, SignupActivity.class);
				startActivity(intent);
				break;
				
			case R.id.button_skip:
				loginWithGuest();
				break;
		}
	}
		
	public void loginWithGuest(){
		UtilityMethods.hideKeyboard(LandingActivity.this);
		RockMobileApplication.getInstance().showProgressDialog(LandingActivity.this, R.string.logging_in, null);
		
		ParseAnonymousUtils.logIn(new LogInCallback() {
			@Override
			public void done(ParseUser user, ParseException e) {
				RockMobileApplication.getInstance().hideProgressDialog();
				
				if (e != null) {
					Log.d(TAG, "Anonymous login failed.");
					RockMobileApplication.getInstance().showToast(LandingActivity.this, "Can't login now.", Toast.LENGTH_SHORT);
					
			    } else {
			    	Log.d(TAG, "Anonymous user logged in.");
			    	Intent intent;	
		            intent = new Intent(LandingActivity.this, MainActivity.class);
		            AppManager.mAnonymousLogin = true;
			        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
			        startActivity(intent);
			    }
			}
		});
	}
}
