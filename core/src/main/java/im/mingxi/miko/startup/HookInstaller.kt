package im.mingxi.miko.startup

import im.mingxi.miko.hook.BaseComponentHook
import im.mingxi.miko.hook.BaseFuncHook

object HookInstaller {
    private val mItems = ArrayList<BaseFuncHook>()
    val uiList = ArrayList<BaseComponentHook>()

    fun scanAndInstall() {
        // 先获取所有hook条目

        val resultMet = Class.forName(
            "im.mingxi.miko.annotation.result.FunctionHookEntryResult"
        ).getDeclaredMethod("getAnnotatedFunctionHookEntryList")
        resultMet.isAccessible = true
        val allItems = resultMet.invoke(null) as Array<BaseFuncHook>
        val loadType = StartUp.hostType

        // 筛选所有可用的hook条目
        allItems.forEach {
            when (loadType) {
                0 -> mItems.add(it)
                1 -> if (it.TAG.contains("mm")) mItems.add(it)

                2 -> if (it.TAG.contains("mobileqq")) mItems.add(it)

            }
        }

        // 加载可用条目
        mItems.forEach {
            it.initialize()
        }

        // 筛选ui条目
        mItems.forEach {
            if (it is BaseComponentHook) uiList.add(it)
        }
    }

}