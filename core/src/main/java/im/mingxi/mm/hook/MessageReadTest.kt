package im.mingxi.mm.hook

import android.content.ContentValues
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.toAppClass
import java.lang.reflect.Method

// @FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class MessageReadTest : SwitchHook() {
    //, IFinder {
    override val name: String
        get() = "消息已读"
    override val uiItemLocation: String
        get() = FuncRouter.CHAT

    companion object {
        lateinit var chatFooter: Any
        lateinit var msgInfo: Any
    }

    private val onMsgInsertWithOnConflict: Method =
        Reflex.findMethod(
            Reflex.loadClass("com.tencent.wcdb.database.SQLiteDatabase")
        )
            .setParams(
                String::class.java,
                String::class.java,
                ContentValues::class.java,
                Int::class.javaPrimitiveType
            )
            .setMethodName("insertWithOnConflict")
            .get()

    private lateinit var msgInfoStorage: Any

    override fun initOnce(): Boolean {
        "com.tencent.mm.pluginsdk.ui.chat.ChatFooter".toAppClass().constructors.forEach {
            it.hookAfterIfEnable {
                chatFooter = it.thisObject
            }
        }



        Adapter.toMethod(loader).hookAfterIfEnable {

            val holder: Any = it.args[0]
            val position = it.args[1] as Int
            if (position == 0) return@hookAfterIfEnable

            val adapterInstance: Any = it.thisObject
            val itemView = holder::class.resolve()
                .firstField {
                    type = View::class.java
                    superclass()
                }.self.get(holder) as View
            val dataList =
                Reflex.findFieldObj(adapterInstance).setReturnType(List::class.java).get()
                    .get(adapterInstance) as java.util.List<*>
            val messageObject =
                dataList.get(position)!!

            msgInfo = messageObject
            Reflex.findFieldObj(msgInfo).setFieldName("x0").get()
                .set(msgInfo, "已阻止一条消息撤回")
            getSendTip.toMethod(loader)
                .invoke(this.msgInfoStorage, msgInfo, false, false)
            //itemView.setTag(im.mingxi.core.R.id.your_q9, messageObject)


        }

        getSendTip.toMethod(loader)
            .hookAfterIfEnable {
                this.msgInfoStorage = it.thisObject
            }
        return true
    }

    private val Adapter =
        DexMethodDescriptor(this, "${simpleTAG}.Method.Adapter")
    private val getSendTip =
        DexMethodDescriptor(this, "${simpleTAG}.Method.getSendTip")

//    override fun dexFind(finder: DexFinder) {
//        with(finder) {
//            Adapter.findDexMethod {
//                searchPackages("com.tencent.mm.ui.chatting.adapter")
//                matcher {
//                    usingStrings("_onBindViewHolder[", "MicroMsg.ChattingDataAdapterV3")
//                }
//            }
//
//            getSendTip.findDexMethod {
//                searchPackages("com.tencent.mm.storage")
//
//                matcher {
//                    returnType(Long::class.java)
//                    usingStrings("check table name from id:%d table:%s getTableNameByLocalId:%s")
//                }
//            }
//
//        }
//    }
}