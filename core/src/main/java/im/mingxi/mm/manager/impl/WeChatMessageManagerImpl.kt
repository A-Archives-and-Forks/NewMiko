package im.mingxi.mm.manager.impl

import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexKit
import im.mingxi.miko.util.toAppClass
import im.mingxi.mm.manager.WeChatMessageManager

class WeChatMessageManagerImpl : WeChatMessageManager {

    override fun sendText(wxId: String, content: String) {
        val constructor = DexKit.requireClassFromCache("NetSceneSendMsg").toAppClass()
            .getDeclaredConstructor(
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Long::class.javaPrimitiveType
            )
        val model = constructor.newInstance(wxId, content, 1, 0, 0)
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
}