package im.mingxi.mm.hook.inject

import android.content.Context
import android.widget.AdapterView
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.controller.HomeController
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.Reflex.findMethod
import im.mingxi.miko.util.Reflex.findMethodObj
import im.mingxi.miko.util.Reflex.loadClass
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.mm.struct.preferenceClass
import im.mingxi.net.Beans
import im.mingxi.net.bean.ModuleInfo
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Method
import java.util.LinkedList


@FunctionHookEntry(itemName = "设置注入", itemType = FunctionHookEntry.WECHAT_ITEM)
class SettingInject : SwitchHook(defaultEnabled = true), IFinder {
    private val MethodPreferenceTitle = DexDesc("${simpleTAG}.MethodPreferenceTitle")
    private val settingName = "NewMiko"

    /**
     * 微信不会主动销毁缓存导致第二次进入设置无入口的问题
     */
    // private var mCacheItem: Any? = null
    private val settingTip = Beans.getBean(ModuleInfo::class.java).versionName
    override val name: String
        get() = "允许在设置界面创建入口"
    override val uiItemLocation: String
        get() = "模块设置及调试"

    override fun initOnce(): Boolean {

        // 创建入口
        val ctors = findMethod(loadClass("com.tencent.mm.ui.base.preference.MMPreference"))
            .setMethodName("createAdapter")
            .get()
            .returnType.declaredConstructors
        ctors.forEach {
            it.hookAfterIfEnable { param ->
                //if (mCacheItem != null) return@hookAfterIfEnable
                // 排除非设置防止全部注入
                if (!XPHelper.getStackData()
                        .contains("com.tencent.mm.plugin.setting.ui.setting.SettingsUI.onCreate")
                ) return@hookAfterIfEnable
                // 新方案
                // 具体原理我不记得了
                // 去年写的，没写注解我也看不懂
                if ((Reflex.findFieldObj(param.thisObject)
                        .setReturnType(LinkedList::class.java)
                        .get()[param.thisObject] as LinkedList<*>)
                        .size
                    == 1
                ) return@hookAfterIfEnable
                val app = HookEnv.hostActivity
                // 创建basePreference对象
                val preference =
                    preferenceClass.getDeclaredConstructor(Context::class.java).newInstance(app)
                // 设置preference的key
                // 其实这一句应该已经没用了
                // 旧Miko的逻辑就是用key判断点击事件
                findMethodObj(preference).setReturnType(Void.TYPE).setParams(String::class.java)
                    .get().invoke(preference, "new_miko_entry")
                // 设置标题
                MethodPreferenceTitle.toMethod().invoke(preference, settingName)
                // 设置右侧提示
                findTipMethod().invoke(preference, settingTip)
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
                //mCacheItem = preference
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
                if ("$settingName $settingTip" == preferenceInst.toString()) {
                    HomeController.openHomeActivity()
                    it.resultNull()
                }
            }
        }
        return true
    }

    override fun dexFind(finder: DexKitBridge) {
        MethodPreferenceTitle.findDexMethod(finder) {
                searchPackages("com.tencent.mm.ui.base.preference")

                matcher {
                    usingNumbers(0)
                    returnType(Void.TYPE)
                    paramCount(1)
                    paramTypes(CharSequence::class.java)
                }

        }

    }

    private fun findTipMethod(): Method {
        val tipCls = MethodPreferenceTitle.toMethod().declaringClass
        tipCls.declaredMethods.forEach {
            if (it.parameterCount == 1 && it.parameterTypes[0] == CharSequence::class.java) {
                if (it != MethodPreferenceTitle.toMethod()) {
                    it.isAccessible = true
                    return it
                }
            }
        }
        throw RuntimeException("未找到tip方法")
    }
}