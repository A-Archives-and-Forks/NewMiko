package im.mingxi.mm.hook

import android.app.Activity
import android.content.Context
import android.widget.AdapterView
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.Reflex.findMethod
import im.mingxi.miko.util.Reflex.findMethodObj
import im.mingxi.miko.util.Reflex.loadClass
import im.mingxi.miko.util.dexkit.DexKit
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.hookAfterIfEnable
import im.mingxi.mm.struct.preferenceClass
import java.util.LinkedList

@FunctionHookEntry
class DisplayFriendInfo : SwitchHook() {
    private lateinit var friendInfo: String
    override val name: String
        get() = "展示好友详细信息"
    override val uiItemLocation: String
        get() = FuncRouter.CONTACTS

    override fun initOnce(): Boolean {
        // 建一个钩子去获取最近打开的联系人信息
        // 虽然这段很草台班子
        hookAfterIfEnable(Reflex.findMethod(Activity::class.java).setMethodName("onCreate").get()) {
            val intent = (it.thisObject as Activity).intent
            val user = intent.getStringExtra("Contact_User")
            // XPBridge.log(intent.extras)
            if (user != null) friendInfo = user
        }
        // 创建入口
        val ctors = findMethod(loadClass("com.tencent.mm.ui.base.preference.MMPreference"))
            .setMethodName("createAdapter")
            .get()
            .returnType.declaredConstructors
        ctors.forEach {
            it.hookAfterIfEnable { param ->
                if (!XPHelper.getStackData()
                        .contains("com.tencent.mm.plugin.profile.ui.ProfileSettingUI.onCreate")
                )
                    return@hookAfterIfEnable
                if ((Reflex.findFieldObj(param.thisObject)
                        .setReturnType(LinkedList::class.java)
                        .get()[param.thisObject] as LinkedList<*>)
                        .size
                    == 1
                ) return@hookAfterIfEnable
                val app = HookEnv.hostActivity

                val preference =
                    preferenceClass.getDeclaredConstructor(Context::class.java).newInstance(app)

                findMethodObj(preference).setReturnType(Void.TYPE).setParams(String::class.java)
                    .get().invoke(preference, "new_miko_friend_hook")

                DexKit.requireMethodFromCache("SettingInject.MethodPreferenceTitle")
                    .toMethod(loader).invoke(preference, friendInfo)

                // 通过反射添加进适配器
                for (method in param.thisObject.javaClass.declaredMethods) {
                    if (method.parameterCount == 2
                        && method.getParameterTypes()[0]
                        == preferenceClass
                        && method.getParameterTypes()[1] == Int::class.java
                    ) {
                        method.isAccessible = true
                        method.invoke(param.thisObject, preference, 0)
                        break
                    }

                }

            }
        }

        // 处理点击事件
        DexMethodDescriptor(desc = "Lcom/tencent/mm/ui/widget/listview/PullDownListView;->onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V").toMethod(
            loader
        ).hookBeforeIfEnable {
            val adapterView = it.args[0] as AdapterView<*>
            val adapter = adapterView.adapter
            val position = it.args[2] as Int
            val preference = findMethodObj(adapter).setMethodName("getItem").get()
            val preferenceInst = preference.invoke(adapter, position)
            if (preferenceInst != null) {
                if (name == preferenceInst.toString()) {
                    it.resultNull()
                }
            }
        }
        return true
    }
}