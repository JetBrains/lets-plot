package org.jetbrains.letsPlot.commons.values.awt

import org.jetbrains.letsPlot.commons.values.Bitmap
import java.awt.image.BufferedImage

object BitmapUtil {
    fun fromBufferedImage(image: BufferedImage): Bitmap {
        val argbArray = IntArray(image.width * image.height)
        image.getRGB(0, 0, image.width, image.height, argbArray, 0, image.width)
        return Bitmap(image.width, image.height, argbArray)
    }

    fun toBufferedImage(bitmap: Bitmap): BufferedImage {
        val image = BufferedImage(bitmap.width, bitmap.height, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, bitmap.width, bitmap.height, bitmap.argbInts, 0, bitmap.width)
        return image
    }
}