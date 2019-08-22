package org.church.rockmobile.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.model.CampusModel;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.GroupNotificationModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.SeriesMessageModel;
import org.church.rockmobile.model.SeriesModel;
import org.church.rockmobile.model.StoryModel;
import org.church.rockmobile.service.AlarmReceiver;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.WindowManager;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class AppManager {
	static volatile AppManager mInstance;
	Context mContext;

	public static String[] mCategories;
	public int mScreenWidth;
	public int mScreenHeight;
	public DisplayImageOptions options;
	public ImageLoader mImageLoader;
	public static TypedArray mTabIconArray;
	public static String[] mTabTitleArray;
	public static List<GroupModel> mAllGroupsList;
	public static List<GroupModel> mMyGroupsList;
	public static List<RequestModel> mFeedsList;
	public static List<RequestModel> mRequestsList;
	public static List<StoryModel> mNewestStoryList;
	public static List<StoryModel> mFeaturedStoryList;
	public static List<StoryModel> mBookmarkStoryList;
	public static List<SeriesModel> mSeriesList;
	public static List<SeriesMessageModel> mSeriesMessagesList;
	public static List<CampusModel> mCampusesList;
	public static List<GroupNotificationModel> mGroupNotificationList;
	public static int mRequestNotificationCount = 0;
	public static int mInvitationCount = 0;
	public static String mStartActivity = "";
	public static boolean mAnonymousLogin;
	public static boolean mIsThreadMessageDataChanged;
	public static boolean mIsMyGroupsChanged = true;
	public static boolean mIsAllGroupsChanged = true;
	public static boolean mIsRequestsDataChanged;

	public AppManager() {
		mAllGroupsList = new ArrayList<GroupModel>();
		mMyGroupsList = new ArrayList<GroupModel>();
		mFeedsList = new ArrayList<RequestModel>();
		mRequestsList = new ArrayList<RequestModel>();
		mNewestStoryList = new ArrayList<StoryModel>();
		mFeaturedStoryList = new ArrayList<StoryModel>();
		mBookmarkStoryList = new ArrayList<StoryModel>();
		mSeriesList = new ArrayList<SeriesModel>();
		mSeriesMessagesList = new ArrayList<SeriesMessageModel>();
		mCampusesList = new ArrayList<CampusModel>();
		mGroupNotificationList = new ArrayList<GroupNotificationModel>();
		AppManager.mIsMyGroupsChanged = true;
		AppManager.mIsAllGroupsChanged = true;
		
		// initialize ImageLoader
		mImageLoader = ImageLoader.getInstance();
		initImageLoader();
	}

	public static AppManager getInstance() {
		if (mInstance == null)
			mInstance = new AppManager();

		return mInstance;
	}

	public void logout(Context context) {
		ParseInstallation currentInstallation = ParseInstallation.getCurrentInstallation();
		currentInstallation.remove("user");
		currentInstallation.saveInBackground();

		ParseUser.logOut();
		mInstance = new AppManager();

		setIsLoggedIn(context, false);
	}

	@SuppressLint("Recycle")
	@SuppressWarnings("deprecation")
	public void initDataManager(Context context) {
		mContext = context;
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		mScreenWidth = display.getWidth();
		mScreenHeight = display.getHeight();

		mTabIconArray = context.getResources().obtainTypedArray(R.array.tab_imgages);
		mTabTitleArray = context.getResources().getStringArray(R.array.tab_titles);

		loadCategories();
	}

	public void initImageLoader() {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(RockMobileApplication.getInstance())
				.threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator()).diskCacheSize(50 * 1024 * 1024) // 50
																										// Mb
				.tasksProcessingOrder(QueueProcessingType.LIFO).writeDebugLogs() // Remove
																					// for
																					// release
																					// app
				.build();

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);

		options = new DisplayImageOptions.Builder().showImageOnLoading(R.color.transparent)
				.showImageForEmptyUri(R.color.transparent).cacheInMemory(true).cacheOnDisk(true)
				.considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();
	}

	public void loadCategories() {
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Category");
		query.whereEqualTo("churchId", Constants.CHURCH_ID);
		query.setLimit(Constants.PARSE_QUERY_MAX_LIMIT_COUNT);
		query.orderByAscending("title");
		query.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objList, ParseException err) {
				// TODO Auto-generated method stub
				if (err == null && objList != null) {
					objList = UtilityMethods.sortBySortingValue(objList);
					mCategories = new String[objList.size()];
					for (int i = 0; i < objList.size(); i++) {
						mCategories[i] = objList.get(i).getString("title");
					}
				}
			}

		});
	}

	public String[] getCategories() {
		return mCategories;
	}

	public void setIsLoggedIn(Context context, boolean loggedIn) {
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putBoolean(Constants.PREF_LOGGEDIN, loggedIn);
		editor.commit();
	}

	public boolean getIsLoggedIn(Context context) {
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(context);
		boolean loginStatus = sharedPred.getBoolean(Constants.PREF_LOGGEDIN, false);
		return loginStatus;
	}

	public void setIsTutorialPassed(Context context, boolean tutorialPassed) {
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPred.edit();
		editor.putBoolean(Constants.PREF_TUTORIAL_PASSED, tutorialPassed);
		editor.commit();
	}

	public boolean getIsTutorialPassed(Context context) {
		SharedPreferences sharedPred = PreferenceManager.getDefaultSharedPreferences(context);
		boolean status = sharedPred.getBoolean(Constants.PREF_TUTORIAL_PASSED, false);
		return status;
	}

	public boolean checkIfpendingIntentIsRegistered(Context context, int requestCode) {
		// Intent alarmIntent = new Intent(context, AlarmReceiver.class);
		Intent alarmIntent = new Intent("org.church.rockmobile.ALARM_RECEIVER");
		boolean isRegistered = (PendingIntent.getBroadcast(context, requestCode, alarmIntent,
				PendingIntent.FLAG_NO_CREATE) != null);
		return isRegistered;
	}

	public void notifyEvent(Context context, String strId, String message, Date date,
			ArrayList<Integer> reminderMinutes) {
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		// Intent alarmIntent = new Intent(context, AlarmReceiver.class);
		Intent alarmIntent = new Intent("org.church.rockmobile.ALARM_RECEIVER");

		int id = UtilityMethods.convertStringToUniqueInt(strId);
		// cancel previous notification
		for (int i = 0; i < 4; i++) {
			int newId = id * 10 + i;
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, newId, alarmIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			alarmManager.cancel(pendingIntent);
			pendingIntent.cancel();

		}

		long currentTime = System.currentTimeMillis();
		for (int i = 0; i < reminderMinutes.size(); i++) {
			long when = date.getTime() - reminderMinutes.get(i) * 60 * 1000;
			if (when < currentTime)
				continue;

			int newId = 0;
			switch (reminderMinutes.get(i)) {
			case 10:
				newId = id * 10;
				break;

			case 30:
				newId = id * 10 + 1;
				break;

			case 60:
				newId = id * 10 + 2;
				break;

			case 1440:
				newId = id * 10 + 3;
				break;
			}
			alarmIntent.putExtra(AlarmReceiver.BUNDLE_MESSAGE, message);
			alarmIntent.putExtra(AlarmReceiver.BUNDLE_ALARM_ID, newId);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, newId, alarmIntent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			alarmManager.set(AlarmManager.RTC_WAKEUP, when, pendingIntent);
		}
	}

	// @SuppressWarnings("deprecation")
	// public void notifyEvent(Context context, String strId, String message,
	// Date date, List<Integer> reminderMinutes) {
	// NotificationManager notificationManager = (NotificationManager)
	// context.getSystemService(Context.NOTIFICATION_SERVICE);
	//
	// //cancel previous notification
	// for(int i=0;i<4;i++){
	// int id = UtilityMethods.convertStringToUniqueInt(strId);
	// int newId = id * 10 + i;
	// notificationManager.cancel(newId);
	// setIsEventRegistered(mContext, String.format("%s_%d", strId, i), false);
	// }
	//
	// if(reminderMinutes != null && reminderMinutes.size() > 0){
	//
	// boolean isAvailableReminder = false;
	// long currentTime = System.currentTimeMillis();
	// for(int i=0;i<reminderMinutes.size();i++){
	// int icon = R.drawable.ic_launcher;
	// long when = date.getTime() - reminderMinutes.get(i) * 60 * 1000 ;
	// if(when < currentTime) continue;
	//
	// isAvailableReminder = true;
	// Notification notification = new Notification(icon, message, when);
	// String title = context.getString(R.string.app_name);
	// Intent notificationIntent = new Intent(context, MainActivity.class);
	// // set intent so it does not start a new activity
	//// notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
	//// Intent.FLAG_ACTIVITY_SINGLE_TOP);
	// PendingIntent intent =
	// PendingIntent.getActivity(context, 0, notificationIntent,
	// PendingIntent.FLAG_UPDATE_CURRENT);
	// notification.setLatestEventInfo(context, title, message, intent);
	// notification.flags |= Notification.FLAG_AUTO_CANCEL;
	//
	// // Play default notification sound
	// notification.defaults |= Notification.DEFAULT_SOUND;
	//
	// //notification.sound = Uri.parse("android.resource://" +
	// context.getPackageName() + "your_sound_file_name.mp3");
	//
	// // Vibrate if vibrate is enabled
	// notification.defaults |= Notification.DEFAULT_VIBRATE;
	//
	// //set notification
	// int id = UtilityMethods.convertStringToUniqueInt(strId);
	// switch(reminderMinutes.get(i)){
	// case 10:
	// notificationManager.notify(id * 10 + 0, notification);
	// setIsEventRegistered(mContext, strId + "_0", true);
	// break;
	//
	// case 30:
	// notificationManager.notify(id * 10 + 1, notification);
	// setIsEventRegistered(mContext, strId + "_1", true);
	// break;
	//
	// case 60:
	// notificationManager.notify(id * 10 + 2, notification);
	// setIsEventRegistered(mContext, strId + "_2", true);
	// break;
	//
	// case 1440:
	// notificationManager.notify(id * 10 + 3, notification);
	// setIsEventRegistered(mContext, strId + "_3", true);
	// break;
	// }
	// }
	//
	// if(isAvailableReminder == false){
	// RockMobileApplication.getInstance().showToast(mContext, "Reminder time is
	// before of current date.", Toast.LENGTH_SHORT);
	// }
	// }
	// }

	public GroupNotificationModel getGroupNotification(String groupId) {
		if (groupId == null || mGroupNotificationList == null)
			return null;

		for (int i = 0; i < mGroupNotificationList.size(); i++) {
			if (groupId.equals(mGroupNotificationList.get(i).getGroupId())) {
				return mGroupNotificationList.get(i);
			}
		}

		return null;
	}
}
