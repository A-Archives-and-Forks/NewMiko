package im.mingxi.mm.manager.impl

import android.content.Context
import im.mingxi.loader.XposedPackage
import im.mingxi.miko.util.HookEnv
import im.mingxi.mm.manager.MMEnvManager

@Suppress("DEPRECATION")
class MMEnvManagerImpl : MMEnvManager {
    override fun getCurrentUin(): String {
        val nPackage: String = XposedPackage.packageName
        return HookEnv.hostContext.getSharedPreferences(
            "${nPackage}_preferences",
            Context.MODE_MULTI_PROCESS
        )
            .getString("login_user_name", "") ?: ""
    }

    override fun getCurrentName(): String {
        val nPackage: String = XposedPackage.packageName
        return HookEnv.hostContext.getSharedPreferences(
            "${nPackage}_preferences",
            Context.MODE_MULTI_PROCESS
        )
            .getString("last_login_nick_name", "") ?: ""
    }

    override fun getPhoneNumber(): String {
        val nPackage: String = XposedPackage.packageName
        return HookEnv.hostContext.getSharedPreferences(
            "${nPackage}_preferences",
            Context.MODE_MULTI_PROCESS
        )
            .getString("last_login_bind_mobile", "") ?: ""
    }

    override fun getWxId(): String {
        val nPackage: String = XposedPackage.packageName
        return HookEnv.hostContext.getSharedPreferences(
            "${nPackage}_preferences",
            Context.MODE_MULTI_PROCESS
        )
            .getString("login_weixin_username", "") ?: ""
    }

    override fun isLogin(): Boolean {
        val nPackage: String = XposedPackage.packageName
        return HookEnv.hostContext.getSharedPreferences(
            "${nPackage}_preferences",
            Context.MODE_MULTI_PROCESS
        )
            .getBoolean("isLogin", false)
    }

    override fun getAvatarFilePath(): String {
        val nPackage: String = XposedPackage.packageName
        return HookEnv.hostContext.getSharedPreferences(
            "${nPackage}_preferences",
            Context.MODE_MULTI_PROCESS
        )
            .getString("last_avatar_path", "") ?: ""
    }
}