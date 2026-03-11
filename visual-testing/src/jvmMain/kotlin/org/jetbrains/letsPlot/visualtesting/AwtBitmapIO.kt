package org.jetbrains.letsPlot.visualtesting

import org.jetbrains.letsPlot.commons.values.Bitmap
import org.jetbrains.letsPlot.commons.values.awt.BitmapUtil
import java.io.File
import javax.imageio.ImageIO

class AwtBitmapIO(
    expectedImagesDir: String = DEFAULT_EXPECTED_IMAGES_DIR,
    outputDir: String = DEFAULT_OUTPUT_DIR,
    subdir: String = ""
) : ImageComparer.BitmapIO {

    companion object {
        const val DEFAULT_EXPECTED_IMAGES_DIR = "/src/test/resources/expected-images"
        const val DEFAULT_OUTPUT_DIR = "/build/reports/actual-images"
    }

    val expectedImagesDir = if (subdir.isNotEmpty()) "$expectedImagesDir/$subdir" else expectedImagesDir
    val outputDir = if (subdir.isNotEmpty()) "$outputDir/$subdir" else outputDir

    override fun write(bitmap: Bitmap, fileName: String) {
        val filePath = getWriteFilePath(fileName)

        try {
            val img = BitmapUtil.toBufferedImage(bitmap)
            ImageIO.write(img, "png", File(filePath))
        } catch (e: Exception) {
            throw RuntimeException("Failed to write image to $filePath", e)
        }
    }

    override fun read(fileName: String): Bitmap {
        val filePath = getReadFilePath(fileName)

        try {
            val img = ImageIO.read(File(filePath))
                ?: throw RuntimeException("Failed to read image from $filePath")
            return BitmapUtil.fromBufferedImage(img)
        } catch (e: Exception) {
            throw RuntimeException("Failed to read image from $filePath", e)
        }
    }

    override fun getReadFilePath(fileName: String): String {
        return System.getProperty("user.dir") + "$expectedImagesDir/$fileName"
    }

    override fun getWriteFilePath(fileName: String): String {
        val dir = File(System.getProperty("user.dir") + outputDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return "$dir/$fileName"
    }
}