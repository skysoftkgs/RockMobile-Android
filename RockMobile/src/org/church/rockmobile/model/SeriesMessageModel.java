package org.church.rockmobile.model;

import java.util.Date;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("SeriesMessage")
public class SeriesMessageModel extends ParseObject {

	public SeriesMessageModel() {
		// A default constructor is required.
	}

	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}

	public SeriesModel getSeries() {
		if (get("series") instanceof SeriesModel) {
			SeriesModel obj = (SeriesModel) get("series");
			return obj;
		}
		return null;
	}

	public void setSeries(SeriesModel series) {
		put("series", series);
	}

	public Date getStartDate() {
		Date obj = (Date) get("startDate");
		return obj;
	}

	public void setStartDate(Date startDate) {
		put("startDate", startDate);
	}

	public Date getEndDate() {
		Date obj = (Date) get("endDate");
		return obj;
	}

	public void setEndDate(Date endDate) {
		put("endDate", endDate);
	}

	public String getTitle() {
		String obj = getString("title");
		return obj;
	}

	public void setTitle(String title) {
		put("title", title);
	}

	public String getSpeakerName() {
		String obj = getString("speakerName");
		return obj;
	}

	public void setSpeakerName(String speakerName) {
		put("speakerName", speakerName);
	}

	public String getMainPassage() {
		String obj = getString("mainPassage");
		return obj;
	}

	public void setMainPassage(String mainPassage) {
		put("mainPassage", mainPassage);
	}

	public String getRecap() {
		String obj = getString("recap");
		return obj;
	}

	public void setRecap(String recap) {
		put("recap", recap);
	}

	public ParseFile getPhotoFile() {
		return getParseFile("image");
	}

	public void setPhotoFile(ParseFile file) {
		put("image", file);
	}

	public ParseFile getAudioFile() {
		return getParseFile("audio");
	}

	public void setAudioFile(ParseFile file) {
		put("audio", file);
	}

	public ParseFile getVideoFile() {
		return getParseFile("video");
	}

	public void setVideoFile(ParseFile file) {
		put("video", file);
	}

	public String getAudioLink() {
		String obj = getString("audioLink");
		return obj;
	}

	public void setAudioLink(String audioLink) {
		put("audioLink", audioLink);
	}

	public String getVideoLink() {
		String obj = getString("videoLink");
		return obj;
	}

	public void setVideoLink(String videoLink) {
		put("videoLink", videoLink);
	}

	public String getImageLink() {
		String obj = getString("imageLink");
		return obj;
	}

	public void setImageLink(String imageLink) {
		put("imageLink", imageLink);
	}

	public String getShareLink() {
		String obj = getString("shareLink");
		return obj;
	}

	public void setShareLink(String shareLink) {
		put("shareLink", shareLink);
	}
}
