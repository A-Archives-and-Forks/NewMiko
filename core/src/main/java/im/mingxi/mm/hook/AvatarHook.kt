package im.mingxi.mm.hook

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import im.mingxi.miko.annotation.FunctionHookEntry
import im.mingxi.miko.hook.SwitchHook
import im.mingxi.miko.util.dexkit.DexFinder
import im.mingxi.miko.util.dexkit.DexMethodDescriptor
import im.mingxi.miko.util.dexkit.IFinder


@FunctionHookEntry
class AvatarHook : SwitchHook(), IFinder {
    private val getRoundedCornerBitmap =
        DexMethodDescriptor(this, "${simpleTAG}.Method.getRoundedCornerBitmap")
    override val name: String
        get() = "启用圆形头像"
    override val uiItemLocation: Array<String>
        get() = arrayOf("美化", "其他")


    override fun initOnce(): Boolean {
        getRoundedCornerBitmap.toMethod(loader).hookAfterIfEnable {
            val bitmap = it.result as Bitmap
            it.result = createRoundedBitmap(bitmap, 100f)
        }
        return true
    }

    override fun dexFind(finder: DexFinder) {
        with(finder) {
            getRoundedCornerBitmap.findDexMethod {
                searchPackages("com.tencent.mm.sdk.platformtools")
                matcher {
                    usingStrings("getRoundedCornerBitmap bitmap recycle %s")
                    paramCount(5)
                }
            }
        }
    }

    fun createRoundedBitmap(bitmap: Bitmap, cornerRadius: Float): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val roundedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(roundedBitmap)

        val paint = Paint()
        paint.isAntiAlias = true

        val shader =
            BitmapShader(
                bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
            )
        paint.setShader(shader)

        val path: Path = Path()
        path.addRoundRect(
            RectF(0f, 0f, width.toFloat(), height.toFloat()),
            cornerRadius,
            cornerRadius,
            Path.Direction.CCW
        )

        canvas.drawPath(path, paint)

        return roundedBitmap
    }

}