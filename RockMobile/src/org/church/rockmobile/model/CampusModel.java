package org.church.rockmobile.model;

import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

@ParseClassName("Campus")
public class CampusModel extends ParseObject{
	
	public CampusModel() {
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

	public String getPhoneNumber() {
		String obj = getString("phoneNumber");
		return obj;
	}

	public void setPhoneNumber(String phoneNumber) {
		put("phoneNumber", phoneNumber);
	}
	
	public String getEmail() {
		String obj = getString("email");
		return obj;
	}

	public void setEmail(String email) {
		put("email", email);
	}
	
	public String getWebsiteUrl() {
		String obj = getString("websiteUrl");
		return obj;
	}

	public void setWebsiteUrl(String websiteUrl) {
		put("websiteUrl", websiteUrl);
	}
	
	public String getAddress() {
		String obj = getString("address");
		return obj;
	}

	public void setAddress(String address) {
		put("address", address);
	}
	
	public List<String> getServiceTimes() {
		return getList("serviceTimes");
	}

	public void setServiceTimes(List<String> serviceTimes) {
		put("serviceTimes", serviceTimes);
	}
	
	public ParseGeoPoint getGeoLocation() {
		ParseGeoPoint obj = getParseGeoPoint("geoLocation");
		return obj;
	}

	public void setGeoLocation(ParseGeoPoint location) {
		put("geoLocation", location);
	}
	
	public GroupModel getGroup() {
		GroupModel obj = (GroupModel) get("group");
		return obj;
	}

	public void setGroup(GroupModel group) {
		put("group", group);
	}
}
