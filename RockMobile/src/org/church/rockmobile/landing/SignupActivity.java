package org.church.rockmobile.landing;

import java.util.HashMap;

import org.church.rockmobile.MainActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.SessionManager;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.SignupUserModel;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;

public class SignupActivity extends Activity implements OnClickListener{

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_signup);
        
        ImageButton signupImageButton = (ImageButton) findViewById(R.id.imageButton_apply);
        signupImageButton.setOnClickListener(this);
        
        ImageButton closeImageButton = (ImageButton) findViewById(R.id.imageButton_close);
        closeImageButton.setOnClickListener(this);
        
        TextView signinTextView = (TextView) findViewById(R.id.textView_signin);
        signinTextView.setOnClickListener(this);
    }
	
	@SuppressLint("DefaultLocale")
	public void signupWithEmail(){
		EditText firstNameEditText = (EditText) findViewById(R.id.editText_firstName);
		EditText lastNameEditText = (EditText) findViewById(R.id.editText_lastName);
		EditText emailEditText = (EditText) findViewById(R.id.editText_email);
		EditText confirmEmailEditText = (EditText) findViewById(R.id.editText_confirm_email);
		EditText passwordEditText = (EditText) findViewById(R.id.editText_password);
				
		if(UtilityMethods.checkIsEmptyForEditText(SignupActivity.this, firstNameEditText, "First name ") == false) return;
		if(UtilityMethods.checkIsEmptyForEditText(SignupActivity.this, lastNameEditText, "Last name ") == false) return;
		if(UtilityMethods.checkIsEmptyForEditText(SignupActivity.this, emailEditText, "Email ") == false) return;
		if(UtilityMethods.checkIsEmptyForEditText(SignupActivity.this, confirmEmailEditText, "Confirm email ") == false) return;
		if(UtilityMethods.checkIsEmptyForEditText(SignupActivity.this, passwordEditText, "Password ") == false) return;
		
		final String firstName = firstNameEditText.getText().toString();
		final String lastName = lastNameEditText.getText().toString();
		final String email = emailEditText.getText().toString().toLowerCase();
		final String confirmEmail = confirmEmailEditText.getText().toString().toLowerCase();
		final String password = passwordEditText.getText().toString();
		
		if(!UtilityMethods.isValidEmail(email)){
			RockMobileApplication.getInstance().showToast(this, "Please type valid email.", Toast.LENGTH_SHORT);
			return;
		}
		
		if(!UtilityMethods.isValidEmail(confirmEmail)){
			RockMobileApplication.getInstance().showToast(this, "Please type valid confirm email.", Toast.LENGTH_SHORT);
			return;
		}
		
		if(email.equals(confirmEmail) == false)
		{
			RockMobileApplication.getInstance().showToast(this, "Email doesn't match.", Toast.LENGTH_SHORT);
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
		
		UtilityMethods.hideKeyboard(SignupActivity.this);
		RockMobileApplication.getInstance().showProgressDialog(SignupActivity.this, R.string.signing_up, null);
				
		SignupUserModel userModel = new SignupUserModel();
		userModel.setEmail(email);
		userModel.setPassword(password);
		userModel.setFirstName(firstName);
		userModel.setLastName(lastName);
		
		signupWithJuiceID(userModel);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_apply:
				signupWithEmail();
				break;
				
			case R.id.imageButton_close:
				super.onBackPressed();
				break;
				
			case R.id.textView_signin:
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
				break;
		}
	}
	
	private void signupWithJuiceID(final SignupUserModel signupUserModel)
	{
	    AsyncTask<Void, Void, JSONObject> signupTask = new AsyncTask<Void, Void, JSONObject>() {

				@Override
				protected JSONObject doInBackground(Void... params) {

					return SessionManager.getInstance().signUpWithUserData(signupUserModel);
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					super.onPostExecute(result);
					
					try {
						if(result == null){
							RockMobileApplication.getInstance().hideProgressDialog();
							RockMobileApplication.getInstance().showToast(SignupActivity.this, "Email already exists.", Toast.LENGTH_SHORT);
							return;
						}
						
						JSONObject profile = result.getJSONObject("profile");
						HashMap<String, Object> params = new HashMap<String, Object>();
						params.put("profile", profile);
						params.put("app_id", Constants.CHURCH_ID);
						ParseCloud.callFunctionInBackground("accessJuiceIDUser", params, new FunctionCallback<String>() {

							@Override
							public void done(String arg0, ParseException arg1) {
								// TODO Auto-generated method stub
								ParseUser.becomeInBackground(arg0, new LogInCallback() {
									
									@Override
									public void done(ParseUser user, ParseException arg1) {
										// TODO Auto-generated method stub
										RockMobileApplication.getInstance().hideProgressDialog();
										
										if(user != null){
											ParseInstallation installation = ParseInstallation.getCurrentInstallation();
											installation.put("user", user);
											installation.saveInBackground();
																
											AppManager.getInstance().setIsLoggedIn(SignupActivity.this, true);
											
											Intent intent;
											intent = new Intent(SignupActivity.this, MainActivity.class);
											AppManager.mAnonymousLogin = false;
									        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
									        startActivity(intent);
											
										}else{
											RockMobileApplication.getInstance().showToast(SignupActivity.this, arg1.getLocalizedMessage(), Toast.LENGTH_SHORT);
										}
									}
								});
							}
						});
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			};
			
		// execute AsyncTask
		signupTask.execute(); 

	}
}
