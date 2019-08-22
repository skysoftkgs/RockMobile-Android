package org.church.rockmobile.model;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Thread")
public class ThreadModel extends ParseObject{
	
	public ThreadModel() {
		// A default constructor is required.
	}
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
	
	public String getTitle() {
		String obj = getString("title");
		return obj;
	}

	public void setTitle(String title) {
		put("title", title);
	}
	
	public GroupModel getGroup() {
		GroupModel obj = (GroupModel) get("group");
		return obj;
	}

	public void setGroup(GroupModel group) {
		put("group", group);
	}
	
	public UserModel getUser() {
		UserModel obj = (UserModel) get("creator");
		return obj;
	}

	public void setUser(ParseUser user) {
		put("creator", user);
	}
	
	public boolean getIsMessageEnabled() {
		return getBoolean("isMessageEnabled");
	}

	public void setIsMessageEnabled(boolean enabled) {
		put("isMessageEnabled", enabled);
	}
	
	public String getStartUser() {
		String obj = getString("startUser");
		return obj;
	}

	public void setStartUser(String startUser) {
		put("startUser", startUser);
	}
	
	public String getLastUser() {
		String obj = getString("lastUser");
		return obj;
	}

	public void setLastUser(String lastUser) {
		put("lastUser", lastUser);
	}
	
	public int getMessageCount() {
		return getInt("messageCount");
	}
	
	public void setMessageCount(int count) {
		put("messageCount", count);
	}
}
