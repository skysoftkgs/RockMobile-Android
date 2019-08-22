package org.church.rockmobile;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

public class RockMobileLifecycleHandler implements ActivityLifecycleCallbacks{
	private static int resumed;
    private static int stopped;
    
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    }

    public void onActivityDestroyed(Activity activity) {
    }
    
    public void onActivityResumed(Activity activity) {
        ++resumed;
        
//        startActivity(activity);
    }
    
    public void onActivityPaused(Activity activity) {
    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    public void onActivityStarted(Activity activity) {
    }

    public void onActivityStopped(Activity activity) {
        ++stopped;
        android.util.Log.w("test", "application is being backgrounded: " + (resumed == stopped));
    }
    
    public static boolean isApplicationInForeground() {
        return resumed > stopped;
    }
    
//    public synchronized void startActivity(Activity activity){
//    	if (AppManager.mStartActivity.equals(Constants.START_REQUEST_NOTIFICATION_ACTIVITY)){
//        	AppManager.mStartActivity = "";
//        	Intent intent = new Intent(activity, RequestNotificationActivity.class);
//        	activity.startActivity(intent);
//        }
//    }
}
