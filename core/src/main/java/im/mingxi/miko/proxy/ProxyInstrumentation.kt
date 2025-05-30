package im.mingxi.miko.proxy

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.app.UiAutomation
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.os.PersistableBundle
import android.os.TestLooperManager
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.annotation.RequiresApi
import im.mingxi.miko.startup.util.XRes.addAssetsPath

class ProxyInstrumentation(private val mBase: Instrumentation) : Instrumentation() {
    @Throws(
        InstantiationException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    override fun newActivity(cl: ClassLoader, className: String, intent: Intent): Activity {
        try {
            return mBase.newActivity(cl, className, intent)
        } catch (e: Exception) {
            if (IActivityManagerHandler.isModuleActivity(className)) {
                return ActivityProxyManager.ModuleClassLoader!!.loadClass(className)
                    .newInstance() as Activity
            }
            throw e
        }
    }

    override fun onCreate(arguments: Bundle) {
        mBase.onCreate(arguments)
    }

    override fun start() {
        mBase.start()
    }

    override fun onStart() {
        mBase.onStart()
    }

    override fun onException(obj: Any, e: Throwable): Boolean {
        return mBase.onException(obj, e)
    }

    override fun sendStatus(resultCode: Int, results: Bundle) {
        mBase.sendStatus(resultCode, results)
    }

    override fun addResults(results: Bundle) {
        mBase.addResults(results)
    }

    override fun finish(resultCode: Int, results: Bundle) {
        mBase.finish(resultCode, results)
    }

    override fun setAutomaticPerformanceSnapshots() {
        mBase.setAutomaticPerformanceSnapshots()
    }

    override fun startPerformanceSnapshot() {
        mBase.startPerformanceSnapshot()
    }

    override fun endPerformanceSnapshot() {
        mBase.endPerformanceSnapshot()
    }

    override fun onDestroy() {
        mBase.onDestroy()
    }

    override fun getContext(): Context {
        return mBase.context
    }

    override fun getComponentName(): ComponentName {
        return mBase.componentName
    }

    override fun getTargetContext(): Context {
        return mBase.targetContext
    }

    override fun getProcessName(): String {
        return mBase.processName
    }

    override fun isProfiling(): Boolean {
        return mBase.isProfiling
    }

    override fun startProfiling() {
        mBase.startProfiling()
    }

    override fun stopProfiling() {
        mBase.stopProfiling()
    }

    override fun setInTouchMode(inTouch: Boolean) {
        mBase.setInTouchMode(inTouch)
    }

    override fun waitForIdle(recipient: Runnable) {
        mBase.waitForIdle(recipient)
    }

    override fun waitForIdleSync() {
        mBase.waitForIdleSync()
    }

    override fun runOnMainSync(runner: Runnable) {
        mBase.runOnMainSync(runner)
    }

    override fun startActivitySync(intent: Intent): Activity {
        return mBase.startActivitySync(intent)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun startActivitySync(intent: Intent, options: Bundle?): Activity {
        return mBase.startActivitySync(intent, options)
    }

    override fun addMonitor(monitor: ActivityMonitor) {
        mBase.addMonitor(monitor)
    }

    override fun addMonitor(cls: String, result: ActivityResult, block: Boolean): ActivityMonitor {
        return mBase.addMonitor(cls, result, block)
    }

    override fun addMonitor(
        filter: IntentFilter,
        result: ActivityResult,
        block: Boolean
    ): ActivityMonitor {
        return mBase.addMonitor(filter, result, block)
    }

    override fun checkMonitorHit(monitor: ActivityMonitor, minHits: Int): Boolean {
        return mBase.checkMonitorHit(monitor, minHits)
    }

    override fun waitForMonitor(monitor: ActivityMonitor): Activity {
        return mBase.waitForMonitor(monitor)
    }

    override fun waitForMonitorWithTimeout(monitor: ActivityMonitor, timeOut: Long): Activity {
        return mBase.waitForMonitorWithTimeout(monitor, timeOut)
    }

    override fun removeMonitor(monitor: ActivityMonitor) {
        mBase.removeMonitor(monitor)
    }

    override fun invokeContextMenuAction(targetActivity: Activity, id: Int, flag: Int): Boolean {
        return mBase.invokeContextMenuAction(targetActivity, id, flag)
    }

    override fun invokeMenuActionSync(targetActivity: Activity, id: Int, flag: Int): Boolean {
        return mBase.invokeMenuActionSync(targetActivity, id, flag)
    }

    override fun sendCharacterSync(keyCode: Int) {
        mBase.sendCharacterSync(keyCode)
    }

    override fun sendKeyDownUpSync(key: Int) {
        mBase.sendKeyDownUpSync(key)
    }

    override fun sendKeySync(event: KeyEvent) {
        mBase.sendKeySync(event)
    }

    override fun sendPointerSync(event: MotionEvent) {
        mBase.sendPointerSync(event)
    }

    override fun sendStringSync(text: String) {
        mBase.sendStringSync(text)
    }

    override fun sendTrackballEventSync(event: MotionEvent) {
        mBase.sendTrackballEventSync(event)
    }

    @Throws(
        InstantiationException::class,
        IllegalAccessException::class,
        ClassNotFoundException::class
    )
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return mBase.newApplication(cl, className, context)
    }

    override fun callApplicationOnCreate(app: Application) {
        mBase.callApplicationOnCreate(app)
    }

    @Throws(InstantiationException::class, IllegalAccessException::class)
    override fun newActivity(
        clazz: Class<*>?,
        context: Context,
        token: IBinder,
        application: Application,
        intent: Intent,
        info: ActivityInfo,
        title: CharSequence,
        parent: Activity,
        id: String,
        lastNonConfigurationInstance: Any
    ): Activity {
        return mBase.newActivity(
            clazz,
            context,
            token,
            application,
            intent,
            info,
            title,
            parent,
            id,
            lastNonConfigurationInstance
        )
    }

    private fun inject(activity: Activity, icicle: Bundle?) {
        if (icicle != null) {
            val clzName = activity.javaClass.name
            if (IActivityManagerHandler.isModuleActivity(clzName)) {
                icicle.classLoader = ActivityProxyManager.ModuleClassLoader
            }
        }

        addAssetsPath(activity)
    }

    override fun callActivityOnCreate(
        activity: Activity, icicle: Bundle, persistentState: PersistableBundle
    ) {
        inject(activity, icicle)
        mBase.callActivityOnCreate(activity, icicle, persistentState)
    }

    override fun callActivityOnCreate(activity: Activity, icicle: Bundle) {
        inject(activity, icicle)
        mBase.callActivityOnCreate(activity, icicle)
    }

    override fun callActivityOnDestroy(activity: Activity) {
        mBase.callActivityOnDestroy(activity)
    }

    override fun callActivityOnRestoreInstanceState(
        activity: Activity,
        savedInstanceState: Bundle
    ) {
        mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState)
    }

