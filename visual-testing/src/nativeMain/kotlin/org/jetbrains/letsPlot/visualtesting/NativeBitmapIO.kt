package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.intern.io.Native
import org.jetbrains.letsPlot.commons.values.Bitmap

class NativeBitmapIO(
    expectedImagesDir: String = DEFAULT_EXPECTED_IMAGES_DIR,
    outputDir: String = DEFAULT_OUTPUT_DIR,
    subdir: String = ""
) : ImageComparer.BitmapIO {

    companion object {
        const val DEFAULT_EXPECTED_IMAGES_DIR = "/src/nativeTest/resources/expected-images"
        const val DEFAULT_OUTPUT_DIR = "/build/reports/actual-images"
    }

    val expectedImagesDir = if (subdir.isNotEmpty()) "$expectedImagesDir/$subdir" else expectedImagesDir
    val outputDir = if (subdir.isNotEmpty()) "$outputDir/$subdir" else outputDir

    override fun write(bitmap: Bitmap, fileName: String) {
        val pngData = Png.encode(bitmap)
        Native.writeToFile(getWriteFilePath(fileName), pngData)
    }

    override fun read(fileName: String): Bitmap {
        val pngData = Native.readFromFile(getReadFilePath(fileName))
        return Png.decode(pngData)
    }

    override fun getReadFilePath(fileName: String): String {
        return Native.getCurrentDir() + "$expectedImagesDir/$fileName"
    }

    override fun getWriteFilePath(fileName: String): String {
        val dirPath = Native.getCurrentDir() + outputDir
        if (!Native.dirExists(dirPath)) {
            Native.mkdirs(dirPath)
        }

        return "$dirPath/$fileName"
    }
}