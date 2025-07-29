package im.mingxi.mm.hook

import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.loader.bridge.XPBridge
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.util.Reflex
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.toAppClass
import im.mingxi.miko.util.xpcompat.XPHelpers
import im.mingxi.mm.hook.listener.OlTouchListener
import im.mingxi.mm.hook.listener.SwipeState
import org.jetbrains.annotations.Nullable
import org.luckypray.dexkit.DexKitBridge
import kotlin.math.abs
import kotlin.math.min

// Authed by 电报@coolia_cc
@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class MsgLeftSileHook : SwitchHook(), IFinder {


    companion object {
        lateinit var chatFooter: Any
        lateinit var Q9: Any
    }

    override val name: String
        get() = "消息左滑引用"
    override val uiItemLocation: String
        get() = FuncRouter.CHAT

    lateinit var mdy7: Any

    override fun initOnce(): Boolean {
        "com.tencent.mm.pluginsdk.ui.chat.ChatFooter".toAppClass().constructors.forEach {
            it.hookAfterIfEnable {
                chatFooter = it.thisObject
                //XPBridge.log(chatFooter.toString())
            }
        }
        Component.toMethod().declaringClass
        .constructors.forEach {
            it.hookAfterIfEnable {
                mdy7 = it.thisObject
            }
        }
        /*"com.tencent.mm.ui.chatting.viewitems.p0".toAppClass()!!*/
      /*  val constructor = ChattingItem_onMMMenuItemSelected.toMethod(loader).declaringClass
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
           // XPBridge.log(mdy7)
        }*/
        /*"com.tencent.mm.ui.chatting.adapter.j".toAppClass()!!.resolve()
            .firstMethod { name = "B" }.self*/Adapter.toMethod().hookAfterIfEnable {
            //if (true) return@hookAfterIfEnable

            val holder: Any = it.args[0]
            val position = it.args[1] as Int

                val adapterInstance: Any = it.thisObject
                // val itemView = XPHelpers.getObjectField(holder, "itemView") as View
            val itemView = holder::class.resolve()
                .firstField {
                    type = View::class.java
                    superclass()
                }.self.get(holder) as View
                if (false) {
                    XPBridge.log("itemView == null")
                    return@hookAfterIfEnable
                } else {
                    val dataList = Reflex.findFieldObj(adapterInstance).setReturnType(List::class.java).get().get(adapterInstance) as java.util.List<*>
                        // XPHelpers.getObjectField(adapterInstance, "K") as java.util.List<*>

                    val messageObject =
                        dataList.get(position)!!

                    Q9 = messageObject

                    itemView.setTag(im.mingxi.core.R.id.your_q9, messageObject)


                    itemView.setOnTouchListener(OlTouchListener(messageObject, loader))

                }
            }
        val desc = cache.decodeString("${simpleTAG}.Method.ViewItemsContainer")!!
        val olClass: Class<*> =// DexKit.requireClassFromCache("${simpleTAG}.Class.ViewItems_Container").toAppClass()!!
            //ViewItemContainer.name.toAppClass()!!
            //XPHelpers.getClass("com.tencent.mm.ui.chatting.viewitems.ml")
            desc.substring(0, desc.indexOf(";") + 1).toAppClass()
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
            val event = param.args[0] as MotionEvent

            @Nullable
            var state: SwipeState? = olView.getTag(SWIPE_STATE_KEY) as SwipeState?
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // ... ACTION_DOWN 部分代码保持不变 ...
                    state = SwipeState()
                    state.initialRawX = event.rawX
                    state.initialRawY = event.rawY
                    state.touchSlop = ViewConfiguration.get(olView.context).scaledTouchSlop
                    olView.setTag(SWIPE_STATE_KEY, state)
                }

                MotionEvent.ACTION_MOVE -> {
                    if (state == null) {
                        return@hookBeforeIfEnable
                    }

                    if (!state.isDragging) {
                        val deltaX: Float = event.rawX - state.initialRawX
                        val deltaY: Float = event.rawY - state.initialRawY

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
                                //arrayOf<MotionEvent>(cancelEvent)
                                cancelEvent
                            )

                            cancelEvent.recycle()

                            if (olView.parent != null) {
                                olView.parent.requestDisallowInterceptTouchEvent(true)
                            }
                        }
                    }

                    // 如果正在拖动，更新UI并消费事件
                    if (state.isDragging) {
                        val currentDeltaX: Float = event.rawX - state.initialRawX
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
                        val finalDeltaX: Float = event.rawX - state.initialRawX
                        if (event.action === MotionEvent.ACTION_UP && finalDeltaX < -200) {
//                                    XposedBridge.log("左滑动作触发! 距离: " + finalDeltaX);
//                                    Toast.makeText(olView.getContext(), "左滑动作触发!", Toast.LENGTH_SHORT).show();
                            if (mdy7 != null && olView.getTag(im.mingxi.core.R.id.your_q9) != null) {
                                /*XPHelpers.callMethod(
                                    mdy7,
                                    "j0",
                                    olView.getTag(im.mingxi.core.R.id.your_q9),
                                    null
                                )*/
                               /* Reflex.findMethodObj(mdy7).setReturnType(Boolean::class.java).setParamsLength(2).setParams().get().invoke(
                                    mdy7, olView.getTag(im.mingxi.core.R.id.your_q9),
                                    null
                                )*/
                                try {
                                    chatFooter.resolve().firstMethod {
                                        returnType(Boolean::class.java)
                                        parameters(Q9::class.java)
                                        }.self.invoke(chatFooter,Q9)
                                }catch (_: Exception) {

                                }
                                //XPHelpers.callMethod(MsgLeftSileHook.chatFooter, "A", Q9);
                                mdy7::class.java.declaredMethods.forEach {
                                    if (it.returnType == Boolean::class.java) {
                                        if (it.parameterCount == 2) {
                                            if (it.parameterTypes[0] != Int::class.java) {
                                                it.isAccessible = true
                                                it.invoke(mdy7, olView.getTag(im.mingxi.core.R.id.your_q9),null)

                                            }
                                        }
                                    }
                                }
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

  //  private val ChattingItem_onMMMenuItemSelected =
      //  DexMethodDescriptor(this, "${simpleTAG}.Method.ChattingItem_onMMMenuItemSelected")
 //   private val Chat_Manager =
   //     DexMethodDescriptor(this, "${simpleTAG}.Method.Chat_Manager")
    private val ViewItemContainer =
      DexDesc("${simpleTAG}.Method.ViewItemsContainer")
    private val Adapter =
        DexDesc("${simpleTAG}.Method.Adapter")
    private val Component = DexDesc("${simpleTAG}.Method.Component")


    override fun dexFind(finder: DexKitBridge) {

//            ChattingItem_onMMMenuItemSelected.findDexMethod {
//
//                searchPackages("com.tencent.mm.ui.chatting.viewitems")
//
//                matcher {
//                    usingStrings("context item select failed, null dataTag")
//                }
//            }

//            Chat_Manager.findDexMethod {
//                searchPackages("com.tencent.mm.ui.chatting.manager")
//
//                matcher {
//                    usingStrings(" is not a interface!")
//                }
//            }


          //  DexKit.findDexClass("${simpleTAG}.Class.ViewItems_Container") {
          //      searchPackages("com.tencent.mm.ui.chatting.viewitems")
            //   matcher {
           //        usingStrings("com.tencent.mm.view.x2c.X2CCheckBox")



            //    }
          //  }

        Component.findDexMethod(finder) {
            searchPackages("com.tencent.mm.ui.chatting.component")
            matcher {
                usingStrings("clear video generate callback")
            }

        }

        ViewItemContainer.findDexMethod(finder) {
            searchPackages("com.tencent.mm.ui.chatting")

            matcher {
                declaredClass {
                    usingStrings("x2c.X2CCheckBox")
                    methodCount(1..3)
                }

            }
        }



        Adapter.findDexMethod(finder) {
            searchPackages("com.tencent.mm.ui.chatting.adapter")
                matcher {
                    usingStrings("_onBindViewHolder[","MicroMsg.ChattingDataAdapterV3")
                }
            }


    }




}