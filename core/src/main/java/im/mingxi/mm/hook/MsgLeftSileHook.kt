package im.mingxi.mm.hook

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.toAppClass
import im.mingxi.miko.util.xpcompat.XPHelpers
import im.mingxi.mm.hook.listener.OlTouchListener
import im.mingxi.mm.hook.listener.SwipeState
import org.jetbrains.annotations.Nullable
import kotlin.math.abs
import kotlin.math.min

// Authed by 电报@coolia_cc
@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class MsgLeftSileHook : SwitchHook(), IFinder {


    companion object {
        lateinit var chatFooter: Any
    }

    override val name: String
        get() = "[8.0.61]消息左滑引用"
    override val uiItemLocation: Array<String>
        get() = arrayOf("聊天", "消息")

    lateinit var mdy7: Any

    override fun initOnce(): Boolean {
        "com.tencent.mm.pluginsdk.ui.chat.ChatFooter".toAppClass()!!.constructors.forEach {
            it.hookAfterIfEnable {
                chatFooter = it.thisObject
            }
        }
        /*"com.tencent.mm.ui.chatting.viewitems.p0".toAppClass()!!*/
        val constructor = ChattingItem_onMMMenuItemSelected.toMethod(loader).declaringClass
            ./*getConstructor("com.tencent.mm.ui.chatting.viewitems.q0".toAppClass())*/constructors[0]
        constructor.hookAfterIfEnable {
            val e/*Type:q0*/ = Reflex.findFieldObj(it.thisObject)
                ./*setFieldName("e")*/setReturnType(constructor.parameterTypes[0]).get()
                .get(it.thisObject)
            /*val o: Any = XPHelpers.callMethod(
                XPHelpers.getObjectField(
                    XPHelpers.getObjectField(e, "d"),
                    "c"
                ), "a",
                XPHelpers.getClass("zt4.u0")
            )*/
            val method = Chat_Manager.toMethod(loader)
            val cls = method.declaringClass.declaredConstructors[0].parameterTypes[0]
            val c = Reflex.findFieldObj(e).setReturnType(cls).get().get(e)!!
            val a /*manager*/: Any =
                Reflex.findFieldObj(c).setReturnType(method.declaringClass).get().get(c)!!
            val o = Reflex.findMethodObj(a).setParamsLength(1).get()
                .invoke(a, loader.loadClass("zt4.u0"))
            val y7: Any = XPHelpers.callMethod(
                XPHelpers.getObjectField(XPHelpers.getObjectField(o, "d"), "c"), "a",
                XPHelpers.getClass("zt4.j0")
            )
            mdy7 = y7
        }
        "com.tencent.mm.ui.chatting.adapter.j".toAppClass()!!.resolve()
            .firstMethod { name = "B" }.self.hookAfterIfEnable {

                val holder: Any = it.args.get(0)
                val position = it.args.get(1) as Int

                val adapterInstance: Any = it.thisObject
                val itemView = XPHelpers.getObjectField(holder, "itemView") as View
                if (itemView == null) {
                    XPBridge.log("itemView == null")
                    return@hookAfterIfEnable
                } else {
                    val dataList =
                        XPHelpers.getObjectField(adapterInstance, "K") as java.util.List<*>

                    val messageObject =
                        dataList.get(position)!!

                    itemView.setTag(im.mingxi.core.R.id.your_q9, messageObject)


                    itemView.setOnTouchListener(OlTouchListener(messageObject, loader))

                }
            }
        val olClass: Class<*> =
            XPHelpers.getClass("com.tencent.mm.ui.chatting.viewitems.ml")
        val relativeLayoutClass: Class<*> =
            XPHelpers.getClass("android.view.ViewGroup")

        val SWIPE_STATE_KEY: Int = im.mingxi.core.R.id.xposed_tag_key_for_swipe

        Reflex.findMethod(relativeLayoutClass).setMethodName("dispatchTouchEvent").setParams(
            MotionEvent::class.java
        ).get().hookBeforeIfEnable { param ->
            if (!olClass.isInstance(param.thisObject)) {
                return@hookBeforeIfEnable
            }

            val olView = param.thisObject as View
            val event = param.args.get(0) as MotionEvent

            @Nullable
            var state: SwipeState? = olView.getTag(SWIPE_STATE_KEY) as SwipeState?
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ... ACTION_DOWN 部分代码保持不变 ...
                    state = SwipeState()
                    state.initialRawX = event.getRawX()
                    state.initialRawY = event.getRawY()
                    state.touchSlop = ViewConfiguration.get(olView.context).scaledTouchSlop
                    olView.setTag(SWIPE_STATE_KEY, state)
                }

                MotionEvent.ACTION_MOVE -> {
                    if (state == null) {
                        return@hookBeforeIfEnable
                    }

                    if (!state.isDragging) {
                        val deltaX: Float = event.getRawX() - state.initialRawX
                        val deltaY: Float = event.getRawY() - state.initialRawY

                        if (deltaX < 0 && abs(deltaX.toDouble()) > state.touchSlop && abs(deltaX.toDouble()) > abs(
                                deltaY.toDouble()
                            )
                        ) {
                            state.isDragging = true


                            val cancelEvent = MotionEvent.obtain(event)
                            cancelEvent.action = MotionEvent.ACTION_CANCEL

                            XPBridge.invoke(
                                param.method,
                                param.thisObject,
                                arrayOf<MotionEvent>(cancelEvent)
                            )

                            cancelEvent.recycle()

                            if (olView.parent != null) {
                                olView.parent.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }

                    // 如果正在拖动，更新UI并消费事件
                    if (state.isDragging) {
                        val currentDeltaX: Float = event.getRawX() - state.initialRawX
                        olView.translationX = min(0.0, currentDeltaX.toDouble()).toFloat()

                        // 消费掉这个 MOVE 事件
                        param.result = true
                        return@hookBeforeIfEnable
                    }
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    // ... ACTION_UP / ACTION_CANCEL 部分代码保持不变 ...
                    if (state == null) {
                        return@hookBeforeIfEnable
                    }
                    val wasDragging = state.isDragging
                    olView.setTag(SWIPE_STATE_KEY, null)

                    if (wasDragging) {
                        val finalDeltaX: Float = event.getRawX() - state.initialRawX
                        if (event.getAction() === MotionEvent.ACTION_UP && finalDeltaX < -200) {
//                                    XposedBridge.log("左滑动作触发! 距离: " + finalDeltaX);
//                                    Toast.makeText(olView.getContext(), "左滑动作触发!", Toast.LENGTH_SHORT).show();
                            if (mdy7 != null && olView.getTag(im.mingxi.core.R.id.your_q9) != null) {
                                /*XPHelpers.callMethod(
                                    mdy7,
                                    "j0",
                                    olView.getTag(im.mingxi.core.R.id.your_q9),
                                    null
                                )*/
                                Reflex.findMethodObj(mdy7).setMethodName("j0").get().invoke(
                                    mdy7, olView.getTag(im.mingxi.core.R.id.your_q9),
                                    null
                                )
                            }
                        }
                        olView.animate().translationX(0f).setDuration(200).start()
                        param.result = true
                        return@hookBeforeIfEnable
                    }
                }
            }
        }
        return true
    }

    private val ChattingItem_onMMMenuItemSelected =
        DexMethodDescriptor(this, "${simpleTAG}.Method.ChattingItem_onMMMenuItemSelected")
    private val Chat_Manager =
        DexMethodDescriptor(this, "${simpleTAG}.Method.Chat_Manager")


    override fun dexFind(finder: DexFinder) {
        with(finder) {
            ChattingItem_onMMMenuItemSelected.findDexMethod {

                searchPackages("com.tencent.mm.ui.chatting.viewitems")

                matcher {
                    usingStrings("context item select failed, null dataTag")
                }
            }

            Chat_Manager.findDexMethod {
                searchPackages("com.tencent.mm.ui.chatting.manager")

                matcher {
                    usingStrings(" is not a interface!")
                }
            }

        }

    }


}