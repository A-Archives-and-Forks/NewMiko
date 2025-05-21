package im.mingxi.mm.manager

interface MMEnvManager {
    fun getCurrentUin(): String? // 获取微信账号

    fun getCurrentName(): String? // 获取微信昵称

    fun getPhoneNumber(): String? // 获取绑定手机号，谨慎使用

    fun getWxId(): String? // 获取微信ID

    fun isLogin(): Boolean // 是否登录

    fun getAvatarFilePath(): String? // 获取头像文件路径
}