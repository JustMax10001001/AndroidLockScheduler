package com.justsoft.lockscheduler.preferences;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.justsoft.lockscheduler.R;

import java.util.Locale;


public class TimePikerPreference extends DialogPreference {

	private static final String appns = "http://schemas.android.com/apk/res/com.justsoft.admin";

	private Context context;
	private TimePicker picker;
	private int value, defaultVal;
	private Toast wrongValToast;
	private String earlierThan, laterThan;

	public TimePikerPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		defaultVal = attrs.getAttributeIntValue(appns, "defaultValue", 0);
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimePikerPreference);
		earlierThan = ta.getString(R.styleable.TimePikerPreference_isEarlierThan);
		laterThan = ta.getString(R.styleable.TimePikerPreference_isLaterThan);
		ta.recycle();
		setPersistent(true);
	}

	private int getEarlier(){
		return earlierThan != null?PreferenceManager.getDefaultSharedPreferences(context).getInt(earlierThan, 720):-1;
	}
	private int getLater(){
		return laterThan != null?PreferenceManager.getDefaultSharedPreferences(context).getInt(laterThan, 120):-1;
	}

	@Override
	protected void onSetInitialValue(boolean restore, Object defaultValue) {
		super.onSetInitialValue(restore, defaultValue);
		if (restore)
			value = getPersistedInt(this.defaultVal);
		else
			value = this.defaultVal;
	}

	public int getValue() {
		return picker.getHour() * 60 + picker.getMinute();
	}

	@Override
	public void showDialog(Bundle state) {

		super.showDialog(state);

		Button positiveButton = ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE);
		positiveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getEarlier() != -1 && getEarlier() <= value) {
					wrongValToast = Toast.makeText(getContext(), String.format(Locale.getDefault(), "Selected time should be earlier than %02d:%02d", getEarlier()/60, getEarlier()%60), Toast.LENGTH_LONG);
					wrongValToast.show();
				}else if (getLater() != -1 && getLater() >= value) {
					wrongValToast = Toast.makeText(getContext(), String.format(Locale.getDefault(), "Selected time should be later than %02d:%02d", getLater()/60, getLater()%60), Toast.LENGTH_LONG);
					wrongValToast.show();
				}else {
					getDialog().dismiss();
					value = getValue();
					persistInt(value);
					callChangeListener(getValue());
				}
			}
		});
	}

	@Override
	protected void onBindDialogView(View v) {
		super.onBindDialogView(v);
		picker.setHour(value / 60);
		picker.setMinute(value % 60);
	}



	@Override
	protected View onCreateDialogView() {
		LinearLayout ll = new LinearLayout(context);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setPadding(8, 8, 8, 8);

		picker = new TimePicker(context);
		value = getPersistedInt(0);
		picker.setHour(value / 60);
		picker.setMinute(value % 60);
		picker.setClickable(false);
		picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			@Override
			public void onTimeChanged(TimePicker timePicker, int i, int i1) {
				value = timePicker.getHour() * 60 + timePicker.getMinute();
			}
		});
		picker.setIs24HourView(true);
		picker.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		ll.addView(picker);

		Log.d("PTimePiker", "Hour " + value / 60 + "; Minute " + value % 60 + "; value " + value);
		return ll;
	}
}
