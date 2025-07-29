package im.mingxi.mm.hook

import android.content.ContentValues
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.dexkit.OFinder
import im.mingxi.miko.util.toAppClass
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Method

@FunctionHookEntry(itemName = "防撤回", itemType = FunctionHookEntry.WECHAT_ITEM)
class AntiRevoke : SwitchHook(), IFinder, OFinder {
    val msgCacheMap = HashMap<Long, Any>()
    lateinit var msgInfoStorage: Any
    override val name: String
        get() = "消息防撤回"
    override val uiItemLocation: String
        get() = "聊天"

    private object MethodGetSendTip : DexDesc("AntiRevoke.MethodGetSendTip")
    private object MethodNativeFileSystem : DexDesc("AntiRevoke.Method.NativeFileSystem")

    private lateinit var updateWithOnConflict: Method
    private lateinit var sqliteClass: Class<*>
    private lateinit var delete: Method

    override fun initOnce(): Boolean {


        MethodGetSendTip.toMethod()
            .hookAfterIfEnable {
                val msgInfo = it.args[0]
                this.msgInfoStorage = it.thisObject

                if (msgInfo != null) {
                    val msgId = Reflex.findFieldObj(msgInfo).setFieldName("field_msgId").get()
                        .get(msgInfo) as Long
                    msgCacheMap.put(
                        msgId, msgInfo
                    )
                }

            }

        delete.hookBeforeIfEnable {
            // XPHelper.getStackData().d()
            val str = it.args[0].toString()
            if (str.contains("ImgInfo2") || str.contains("voiceinfo") || str.contains("videoinfo2") || str.contains(
                    "WxFileIndex2"
                )
            )
                it.result = 1
        }

        MethodNativeFileSystem.toMethod().declaringClass.declaredMethods.forEach { method ->
            if (method.returnType == Boolean::class.java && method.parameterTypes[0] == String::class.java && method.parameterCount == 1 && "qwertyuiopasdfghjklzxcvbnm".contains(
                    method.name
                ) && method.name != MethodNativeFileSystem.toMethod().name
            ) {
                method.hookBeforeIfEnable { param ->
                    val str = param.thisObject.resolve().firstField {
                        type = String::class.java
                    }.self
                    str.isAccessible = true
                    val result = str.get(param.thisObject) as String
                    if (XPHelper.getStackData()
                            .contains("com.tencent.mm.modelimage")
                    ) return@hookBeforeIfEnable
                    if (result.contains("image2") || result.contains("emoji") || result.contains("voice2") || result.contains(
                            "video"
                        )
                    ) {
                        param.resultTrue()
                    }
                }
            }
        }

        updateWithOnConflict.hookBeforeIfEnable { param ->
            if (param.args[0].toString() != "message") return@hookBeforeIfEnable
            val values = param.args[1] as ContentValues
            if (values.containsKey("msgId") && values.containsKey("type") && values.containsKey("content")) {
                val content = values.getAsString("content")
                if (content.contains("撤回了一条消息") && !content.contains("你撤回了一条消息")) {
                    val msgInfo = msgCacheMap[values.getAsLong("msgId")]
                    if (msgInfo != null) {
                        val createTime =
                            (Reflex.findFieldObj(msgInfo).setFieldName("field_createTime").get()
                                .get(msgInfo) as Long) + 1
                        Reflex.findFieldObj(msgInfo).setFieldName("field_content").get()
                            .set(msgInfo, "已阻止一条消息撤回")
                        Reflex.findFieldObj(msgInfo).setFieldName("field_createTime").get()
                            .set(msgInfo, createTime)
                        Reflex.findFieldObj(msgInfo).setFieldName("field_type").get()
                            .set(msgInfo, values.getAsInteger("type"))
                        Reflex.findFieldObj(msgInfo).setFieldName("x0").get()
                            .set(msgInfo, "已阻止一条消息撤回")
                        try {
                            MethodGetSendTip.toMethod()
                                .invoke(this.msgInfoStorage, msgInfo, false, false)
                        } catch (_: Exception) {
                            MethodGetSendTip.toMethod()
                                .invoke(this.msgInfoStorage, msgInfo, false)
                        }
                    }
                    param.result = 1
                }
            }
        }


        return true
    }

    override fun dexFind(dexkit: DexKitBridge) {

        MethodGetSendTip.findDexMethod(dexkit) {
            searchPackages("com.tencent.mm.storage")

            matcher {
                returnType(Long::class.java)
                usingStrings("check table name from id:%d table:%s getTableNameByLocalId:%s")
            }
        }
        MethodNativeFileSystem.findDexMethod(dexkit) {
            searchPackages("com.tencent.mm")
            matcher {
                usingStrings("VFS.NativeFileSystem", "Cannot create directory")
            }
        }
    }

    override fun onInstance() {
        sqliteClass = "com.tencent.wcdb.database.SQLiteDatabase".toAppClass()
        updateWithOnConflict = sqliteClass
            .resolve()
            .firstMethod {
                name = "updateWithOnConflict"
                parameterCount(5)
            }.self.apply { isAccessible = true }
        delete = "com.tencent.wcdb.database.SQLiteDatabase".toAppClass()
            .resolve()
            .firstMethod {
                name = "delete"
                parameters(String::class.java, String::class.java, Array<String>::class.java)
            }
            .self.apply { isAccessible = true }
    }

}