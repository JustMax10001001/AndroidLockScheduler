package com.justsoft.lockscheduler.utils;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.justsoft.lockscheduler.activities.AdminObtainActivity;

/**
 * Created by IMax on 16.08.2017.
 */

public class AdminUtils {
	private static AdminUtils ourInstance = null;

	public synchronized static AdminUtils getInstance(Context c) {                      //We rarely call getInstance(), so synchronized singleton implementation will be fine
		if(ourInstance==null){
			ourInstance = new AdminUtils(c);
		}
		return ourInstance;
	}

	private DevicePolicyManager dpm;
	private ComponentName deviceSample;
	private OnAdminReceivedListener adminReceivedListener;

	public DevicePolicyManager getDevicePolicyManager(){
		return dpm;
	}

	public ComponentName getDeviceSample(){
		return deviceSample;
	}

	public OnAdminReceivedListener getAdminReceivedListener(){
		return adminReceivedListener;
	}

	private AdminUtils(Context c) {
		dpm = (DevicePolicyManager) c.getSystemService(Context.DEVICE_POLICY_SERVICE);
		deviceSample = new ComponentName(c, AdminUtils.AdminReceiver.class);
	}

	public boolean adminActive(){
		return dpm.isAdminActive(deviceSample);
	}

	public void obtainAdmin(@NonNull Context c, @Nullable OnAdminReceivedListener l){
		this.adminReceivedListener = l;
		c.startActivity(new Intent(c, AdminObtainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));      //Starting activity outside activity context, so flag is needed
	}

	public interface OnAdminReceivedListener{
		void adminReceived();
	}

	public static class AdminReceiver extends DeviceAdminReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_DEVICE_ADMIN_DISABLE_REQUESTED)) {
				abortBroadcast();
			}
			Log.d("Admin Receiver", "onReceive() action: "+intent.getAction());
		}

		@Override
		public void onEnabled(Context context, Intent intent){
			Toast.makeText(context, "Admin granted", Toast.LENGTH_SHORT).show();

			if(AdminUtils.getInstance(context).getAdminReceivedListener()!=null)
				AdminUtils.getInstance(context).getAdminReceivedListener().adminReceived();                  //fire listener
		}
	}
}
