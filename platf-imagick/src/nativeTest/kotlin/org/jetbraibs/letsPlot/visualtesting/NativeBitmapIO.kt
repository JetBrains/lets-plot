package org.jetbraibs.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.visualtesting.ImageComparer

object NativeBitmapIO : ImageComparer.BitmapIO {
    override fun write(bitmap: Bitmap, fileName: String) {
        val pngData = Png.encode(bitmap)
        Native.writeToFile(getWriteFilePath(fileName), pngData)
    }

    override fun read(fileName: String): Bitmap {
        val pngData = Native.readFromFile(getReadFilePath(fileName))
        return Png.decode(pngData)
    }

    override fun getReadFilePath(fileName: String): String {
        return Native.getCurrentDir() + "/src/nativeTest/resources/expected/$fileName"
    }

    override fun getWriteFilePath(fileName: String): String {
        return Native.getCurrentDir() + "/build/reports/$fileName"
    }
}