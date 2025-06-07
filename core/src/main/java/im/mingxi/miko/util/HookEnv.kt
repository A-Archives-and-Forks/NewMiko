package im.mingxi.miko.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context


@SuppressLint("StaticFieldLeak")
object HookEnv {
    lateinit var hostContext: Context
    lateinit var hostActivity: Activity
    lateinit var hostApplication: Application
    lateinit var hostClassLoader: ClassLoader
    lateinit var moduleClassLoader: ClassLoader
}