package org.church.rockmobile.model;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Livestream")
public class LiveStreamModel extends ParseObject{
	
	public LiveStreamModel() {
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

	public String getDescription() {
		String obj = getString("description");
		return obj;
	}

	public void setDescription(String description) {
		put("description", description);
	}
		
	public String getVideoLink() {
		String obj = getString("viewLink");
		return obj;
	}

	public void setVideoLink(String videoLink) {
		put("viewLink", videoLink);
	}
	
	public Date getStartTime() {
		Date obj = (Date) get("startTime");
		return obj;
	}

	public void setStartTime(Date startTime) {
		put("startTime", startTime);
	}
	
	public Date getEndTime() {
		Date obj = (Date) get("endTime");
		return obj;
	}

	public void setEndTime(Date endTime) {
		put("endTime", endTime);
	}
	
	public ParseFile getPhotoFile() {
		return getParseFile("image");
	}

	public void setPhotoFile(ParseFile file) {
		put("image", file);
	}
}
