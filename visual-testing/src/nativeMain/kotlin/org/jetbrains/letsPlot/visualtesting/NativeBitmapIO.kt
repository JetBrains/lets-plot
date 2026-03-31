package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.encoding.Png
import org.jetbrains.letsPlot.commons.intern.io.NativeIO
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
        NativeIO.writeToFile(getWriteFilePath(fileName), pngData)
    }

    override fun read(fileName: String): Bitmap {
        val pngData = NativeIO.readFromFile(getReadFilePath(fileName))
        return Png.decode(pngData)
    }

    override fun getReadFilePath(fileName: String): String {
        return NativeIO.getCurrentDir() + "$expectedImagesDir/$fileName"
    }

    override fun getWriteFilePath(fileName: String): String {
        val dirPath = NativeIO.getCurrentDir() + outputDir
        if (!NativeIO.dirExists(dirPath)) {
            NativeIO.mkdirs(dirPath)
        }

        return "$dirPath/$fileName"
    }
}