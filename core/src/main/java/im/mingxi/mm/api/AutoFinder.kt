package im.mingxi.mm.api

import im.mingxi.miko.startup.HookInstaller
import im.mingxi.miko.util.cache
import im.mingxi.mm.struct.Struct
import org.luckypray.dexkit.DexKitBridge


object AutoFinder {
    val AppMgr = Struct(
        "AppMgr", Struct.TYPE_MEMBER, findMethod = {
            searchPackages("com.tencent.mm.app")
            matcher {
                declaredClass {
                    usingStrings(
                        "MicroMsg.MMAppMgr",
                        "showLbsTipsAlert error"
                    )
                }
            }
        }
    )

    val NetSceneSendMsg = Struct(
        "NetSceneSendMsg", Struct.TYPE_CLASS, findClass = {
            matcher {
                usingStrings(
                    "MicroMsg.NetSceneSendMsg",
                    "send msg fail ret = %s MsgId=%s MsgSource=%s"
                )
            }
        }
    )

    val NetSceneQueue = Struct(name = "NetSceneQueue", type = Struct.TYPE_CLASS, findClass = {
        searchPackages("com.tencent.mm.modelbase")

        matcher {
            usingStrings(
                "MicroMsg.NetSceneQueue",
                "doScene failed",
                "reset::cancel scene",
                "clearRunningQueue"
            )
        }
    })

    val ContactStorage = Struct(name = "ContactStorage", type = Struct.TYPE_CLASS, findClass = {
        searchPackages("com.tencent.mm.storage")
        matcher {
            usingStrings("MicroMsg.ContactStorage", "FATAL ERROR, invalid contact, empty username")
        }
    })

    val ConversationStorage =
        Struct(name = "ConversationStorage", type = Struct.TYPE_CLASS, findClass = {
            searchPackages("com.tencent.mm.storage")
            matcher {
                usingStrings(
                    "MicroMsg.ConversationStorage",
                    "insert conversation failed, username empty"
                )
            }
        })

    val AvatarStorage = Struct(name = "AvatarStorage", type = Struct.TYPE_CLASS, findClass = {
        searchPackages("com.tencent.mm.modelavatar")
        matcher {
            usingStrings("MicroMsg.AvatarStorage", "exception:%s", "I_AM_NO_SDCARD_USER_NAME")
        }
    })

    val structs: List<Struct> by lazy {
        return@lazy listOf(
            AppMgr,
            NetSceneSendMsg,
            NetSceneQueue,
            ContactStorage,
            ConversationStorage,
            AvatarStorage
        )
    }

    fun onLoad(dexKit: DexKitBridge) {
        structs.forEachIndexed { i, struct ->
            HookInstaller.sendMessageToDialog("正在加载查找器[${i + 1}/${structs.size}](${struct.name})")
            val desc = if (struct.findClass != null) {
                dexKit.findClass(struct.findClass)[0].descriptor
            } else if (struct.findMethod != null) {
                dexKit.findMethod(struct.findMethod)[0].descriptor
            } else if (struct.findField != null) {
                dexKit.findField(struct.findField)[0].descriptor
            } else {
                throw RuntimeException("findClass or findMethod or findField is null")
            }
            cache.encode(
                struct.name,
                desc
            )
        }
    }


}