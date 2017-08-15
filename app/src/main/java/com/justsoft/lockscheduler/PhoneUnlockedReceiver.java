package com.justsoft.lockscheduler;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.util.Log;

import com.justsoft.lockscheduler.activities.SettingsActivity;
import com.justsoft.lockscheduler.utils.AdminUtils;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by IMax on 02.07.2017
 */
public class PhoneUnlockedReceiver extends BroadcastReceiver {

	private static final String LOG_TAG = "Unlocked Receiver";

	@Override
	public void onReceive(final Context context, Intent intent) {
		if(lockerEnabled(context)) {                                            //Why to do stuff if its even not enabled?
			AdminUtils au = AdminUtils.getInstance(context);
			if (!au.adminActive())
				au.obtainAdmin(context, new AdminUtils.OnAdminReceivedListener() {
					@Override
					public void adminReceived() {
						doWork(context);
					}
				});
			else
				doWork(context);
		}
	}

	private void doWork(Context context) {
		DevicePolicyManager dpm = AdminUtils.getInstance(context).getDevicePolicyManager();
		ComponentName deviceSample = AdminUtils.getInstance(context).getDeviceSample();

		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Log.d(LOG_TAG, "Device screen turned on");
		KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		if (keyguardManager.isKeyguardSecure()) {
			Log.d(LOG_TAG, "Device has password");
			Calendar c = Calendar.getInstance();
			if (isAfterLockEnd(c, context)) {
				Log.d(LOG_TAG, "Unlocking phone...");
				dpm.setPasswordMinimumLength(deviceSample, 0);
				dpm.resetPassword("", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
			}
		} else {
			Log.d(LOG_TAG, "Device doesn\'t have password");
			Calendar c = Calendar.getInstance();
			if (isAfterLockStart(c, context) && !isAfterLockEnd(c, context)) {
				Log.d(LOG_TAG, "Locking phone...");
				dpm.setPasswordQuality(deviceSample, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
				dpm.resetPassword(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_password), "qwerty"), DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
				dpm.lockNow();
			}
		}
		nm.notify(10, buildNotification(context, keyguardManager.isKeyguardSecure()));
	}

	private boolean lockerEnabled(Context c){
		return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(c.getString(R.string.pref_enabled), false);          //default is 'false' for case of install but app didn't open at least once
	}

	private boolean isAfterLockStart(Calendar c, Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int time = sp.getInt(context.getString(R.string.pref_start_block), 120);
		return time <= (c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE));
	}

	private boolean isAfterLockEnd(Calendar c, Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		int time = sp.getInt(context.getString(R.string.pref_end_block), 720);
		return time <= (c.get(Calendar.HOUR_OF_DAY) * 60 + c.get(Calendar.MINUTE));
	}

	public Notification buildNotification(Context c, boolean locked) {
		PendingIntent pi = PendingIntent.getActivity(c, (int) System.currentTimeMillis(), new Intent(c, SettingsActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

		return new NotificationCompat.Builder(c)
				.setContentText(Html.fromHtml(String.format(Locale.getDefault(), "Device is <b>%s</b>", locked?"locked":"unlocked")))
				.setContentTitle("Admin")
				.setContentIntent(pi)
				.setSmallIcon(R.mipmap.ic_launcher).build();
	}
}
