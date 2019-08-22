package org.church.rockmobile;

import org.church.rockmobile.common.AppManager;
import org.church.rockmobile.common.Constants;
import org.church.rockmobile.common.NotificationService;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.GroupNotificationModel;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class RockReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle bundle = intent.getExtras();
		JSONObject json;
		try {
			json = new JSONObject(bundle.getString("com.parse.Data"));
			if (json == null || json.getString("type") == null)
				return;

			if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_PENDING)
					|| json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_GROUP_JOIN_REQUEST)) {
				AppManager.mRequestNotificationCount++;
				AppManager.mIsRequestsDataChanged = true;
				NotificationService.getInstance().postNotification(Constants.LOCAL_NOTIFICATION_TYPE_PENDING, null);

			} else if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_PENDING_REMOVE)) {
				AppManager.mRequestNotificationCount--;
				AppManager.mIsRequestsDataChanged = true;
				if (AppManager.mRequestNotificationCount < 0)
					AppManager.mRequestNotificationCount = 0;
				NotificationService.getInstance().postNotification(Constants.LOCAL_NOTIFICATION_TYPE_PENDING, null);

			} else if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_CANCEL_INVITATION)) {
				AppManager.mRequestNotificationCount--;
				AppManager.mIsRequestsDataChanged = true;
				if (AppManager.mRequestNotificationCount < 0)
					AppManager.mRequestNotificationCount = 0;

				AppManager.mInvitationCount--;
				if (AppManager.mInvitationCount < 0)
					AppManager.mInvitationCount = 0;

				NotificationService.getInstance().postNotification(Constants.LOCAL_NOTIFICATION_TYPE_PENDING, null);

			} else if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_FEED)) {
				FeedFragment.mIsDataChanged = true;
				NotificationService.getInstance().postNotification(Constants.LOCAL_NOTIFICATION_TYPE_FEED, null);

			} else if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_THREAD_MESSAGE)) {
				AppManager.mIsThreadMessageDataChanged = true;
				String threadMessageId = json.getString("threadMessageId");
				String groupId = json.getString("groupId");
				GroupNotificationModel notificationModel = AppManager.getInstance().getGroupNotification(groupId);
				if (notificationModel != null) {
					notificationModel.setNotificationCount(notificationModel.getNotificationCount() + 1);
				} else {
					GroupNotificationModel newModel = new GroupNotificationModel();
					newModel.setNotificationCount(1);
					newModel.setGroupId(groupId);
					AppManager.mGroupNotificationList.add(newModel);
				}

				NotificationService.getInstance().postNotification(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_MESSAGE,
						threadMessageId);

			} else if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_THREAD_REFRESH)) {
				String threadId = json.getString("threadId");
				NotificationService.getInstance().postNotification(Constants.LOCAL_NOTIFICATION_TYPE_THREAD_REFRESH,
						threadId);

			} else if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_GROUP_REFRESH)) {
				String groupId = json.getString("groupId");
				if (AppManager.mMyGroupsList != null) {
					for (GroupModel group : AppManager.mMyGroupsList) {
						if (groupId.equals(group.getObjectId()))
							group.fetchInBackground(null);
					}
				}
				if (AppManager.mAllGroupsList != null) {
					for (GroupModel group : AppManager.mAllGroupsList) {
						if (groupId.equals(group.getObjectId()))
							group.fetchInBackground(null);
					}
				}
				NotificationService.getInstance().postNotification(Constants.LOCAL_NOTIFICATION_TYPE_GROUP_REFRESH,
						groupId);

			} else if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_GROUP_JOIN_ACCEPTED)) {
				AppManager.mIsMyGroupsChanged = true;
				AppManager.mIsAllGroupsChanged = true;

			} else if (json.getString("type").equals(Constants.PUSH_NOTIFICATION_TYPE_GROUP_INVITE)) {
				AppManager.mRequestNotificationCount++;
				AppManager.mInvitationCount++;
				AppManager.mIsRequestsDataChanged = true;
				NotificationService.getInstance().postNotification(Constants.LOCAL_NOTIFICATION_TYPE_PENDING, null);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}