    override fun callActivityOnRestoreInstanceState(
        activity: Activity, savedInstanceState: Bundle?, persistentState: PersistableBundle?
    ) {
        mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState)
    }

    override fun callActivityOnPostCreate(activity: Activity, savedInstanceState: Bundle?) {
        mBase.callActivityOnPostCreate(activity, savedInstanceState)
    }

    override fun callActivityOnPostCreate(
        activity: Activity, savedInstanceState: Bundle?, persistentState: PersistableBundle?
    ) {
        mBase.callActivityOnPostCreate(activity, savedInstanceState, persistentState)
    }

    override fun callActivityOnNewIntent(activity: Activity, intent: Intent) {
        mBase.callActivityOnNewIntent(activity, intent)
    }

    override fun callActivityOnStart(activity: Activity) {
        mBase.callActivityOnStart(activity)
    }

    override fun callActivityOnRestart(activity: Activity) {
        mBase.callActivityOnRestart(activity)
    }

    override fun callActivityOnPause(activity: Activity) {
        mBase.callActivityOnPause(activity)
    }

    override fun callActivityOnResume(activity: Activity) {
        mBase.callActivityOnResume(activity)
    }

    override fun callActivityOnStop(activity: Activity) {
        mBase.callActivityOnStop(activity)
    }

    override fun callActivityOnUserLeaving(activity: Activity) {
        mBase.callActivityOnUserLeaving(activity)
    }

    override fun callActivityOnSaveInstanceState(activity: Activity, outState: Bundle) {
        mBase.callActivityOnSaveInstanceState(activity, outState)
    }

    override fun callActivityOnSaveInstanceState(
        activity: Activity, outState: Bundle, outPersistentState: PersistableBundle
    ) {
        mBase.callActivityOnSaveInstanceState(activity, outState, outPersistentState)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun callActivityOnPictureInPictureRequested(activity: Activity) {
        mBase.callActivityOnPictureInPictureRequested(activity)
    }

    @Suppress("deprecation")
    override fun startAllocCounting() {
        mBase.startAllocCounting()
    }

    @Suppress("deprecation")
    override fun stopAllocCounting() {
        mBase.stopAllocCounting()
    }

    override fun getAllocCounts(): Bundle {
        return mBase.allocCounts
    }

    override fun getBinderCounts(): Bundle {
        return mBase.binderCounts
    }

    override fun getUiAutomation(): UiAutomation {
        return mBase.uiAutomation
    }

    override fun getUiAutomation(flags: Int): UiAutomation {
        return mBase.getUiAutomation(flags)
    }

    override fun acquireLooperManager(looper: Looper): TestLooperManager {
        return mBase.acquireLooperManager(looper)
    }
}