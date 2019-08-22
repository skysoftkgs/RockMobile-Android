/**
 * 
 */
package org.church.rockmobile.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;

/**
 * @author zaheerahmad
 *
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {

	@SuppressLint("SimpleDateFormat")
	public static String getFormattedDate(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm a");

		long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

		long difference = (new Date().getTime() - date.getTime());
		int diffInDays = (int) (difference / DAY_IN_MILLIS);
		long diffMinutes = difference / (60 * 1000) % 60;
		long diffHours = difference / (60 * 60 * 1000);

		StringBuilder formattedDate = new StringBuilder();

		if (diffInDays == 0) {
			if (diffHours > 12) {
				formattedDate.append("Today at " + sdf1.format(date));
			} else if (diffHours >= 1) {
				formattedDate.append(diffHours + " hours " + diffMinutes + " mins");
			} else {
				formattedDate.append(diffMinutes + " mins");
			}
		} else if (diffInDays == 1) {
			formattedDate.append("Yesterday at " + sdf1.format(date));
		} else {
			formattedDate.append(sdf.format(date) + " at " + sdf1.format(date));
		}

		return formattedDate.toString();
	}

	public static String getCurrentYear() {
		int year = Calendar.getInstance().get(Calendar.YEAR);
		return String.valueOf(year);
	}

}
