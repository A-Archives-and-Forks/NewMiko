package im.mingxi.mm.hook

import android.content.ContentValues
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.debug.DebugUtil
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.toAppClass

@FunctionHookEntry(itemName = "防撤回", itemType = FunctionHookEntry.WECHAT_ITEM)
class AntiRevoke : SwitchHook(), IFinder {
    val msgCacheMap = HashMap<Long, Any>()
    lateinit var msgInfoStorage: Any
    override val name: String
        get() = "消息防撤回"
    override val uiItemLocation: Array<String>
        get() = arrayOf("聊天", "消息")


    private val getSendTip =
        DexMethodDescriptor(this, "${simpleTAG}.Method.getSendTip")

    override fun initOnce(): Boolean {
        val sqliteClass = "com.tencent.wcdb.database.SQLiteDatabase".toAppClass()!!
        val updateWithOnConflict = sqliteClass
            .resolve()
            .firstMethod {
                name = "updateWithOnConflict"
                parameterCount(5)
            }.self
        val delete = "com.tencent.wcdb.database.SQLiteDatabase".toAppClass()!!
            .resolve()
            .firstMethod {
                name = "delete"
                parameters(String::class.java, String::class.java, Array<String>::class.java)
            }
            .self

        /*val getSendTip = Reflex.findMethod(Reflex.loadClass("com.tencent.mm.storage.s9")).setParams(
            Reflex.loadClass("Lcom/tencent/mm/storage/q9;"),
            Boolean::class.java,
            Boolean::class.java
        )*/
        //    .get()//Reflex.findMethod(Reflex.loadClass("com.tencent.mm.storage.s9")).setMethodName("a9").get()


        getSendTip.toMethod(loader)
            .hookAfterIfEnable {
            val msgInfo = it.args[0]
            this.msgInfoStorage = it.thisObject

            if (msgInfo != null) {
                val msgId = Reflex.findFieldObj(msgInfo).setFieldName("field_msgId").get()
                    .get(msgInfo) as Long/*msgInfo
                    .resolve()
                    .firstField {
                        name = "field_msgId"
                        superclass()
                    }.of(msgInfo).get() as Long*/
                msgCacheMap.put(
                    msgId, msgInfo
                )
                DebugUtil.printAllField(msgInfo)
            }

        }

        delete.hookBeforeIfEnable {
            it.result = 1
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

                        getSendTip.toMethod(loader)
                            .invoke(this.msgInfoStorage, msgInfo, false, false)
                    }
                    param.result = 1
                }
            }
        }


        return true
    }

    override fun dexFind(finder: DexFinder) {
        with(finder) {
            getSendTip.findDexMethod {
                searchPackages("com.tencent.mm.storage")

                matcher {
                    usingStrings("check table name from id:%d table:%s getTableNameByLocalId:%s")
                }
            }
        }
    }
}