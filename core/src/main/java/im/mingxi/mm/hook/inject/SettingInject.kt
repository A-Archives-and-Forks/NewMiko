package im.mingxi.mm.hook.inject

import android.content.Context
import android.widget.AdapterView
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.controller.HomeController
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.Reflex.findMethodObj
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.hookAfterIfEnable
import im.mingxi.miko.util.hookBeforeIfEnable
import im.mingxi.mm.struct.MMPreferenceAdapter
import im.mingxi.mm.struct.preferenceClass

@FunctionHookEntry(itemName = "设置注入", itemType = FunctionHookEntry.WECHAT_ITEM)
class SettingInject : BaseFuncHook(defaultEnabled = true), IFinder {
    private val preferenceTitle = DexMethodDescriptor(this, "${simpleTAG}.Method.preferenceTitle")
    override fun initOnce(): Boolean {

        // 创建入口
        val ctors = MMPreferenceAdapter.hostClass.declaredConstructors
        ctors.forEach {
            it.hookAfterIfEnable(this) { param ->
                // 排除非设置防止卡顿
                if (!XPHelper.getStackData()
                        .contains("com.tencent.mm.plugin.setting.ui.setting.SettingsUI.onCreate")
                ) return@hookAfterIfEnable
                val app = HookEnv.hostActivity
                val preference =
                    preferenceClass.getDeclaredConstructor(Context::class.java).newInstance(app)
                findMethodObj(preference).setReturnType(Void.TYPE).setParams(String::class.java)
                    .get().invoke(preference, "new_miko_entry")
                /*    for (method in preference.javaClass.declaredMethods) {
                     if (method.parameterCount == 1 && method.parameterTypes[0] == CharSequence::class.java) {
                         method.isAccessible = true
                         method.invoke(preference, "NewMiko")
                         break
                     }
                 }*/
                preferenceTitle.toMethod(loader).invoke(preference, "NewMiko")
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
        ).hookBeforeIfEnable(this) {
            val adapterView = it.args[0] as AdapterView<*>
            val adapter = adapterView.adapter
            val position = it.args[2] as Int
            val preference = findMethodObj(adapter).setMethodName("getItem").get()
            val preferenceInst = preference.invoke(adapter, position)
            if (preferenceInst != null) {
                if ("NewMiko" == preferenceInst.toString()) {
                    HomeController.openHomeActivity()
                    it.resultNull()
                }
            }
        }
        return true
    }

    override fun dexFind(finder: DexFinder) {
        with(finder) {
            preferenceTitle.findDexMethod {
                searchPackages("com.tencent.mm.ui.base.preference")

                matcher {
                    usingNumbers(0)
                    returnType(Void.TYPE)
                    paramCount(1)
                    paramTypes(CharSequence::class.java)
                }

            }
        }


    }
}