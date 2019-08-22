package org.church.rockmobile.landing;

import java.util.HashMap;

import org.church.rockmobile.MainActivity;
import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.SessionManager;
import org.church.rockmobile.common.UtilityMethods;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class LoginActivity extends Activity implements OnClickListener{

	public static final String TAG = "LoginActivity";
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        
        ImageButton loginButton = (ImageButton) findViewById(R.id.imageButton_apply);
        loginButton.setOnClickListener(this);
        
        ImageButton closeButton = (ImageButton) findViewById(R.id.imageButton_close);
        closeButton.setOnClickListener(this);
        
        Button forgotPasswordButton = (Button) findViewById(R.id.button_forgotPassword);
        forgotPasswordButton.setOnClickListener(this);
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.imageButton_apply:
				loginWithEmail();
				break;
				
			case R.id.imageButton_close:
				super.onBackPressed();
				break;
				
			case R.id.button_forgotPassword:
				Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
				startActivity(intent);
				break;
		}
	}
	
	public void loginWithEmail(){
		EditText emailEditText = (EditText) findViewById(R.id.editText_email);
		final EditText passwordEditText = (EditText) findViewById(R.id.editText_password);
		if(UtilityMethods.checkIsEmptyForEditText(LoginActivity.this, emailEditText, "Email ") == false) return;
		if(UtilityMethods.checkIsEmptyForEditText(LoginActivity.this, passwordEditText, "Password ") == false) return;
		
		UtilityMethods.hideKeyboard(LoginActivity.this);
		RockMobileApplication.getInstance().showProgressDialog(LoginActivity.this, R.string.logging_in, null);
		
		loginWithJuiceID(emailEditText.getText().toString(), passwordEditText.getText().toString());
	}
	
	private void loginWithJuiceID(final String email, final String password)
	{
	    AsyncTask<Void, Void, JSONObject> loginTask = new AsyncTask<Void, Void, JSONObject>() {

				@Override
				protected JSONObject doInBackground(Void... params) {

					return SessionManager.getInstance().loginWithUsername(email, password);
				}

				@Override
				protected void onPostExecute(JSONObject result) {
					super.onPostExecute(result);
					
					try {
						if(result == null){
							RockMobileApplication.getInstance().hideProgressDialog();
							RockMobileApplication.getInstance().showToast(LoginActivity.this, "Wrong email or password", Toast.LENGTH_SHORT);
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
											
											AppManager.getInstance().setIsLoggedIn(LoginActivity.this, true);
								            					            
								            Intent intent;	
							            	intent = new Intent(LoginActivity.this, MainActivity.class);
							            	AppManager.mAnonymousLogin = false;
								            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
								            startActivity(intent);

										}else{
											RockMobileApplication.getInstance().showToast(LoginActivity.this, R.string.wrong_username_or_password, Toast.LENGTH_SHORT);
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
		loginTask.execute(); 

	}
	
	public void showResetPasswordDialog(){
		AlertDialog.Builder customField = new AlertDialog.Builder(this);
    	customField.setTitle("Reset password");
        LayoutInflater customInflater = getLayoutInflater();
        final View customView=customInflater.inflate(R.layout.dialog_reset_password, null);
        final EditText emailEditText = (EditText) customView.findViewById(R.id.editText_email);
        final String email = emailEditText.getText().toString();
        customField.setView(customView)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   //hide keyboard
                	   InputMethodManager imm = (InputMethodManager)getSystemService(
                			      Context.INPUT_METHOD_SERVICE);
                	   imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
                	   
                	   RockMobileApplication.getInstance().showProgressFullScreenDialog(LoginActivity.this);
                	   ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                		   
                		   @Override
                		   public void done(ParseException err) {
                			   // TODO Auto-generated method stub
                			   RockMobileApplication.getInstance().hideProgressDialog();
                			   if(err == null){
                				   RockMobileApplication.getInstance().showToast(LoginActivity.this, 
                						   getResources().getString(R.string.confirm_your_email), Toast.LENGTH_SHORT);
                				   
                			   }else{
                				   RockMobileApplication.getInstance().showErrorDialog(LoginActivity.this, 
                						   getResources().getString(R.string.no_found_email) + " " + email);
                			   }
                		   }
                	   });
                   }

               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       //LoginDialogFragment.this.getDialog().cancel();
                	   //hide keyboard
                	   InputMethodManager imm = (InputMethodManager)getSystemService(
             			      Context.INPUT_METHOD_SERVICE);
                	   imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
                   }
               });         
        AlertDialog alert = customField.create();
        alert.show();	
	}
}
