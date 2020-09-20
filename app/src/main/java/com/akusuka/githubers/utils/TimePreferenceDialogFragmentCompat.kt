package com.akusuka.githubers.utils

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import androidx.preference.DialogPreference
import androidx.preference.PreferenceDialogFragmentCompat
import com.akusuka.githubers.R


class TimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

    private var mTimePicker: TimePicker? = null

    companion object {
        fun newInstance(key: String?): TimePreferenceDialogFragmentCompat {
            val fragment = TimePreferenceDialogFragmentCompat()
            val b = Bundle(1)
            b.putString(ARG_KEY, key)
            fragment.arguments = b
            return fragment
        }
    }

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        mTimePicker = view.findViewById(R.id.time_edit)

        // Get the time from the related Preference
        var time: String? = null
        val preference: DialogPreference = preference
        if (preference is TimePreference) {
            time = (preference).time
        }

        // Set the time to the TimePicker
        if (time != null) {
            val timeArray = time.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val hours = Integer.parseInt(timeArray[0])
            val minutes = Integer.parseInt(timeArray[1])
            val is24hour: Boolean = DateFormat.is24HourFormat(context)
            mTimePicker!!.setIs24HourView(is24hour)
            if (Build.VERSION.SDK_INT >= 23) {
                mTimePicker!!.hour = hours
                mTimePicker!!.minute = minutes
            } else {
                mTimePicker!!.currentHour = hours
                mTimePicker!!.currentMinute = minutes
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) {
            // Get the current values from the TimePicker
            val hours: Int
            val minutes: Int
            if (Build.VERSION.SDK_INT >= 23) {
                hours = mTimePicker!!.hour
                minutes = mTimePicker!!.minute
            } else {
                hours = mTimePicker!!.currentHour
                minutes = mTimePicker!!.currentMinute
            }

            // Generate value to save
            val time = hours.toString().padStart(2, '0') + ":" + minutes.toString().padStart(2, '0')

            // Save the value
            val preference: DialogPreference = preference
            if (preference is TimePreference) {
                if (preference.callChangeListener(time)) {
                    preference.time = time
                }
            }
        }
    }

}