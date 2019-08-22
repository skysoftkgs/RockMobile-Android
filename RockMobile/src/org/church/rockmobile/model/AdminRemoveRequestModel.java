package org.church.rockmobile.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("adminRemoveRequest")
public class AdminRemoveRequestModel extends ParseObject{
	
	public AdminRemoveRequestModel() {
		// A default constructor is required.
	}
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
	
	public ParseUser getFromUser() {
		return getParseUser("fromUser");
	}

	public void setFromUser(ParseUser user) {
		put("fromUser", user);
	}
	
	public ParseUser getToUser() {
		return getParseUser("toUser");
	}

	public void setToUser(ParseUser user) {
		put("toUser", user);
	}
	
	public GroupModel getGroup() {
		GroupModel obj = (GroupModel) get("group");
		return obj;
	}

	public void setGroup(GroupModel group) {
		put("group", group);
	}
}
