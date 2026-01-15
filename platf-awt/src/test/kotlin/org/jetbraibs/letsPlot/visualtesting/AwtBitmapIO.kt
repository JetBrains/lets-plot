package org.jetbraibs.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import org.jetbrains.letsPlot.visualtesting.ImageComparer
import java.io.File
import javax.imageio.ImageIO

object AwtBitmapIO : ImageComparer.BitmapIO {

    override fun write(bitmap: Bitmap, fileName: String) {
        val img = BitmapUtil.toBufferedImage(bitmap)
        ImageIO.write(img, "png", File(getWriteFilePath(fileName)))
    }

    override fun read(fileName: String): Bitmap {
        val img = ImageIO.read(File(getReadFilePath(fileName)))
        if (img == null) {
            throw RuntimeException("Failed to read image from $fileName")
        }
        return BitmapUtil.fromBufferedImage(img)
    }

    override fun getReadFilePath(fileName: String): String {
        return System.getProperty("user.dir") + "/src/test/resources/expected-images/$fileName"
    }

    override fun getWriteFilePath(fileName: String): String {
        return System.getProperty("user.dir") + "/build/reports/$fileName"
    }
}