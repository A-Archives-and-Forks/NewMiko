## 贡献指北

## 简单介绍
本项目目前由以下4个模块组成
- app 模块应用界面
- annotation 注解处理(因为没有意识到autoService难以处理kt类，已删除，等待我了解ksp后用ksp重写)
- core 模块核心(你可以只看这个)
- loader 模块加载器

## 贡献功能
1.在core模块下找到im.mingxi
2.根据功能所需要作用的宿主，打开对应文件夹，若为QQ则打开im.mingxi.mobileqq，若为微信则打开im.mingxi.mm
3.在im.mingxi.***.hook里面创建你的功能类，分类是可选的
4.完成你的功能，以下是一个代码例子
```kotlin
package im.mingxi.mm.hook

import android.app.Activity
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.BaseFuncHook
import im.mingxi.miko.util.Reflex

@FunctionHookEntry(/*项目内部名称，可选*/itemName = "去你妈的图片数量限制",/*作用宿主，默认均可作用*/ itemType = FunctionHookEntry.WECHAT_ITEM)
class FuckPicCountLimit : BaseFuncHook(defaultEnabled = true)/*目前你继承im.mingxi.miko.hook下的哪个都没啥用，没写界面，只能默认打开*/ {
    override fun initOnce(): Boolean {
        // 通过Reflex类获取Class对象
        val albumPreviewUIClass = Reflex.loadClass("com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI")
        // 获取方法
        val targetMet = Reflex.findMethod(albumPreviewUIClass).setMethodName("onCreate").get()
        XPBridge.hookBefore(targetMet) {
            //实现逻辑
            val activity = it.thisObject as Activity
            val intent = activity.intent
            intent.putExtra("max_select_count", 6666)
        }
        return true // 返回true表示初始化成功
    }
}
```
5.手动到im.mingxi.miko.startup.HookInstaller#scanAndInstall中初始化你的功能(这一步在我写完ksp之前必须)

## 贡献Miko本体
没有什么要求，不要太离谱就行

## 最后
1.感谢你的支持，本人高二学生，学业压力较大，回应可能较慢，代码水平也怎么样，多多包涵。
~~2.不要release，没配置好混淆~~
3.代码请务必格式化
4.玩的开心
