package org.church.rockmobile.model;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("GroupEvent")
public class EventModel extends ParseObject{
	
	public EventModel() {
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

	public String getAddress() {
		String obj = getString("address");
		return obj;
	}

	public void setAddress(String address) {
		put("address", address);
	}
	
	public Date getEventTime() {
		Date obj = (Date) get("timeStamp");
		return obj;
	}

	public void setEventTime(Date date) {
		put("timeStamp", date);
	}
	
	public GroupModel getGroup() {
		GroupModel obj = (GroupModel) get("group");
		return obj;
	}

	public void setGroup(GroupModel group) {
		if(group == null) return;
		
		put("group", group);
	}
	
	public EventModel getEditedEvent() {
		EventModel obj = (EventModel) get("editedEvent");
		return obj;
	}

	public void setEditedEvent(EventModel event) {
		if(event == null) return;
		
		put("editedEvent", event);
	}
	
	public boolean getIsPending() {
		return getBoolean("isPending");
	}

	public void setIsPending(boolean isPending) {
		put("isPending", isPending);
	}
}
