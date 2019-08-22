package org.church.rockmobile.model;

import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Feed")
public class RequestModel extends ParseObject{

	public RequestModel() {
		// A default constructor is required.
	}
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
	
	public GroupModel getGroup() {
		return (GroupModel) get("group");
	}

	public void setGroup(GroupModel group) {
		put("group", group);
	}
	
	public EventModel getEvent() {
		return (EventModel) get("event");
	}

	public void setEvent(EventModel event) {
		put("event", event);
	}
	
	public EventModel getOldEvent() {
		return (EventModel) get("oldEvent");
	}

	public void setOldEvent(EventModel event) {
		put("oldEvent", event);
	}
	
	public ThreadModel getThread() {
		return (ThreadModel) get("thread");
	}

	public void setThread(ThreadModel thread) {
		put("thread", thread);
	}
	
	public ThreadMessageModel getThreadMessage() {
		return (ThreadMessageModel) get("threadMessage");
	}

	public void setThreadMessage(ThreadMessageModel threadMessage) {
		put("threadMessage", threadMessage);
	}
	
	public SeriesMessageModel getSeriesMessage() {
		return (SeriesMessageModel) get("seriesMessage");
	}

	public void setSeriesMessage(SeriesMessageModel seriesMessage) {
		put("seriesMessage", seriesMessage);
	}
	
	public LiveStreamModel getLiveStream() {
		return (LiveStreamModel) get("livestream");
	}

	public void setLiveStream(LiveStreamModel livestream) {
		put("livestream", livestream);
	}
	
	public UserModel getFromUser() {
		return (UserModel) getParseUser("fromUser");
	}

	public void setFromUser(UserModel user) {
		put("fromUser", user);
	}
	
	public String getType() {
		return getString("type");
	}
	
	public void setType(String type){
		put("type", type);
	}
	
	public String getGroupInviteManager() {
		return getString("groupInviteManager");
	}
	
	public void setGroupInviteManager(String groupInviteManager){
		put("groupInviteManager", groupInviteManager);
	}
	
	public String getRequestStatus() {
		return getString("requestStatus");
	}
	
	public void setRequestStatus(String status){
		put("requestStatus", status);
	}
	
	public List<UserModel> getToUserList() {
		return getList("toUsers");
	}

	public void setToUserList(List<UserModel> toUsers) {
		if(toUsers == null) return;
		
		put("toUsers", toUsers);
	}
	
	public UserModel getGroupInvitationUser() {
		return (UserModel) getParseUser("groupInvitationUser");
	}

	public void setGroupInvitationUser(ParseUser user) {
		put("groupInvitationUser", user);
	}
	
	
	//for story
	public StoryModel getStory() {
		return (StoryModel) getParseObject("story");
	}

	public void setStory(StoryModel story) {
		put("story", story);
	}
		
//	public List<ParseUser> getAdminUserListForAdminApproval() {
//		return getList("adminUsersForInvitationApproval");
//	}
//
//	public void setAdminUserListForAdminApproval(List<ParseUser> userList) {
//		put("adminUsersForInvitationApproval", userList);
//	}
}
