package im.mingxi.mm.hook

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.NinePatch
import android.graphics.Rect
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.NinePatchDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ScaleDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import com.highcapable.kavaref.KavaRef.Companion.resolve
import im.mingxi.core.R
import im.mingxi.loader.bridge.XPHelper
import im.mingxi.loader.util.PathUtil
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.ui.util.FuncRouter
import im.mingxi.miko.ui.util.dpToPx
import im.mingxi.miko.util.dexkit.DexDesc
import im.mingxi.miko.util.dexkit.IFinder
import im.mingxi.miko.util.toAppClass
import org.luckypray.dexkit.DexKitBridge
import java.io.File
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder


@FunctionHookEntry(itemType = FunctionHookEntry.WECHAT_ITEM)
class BubbleHook : SwitchHook(), IFinder {

    class Range(val a: Int, val b: Int) {
        override fun equals(obj: Any?): Boolean {
            if (this === obj) {
                return true
            }
            if (obj !is Range) {
                return false
            }
            val cfcVar = obj
            return this.a == cfcVar.a && this.b == cfcVar.b
        }

        override fun hashCode(): Int {
            return Integer.hashCode(this.b) + (Integer.hashCode(this.a) * 31)
        }

        override fun toString(): String {
            return "Range(start=" + this.a + ", end=" + this.b + ')'
        }
    }

    override val name: String
        get() = "启用气泡美化"
    override val uiItemLocation: String
        get() = FuncRouter.BEAUTIFY
    private val leftPath = "${PathUtil.appPath}left_bubble.9.png"
    private val rightPath = "${PathUtil.appPath}right_bubble.9.png"
    override val description: CharSequence =
        "建议自定义资源文件，自定义说明：自行前往${PathUtil.appPath}设置自定义气泡，左边的气泡为left_bubble.9.png，右边的同理，如无此文件夹请自行创建。"

