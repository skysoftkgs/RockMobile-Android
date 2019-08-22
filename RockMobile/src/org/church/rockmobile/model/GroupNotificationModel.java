package org.church.rockmobile.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("GroupNotification")
public class GroupNotificationModel extends ParseObject{
	
	public GroupNotificationModel() {
		// A default constructor is required.
	}
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
		
	public String getGroupId() {
		String obj = getString("groupId");
		return obj;
	}

	public void setGroupId(String groupId) {
		put("groupId", groupId);
	}
	
	public String getUserId() {
		String obj = getString("userId");
		return obj;
	}

	public void setUserId(String userId) {
		put("userId", userId);
	}
	
	public int getNotificationCount() {
		return getInt("notificationCount");
	}

	public void setNotificationCount(int count) {
		put("notificationCount", count);
	}
}
