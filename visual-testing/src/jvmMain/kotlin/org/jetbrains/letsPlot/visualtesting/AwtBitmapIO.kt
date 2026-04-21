/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

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
        private const val TOOLTIPS_SUBDIR = "/tooltips"
    }

    private val expectedImagesDir = if (subdir.isNotEmpty()) "$expectedImagesDir/$subdir" else expectedImagesDir
    private val outputDir = if (subdir.isNotEmpty()) "$outputDir/$subdir" else outputDir

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
        return resolveFilePath(expectedImagesDir, fileName)
    }

    override fun getWriteFilePath(fileName: String): String {
        val filePath = resolveFilePath(outputDir, fileName)
        File(filePath).parentFile.mkdirs()
        return filePath
    }

    private fun resolveFilePath(baseDir: String, fileName: String): String {
        val tooltipSubdir = if (fileName.contains("tooltip", ignoreCase = true)) TOOLTIPS_SUBDIR else ""
        return System.getProperty("user.dir") + "$baseDir$tooltipSubdir/$fileName"
    }
}