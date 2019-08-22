package org.church.rockmobile.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.church.rockmobile.common.UtilityMethods;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseUser;

import android.text.TextUtils;

@ParseClassName("_User")
public class UserModel extends ParseUser{
	
	public UserModel() {
		// A default constructor is required.
	}
	
	public String getRealUsername(){
		String name = getFirstName() + " " + getLastName();
		return name != null && !TextUtils.isEmpty(name) ? UtilityMethods.toCapitalizedString(name.trim()) : name;
	}
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
	
	public String getFirstName() {
		String obj = getString("firstName");
		return obj;
	}

	public void setFirstName(String firstName) {
		put("firstName", firstName);
	}
	
	public String getLastName() {
		String obj = getString("lastName");
		return obj;
	}

	public void setLastName(String lastName) {
		put("lastName", lastName);
	}
	
	public Date getLastRequestVisitTime() {
		Date obj = getDate("lastRequestVisitTime");
		return obj;
	}

	public void setLastRequestVisitTime(Date lastRequestVisitTime) {
		put("lastRequestVisitTime", lastRequestVisitTime);
	}
	
	public boolean getIsSuperAdmin() {
		boolean obj = getBoolean("isSuperAdmin");
		return obj;
	}

	public void setIsSuperAdmin(boolean isSuperAdmin) {
		put("isSuperAdmin", isSuperAdmin);
	}
	
	public ParseFile getAvatarFile() {
		return getParseFile("avatar");
	}

	public void setAvatarFile(ParseFile file) {
		put("avatar", file);
	}
	
	public List<String> getNotificationOffGroupList() {
		List<String> notificationOffGroupList = getList("notificationOffGroups");
		if(notificationOffGroupList == null)
			notificationOffGroupList = new ArrayList<String>();
		return notificationOffGroupList;
	}

	public void setNotificationOffGroupList(List<String> notificationGroupList) {
		put("notificationOffGroups", notificationGroupList);
	}
			
	public void addNotificationOffGroup(String groupId){
		List<String> notificationOffGroupList = getNotificationOffGroupList();		
		if(!notificationOffGroupList.contains(groupId)){
			notificationOffGroupList.add(groupId);
			setNotificationOffGroupList(notificationOffGroupList);
		}
	}
		
	public boolean removeNotificationOffGroup(String groupId){
		List<String> notificationOffGroupList = getNotificationOffGroupList();
		if(notificationOffGroupList.contains(groupId)){
			notificationOffGroupList.remove(groupId);
			setNotificationOffGroupList(notificationOffGroupList);
		}
		return true;
	}
}
