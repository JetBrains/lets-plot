package demoAndTestShared

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import java.io.File
import javax.imageio.ImageIO

object AwtBitmapIO : ImageComparer.BitmapIO {
    override fun write(bitmap: Bitmap, filePath: String) {
        val img = BitmapUtil.toBufferedImage(bitmap)
        ImageIO.write(img, "png", File(filePath))
    }

    override fun read(filePath: String): Bitmap {
        val img = ImageIO.read(File(filePath))
        if (img == null) {
            throw RuntimeException("Failed to read image from $filePath")
        }
        return BitmapUtil.fromBufferedImage(img)
    }
}
