package im.mingxi.miko.util

import android.app.Activity
import im.mingxi.miko.startup.util.XRes.addAssetsPath


object ActivityUtil {
    fun getTopActivity(): Activity? {
        try {
            val ActivityThread: Any = Reflex.findField(Class.forName("android.app.ActivityThread"))
                .setFieldName("sCurrentActivityThread").get().get(null)
            val activities =
                Reflex.findFieldObj(ActivityThread).setFieldName("mActivities") as Map<*, *>
            for (activityRecord in activities.values) {
                val isPause: Boolean =
                    Reflex.findFieldObj(activityRecord).setFieldName("paused").get()
                        .getBoolean(activityRecord)
                if (!isPause) {
                    val act = Reflex.findFieldObj(activityRecord).setFieldName("activity")
                        .get() as Activity
                    addAssetsPath(act)
                    return act
                }
            }
        } catch (e: Exception) {
        }
        return null
    }
}