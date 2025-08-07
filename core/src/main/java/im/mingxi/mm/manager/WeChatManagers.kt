package im.mingxi.mm.manager

import im.mingxi.mm.manager.impl.MMAvatarStorageManagerImpl
import im.mingxi.mm.manager.impl.MMEnvManagerImpl
import im.mingxi.mm.manager.impl.WeChatContactStorageImpl
import im.mingxi.mm.manager.impl.WeChatMessageManagerImpl

object WeChatManagers {
    val envManager: MMEnvManager = MMEnvManagerImpl()
    val messageManager: WeChatMessageManager = WeChatMessageManagerImpl()
    val avatarStorageManager = MMAvatarStorageManagerImpl()
    val chatContactStorageManager = WeChatContactStorageImpl()
}