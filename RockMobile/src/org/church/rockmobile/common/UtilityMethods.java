package org.church.rockmobile.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.church.rockmobile.R;
import org.church.rockmobile.RockMobileApplication;
import org.church.rockmobile.model.GroupNotificationModel;
import org.church.rockmobile.model.UserModel;

import com.parse.ParseObject;
import com.parse.ParseUser;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;
import android.provider.CalendarContract.Reminders;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UtilityMethods {
	
	public static boolean checkIsEmptyForEditText(Context context, EditText et, String fieldName){
		if(TextUtils.isEmpty(et.getText().toString().trim())){
			RockMobileApplication.getInstance().showToast(context, fieldName + context.getResources().getString(R.string.can_not_be_empty), Toast.LENGTH_SHORT, true);
			return false;
		}
		return true;
	}
	
	public static boolean checkIsEmptyForTextView(Context context, TextView tv, String fieldName){
		if(TextUtils.isEmpty(tv.getText().toString().trim())){
			RockMobileApplication.getInstance().showToast(context, fieldName + context.getResources().getString(R.string.can_not_be_empty), Toast.LENGTH_SHORT, true);
			return false;
		}
		return true;
	}
	
	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
	    } else {
	        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	    }
	}
	
	public final static boolean isValidPhoneNumber(CharSequence target) {
		if (target == null) {
			return false;
	    } else {
	        return android.util.Patterns.PHONE.matcher(target).matches();
	    }
	}
	
	public final static boolean isValidPassword(String target) {
		if (target == null) {
			return false;
	    } else {
	    	String regex = "^.*(?=.{6,})(?=.*[a-z])(?=.*[A-Z]).*$";
	        return target.matches(regex);
	    }
	}
	
	public static void hideSoftInput(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
	
	public static void hideKeyboard(Activity activity) {
		InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);

	    // check if no view has focus:
	    View view = activity.getCurrentFocus();
	    if (view != null) {
	    	inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	    }
	}
	
	public static List<String> convertStringsToList(String[] strArray){
		if(strArray == null) return null;
		
		List<String> strList = new ArrayList<String>();
		for(int i = 0;i<strArray.length;i++){
			strList.add(strArray[i]);
		}
		
		return strList;
	}
		
	public static String getStringWithChurchId(String str){
		return String.format("%s_%s", Constants.CHURCH_ID, str);
	}
	
	public static boolean containsParseUser(List<UserModel> userList, ParseUser user){
		if(userList == null || user == null) return false;
		
		for(int i = 0;i<userList.size();i++){
			if(userList.get(i).getObjectId().equals(user.getObjectId()))
				return true;
		}
		
		return false;
	}
	
	public static List<UserModel> removeParseUser(List<UserModel> userList, ParseUser user){
		if(userList == null || user == null) return null;
		
		for(int i = 0;i<userList.size();i++){
			if(userList.get(i).getObjectId().equals(user.getObjectId()))
				userList.remove(i);
		}
		
		return userList;
	}
	
	public static List<UserModel> removeDuplicateUsers(List<UserModel> userList) {
		Set<String> set = new HashSet<String>();
		List<UserModel> list = new ArrayList<UserModel>();
		for (UserModel item : userList) {
			if (set.add(item.getObjectId())) {
				list.add(item);
			}
		}
		return list;
	}

	public static int getIndexofArray(String[] strArray, String str){
		if(strArray == null || str == null) return -1;
		
		for(int i=0;i<strArray.length;i++){
			if(strArray[i].equals(str))
				return i;
		}
		
		return -1;
	}

	public static int getIndexOfGroupNotificationArray(UserModel user, List<GroupNotificationModel> list){
		if(user == null || list == null) return -1;
		
		for(int i=0;i<list.size();i++){
			GroupNotificationModel model = list.get(i);
			if(model.getUserId().equals(user.getObjectId())){
				return i;
			}
		}
		
		return -1;
	}
		
	// Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f, int reqWidth, int reqHeight) {
        try {
            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1 = new FileInputStream(f);
            BitmapFactory.decodeStream(stream1, null, o);
            stream1.close();
 
            // Find the correct scale value. It should be the power of 2.
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            
            if(reqWidth > 0 && reqHeight >0){
	            while (true) {
	                if (width_tmp / 2 < reqWidth
	                        || height_tmp / 2 < reqHeight)
	                    break;
	                width_tmp /= 2;
	                height_tmp /= 2;
	                scale *= 2;
	            }
            }
            
            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            o2.inPurgeable = true;
            o2.inInputShareable = true;
            o2.inPreferredConfig = Bitmap.Config.RGB_565;
            o2.inJustDecodeBounds = false;
            
            FileInputStream stream2 = new FileInputStream(f);
            Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Bitmap rotateBitmap(Bitmap bitmap, String photoPath) {

		ExifInterface exif;
		int orientation;
	    try {
			exif = new ExifInterface(photoPath);
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);  
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}  
	    
	    try{
	        Matrix matrix = new Matrix();
	        switch (orientation) {
	            case ExifInterface.ORIENTATION_NORMAL:
	                return bitmap;
	            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
	                matrix.setScale(-1, 1);
	                break;
	            case ExifInterface.ORIENTATION_ROTATE_180:
	                matrix.setRotate(180);
	                break;
	            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
	                matrix.setRotate(180);
	                matrix.postScale(-1, 1);
	                break;
	            case ExifInterface.ORIENTATION_TRANSPOSE:
	                matrix.setRotate(90);
	                matrix.postScale(-1, 1);
	                break;
	           case ExifInterface.ORIENTATION_ROTATE_90:
	               matrix.setRotate(90);
	               break;
	           case ExifInterface.ORIENTATION_TRANSVERSE:
	               matrix.setRotate(-90);
	               matrix.postScale(-1, 1);
	               break;
	           case ExifInterface.ORIENTATION_ROTATE_270:
	               matrix.setRotate(-90);
	               break;
	           default:
	               return bitmap;
	        }
	        try {
	            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
	            bitmap.recycle();
	            return bmRotated;
	        }
	        catch (OutOfMemoryError e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
    
    public static Bitmap getCropedBitmap(Bitmap srcBmp, int width){
		
		System.gc();
		Bitmap dstBmp;
		
		try {
        	if (srcBmp.getWidth() >= srcBmp.getHeight()){
		
				  dstBmp = Bitmap.createBitmap(
				     srcBmp, 
				     srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
				     0,
				     srcBmp.getHeight(), 
				     srcBmp.getHeight()
				     );
		
			}else{
		
				  dstBmp = Bitmap.createBitmap(
				     srcBmp,
				     0, 
				     srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
				     srcBmp.getWidth(),
				     srcBmp.getWidth()
				     );
			}
					
			if(width>0){
				Bitmap bm = Bitmap.createScaledBitmap(dstBmp, width, width, false);
				dstBmp.recycle();
				return bm;
			}

			return dstBmp;
		}
		catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
	}
    
    public static int dp2px(Context context, int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				context.getResources().getDisplayMetrics());
	}
    
    public static void addEventToCalendar(Context context, String title, String description, 
    		long startTimeMillis, long endTimeMillis, String location, List<Integer> reminderMinutes){
    	final ContentValues event = new ContentValues();
        event.put(Events.CALENDAR_ID, 1);

        event.put(Events.TITLE, title);
        event.put(Events.DESCRIPTION, description);
        event.put(Events.EVENT_LOCATION, location);

        event.put(Events.DTSTART, startTimeMillis);
        event.put(Events.DTEND, endTimeMillis);
        event.put(Events.ALL_DAY, 0);   // 0 for false, 1 for true
        event.put(Events.HAS_ALARM, 1); // 0 for false, 1 for true
             
        String timeZone = TimeZone.getDefault().getID();
        event.put(Events.EVENT_TIMEZONE, timeZone);

        Uri baseUri;
        if (Build.VERSION.SDK_INT >= 8) {
            baseUri = Uri.parse("content://com.android.calendar/events");
        } else {
            baseUri = Uri.parse("content://calendar/events");
        }

        Uri uri = context.getContentResolver().insert(baseUri, event);
        
        if(reminderMinutes != null){
        	long eventID = Long.parseLong(uri.getLastPathSegment());
        	for(int i=0;i<reminderMinutes.size();i++){
        		ContentValues reminders = new ContentValues();
                reminders.put(Reminders.EVENT_ID, eventID);
                reminders.put(Reminders.METHOD, Reminders.METHOD_ALERT);
	            reminders.put(Reminders.MINUTES, reminderMinutes.get(i));
	            context.getContentResolver().insert(Reminders.CONTENT_URI, reminders);
        	}
        }
        
        RockMobileApplication.getInstance().showToast(context, R.string.notify_event_reminder_saved, Toast.LENGTH_SHORT);
    }
    
    public static int convertStringToUniqueInt(String str){
    	int value = 0;
    	for(int i=0;i<str.length();i++){
    		value = value * 4 + (int)str.charAt(i);
    	}
    	
    	return value;
    }
    
    public static String toCapitalizedString(String source){
    	StringBuffer res = new StringBuffer();

        String[] strArr = source.split(" ");
        for (String str : strArr) {
        	if(str.trim().length() == 0) continue;
        	
            char[] stringArray = str.trim().toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            str = new String(stringArray);

            res.append(str).append(" ");
        }

        return res.toString().trim();
    }
    
	public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }
    
    public static List<ParseObject> sortBySortingValue(List<ParseObject> list){
    	if (list == null) return null;
    	
		List<ParseObject> newList = new ArrayList<ParseObject>();
		boolean flag;
		for (ParseObject object : list){
			flag = false;
			for (int i=newList.size() -1;i>=0;i--){
				ParseObject newObject = newList.get(i);
				if (object.getInt("sorting") <= newObject.getInt("sorting")){
					newList.add(i+1, object);
					flag = true;
					break;
				}
			}
			if (flag == false){
				newList.add(0, object);
			}
		}
		
		return newList;
	}
}
