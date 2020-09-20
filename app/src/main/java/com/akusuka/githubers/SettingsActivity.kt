package com.akusuka.githubers

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.akusuka.githubers.utils.TimePreference
import com.akusuka.githubers.utils.TimePreferenceDialogFragmentCompat
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        private lateinit var DEFAULT_MESSAGE: String
        private lateinit var alarmReceiver: AlarmReceiver

        private lateinit var ALARM_ON: String
        private lateinit var ALARM_TIME: String
        private lateinit var ALARM_MESSAGE: String
        private lateinit var CHANGE_LANGUAGE: String

        private lateinit var alarmOnPreference: SwitchPreferenceCompat
        private lateinit var alarmTimePreference: TimePreference
        private lateinit var alarmMessagePreference: EditTextPreference
        private lateinit var languagePreference: Preference

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            init()
        }

        private fun init(){
            DEFAULT_MESSAGE = resources.getString(R.string.default_message)
            ALARM_ON = resources.getString(R.string.key_alarmOn)
            ALARM_TIME = resources.getString(R.string.key_alarmTime)
            ALARM_MESSAGE = resources.getString(R.string.key_alarmMessage)
            CHANGE_LANGUAGE = resources.getString(R.string.key_changeLanguange)

            alarmOnPreference = findPreference<SwitchPreferenceCompat>(ALARM_ON) as SwitchPreferenceCompat
            alarmTimePreference = findPreference<TimePreference>(ALARM_TIME) as TimePreference
            alarmMessagePreference = findPreference<EditTextPreference>(ALARM_MESSAGE) as EditTextPreference
            languagePreference = findPreference<Preference>(CHANGE_LANGUAGE) as Preference
            languagePreference.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val viewIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(viewIntent)
                true
            }

            // set summaries
            val sh = preferenceManager.sharedPreferences
            alarmOnPreference.isChecked = sh.getBoolean(ALARM_ON, false)
            alarmTimePreference.summary = sh.getString(ALARM_TIME, "09:00")
            alarmMessagePreference.summary = sh.getString(ALARM_MESSAGE, DEFAULT_MESSAGE)
            languagePreference.summary = Locale.getDefault().displayLanguage

            alarmReceiver = AlarmReceiver()
        }

        override fun onDisplayPreferenceDialog(preference: Preference) {

            var dialogFragment: DialogFragment? = null
            if (preference is TimePreference) {
                dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey())
            }
            if (dialogFragment != null) {
                // The dialog was created (it was one of our custom Preferences), show the dialog for it
                dialogFragment.setTargetFragment(this, 0)
                dialogFragment.show(
                    parentFragmentManager, "TimePreferenceDialogFragmentCompat"
                )
            } else {
                super.onDisplayPreferenceDialog(preference)
            }
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
            if (key == ALARM_ON) {
                val isChecked = sharedPreferences.getBoolean(ALARM_ON, false)
                alarmOnPreference.isChecked = isChecked
                if (isChecked) {
                    val time = sharedPreferences.getString(ALARM_TIME, "09:00")
                    val message = sharedPreferences.getString(ALARM_MESSAGE, DEFAULT_MESSAGE)
                    alarmReceiver.setRepeatingAlarm(requireContext(), time!!, message!!)
                } else {
                    alarmReceiver.cancelAlarm(requireContext())
                }
            }

            if (key == ALARM_TIME) {
                alarmTimePreference.summary = sharedPreferences.getString(ALARM_TIME, "09:00")

                val isChecked = sharedPreferences.getBoolean(ALARM_ON, false)
                if (isChecked) {
                    val time = sharedPreferences.getString(ALARM_TIME, "09:00")
                    val message = sharedPreferences.getString(ALARM_MESSAGE, DEFAULT_MESSAGE)
                    alarmReceiver.setRepeatingAlarm(requireContext(), time!!, message!!)
                }
            }

            if (key == ALARM_MESSAGE) {
                alarmMessagePreference.summary = sharedPreferences.getString(ALARM_MESSAGE, DEFAULT_MESSAGE)

                val isChecked = sharedPreferences.getBoolean(ALARM_ON, false)
                if (isChecked) {
                    val time = sharedPreferences.getString(ALARM_TIME, "09:00")
                    val message = sharedPreferences.getString(ALARM_MESSAGE, DEFAULT_MESSAGE)
                    alarmReceiver.setRepeatingAlarm(requireContext(), time!!, message!!)
                }
            }
        }
    }
}