package im.mingxi.mm.hook

import android.content.ContentValues
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import im.mingxi.core.databinding.AutoReplyBinding
import im.mingxi.core.databinding.TextColorBinding
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.config
import im.mingxi.mm.manager.WeChatManagers
import im.mingxi.mm.model.DBInsertMsg
import java.lang.reflect.Method

@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class AutoReply : SwitchHook() {
    override val name: String
        get() = "自动回复"
    override val uiItemLocation: String
        get() = FuncRouter.CHAT
    override val description: CharSequence
        get() = "点击进行详细设置"
    override val onClick: ((View) -> Unit)
        get() = { v ->
            XDialog.create(v.context).apply {
                title = name
                val mBinding = AutoReplyBinding.inflate(LayoutInflater.from(v.context))
                mBinding.autoReplyList.adapter = ArrayAdapter(
                    v.context, android.R.layout.simple_list_item_1,
                    config.decodeString("$TAG.autoReplyList", "")!!.split("|").toTypedArray()
                )
                mBinding.autoReplyList.setOnItemClickListener { parent, view, position, id ->
                    val origin = config.decodeString("$TAG.autoReplyList", "")!!
                    val list = origin.split("|").toMutableList()
                    list.removeAt(position)
                    config.encode("$TAG.autoReplyList", list.joinToString("|"))
                    mBinding.autoReplyList.adapter = ArrayAdapter(
                        v.context, android.R.layout.simple_list_item_1,
                        config.decodeString("$TAG.autoReplyList", "")!!.split("|").toTypedArray()
                    )
                }
                confirmButton.text = "确定"
                mBinding.autoReplyAdd.setOnClickListener { v ->
                    XDialog.create(v.context).apply {
                        title = "添加"
                        val binding = TextColorBinding.inflate(LayoutInflater.from(v.context))
                        binding.colorText.setHint("输入回复关键词")
                        binding.colorLink.setHint("输入回复内容")
                        // WeChatManagers.messageManager.sendText("52320127241@chatroom", "我好")
                        confirmButtonClickListener = View.OnClickListener { v ->
                            val origin = config.decodeString("$TAG.autoReplyList", "")!!
                            if (origin == "") {
                                config.encode(
                                    "$TAG.autoReplyList",
                                    "${binding.colorText.text}->${binding.colorLink.text}"
                                )
                            } else config.encode(
                                "$TAG.autoReplyList",
                                "$origin|${binding.colorText.text}->${binding.colorLink.text}"
                            )
                            mBinding.autoReplyList.adapter = ArrayAdapter(
                                v.context, android.R.layout.simple_list_item_1,
                                config.decodeString("$TAG.autoReplyList", "")!!.split("|")
                                    .toTypedArray()
                            )
                            dismiss()
                        }
                        contain(binding.root)
                    }.build()
                }
                contain(mBinding.root)
            }.build()
        }

    private val onMsgInsertWithOnConflict: Method =
        Reflex.findMethod(
            Reflex.loadClass("com.tencent.wcdb.database.SQLiteDatabase")
        )
            .setMethodName("insertWithOnConflict")
            .get()

    override fun initOnce(): Boolean = true.also { bool ->
        onMsgInsertWithOnConflict.hookAfterIfEnable { param ->
            val contentValues = param.args[2] as ContentValues
            if (contentValues.containsKey("content") && contentValues.containsKey("msgSvrId") && contentValues.containsKey(
                    "msgSeq"
                ) && contentValues.containsKey("talker")
            ) {
                val talker = contentValues.getAsString("talker")
                val content = contentValues.getAsString("content")
                var dbInsertMsg: DBInsertMsg? = null
                if (talker.contains("@")) {
                    val sender = contentValues.getAsString("content")
                        .substring(0, contentValues.getAsString("content").indexOf(":"))
                    dbInsertMsg = DBInsertMsg(
                        contentValues.getAsLong("msgSvrId"),
                        contentValues.getAsLong("msgSeq"),
                        contentValues.getAsString("talker"),
                        content.replace("${talker},", ""),
                        sender
                    )
                    // if (sender == WeChatManagers.envManager.getWxId()) return@hookAfterIfEnable
                    if (config.decodeString("$TAG.autoReplyList", "")!!
                            .contains(dbInsertMsg.content)
                    ) {
                        val items = config.decodeString("$TAG.autoReplyList", "")!!.split("|")
                        items.forEach { item ->
                            val key = item.substring(0, item.indexOf("->"))
                            val value = item.substring(item.indexOf("->") + 2)
                            if (dbInsertMsg.content.contains(key)) {
                                WeChatManagers.messageManager.sendText(talker, value)
                                return@hookAfterIfEnable
                            }
                        }
                        // WeChatManagers.messageManager.sendText(talker, "")
                        // WeChatManagers.messageManager.sendText(talker, config.decodeString("$TAG.autoReplyList")!!.split("|")[config.decodeString("$TAG.autoReplyList")!!.split("|").indexOf(dbInsertMsg.content)].split("->")[1])
                    }
                } else {
                    dbInsertMsg = DBInsertMsg(
                        contentValues.getAsLong("msgSvrId"),
                        contentValues.getAsLong("msgSeq"),
                        contentValues.getAsString("talker"),
                        content,
                        null
                    )
                    //if (talker == WeChatManagers.envManager.getWxId()) return@hookAfterIfEnable
                    if (config.decodeString("$TAG.autoReplyList", "")!!
                            .contains(dbInsertMsg.content)
                    ) {
                        val items = config.decodeString("$TAG.autoReplyList", "")!!.split("|")
                        items.forEach { item ->
                            val key = item.substring(0, item.indexOf("->"))
                            val value = item.substring(item.indexOf("->") + 2)
                            if (dbInsertMsg.content.contains(key)) {
                                WeChatManagers.messageManager.sendText(talker, value)
                                return@hookAfterIfEnable
                            }
                        }
                        // WeChatManagers.messageManager.sendText(talker, "")
                        // WeChatManagers.messageManager.sendText(talker, config.decodeString("$TAG.autoReplyList")!!.split("|")[config.decodeString("$TAG.autoReplyList")!!.split("|").indexOf(dbInsertMsg.content)].split("->")[1])
                    }
                }
                // if (dbInsertMsg.content)
            }
        }
    }

}