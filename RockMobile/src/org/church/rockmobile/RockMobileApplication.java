package org.church.rockmobile;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

import org.church.rockmobile.common.Constants;
import org.church.rockmobile.model.AdminRemoveRequestModel;
import org.church.rockmobile.model.CampusModel;
import org.church.rockmobile.model.EventModel;
import org.church.rockmobile.model.GroupModel;
import org.church.rockmobile.model.GroupNotificationModel;
import org.church.rockmobile.model.LiveStreamModel;
import org.church.rockmobile.model.RequestModel;
import org.church.rockmobile.model.SeriesMessageModel;
import org.church.rockmobile.model.SeriesModel;
import org.church.rockmobile.model.StoryModel;
import org.church.rockmobile.model.ThreadMessageModel;
import org.church.rockmobile.model.ThreadModel;
import org.church.rockmobile.model.UserModel;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.PushService;

public class RockMobileApplication extends Application {

	static final String TAG = "Rock Mobile";
	static volatile RockMobileApplication mApplication = null;
	ProgressDialog mProgressDialog = null;
	private ArrayList<Toast> mToasts = new ArrayList<Toast>();

	public RockMobileApplication() {
		super();
		mApplication = this;
	}

	public static RockMobileApplication getInstance() {
		return mApplication;
	}

	@Override
	public void onCreate() {

		super.onCreate();
		if (!getResources().getBoolean(R.bool.isDebug)) {
			Fabric.with(this, new Crashlytics());
		}

		ParseObject.registerSubclass(UserModel.class);
		ParseObject.registerSubclass(GroupModel.class);
		ParseObject.registerSubclass(EventModel.class);
		ParseObject.registerSubclass(ThreadModel.class);
		ParseObject.registerSubclass(ThreadMessageModel.class);
		ParseObject.registerSubclass(RequestModel.class);
		ParseObject.registerSubclass(StoryModel.class);
		ParseObject.registerSubclass(SeriesModel.class);
		ParseObject.registerSubclass(SeriesMessageModel.class);
		ParseObject.registerSubclass(CampusModel.class);
		ParseObject.registerSubclass(LiveStreamModel.class);
		ParseObject.registerSubclass(AdminRemoveRequestModel.class);
		ParseObject.registerSubclass(GroupNotificationModel.class);

		// Configure project environment for integrating Parse
		Parse.initialize(this, Constants.PARSE_APPID, Constants.PARSE_CLIENTKEY);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();

		defaultACL.setPublicReadAccess(true);
		defaultACL.setPublicWriteAccess(true);
		ParseACL.setDefaultACL(defaultACL, true);

		// Specify a Activity to handle all pushes by default.
		PushService.setDefaultPushCallback(this, MainActivity.class);

		registerActivityLifecycleCallbacks(new RockMobileLifecycleHandler());

		// configure Flurry
		FlurryAgent.setLogEnabled(false);
	}

	public String getString(Object stringOrId) {

		String string = null;
		if (stringOrId instanceof String)
			string = (String) stringOrId;
		else if (stringOrId instanceof Integer)
			string = super.getString((Integer) stringOrId);

		return string;
	}

	/**
	 * Alert Dialog
	 */
	private Dialog commonOkDialog(Context context, Object titleOrId, Object messageOrId,
			final OnClickListener onClickListener) {

		final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
		dialogBuilder.setCancelable(false);
		dialogBuilder.setTitle(getString(titleOrId));
		dialogBuilder.setMessage(getString(messageOrId));
		dialogBuilder.setNegativeButton("OK", onClickListener);

		return dialogBuilder.create();
	}

	public void showErrorDialog(Context context, Object messageOrId) {
		showErrorDialog(context, messageOrId, null);
	}

	public void showErrorDialog(Context context, Object messageOrId, final OnClickListener onClickListener) {
		hideProgressDialog();
		commonOkDialog(context, RockMobileApplication.getInstance().getString(R.string.app_name), messageOrId,
				onClickListener).show();
	}

	public void showWarningDialog(Context context, Object messageOrId) {
		showWarningDialog(context, messageOrId, null);
	}

	public void showWarningDialog(Context context, Object messageOrId, final OnClickListener onClickListener) {
		hideProgressDialog();
		commonOkDialog(context, "Warning", messageOrId, onClickListener).show();
	}

	/**
	 * Progress Dialog
	 */
	private void showProgressDialog(Context context, Object messageOrId, boolean cancelable,
			final OnCancelListener onCancelListener) {
		hideProgressDialog();

		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(context) {
				@Override
				public void onBackPressed() {
					if (onCancelListener != null)
						super.onBackPressed();
				}
			};
		}

		mProgressDialog.setMessage(getString(messageOrId));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCanceledOnTouchOutside(cancelable);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				if (onCancelListener != null)
					onCancelListener.onCancel(dialog);
			}
		});

		mProgressDialog.setIndeterminate(true);

		mProgressDialog.show();
	}

	public void showProgressDialog(Context context, Object messageOrId, final OnCancelListener onCancelListener) {
		showProgressDialog(context, messageOrId, false, onCancelListener);
	}

	public void showCancelableProgressDialog(Context context, Object messageOrId,
			final OnCancelListener onCancelListener) {
		showProgressDialog(context, messageOrId, true, onCancelListener);
	}

	public void showProgressFullScreenDialog(Activity activity) {
		hideProgressDialog();

		mProgressDialog = ProgressDialog.show(activity, null, null, true);
		mProgressDialog.setContentView(R.layout.progress_layout);
	}

	public void hideProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}

		mProgressDialog = null;
	}

	/*
	 * Toasts
	 */
	public void showToast(Context context, final Object messageOrId, int duration, boolean cancelAllPrevious) {
		if (cancelAllPrevious)
			cancelAllToasts();

		Toast toast = Toast.makeText(context, getString(messageOrId), duration);
		mToasts.add(toast);
		toast.show();
	}

	public void showToast(Context context, final Object messageOrId, int duration) {
		showToast(context, messageOrId, duration, true);
	}

	public void cancelAllToasts() {
		for (Toast toast : mToasts) {
			toast.cancel();
		}

		mToasts.clear();
	}
}