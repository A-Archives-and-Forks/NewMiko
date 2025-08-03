package im.mingxi.mobileqq.hook.inject

import android.app.Activity
import android.widget.LinearLayout
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.OFinder
import im.mingxi.miko.util.xpcompat.XPHelpers
import java.lang.reflect.Method


@FunctionHookEntry(itemType = FunctionHookEntry.QQ_ITEM)
class QQPlusMenuInject : SwitchHook(true), OFinder {
    override val name: String
        get() = "允许在加号菜单注入入口"
    override val uiItemLocation: String
        get() = FuncRouter.MODULE_SETTINGS_AND_DEBUG

    private var ItemCache: Any? = null

    private lateinit var createAndAttachItemsView: Method
    private lateinit var mOnClick: Method

    override fun initOnce(): Boolean = true.also {
        createAndAttachItemsView.hookBeforeIfEnable { param ->
            val mMenu = param.args[1] as MutableList<Any?>
            val mAddItem: Any? =
                XPHelpers.findConstructorBestMatch(
                    Reflex.loadClass("com.tencent.widget.PopupMenuDialog\$MenuItem"),
                    arrayOf<Class<*>>(
                        Int::class.javaPrimitiveType!!,
                        String::class.java,
                        String::class.java,
                        Int::class.javaPrimitiveType!!
                    )
                )
                    .newInstance(1666, "Miko", "打死南浔", im.mingxi.core.R.drawable.more)
            ItemCache = mAddItem
            mMenu.add(0, mAddItem)
        }
    }

    override fun onInstance() {
        createAndAttachItemsView =
            Reflex.findMethod(Reflex.loadClass("com.tencent.widget.PopupMenuDialog"))
                .setMethodName("createAndAttachItemsView")
                .setParams(
                    Activity::class.java,
                    List::class.java,
                    LinearLayout::class.java,
                    Boolean::class.java
                )
                .get()
        mOnClick = Reflex.findMethod(Reflex.loadClass("com.tencent.widget.PopupMenuDialog"))
            .setMethodName("onClick")
            .get()
    }

}