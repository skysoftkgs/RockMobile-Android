package org.church.rockmobile.model;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Series")
public class SeriesModel extends ParseObject{
			
	public SeriesModel() {
		// A default constructor is required.
	}
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
	
	public String getName() {
		String obj = getString("name");
		return obj;
	}

	public void setName(String name) {
		put("name", name);
	}

	public ParseFile getPhotoFile() {
		return getParseFile("image");
	}

	public void setPhotoFile(ParseFile file) {
		put("image", file);
	}
	
	public String getImageLink() {
		String obj = getString("imageLink");
		return obj;
	}

	public void setImageLink(String imageLink) {
		put("imageLink", imageLink);
	}
	
	public Date getStartDate() {
		Date obj = getDate("startDate");
		return obj;
	}

	public void setStartDate(Date date) {
		put("startDate", date);
	}
	
	public Date getEndDate() {
		Date obj = getDate("endDate");
		return obj;
	}

	public void setEndDate(Date date) {
		put("endDate", date);
	}
}
