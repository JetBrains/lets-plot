package kotlin.org.jetbrains.letsPlot.pythonExtension.interop

import demoAndTestShared.ImageComparer
import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.pythonExtension.interop.readFromFile
import org.jetbrains.letsPlot.pythonExtension.interop.writeToFile

/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

object PngBitmapIO : ImageComparer.BitmapIO {
    override fun write(bitmap: Bitmap, filePath: String) {
        val pngData = Png.encode(bitmap)
        writeToFile(filePath, pngData)
    }

    override fun read(filePath: String): Bitmap {
        val pngData = readFromFile(filePath)
        return Png.decode(pngData)
    }
}