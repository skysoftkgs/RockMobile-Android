package org.church.rockmobile.service;

import java.util.HashMap;

import org.church.rockmobile.FeedFragment;
import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.UtilityMethods;
import org.church.rockmobile.model.EventModel;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.ThreadMessageModel;
import org.church.rockmobile.model.ThreadModel;
import org.church.rockmobile.model.UserModel;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;


public class PushNotificationService {
	static volatile PushNotificationService mInstance;
	
	public interface ParseFunctionCallback{
		void done(String result, ParseException err);
	}
	public PushNotificationService(){
		
	}
	
	public static PushNotificationService getInstance(){
		if(mInstance == null)
			mInstance = new PushNotificationService();
		
		return mInstance;
	}
	
	//user asks to join a group
	public void joinGroup(final GroupModel group, final UserModel user, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_id", group.getObjectId());
		params.put("user_id", user.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("joinGroup", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(err == null){
					if(UtilityMethods.containsParseUser(group.getJoinedUserList(), user) == false){
						group.addUserToJoinedUserList(user);
					}
				}
				
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	//user asks to join a group
	public void requestJoinGroup(final GroupModel group, final UserModel user, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_id", group.getObjectId());
		params.put("user_id", user.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("requestJoinGroup", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(err == null){
					if(UtilityMethods.containsParseUser(group.getPendingUserList(), user) == false){
						group.addUserToPendingUserList(user);
					}
				}
				
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	//user invites another user to a group
	public void inviteToGroup(GroupModel group, ParseUser user, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_id", group.getObjectId());
		params.put("user_id", user.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("inviteToGroup", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException arg1) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, arg1);
			}
		});
	}
	
	//user cancels request to join a group
	public void cancelJoinGroup(GroupModel group, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_id", group.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("cancelJoinGroup", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException arg1) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, arg1);
			}
		});
	}
	
	//admin cancels invitation to user
	public void cancelInviteUserToGroup(GroupModel group, ParseUser user, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_id", group.getObjectId());
		params.put("user_id", user.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("cancelInviteUserToGroup", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException arg1) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, arg1);
			}
		});
	}
	
	/*
	 * **************************** Request Methods ****************************
	 */
	
	public void acceptRequest(final RequestModel request, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("request_id", request.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("acceptRequest", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(err == null){
					AppManager.mRequestsList.remove(request);
					FeedFragment.mIsDataChanged = true;

					if(request.getType().equals(Constants.REQUEST_TYPE_GROUP_JOIN)){
						for (GroupModel group1 : AppManager.mMyGroupsList) {
							if (request.getGroup().getObjectId().equals(
									group1.getObjectId())) {
								group1.fetchInBackground(null);
							}
						}
						
					}else if(request.getType().equals(Constants.REQUEST_TYPE_GROUP_INVITATION)){
						//remove same invitation requests
						for (RequestModel aRequest : AppManager.mRequestsList){
							if(aRequest.getType().equals(Constants.REQUEST_TYPE_GROUP_INVITATION) && 
									aRequest.getGroup().getObjectId().equals(request.getGroup().getObjectId())){
								AppManager.mRequestsList.remove(aRequest);
							}
						}
						
						AppManager.mMyGroupsList.add(0, request.getGroup());
						request.getGroup().fetchInBackground(null);
						AppManager.mAllGroupsList.remove(request.getGroup());
					
					}else if(request.getType().equals(Constants.REQUEST_TYPE_EVENT_CHANGE)){
						//remove same event change requests
						for (RequestModel aRequest : AppManager.mRequestsList){
							if(aRequest.getType().equals(Constants.REQUEST_TYPE_EVENT_CHANGE) && 
									aRequest.getEvent().getObjectId().equals(request.getEvent().getObjectId())){
								AppManager.mRequestsList.remove(aRequest);
							}
						}
					}
					
				}
				
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	public void rejectRequest(final RequestModel request, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("request_id", request.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("rejectRequest", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(err == null){
					AppManager.mRequestsList.remove(request);
					FeedFragment.mIsDataChanged = true;

					if(request.getType().equals(Constants.REQUEST_TYPE_GROUP_JOIN)){
						for (GroupModel group1 : AppManager.mMyGroupsList) {
							if (request.getGroup().getObjectId().equals(
									group1.getObjectId())) {
								group1.fetchInBackground(null);
							}
						}
						
					}else if(request.getType().equals(Constants.REQUEST_TYPE_GROUP_INVITATION)){
						//remove same invitation requests
						for (RequestModel aRequest : AppManager.mRequestsList){
							if(aRequest.getType().equals(Constants.REQUEST_TYPE_GROUP_INVITATION) && 
									aRequest.getGroup().getObjectId().equals(request.getGroup().getObjectId())){
								AppManager.mRequestsList.remove(aRequest);
							}
						}
					
					}else if(request.getType().equals(Constants.REQUEST_TYPE_EVENT_CHANGE)){
						//remove same event change requests
						for (RequestModel aRequest : AppManager.mRequestsList){
							if(aRequest.getType().equals(Constants.REQUEST_TYPE_EVENT_CHANGE) && 
									aRequest.getEvent().getObjectId().equals(request.getEvent().getObjectId())){
								AppManager.mRequestsList.remove(aRequest);
							}
						}
					}
				}
				
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	/*
	 * **************************** Send Notifications ****************************
	 */
	
	public void sendGroupRefresh(GroupModel group, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_id", group.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("sendGroupRefresh", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	public void sendEventRefresh(EventModel event, GroupModel group, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("event_id", event.getObjectId());
		params.put("group_id", group.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("sendEventRefresh", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	public void sendPendingRequest(GroupModel group, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_id", group.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("sendPendingRequest", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	public void sendFeedUpdate(GroupModel group, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("group_id", group.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("sendFeedUpdate", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	public void sendThreadRefresh(ThreadModel thread, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("thread_id", thread.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("sendThreadRefresh", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	public void sendNewThread(ThreadModel thread, final ParseFunctionCallback listener){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("thread_id", thread.getObjectId());
		params.put("app_id", Constants.CHURCH_ID);
		ParseCloud.callFunctionInBackground("sendNewThread", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
	
	public void sendThreadMessage(ThreadMessageModel threadMessage, String message, boolean alertsEnabled, final ParseFunctionCallback listener){
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("thread_message_id", threadMessage.getObjectId());
		params.put("message", message);
		params.put("app_id", Constants.CHURCH_ID);
		params.put("alerts_enabled", alertsEnabled);
		ParseCloud.callFunctionInBackground("sendThreadMessage", params, new FunctionCallback<String>() {

			@Override
			public void done(String result, ParseException err) {
				// TODO Auto-generated method stub
				if(listener != null)
					listener.done(result, err);
			}
		});
	}
}
