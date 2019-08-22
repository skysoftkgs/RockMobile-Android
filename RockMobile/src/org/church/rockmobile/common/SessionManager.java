package org.church.rockmobile.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.church.rockmobile.model.SignupUserModel;
import org.json.JSONObject;

public class SessionManager {
	
	public static String mWebApiBaseUrl = "https://juiceid.net/api/public/";
	public static String mWebApiLoginUrl = "login";
	public static String mWebApiSignupUrl = "signup";
	public static String mWebApiResetPasswordUrl = "resetPassword";
	
	static volatile SessionManager mInstance;
	
	public static SessionManager getInstance(){
		if(mInstance == null)
			mInstance = new SessionManager();
		
		return mInstance;
	}
	
	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	public JSONObject loginWithUsername(String username, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = JSONParser.getJSONFromUrl(mWebApiBaseUrl + mWebApiLoginUrl, params);
		return json;
	}
	
	/**
	 * function make signup Request
	 * @param email
	 * @param password
	 * @param firstName
	 * @param lastName
	 * */
	public JSONObject signUpWithUserData(SignupUserModel signupUserModel){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", signupUserModel.getEmail()));
		params.add(new BasicNameValuePair("password", signupUserModel.getPassword()));
		params.add(new BasicNameValuePair("firstName", signupUserModel.getFirstName()));
		params.add(new BasicNameValuePair("lastName", signupUserModel.getLastName()));
		params.add(new BasicNameValuePair("email", signupUserModel.getEmail()));
		JSONObject json = JSONParser.getJSONFromUrl(mWebApiBaseUrl + mWebApiSignupUrl, params);
		return json;
	}
	
	/**
	 * function make Reset Password
	 * @param email
	 * @param password
	 * */
	public JSONObject resetPassword(String email, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", email));
		params.add(new BasicNameValuePair("password", password));
		JSONObject json = JSONParser.getJSONFromUrl(mWebApiBaseUrl + mWebApiResetPasswordUrl, params);
		return json;
	}
}
