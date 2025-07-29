package im.mingxi.mm.hook

import android.content.ContentValues
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import im.mingxi.core.databinding.SelectDialogBinding
import im.mingxi.core.databinding.SettingRedpacketBinding
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.adapter.SelectAdapter
import im.mingxi.miko.ui.dialog.XDialog
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.ui.widget.MikoToast
import im.mingxi.miko.util.HookEnv
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.mm.bean.RedPacketBody
import im.mingxi.mm.manager.impl.MMEnvManagerImpl
import im.mingxi.mm.manager.impl.WeChatContactStorageImpl
import im.mingxi.mm.manager.impl.WeChatMessageManagerImpl
import im.mingxi.mm.model.Conservation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.luckypray.dexkit.DexKitBridge
import java.lang.reflect.Method


@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class AutoRedPacketReceiver : SwitchHook(), IFinder {
    override val name: String
        get() = "自动领取红包"
    override val description: CharSequence?
        get() = "点击进行详细设置"
    override val uiItemLocation: String
        get() = FuncRouter.RED_PACKET

    private val activityScope = CoroutineScope(Dispatchers.Main + Job())

    private val sendIds = ArrayList<String>()


    override val onClick: ((View) -> Unit)
        get() = { v ->
            XDialog.create(v.context).apply {
                title = name
                val binding = SettingRedpacketBinding.inflate(LayoutInflater.from(v.context))
                val switch = binding.redpacketToastBtn
                switch.isChecked = mConfig.decodeBool("$TAG.config.isShowToastAfterReceive")
                switch.setOnCheckedChangeListener { _, isChecked ->
                    mConfig.encode("$TAG.config.isShowToastAfterReceive", isChecked)
                }
                val black_switch = binding.redpacketBlackBtn
                black_switch.isChecked = mConfig.decodeBool("$TAG.config.isEnableBlack")
                black_switch.setOnCheckedChangeListener { _, isChecked ->
                    mConfig.encode("$TAG.config.isEnableBlack", isChecked)
                }
                val black_input = binding.redpacketBlackInput
                black_input.setText(mConfig.decodeString("$TAG.config.blackList", ""))
                black_input.doAfterTextChanged {
                    if (!TextUtils.isEmpty(it.toString())) {
                        mConfig.encode("$TAG.config.blackList", it.toString())
                    }
                }
                black_input.setOnLongClickListener { v ->
                    createSelectDialog(true, v.context)
                    true
                }
                val white_switch = binding.redpacketWhiteBtn
                white_switch.isChecked = mConfig.decodeBool("$TAG.config.isEnableWhite")
                white_switch.setOnCheckedChangeListener { _, isChecked ->
                    mConfig.encode("$TAG.config.isEnableWhite", isChecked)
                }
                val white_input = binding.redpacketWhiteInput
                white_input.setText(mConfig.decodeString("$TAG.config.whiteList"))
                white_input.doAfterTextChanged {
                    if (!TextUtils.isEmpty(it.toString())) {
                        mConfig.encode("$TAG.config.whiteList", it.toString())
                    }
                }
                white_input.setOnLongClickListener { v ->
                    createSelectDialog(false, v.context)
                    true
                }
                val wait = binding.waitEdit
                wait.setText(mConfig.decodeInt("$TAG.config.waitTime", 0).toString())
                wait.doAfterTextChanged {
                    if (!TextUtils.isEmpty(it.toString())) {
                        mConfig.encode("$TAG.config.waitTime", it.toString().toInt())
                    }
                }
                val keyword_switch = binding.redpacketKeywordBtn
                keyword_switch.isChecked = mConfig.decodeBool("$TAG.config.isEnableKeyword")
                keyword_switch.setOnCheckedChangeListener { _, isChecked ->
                    mConfig.encode("$TAG.config.isEnableKeyword", isChecked)
                }
                val keyword_input = binding.redpacketKeywordInput
                keyword_input.setText(mConfig.decodeString("$TAG.config.keywordList"))
                keyword_input.doAfterTextChanged {
                    if (!TextUtils.isEmpty(it.toString())) {
                        mConfig.encode("$TAG.config.keywordList", it.toString())
                    }
                }

                val white_keyword_switch = binding.redpacketWhiteKeywordBtn
                white_keyword_switch.isChecked =
                    mConfig.decodeBool("$TAG.config.isEnableWhiteKeyword")
                white_keyword_switch.setOnCheckedChangeListener { _, isChecked ->
                    mConfig.encode("$TAG.config.isEnableWhiteKeyword", isChecked)
                }
                val white_keyword_input = binding.redpacketWhiteKeywordInput
                white_keyword_input.setText(mConfig.decodeString("$TAG.config.whiteKeywordList"))
                white_keyword_input.doAfterTextChanged {
                    if (!TextUtils.isEmpty(it.toString())) {
                        mConfig.encode("$TAG.config.whiteKeywordList", it.toString())
                    }
                }

                val return_btn = binding.redpacketReturnWord
                return_btn.isChecked = mConfig.decodeBool("$TAG.config.isEnableReturnWord")
                return_btn.setOnCheckedChangeListener { _, isChecked ->
                    mConfig.encode("$TAG.config.isEnableReturnWord", isChecked)
                }
                val return_input = binding.redpacketReturnInput
                return_input.setText(mConfig.decodeString("$TAG.config.returnWord"))
                return_input.doAfterTextChanged {
                    if (!TextUtils.isEmpty(it.toString())) {
                        mConfig.encode("$TAG.config.returnWord", it.toString())
                    }
                }

                contain(binding.root)
            }.build()
        }

    private val NetSceneReceiveLuckyMoney: DexDesc =
        DexDesc("$simpleTAG.Method.NetSceneReceiveLuckyMoney")
    private val NetSceneOpenLuckyMoney: DexDesc =
        DexDesc("$simpleTAG.Method.NetSceneOpenLuckyMoney")
    private val NetSceneQueue: DexDesc =
        DexDesc("$simpleTAG.Method.NetSceneQueue")

    private val onMsgInsertWithOnConflict: Method =
        Reflex.findMethod(
            Reflex.loadClass("com.tencent.wcdb.database.SQLiteDatabase")
        )
            .setMethodName("insertWithOnConflict")
            .get()


    // 这个字段是微信预提供的一个加密时间，必须拥有此字段才能打开红包
    lateinit var timingIdentifier: String

    lateinit var redPacketBody: RedPacketBody

    private fun isTarget(): Boolean = if (mConfig.decodeBool(
            "$TAG.config.isEnableBlack",
            false
        ) && !mConfig.decodeString("$TAG.config.blackList", "")!!
            .contains(redPacketBody.sender.toString())
    ) true
    else if (mConfig.decodeBool(
            "$TAG.config.isEnableWhite",
            false
        ) && mConfig.decodeString("$TAG.config.whiteList", "")!!
            .contains(redPacketBody.sender.toString())
    ) true
    else if (!mConfig.decodeBool(
            "$TAG.config.isEnableBlack",
            false
        ) && !mConfig.decodeBool(
            "$TAG.config.isEnableWhite",
            false
        )
    ) true
    else false


    fun receiveRedPacket() {
        // 静默逻辑
        val pre = NetSceneReceiveLuckyMoney.toMethod()
            .declaringClass
            .getDeclaredConstructor(
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                String::class.java,
                String::class.java
            ).newInstance(
                1,
                1,
                redPacketBody.sendId,
                redPacketBody.mNativeUrl,
                2,
                "v1.0",
                redPacketBody.sender
            )
        // call

        Reflex.findMethodObj(
            Reflex.findField(
                NetSceneQueue.toMethod().declaringClass
            )
                .setReturnType(
                    NetSceneQueue.toMethod().declaringClass
                )
                .get()
                .get(null)
        )
            .setParamsLength(1)
            .setReturnType(Boolean::class.java)
            .get().invoke(
                Reflex.findField(
                    NetSceneQueue.toMethod().declaringClass
                )
                    .setReturnType(
                        NetSceneQueue.toMethod().declaringClass
                    )
                    .get()
                    .get(null),
                pre
            )
    }

    // 大概分为三个步骤
    // 读消息开intent，钩住timingIdentifier，发送请求打开红包
    override fun initOnce(): Boolean {

        onMsgInsertWithOnConflict.hookAfterIfEnable { param ->

            val contentValues = param.args[2] as ContentValues
            // 简单判断是否为红包消息
            // XPBridge.log(contentValues.toString())
            if (contentValues.containsKey("mNativeUrl")) {
                this.redPacketBody = RedPacketBody(contentValues)
                if (sendIds.contains(redPacketBody.sendId)) return@hookAfterIfEnable
                if (MMEnvManagerImpl().getWxId() == redPacketBody.sender) return@hookAfterIfEnable
                if (!isTarget()) return@hookAfterIfEnable
            } else if (contentValues.toString().contains("showwxpaytitle")) {
                if (sendIds.contains(redPacketBody.sendId)) return@hookAfterIfEnable
                if (mConfig.decodeBool("$TAG.config.isEnableKeyword", false)) {
                    val keys = mConfig.decodeString("$TAG.config.keywordList", "")!!.split("|")
                    for (key in keys) {
                        if (contentValues.toString()
                                .contains(key)
                        ) return@hookAfterIfEnable
                    }
                }
                if (mConfig.decodeBool("$TAG.config.isEnableWhiteKeyword", false)) {
                    val keys = mConfig.decodeString("$TAG.config.whiteKeywordList", "")!!.split("|")
                    var i = keys.size
                    for (key in keys) {
                        if (contentValues.toString()
                                .contains(key)
                        ) --i
                    }
                    if (i == keys.size) return@hookAfterIfEnable
                }

                val waitTime = mConfig.decodeInt("$TAG.config.waitTime", 0)
                if (waitTime != 0) {
                    activityScope.launch {
                        delay(waitTime.toLong())
                        val result = withContext(Dispatchers.Main) {
                            receiveRedPacket()
                        }
                    }
                    return@hookAfterIfEnable
                }
                receiveRedPacket()
            }


        }


        this.NetSceneReceiveLuckyMoney.toMethod().hookBeforeIfEnable { param ->
            if (!isTarget()) return@hookBeforeIfEnable
            if (sendIds.contains(redPacketBody.sendId)) return@hookBeforeIfEnable
            if (MMEnvManagerImpl().getWxId() == redPacketBody.sender) return@hookBeforeIfEnable
            val json = param.args[2] as JSONObject
            this.timingIdentifier = json.optString("timingIdentifier")
            val o = NetSceneOpenLuckyMoney.toMethod()
                .declaringClass
                .getDeclaredConstructor(
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java,
                    String::class.java
                ).newInstance(
                    1,
                    1,
                    redPacketBody.sendId,
                    redPacketBody.mNativeUrl,
                    null,
                    null,
                    redPacketBody.sender,
                    "v1.0",
                    timingIdentifier,
                    ""
                )

            Reflex.findMethodObj(
                Reflex.findField(NetSceneQueue.toMethod().declaringClass)
                    .setReturnType(
                        NetSceneQueue.toMethod().declaringClass
                    )
                    .get()
                    .get(null)
            )
                .setParamsLength(1)
                .setReturnType(Boolean::class.javaPrimitiveType!!)
                .get()
                .invoke(
                    Reflex.findField(NetSceneQueue.toMethod().declaringClass)
                        .setReturnType(
                            NetSceneQueue.toMethod().declaringClass
                        )
                        .get()
                        .get(null),
                    o
                )
            if (mConfig.decodeBool("$TAG.config.isShowToastAfterReceive")) {
                sendIds.add(redPacketBody.sendId!!)
                MikoToast.makeToast(
                    HookEnv.hostActivity,
                    "-----已抢到红包-----\n来源: ${redPacketBody.sender}"
                )
            }
            if (mConfig.decodeBool("$TAG.config.isEnableReturnWord")) {
                WeChatMessageManagerImpl().sendText(
                    redPacketBody.sender!!,
                    mConfig.decodeString("$TAG.config.returnWord", "")!!
                )
            }

        }

        return true
    }

    override fun dexFind(finder: DexKitBridge) {

        NetSceneReceiveLuckyMoney.findDexMethod(finder) {
            searchPackages("com.tencent.mm.plugin.luckymoney.model")

            matcher {
                declaredClass {
                    usingStrings("/cgi-bin/mmpay-bin/receivewxhb")
                }
                usingEqStrings(
                    "MicroMsg.NetSceneReceiveLuckyMoney",
                    "agree_duty",
                    "timingIdentifier"
                )
            }
        }

        NetSceneOpenLuckyMoney.findDexMethod(finder) {
            searchPackages("com.tencent.mm.plugin.luckymoney.model")

            matcher {
                usingStrings(
                    "MicroMsg.NetSceneOpenLuckyMoney",
                    "insertLocalSysMsgIfNeed error: %s"
                )
            }
        }

        NetSceneQueue.findDexMethod(finder) {
            searchPackages("com.tencent.mm.modelbase")

            matcher {
                usingStrings(
                    "MicroMsg.NetSceneQueue",
                    "doScene failed",
                    "reset::cancel scene",
                    "clearRunningQueue"
                )
            }
        }

    }

    fun createSelectDialog(isBlack: Boolean, context: Context) {
        XDialog.create(context).apply {
            title = "选择联系人/群聊"
            val binding = SelectDialogBinding.inflate(LayoutInflater.from(context))
            binding.recyclerviewSelect.layoutManager = LinearLayoutManager(context)
            val adapter = SelectAdapter(
                isBlack,
                WeChatContactStorageImpl().getAllFriends() as ArrayList<Conservation>
            )
            binding.recyclerviewSelect.adapter = adapter
            adapter.notifyDataSetChanged()

            binding.friend.setOnCheckedChangeListener { _, bool ->
                if (bool) {
                    adapter.dataSet.clear()
                    adapter.dataSet.addAll(WeChatContactStorageImpl().getAllFriends())
                    adapter.notifyDataSetChanged()
                }
            }
            binding.troop.setOnCheckedChangeListener { _, bool ->
                if (bool) {
                    adapter.dataSet.clear()
                    adapter.dataSet.addAll(WeChatContactStorageImpl().getAllGroups())
                    adapter.notifyDataSetChanged()
                }
            }
            contain(binding.root)

        }.build()
    }
}