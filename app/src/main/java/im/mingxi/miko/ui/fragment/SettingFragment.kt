package im.mingxi.miko.ui.fragment

import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import im.mingxi.miko.R
import im.mingxi.miko.util.AppUtil
import im.mingxi.miko.util.RootUtil.deleteAsRoot


class SettingFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        val dayNightSwitch: SwitchPreferenceCompat = findPreference("dark_mode")!!
        val hideDesktopIconSwitch: SwitchPreferenceCompat = findPreference("hide_desktop_icon")!!
        val clearCache: Preference = findPreference("clear_cache")!!

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

        clearCache.setOnPreferenceClickListener { _ ->
            if (AppUtil.isRoot()) {
                var tip = "清除缓存失败"
                if (deleteAsRoot("/data/data/com.tencent.mm/files/Miko_MMKV/")) tip = "缓存已清除"
                Toast.makeText(app, tip, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(app, "请先授予超级用户权限", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }



}