package org.church.rockmobile.landing;

import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.SessionManager;
import org.church.rockmobile.common.UtilityMethods;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ResetPasswordActivity extends Activity implements OnClickListener{

	public static final String TAG = "ResetPasswordActivity";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset_password);
        
        ImageButton applyButton = (ImageButton) findViewById(R.id.imageButton_apply);
        applyButton.setOnClickListener(this);
        
        ImageButton closeButton = (ImageButton) findViewById(R.id.imageButton_close);
        closeButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_apply:
				EditText emailEditText = (EditText) findViewById(R.id.editText_email);
				EditText passwordEditText = (EditText) findViewById(R.id.editText_password);
						
				if(UtilityMethods.checkIsEmptyForEditText(ResetPasswordActivity.this, emailEditText, "Email ") == false) return;
				if(UtilityMethods.checkIsEmptyForEditText(ResetPasswordActivity.this, passwordEditText, "Password ") == false) return;
				
				final String email = emailEditText.getText().toString().toLowerCase();
				final String password = passwordEditText.getText().toString();
				
				if(!UtilityMethods.isValidEmail(email)){
					RockMobileApplication.getInstance().showToast(this, "Please type valid email.", Toast.LENGTH_SHORT);
					return;
				}
				
				if(password == null || password.length() < 6){
					RockMobileApplication.getInstance().showToast(this, "Password should be 6 characters at least.", Toast.LENGTH_SHORT);
					return;
				}
				
				if(!UtilityMethods.isValidPassword(password)){
					RockMobileApplication.getInstance().showToast(this, "Password must contain one lowercase letter, one uppercase letter and a number.", Toast.LENGTH_SHORT);
					return;
				}
				
				resetPassword(email, password);
				break;
				
			case R.id.imageButton_close:
				super.onBackPressed();
				break;
				
		}
	}
	
	private void resetPassword(final String email, final String password)
	{
		UtilityMethods.hideKeyboard(ResetPasswordActivity.this);
		RockMobileApplication.getInstance().showProgressFullScreenDialog(this);
		
		ParseQuery<ParseUser> query = ParseUser.getQuery();
		query.whereEqualTo("email", UtilityMethods.getStringWithChurchId(email));
		query.findInBackground(new FindCallback<ParseUser>() {
			
			@Override
			public void done(List<ParseUser> arg0, ParseException arg1) {
				// TODO Auto-generated method stub
				if(arg0 == null || arg0.size() == 0){
					RockMobileApplication.getInstance().hideProgressDialog();
					RockMobileApplication.getInstance().showErrorDialog(ResetPasswordActivity.this,
							"There is no registered email.");
					
				}else{
					AsyncTask<Void, Void, JSONObject> loginTask = new AsyncTask<Void, Void, JSONObject>() {

						@Override
						protected JSONObject doInBackground(Void... params) {

							return SessionManager.getInstance().resetPassword(email, password);
						}

						@Override
						protected void onPostExecute(JSONObject result) {
							super.onPostExecute(result);
							
							RockMobileApplication.getInstance().hideProgressDialog();
							if(result == null){
								RockMobileApplication.getInstance().showToast(ResetPasswordActivity.this, 
										"There was a problem resetting your password", Toast.LENGTH_SHORT);
								
							}else{
								RockMobileApplication.getInstance().showToast(ResetPasswordActivity.this, 
										"Check your email to confirm.", Toast.LENGTH_SHORT);
							}
						}

					};
					
				// execute AsyncTask
				loginTask.execute(); 
				}
			}
		});
	}
}
