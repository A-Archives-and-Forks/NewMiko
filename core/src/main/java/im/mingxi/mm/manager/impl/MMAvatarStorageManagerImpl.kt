package im.mingxi.mm.manager.impl

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.createBitmap
import im.mingxi.miko.util.dexkit.DexKit
import im.mingxi.miko.util.toAppClass

class MMAvatarStorageManagerImpl {
    @SuppressLint("SuspiciousIndentation")
    fun getAvatarByWxid(wxid: String, name: String = "无名字"): Bitmap {
        val cls = DexKit.requireClassFromCache("AvatarStorage").toAppClass()
        cls.declaredMethods.forEach {
            if (it.parameterTypes.size == 1 && it.parameterTypes[0] == String::class.java) {
                it.isAccessible = true
                val result = it.invoke(cls.newInstance(), wxid)
                return if (result != null) result as Bitmap
                else if (name.isEmpty()) generateCharacterImage(
                    "无".substring(0, 1).toCharArray()[0]
                )
                else generateCharacterImage(name.toString().substring(0, 1).toCharArray()[0])
            }
        }
        throw RuntimeException("getAvatarByWxid not found")
    }

    fun generateCharacterImage(
        char: Char,
        size: Int = 200,
        bgColor: Int = Color.TRANSPARENT,
        textColor: Int = Color.BLACK
    ): Bitmap {
        val bitmap = createBitmap(size, size)
        val canvas = Canvas(bitmap)

        // 绘制背景
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = bgColor
            style = Paint.Style.FILL
        }
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        // 绘制文字
        paint.color = textColor
        paint.textSize = size * 0.6f
        paint.textAlign = Paint.Align.CENTER

        val textBounds = Rect()
        paint.getTextBounds(char.toString(), 0, 1, textBounds)
        val y = size / 2f + (textBounds.height() / 2f) - textBounds.bottom

        canvas.drawText(char.toString(), size / 2f, y, paint)
        return bitmap
    }

}