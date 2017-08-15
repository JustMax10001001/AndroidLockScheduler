package com.justsoft.lockscheduler.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.justsoft.lockscheduler.utils.AdminUtils;

/**
 * Created by IMax on 06.07.2017.
 */
public class SettingsActivity extends AppCompatActivity {

	@Override
	public void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//setContentView(R.layout.activity_settings);
		getFragmentManager().beginTransaction().add(android.R.id.content, new SettingsFragment()).commit();

		final Context c = getApplicationContext();

		if(!AdminUtils.getInstance(this).adminActive()){
			new AlertDialog.Builder(this)
					.setMessage("This app requires administrator privileges to set or remove password on schedule. Do you wish to proceed?")
					.setTitle("Ooops...")
					.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							AdminUtils.getInstance(null).obtainAdmin(c, null);
						}
					})
					.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							finishAffinity();
						}
					}).show();
		}
	}
}
