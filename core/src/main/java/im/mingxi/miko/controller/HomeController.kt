package im.mingxi.miko.controller

import android.content.Intent
import im.mingxi.miko.ui.activity.HomeActivity
import im.mingxi.miko.util.HookEnv
import im.mingxi.mm.manager.impl.MMEnvManagerImpl
import im.mingxi.net.bean.UserBean

object HomeController {

    fun openHomeActivity() {
        val activity = HookEnv.hostActivity
        val intent = Intent(activity, HomeActivity::class.java)
        val envManager = MMEnvManagerImpl()
        intent.putExtra("wxid", envManager.getWxId())
        intent.putExtra("name", envManager.getCurrentName())
        intent.putExtra("avatarPath", envManager.getAvatarFilePath())
        intent.putExtra("uin", envManager.getCurrentUin())
        activity.startActivity(intent)
    }

    // 原本预计操作为从服务器拉取已从github OAhth注册并填入信息的操作
    // 但目前暂时没有能用的服务器了，暂时采取从本地注册的方法
    // TODO: 这里后面用ViewModel来处理
    fun requireUserBean(): UserBean {
        val loginId = MMEnvManagerImpl().getWxId()
        val userBean = UserBean(loginId, 1, false, "", "")
        return userBean
    }
}