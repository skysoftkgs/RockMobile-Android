package org.church.rockmobile.model;

import java.util.ArrayList;
import java.util.List;

import org.church.rockmobile.common.UtilityMethods;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import android.text.TextUtils;

@ParseClassName("Group")
public class GroupModel extends ParseObject{
	
	public GroupModel() {
		// A default constructor is required.
	}
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
	
	public boolean getIsPublic() {
		return getBoolean("isPublic");
	}

	public void setIsPublic(boolean isPublic) {
		put("isPublic", isPublic);
	}
	
	public boolean getIsOrganization() {
		return getBoolean("isOrganization");
	}

	public void setIsOrganization(boolean isOrganization) {
		put("isOrganization", isOrganization);
	}
	
	public boolean getIsFeatured() {
		return getBoolean("isFeatured");
	}
	
	public String getTitle() {
		String obj = getString("title");
		return obj != null && !TextUtils.isEmpty(obj) ? UtilityMethods.toCapitalizedString(obj) : obj;
		
	}

	public void setTitle(String title) {
		put("title", title);
	}

	public String getDescription() {
		String obj = getString("detail");
		return obj;
	}

	public void setDescription(String description) {
		put("detail", description);
	}
	
	public String getCategory() {
		String obj = getString("category");
		return obj;
	}

	public void setCategory(String category) {
		put("category", category);
	}
	
	public UserModel getUser() {
		return (UserModel) getParseUser("user");
	}

	public void setUser(UserModel user) {
		put("user", user);
	}

	public ParseFile getPhotoFile() {
		return getParseFile("picture");
	}

	public void setPhotoFile(ParseFile file) {
		put("picture", file);
	}
	
	public List<UserModel> getAdminUserList() {
		return getList("adminUsers");
	}

	public void setAdminUserList(List<UserModel> adminUsers) {
		put("adminUsers", adminUsers);
	}
	
	public List<UserModel> getJoinedUserList() {
		return getList("joinedUsers");
	}

	public void setJoinedUserList(List<UserModel> joinedUsers) {
		put("joinedUsers", joinedUsers);
	}
		
	public List<UserModel> getPendingUserList() {
		return getList("pendingUsers");
	}

	public void setPendingUserList(List<UserModel> pendingUsers) {
		put("pendingUsers", pendingUsers);
	}
	
	public List<UserModel> getGroupUserList() {
		ArrayList<UserModel> userList = new ArrayList<UserModel>();
		if(getAdminUserList() != null)
			userList.addAll(getAdminUserList());
		
		if(getJoinedUserList() != null)
			userList.addAll(getJoinedUserList());
		
		return userList;
	}
	
	public List<UserModel> getGroupUserListExceptCurrentUser() {
		List<UserModel> userList = getGroupUserList();
		return UtilityMethods.removeParseUser(userList, ParseUser.getCurrentUser());
	}
	
	public void addUserToAdminUserList(UserModel user){
		List<UserModel> adminUserList = getAdminUserList();
		if(adminUserList == null)
			adminUserList = new ArrayList<UserModel>();
		
		if(!UtilityMethods.containsParseUser(adminUserList, user)){
			adminUserList.add(user);
			setAdminUserList(adminUserList);
		}
	}
	
	public void addUserToJoinedUserList(UserModel user){
		List<UserModel> joinedUserList = getJoinedUserList();
		if(joinedUserList == null)
			joinedUserList = new ArrayList<UserModel>();
		
		if(!UtilityMethods.containsParseUser(joinedUserList, user)){
			joinedUserList.add(user);
			setJoinedUserList(joinedUserList);
		}
	}
	
	public void addUserToPendingUserList(UserModel user){
		List<UserModel> pendingUserList = getPendingUserList();
		if(pendingUserList == null)
			pendingUserList = new ArrayList<UserModel>();
		
		if(!UtilityMethods.containsParseUser(pendingUserList, user)){
			pendingUserList.add(user);
			setPendingUserList(pendingUserList);
		}
	}
		
	public boolean removeUserFromAdminUserList(UserModel user){
		List<UserModel> adminUserList = getAdminUserList();
		setAdminUserList(UtilityMethods.removeParseUser(adminUserList, user));
		return true;
	}
	
	public boolean removeUserFromJoinedUserList(UserModel user){
		List<UserModel> joinedUserList = getJoinedUserList();
		setJoinedUserList(UtilityMethods.removeParseUser(joinedUserList, user));
		return true;
	}
	
	public boolean removeUserFromPendingUserList(UserModel user){
		List<UserModel> pendingUserList = getPendingUserList();
		setPendingUserList(UtilityMethods.removeParseUser(pendingUserList, user));
		return true;
	}
	
	public Integer getSorting(){
		return Integer.parseInt(String.valueOf(getNumber("sorting") == null ? 0 : getNumber("sorting")));
	}
	
//	public List<UserModel> getGroupPushUserList(List<UserModel> userList){
//		if(userList == null) return null;
//		
//		List<UserModel> groupPushUserList = new ArrayList<UserModel>();
//		for(UserModel user: userList){
//			if(user.getNotificationGroupList() != null && user.getNotificationGroupList().contains(this.getObjectId())){
//				groupPushUserList.add(user);
//			}
//		}
//		
//		return groupPushUserList;
//	}
	
//	public List<EventModel> getaddedEventList() {
//		return getList("addedEvents");
//	}
//
//	public void setAddedEventList(List<EventModel> addedEvents) {
//		put("addedEvents", addedEvents);
//	}
//	
//	public void addEventToAddedEventList(EventModel event){
//		List<EventModel> addedEventList = getaddedEventList();
//		if(addedEventList == null)
//			addedEventList = new ArrayList<EventModel>();
//		
//		if(!addedEventList.contains(event)){
//			addedEventList.add(event);
//			setAddedEventList(addedEventList);
//		}
//	}
//	
//	public List<EventModel> getPendingEventList() {
//		return getList("pendingEvents");
//	}
//
//	public void setPendingEventList(List<EventModel> pendingEvents) {
//		put("pendingEvents", pendingEvents);
//	}
//	
//	public void addEventToPendingEventList(EventModel event){
//		List<EventModel> pendingEventList = getPendingEventList();
//		if(pendingEventList == null)
//			pendingEventList = new ArrayList<EventModel>();
//		
//		if(!pendingEventList.contains(event)){
//			pendingEventList.add(event);
//			setPendingEventList(pendingEventList);
//		}
//	}
//	
//	public boolean removeEventFromPendingEventList(EventModel event){
//		List<EventModel> pendingEventList = getPendingEventList();
//		if(pendingEventList == null || !pendingEventList.contains(event)) return false;
//		
//		pendingEventList.remove(event);
//		setPendingEventList(pendingEventList);
//		return true;
//	}
}
