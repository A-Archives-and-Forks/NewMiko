package im.mingxi.mm.manager.impl

import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexKit
import im.mingxi.miko.util.toAppClass
import im.mingxi.mm.manager.WeChatManagers
import im.mingxi.mm.manager.WeChatMessageManager
import org.json.JSONObject

class WeChatMessageManagerImpl : WeChatMessageManager {

    override fun sendText(talker: String, content: String) {
        val constructor = DexKit.requireClassFromCache("NetSceneSendMsg").toAppClass()
            .getDeclaredConstructor(
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Long::class.javaPrimitiveType
            )
        val model = constructor.newInstance(talker, content, 1, 0, 0)
        val netSceneQueue = DexKit.requireClassFromCache("NetSceneQueue").toAppClass()
        Reflex.findMethodObj(
            Reflex.findField(netSceneQueue)
                .setReturnType(
                    netSceneQueue
                )
                .get()
                .get(null)
        )
            .setParamsLength(1)
            .setReturnType(Boolean::class.javaPrimitiveType!!)
            .get()
            .invoke(
                Reflex.findField(netSceneQueue)
                    .setReturnType(
                        netSceneQueue
                    )
                    .get()
                    .get(null),
                model
            )
    }

    override fun insertSysMsg(talker: String, content: String, msgId: Long) {

    }

    override fun sendImage(talker: String, imagePath: String, appId: String?) {
        val constructor = DexKit.requireClassFromCache("NetSceneUploadMsgImg").toAppClass()
            .getDeclaredConstructor(
                Int::class.javaPrimitiveType,
                String::class.java,
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Any::class.java,
                Int::class.javaPrimitiveType,
                Any::class.java,
                String::class.java,
                Boolean::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
        if (appId != null) {
            val json = JSONObject()
            val json2 = JSONObject()
            val json3 = JSONObject()
            json3.put("appid", appId)
            json2.put("appinfo", json3)
            json.put("msg", json2)
        } else {
            val messageObj = constructor.newInstance(
                4,
                WeChatManagers.envManager.getWxId(),
                talker,
                imagePath,
                1,
                null,
                0,
                null,
                "",
                true,
                0
            )
            val netSceneQueue = DexKit.requireClassFromCache("NetSceneQueue").toAppClass()
            Reflex.findMethodObj(
                Reflex.findField(netSceneQueue)
                    .setReturnType(
                        netSceneQueue
                    )
                    .get()
                    .get(null)
            )
                .setParamsLength(1)
                .setReturnType(Boolean::class.javaPrimitiveType!!)
                .get()
                .invoke(
                    Reflex.findField(netSceneQueue)
                        .setReturnType(
                            netSceneQueue
                        )
                        .get()
                        .get(null),
                    messageObj
                )
        }
    }
}