package org.church.rockmobile.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("Story")
public class StoryModel extends ParseObject{
	
	boolean isBookMarked;
	
	public String getChurchId() {
		String obj = getString("churchId");
		return obj;
	}

	public void setChurchId(String churchId) {
		put("churchId", churchId);
	}
	
	public StoryModel() {
		// A default constructor is required.
	}
	
	public String getTitle() {
		String obj = getString("title");
		return obj;
	}

	public void setTitle(String title) {
		put("title", title);
	}

	public String getDescription() {
		String obj = getString("content");
		return obj;
	}

	public void setDescription(String description) {
		put("content", description);
	}
	
	public boolean getIsFeatured() {
		return getBoolean("isFeatured");
	}

	public void setIsFeatured(boolean isFeatured) {
		put("isFeatured", isFeatured);
	}
	
	public boolean getIsBookMarked() {
		return isBookMarked;
	}

	public void setIsBookMarked(boolean isBookMarked) {
		this.isBookMarked = isBookMarked;
	}
	
	public ParseFile getPhotoFile() {
		return getParseFile("coverPhoto");
	}

	public void setPhotoFile(ParseFile file) {
		put("coverPhoto", file);
	}
	
	public String getImageLink() {
		String obj = (String) get("imageLink");
		return obj;
	}
	
	public void setImageLink(String imageLink) {
		put("imageLink", imageLink);
	}
	
	public String getShareLink() {
		String obj = (String) get("shareLink");
		return obj;
	}
	
	public void setShareLink(String shareLink) {
		put("shareLink", shareLink);
	}
	
	public String getAuthor() {
		String obj = (String) get("author");
		return obj;
	}
	
	public void setAuthor(String author) {
		put("author", author);
	}
	
	public Date getStoryDate() {
		Date obj = getDate("storyDate");
		return obj;
	}

	public void setStoryDate(Date date) {
		put("storyDate", date);
	}
	
	public List<String> getBookmarkUserList() {
		return getList("bookMarkUsers");
	}
	
	public void setBookmarkUserList(List<String> userList) {
		put("bookMarkUsers", userList);
	}
	
	public void addUserToBookmarkUserList(String userId){
		List<String> bookmarkUserList = getBookmarkUserList();
		if(bookmarkUserList == null)
			bookmarkUserList = new ArrayList<String>();
		
		if(!bookmarkUserList.contains(userId)){
			bookmarkUserList.add(userId);
			setBookmarkUserList(bookmarkUserList);
		}
	}
	
	public boolean removeUserFromBookmarkUserList(String userId){
		List<String> bookmarkUserList = getBookmarkUserList();
		if(bookmarkUserList == null || !bookmarkUserList.contains(userId)) return false;
		
		bookmarkUserList.remove(userId);
		setBookmarkUserList(bookmarkUserList);
		return true;
	}
	
//	public boolean containedIn(List<StoryModel> storyList){
//		if(storyList == null) return false;
//		
//		for(int i = 0;i<storyList.size();i++){
//			if(getObjectId().equals(storyList.get(i).getObjectId()))
//				return true;
//		}
//		
//		return false;
//	}
	
	public List<StoryModel> removeStoryFromList(List<StoryModel> storyList){
		if(storyList == null) return null;
		
		for(int i = 0;i<storyList.size();i++){
			if(getObjectId().equals(storyList.get(i).getObjectId()))
				storyList.remove(i);
		}
		
		return storyList;
	}
	
	public String getThumbnailImageLink() {
		String obj = (String) get("thumbImageLink");
		return obj;
	}
	
	public void setThumbnailImageLink(String thumbImageLink) {
		put("thumbImageLink", thumbImageLink);
	}
}
