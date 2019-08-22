package org.church.rockmobile.model;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("ThreadMessage")
public class ThreadMessageModel extends ParseObject{
	
	public ThreadMessageModel() {
		// A default constructor is required.
	}
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
	
	public ThreadModel getThread() {
		ThreadModel obj = (ThreadModel) get("thread");
		return obj;
	}

	public void setThread(ThreadModel thread) {
		put("thread", thread);
	}
	
	public String getMessage() {
		String obj = getString("message");
		return obj;
	}

	public void setMessage(String message) {
		put("message", message);
	}

	public Date getPostTime() {
		Date obj = (Date) get("postTime");
		return obj;
	}

	public void setPostTime(Date postTime) {
		put("postTime", postTime);
	}
	
	public UserModel getUser() {
		UserModel obj = (UserModel) get("author");
		return obj;
	}

	public void setUser(ParseUser user) {
		put("author", user);
	}
}
