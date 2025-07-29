package im.mingxi.miko.util

import im.mingxi.miko.startup.HookInstaller
import im.mingxi.mm.api.AutoFinder
import im.mingxi.net.Beans
import im.mingxi.net.bean.ModuleInfo

object SignUtil {

    val sign: String by lazy {
        return@lazy "MikoSign{Module = ${Beans.getBean(ModuleInfo::class.java)}(${HookInstaller.mItems.size})(${AutoFinder.structs.size})}, Host = ${
            AppUtil.getVersionCode(
                HookEnv.hostContext
            )
        }}"
    }
}