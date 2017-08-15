package com.justsoft.lockscheduler.activities;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.justsoft.lockscheduler.utils.AdminUtils;

/**
 * Created by IMax on 15.08.2017.
 */

public class AdminObtainActivity extends Activity {
	public AdminObtainActivity(){}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("Admin Obtain Activity", "Activity creating...");

		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
				.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, AdminUtils.getInstance(this).getDeviceSample())
				.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "This app needs following permissions to unlock/lock your phone");
		startActivityForResult(intent, 1);
		Log.d("Admin Obtain Activity", "AddDeviceAdminActivity started");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		finish();
	}
}