    fun extractNinePatchChunk(filePath: String): ByteArray? {
        try {
            // Step 1: 打开文件并加载为 Bitmap
            val file = File(filePath)
            val inputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            // Step 2: 检查 .9.png 的 NinePatchChunk 是否为空
            val chunk = bitmap.ninePatchChunk
            if (chunk != null && NinePatch.isNinePatchChunk(chunk)) {
                return chunk // 如果 chunk 存在且合法，直接返回
            }

            // Step 3: 手动解析文件数据，提取 NinePatchChunk
            val fileBytes = file.readBytes()
            val buffer = ByteBuffer.wrap(fileBytes).order(ByteOrder.BIG_ENDIAN)

            // Step 4: 遍历文件块，寻找 "npTc" 标识 (NinePatchChunk 的标志)
            while (buffer.remaining() > 8) {
                val chunkLength = buffer.int // 当前块的长度
                val chunkType = buffer.int   // 当前块的类型

                if (chunkType == 0x6E705463) { // "npTc" 的 ASCII 值
                    val ninePatchChunk = ByteArray(chunkLength)
                    buffer.get(ninePatchChunk)
                    return ninePatchChunk // 找到 NinePatchChunk 并返回
                }

                // 跳过当前块的内容和 CRC 校验码
                buffer.position(buffer.position() + chunkLength + 4)
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null // 如果没有找到合法的 NinePatchChunk，则返回 null
    }

    fun bc(bitmap: Bitmap, z: Boolean): ArrayList<Range> {
        val width = if (z) bitmap.getWidth() else bitmap.getHeight()
        val arrayList: ArrayList<Range> = ArrayList<Range>()
        val i = width - 1
        var i2 = -1
        for (i3 in 1..<i) {
            val pixel = if (z) bitmap.getPixel(i3, 0) else bitmap.getPixel(0, i3)
            val alpha = Color.alpha(pixel)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            if (alpha == 255 && red == 0 && green == 0 && blue == 0) {
                if (i2 == -1) {
                    i2 = i3 - 1
                }
            } else if (i2 != -1) {
                arrayList.add(Range(i2, i3 - 1))
                i2 = -1
            }
        }
        if (i2 != -1) {
            arrayList.add(Range(i2, width - 2))
        }
        return arrayList
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initOnce(): Boolean {
        val target = "com.tencent.mm.ui.widget.MMNeat7extView".toAppClass()
            .resolve()
            .firstMethod {
                name = "setBackground"
                parameterCount(1)
            }.self
        val right = RightViewItem.toMethod().declaringClass.name
        target.hookBeforeIfEnable {
            if (!XPHelper.getStackData().contains("<init>")) return@hookBeforeIfEnable

            val thiz = it.thisObject as View

            val str = if (XPHelper.getStackData().contains(right))
                leftPath
            else rightPath

            val file = File(str)


            // "left:${thiz.context.pxToDp(31f)},top:${thiz.context.pxToDp(14f)},right:${thiz.context.pxToDp(22f)}".d()
            val drawable =
                if (!file.exists()) ScaleDrawable(
                    InsetDrawable(
                        RippleDrawable(
                            ColorStateList.valueOf(Color.TRANSPARENT),
                            thiz.context.getDrawable(R.drawable.bubble),
                            null
                        ).apply {

                        }, -20, -25, -20, -25
                    ), Gravity.CENTER, 1.2f, 1.2f
                ).apply {
                    level = 10000
                }
                else {
                    val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                    val createBitmap =
                        Bitmap.createBitmap(bitmap, 1, 1, bitmap.width - 2, bitmap.height - 2)
                    val bc: ArrayList<Range> = bc(bitmap, true)
                    val bc2: ArrayList<Range> = bc(bitmap, false)
                    val allocate = ByteBuffer.allocate(((bc2.size + bc.size) * 8) + 68)
                    allocate.order(ByteOrder.nativeOrder())
                    allocate.put(1.toByte())
                    allocate.put((bc.size * 2).toByte())
                    allocate.put((bc2.size * 2).toByte())
                    allocate.put(9.toByte())
                    for (i2 in 0..1) {
                        allocate.putInt(0)
                    }
                    for (i3 in 0..3) {
                        allocate.putInt(0)
                    }
                    allocate.putInt(0)
                    val it: MutableIterator<*> = bc.iterator()
                    while (it.hasNext()) {
                        val cfcVar: Range = it.next() as Range
                        allocate.putInt(cfcVar.a)
                        allocate.putInt(cfcVar.b)
                    }
                    val it2: MutableIterator<*> = bc2.iterator()
                    while (it2.hasNext()) {
                        val cfcVar2: Range = it2.next() as Range
                        allocate.putInt(cfcVar2.a)
                        allocate.putInt(cfcVar2.b)
                    }
                    for (i4 in 0..8) {
                        allocate.putInt(1)
                    }
                    NinePatchDrawable(
                        thiz.context.resources,
                        createBitmap,
                        allocate.array(),
                        Rect(),
                        null
                    )

                }


            it.args[0] = drawable
        }

//        "com.tencent.mm.ui.chatting.viewitems.jt".toAppClass().declaredConstructors.forEach {
//            it.hookBeforeIfEnable { param ->
//                "param:${param.args[0]}-${param.args[1]}-${param.args[2]}-${param.args[3]}".d()
//            }
//        }
        "com.tencent.mm.ui.widget.MMNeat7extView".toAppClass().resolve().firstMethod {
            superclass()
            parameters(
                Context::class.java, AttributeSet::class.java,
                Int::class.javaPrimitiveType!!
            )
        }.self.hookAfterIfEnable { param ->
            if (!XPHelper.getStackData().contains("<init>")) return@hookAfterIfEnable
            val view = param.thisObject as View
            if (XPHelper.getStackData().contains(right)) {
                view.setPadding(
                    view.context.dpToPx(17.5f),
                    view.context.dpToPx(8f),
                    view.context.dpToPx(12.5f),
                    view.context.dpToPx(8f)
                )
            } else view.setPadding(
                view.context.dpToPx(12.5f),
                view.context.dpToPx(8f),
                view.context.dpToPx(17.5f),
                view.context.dpToPx(8f)
            )


//          view::class.java.declaredFields.forEach {
//
//              if (it.get(view) is TextView) {
//
//                  (it.get(view) as View).layoutParams = LinearLayout.LayoutParams(-2,-2).apply {
//                      setMargins(view.context.dpToPx(16f))
//                  }
//              }
//          }
//
//            val thiz = view
//
//            val file = File(filePath)
//
//            val bitmap = BitmapFactory.decodeFile(file.path)
//
//            val drawable =
//                if (!file.exists()) HookEnv.hostContext.getDrawable(R.drawable.bubble)
//                else if (NinePatch.isNinePatchChunk(bitmap.ninePatchChunk)) {
//                    NinePatchDrawable(
//                        thiz.context.resources,
//                        bitmap,
//                        bitmap.ninePatchChunk,
//                        Rect(),
//                        null
//                    )
//                } else {
//                    // 如果不是合法的 .9.png，回退普通 BitmapDrawable
//                    bitmap.toDrawable(HookEnv.hostContext.resources)
//                }
//            text.background = drawable
        }
        return true
    }

    private val RightViewItem = DexDesc("$simpleTAG.Method.RightViewItem")
    override fun dexFind(finder: DexKitBridge) {
        RightViewItem.findDexMethod(finder) {
            searchPackages("com.tencent.mm.ui.chatting.viewitems")
            matcher {
                usingStrings("use chatting_item_from_x2c")

            }
        }
    }
}