package im.mingxi.loader.xp82

import de.robv.android.xposed.IXposedHookZygoteInit
import im.mingxi.loader.util.PathUtil

class ZygoteInit : IXposedHookZygoteInit {

    @Throws(Throwable::class)
    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        PathUtil.moduleApkPath = startupParam.modulePath
    }
}