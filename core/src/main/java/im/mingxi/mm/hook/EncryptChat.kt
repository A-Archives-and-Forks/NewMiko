package im.mingxi.mm.hook

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import im.mingxi.core.databinding.EncryptChatBinding
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.loader.bridge.XPBridge.HookParam
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.AESUtils
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.hookBeforeIfEnable
import im.mingxi.miko.util.toAppClass
import im.mingxi.miko.util.xpcompat.XPHelpers
import org.luckypray.dexkit.DexKitBridge


@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class EncryptChat : SwitchHook(), IFinder {
    override val name: String
        get() = "聊天加密"
    override val uiItemLocation: String
        get() = FuncRouter.CHAT
    override val description: CharSequence =
        "加密发送出去的信息，采用AES256加密，只有装载此模块且密匙相同的人可以解密(双击)，默认使用miko统一密匙，点击设置自定义密匙，热更新可能导致失效"
    override val onClick: ((View) -> Unit)
        get() = { v ->
            XDialog.create(v.context).apply {
                title = "设置自定义密匙"
                val binding = EncryptChatBinding.inflate(layoutInflater)

                val chatEdit = binding.chatEdit
                val randomButton = binding.chatRandomBtn
                confirmButton.text = "恢复成miko统一公匙"

                chatEdit.setText(mConfig.decodeString("$TAG.config.key", publicKey))

                chatEdit.doAfterTextChanged {
                    if (!TextUtils.isEmpty(it.toString()))
                        mConfig.encode("$TAG.config.key", it.toString())
                }

                randomButton.setOnClickListener {
                    chatEdit.setText(AESUtils.generateKeyString())
                }

                confirmButtonClickListener = View.OnClickListener {
                    chatEdit.setText(publicKey)
                    mConfig.encode("$TAG.config.key", publicKey)
                }

                contain(binding.root)

            }.build()
        }
    private val publicKey = "Y/wNIGjOmoeyRlUFPe2yh5YfKUrMIt7aPZKo5OBDl9o=\n"

    @SuppressLint("SuspiciousIndentation")
    override fun initOnce(): Boolean {
        val target = cache.decodeString("${simpleTAG}.Method.mOnClick")!!
        val method = DexMethodDescriptor(target).declaringClass.toAppClass().declaredMethods[0]
        XPBridge.log(method)
        hookBeforeIfEnable(method) { param ->
            val field = param.thisObject::class.java.declaredFields[0]
            field.isAccessible = true
            val chatFooter = field.get(param.thisObject)
            var weEdit: Any? = null
            for (f in chatFooter.javaClass.declaredFields) {
                if (f.type
                        .equals(
                            Reflex.loadClass("com.tencent.mm.ui.widget.cedit.api.MMFlexEditText")
                                .getInterfaces()[0]
                        )
                ) {
                    weEdit = f.get(chatFooter)
                    break
                }
            }
            val origin: String = XPHelpers.callMethod(weEdit, "getText").toString()
            val key = mConfig.decodeString("$TAG.config.key", publicKey)!!
            val encrypt = AESUtils.encrypt("MikoABCD${origin}", key)
            XPHelpers.callMethod(weEdit, "setText", encrypt)
        }

        Reflex.findMethod(TextView::class.java).setMethodName("setText").get()
            // TextView::class.java.methods.filter { name == "setText" }.forEach {

            .hookBeforeIfEnable { param: HookParam ->

                if (param.args[0] == null
                ) return@hookBeforeIfEnable
                val origin = param.args[0].toString()

                if (XPHelper.getStackData()
                        .contains("com.tencent.mm.ui.widget.cedit.api.MMFlexEditText")
                ) {
                    //XPBridge.log(XPHelper.getStackData())
                    return@hookBeforeIfEnable
                }
                val key = mConfig.decodeString("$TAG.config.key", publicKey)!!
                try {
//                        XPBridge.log("origin:${origin}")
//                         XPBridge.log("key:$key")
                    val decrypt = AESUtils.decrypt(origin, key)
                    XPBridge.log(decrypt)
                    if (decrypt.startsWith("MikoABCD")) {
                        //if (decrypt.replace("MikoABCD", "").contains("(${decrypt.replace("MikoABCD", "")})")) return@hookBeforeIfEnable
                        ///XPBridge.log(XPHelper.getStackData())
                        param.args[0] = decrypt.replace("MikoABCD", "")
                    }
                } catch (_: Exception) {
                    //XPBridge.log("解密失败")
                }
            }

        return true
    }

    private val mOnClick = DexDesc("${simpleTAG}.Method.mOnClick")

    override fun dexFind(finder: DexKitBridge) {

        mOnClick.findDexMethod(finder) {
            searchPackages("com.tencent.mm.pluginsdk.ui.chat")
            matcher {
                usingStrings("send msg onClick")
                declaredClass {
                    methodCount(2)
                }

            }
        }
    }
}