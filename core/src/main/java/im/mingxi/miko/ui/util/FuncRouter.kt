package im.mingxi.miko.ui.util

import im.mingxi.miko.hook.BaseComponentHook
import im.mingxi.miko.startup.HookInstaller

object FuncRouter {
    const val CHAT = "聊天"
    const val CONTACTS = "联系人"
    const val EXPLORE = "探索"
    const val RED_PACKET = "红包"
    const val SIMPLIFY = "净化"
    const val BEAUTIFY = "美化"
    const val AMUSEMENT = "娱乐"
    const val EXPERIMENTAL = "实验性功能"
    const val MODULE_SETTINGS_AND_DEBUG = "模块设置及调试"

    fun wrappers(): List<String> {
        return arrayOf(
            CHAT,
            CONTACTS,
            EXPLORE,
            RED_PACKET,
            SIMPLIFY,
            BEAUTIFY,
            AMUSEMENT,
            EXPERIMENTAL,
            MODULE_SETTINGS_AND_DEBUG
        ).toList()
    }

    fun items(wrapper: String): List<BaseComponentHook> {
        return HookInstaller.uiList.filter { it.uiItemLocation == wrapper }
    }
}