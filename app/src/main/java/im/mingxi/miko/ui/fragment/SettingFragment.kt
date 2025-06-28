package im.mingxi.miko.ui.fragment

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import im.mingxi.miko.R

class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val dayNightSwitch: SwitchPreferenceCompat = findPreference("dark_mode")!!
        val hideDesktopIconSwitch: SwitchPreferenceCompat = findPreference("hide_desktop_icon")!!

        dayNightSwitch.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            true
        }
        val app = requireActivity()
        val pm = app.packageManager
        val componentName = ComponentName(app, "im.mingxi.miko.MainActivity")
        val hideName = ComponentName(app, "im.mingxi.miko.MainActivity.Hide")
        hideDesktopIconSwitch.isChecked =
            pm.getComponentEnabledSetting(hideName) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        hideDesktopIconSwitch.setOnPreferenceChangeListener { _, isChecked ->
            if (isChecked as Boolean) {
                pm.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
                pm.setComponentEnabledSetting(
                    hideName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            } else {
                pm.setComponentEnabledSetting(
                    componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
                pm.setComponentEnabledSetting(
                    hideName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
            true
        }

    }
}