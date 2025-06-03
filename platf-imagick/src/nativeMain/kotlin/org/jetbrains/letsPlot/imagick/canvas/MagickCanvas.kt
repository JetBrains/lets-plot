/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.refTo
import kotlinx.cinterop.toKString
import org.jetbrains.letsPlot.commons.encoding.Base64
import org.jetbrains.letsPlot.commons.geometry.Vector
import org.jetbrains.letsPlot.commons.intern.async.Async
import org.jetbrains.letsPlot.commons.intern.async.Asyncs
import org.jetbrains.letsPlot.commons.registration.Disposable
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.nat.encoding.png.ImageInfo
import org.jetbrains.letsPlot.nat.encoding.png.ImageLineByte
import org.jetbrains.letsPlot.nat.encoding.png.OutputPngStream
import org.jetbrains.letsPlot.nat.encoding.png.PngWriter

class MagickCanvas(
    private val _img: CPointer<ImageMagick.MagickWand>,
    override val size: Vector,
    pixelDensity: Double,
) : Canvas {

    // TODO: replace usage in tests with Snapshot
    val img: CPointer<ImageMagick.MagickWand>?
        get() {
            val wand = (context2d as MagickContext2d).wand
            if (false) {
                val v = ImageMagick.DrawGetVectorGraphics(wand)
                println(v!!.toKString())
            }

            ImageMagick.MagickDrawImage(_img, wand)
            return _img
        }

    override val context2d: Context2d = MagickContext2d(_img, pixelDensity)


    override fun takeSnapshot(): Async<Canvas.Snapshot> {
        return Asyncs.constant(immidiateSnapshot())
    }

    override fun immidiateSnapshot(): Canvas.Snapshot {
        return MagickSnapshot(_img)
    }

    fun saveBmp(filename: String) {
        if (ImageMagick.MagickWriteImage(img, filename) == ImageMagick.MagickFalse) {
            throw RuntimeException("Failed to write image")
        }
    }

    companion object {
        fun create(width: Number, height: Number, pixelDensity: Number): MagickCanvas {
            return create(Vector(width.toInt(), height.toInt()), pixelDensity)
        }

        fun create(size: Vector, pixelDensity: Number): MagickCanvas {
            val wand = ImageMagick.NewMagickWand() ?: error("MagickCanvas: Failed to create new MagickWand")
            val background = ImageMagick.NewPixelWand()
            ImageMagick.PixelSetColor(background, "white")
            ImageMagick.MagickNewImage(wand, size.x.toULong(), size.y.toULong(), background)
            return MagickCanvas(wand, size, pixelDensity = pixelDensity.toDouble())
        }
    }

    class MagickSnapshot(
        img: CPointer<ImageMagick.MagickWand>
    ) : Disposable, Canvas.Snapshot {
        val img: CPointer<ImageMagick.MagickWand> = ImageMagick.CloneMagickWand(img) ?: error("MagickSnapshot: Failed to clone image wand")
        override val size: Vector = Vector(
            ImageMagick.MagickGetImageWidth(img).toInt(),
            ImageMagick.MagickGetImageHeight(img).toInt()
        )

        override fun dispose() {
            ImageMagick.DestroyMagickWand(img)
        }

        override fun copy(): Canvas.Snapshot {
            val copiedImg = ImageMagick.CloneMagickWand(img) ?: error("MagickSnapshot: Failed to clone image wand")
            return MagickSnapshot(copiedImg)
        }

        override fun toDataUrl(): String {
            val (pixels, size) = exportPixels(img)

            if (pixels.isEmpty()) {
                return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mP8//8/AwAI/wH+9QAAAABJRU5ErkJggg=="
            }

            val outputStream = OutputPngStream()
            val png = PngWriter(outputStream, ImageInfo(size.x, size.y, bitdepth = 8, alpha = true))
            val iLine = ImageLineByte(png.imgInfo)

            pixels.asSequence()
                .windowed(size.x * 4, size.x * 4)
                .forEachIndexed { _, byteArray ->
                    byteArray.forEachIndexed { col, byte ->
                        iLine.scanline[col] = byte.toByte()
                    }
                    png.writeRow(iLine)
                }

            png.end()
            return "data:image/png;base64,${Base64.encode(outputStream.byteArray)}"
        }

        private fun exportPixels(wand: CPointer<ImageMagick.MagickWand>): Pair<UByteArray, Vector> {
            val width = ImageMagick.MagickGetImageWidth(wand).toInt()
            val height = ImageMagick.MagickGetImageHeight(wand).toInt()
            println("MagickCanvas: Exporting pixels, size: $width x $height")

            if (width <= 0 || height <= 0) {
                return UByteArray(0) to Vector.ZERO
            }


            val pixels = UByteArray(width * height * 4) // RGBA
            val success = ImageMagick.MagickExportImagePixels(
                wand, 0, 0, width.toULong(), height.toULong(),
                "RGBA",
                ImageMagick.StorageType.CharPixel,
                pixels.refTo(0)
            )
            if (success == ImageMagick.MagickFalse) error("Failed to export pixels")
            return pixels to Vector(width, height)
        }

        companion object {
            fun fromPixels(rgba: ByteArray, size: Vector): MagickSnapshot {
                require(size.x > 0 && size.y > 0) { "MagickCanvas: Size must be greater than zero" }
                require(rgba.size == size.x * size.y * 4) { // 4 bytes per pixel (RGBA)
                    "MagickCanvas: Byte array size does not match the specified size"
                }

                val img = ImageMagick.NewMagickWand() ?: error("MagickCanvas: Failed to create new MagickWand")
                ImageMagick.MagickImportImagePixels(
                    img,
                    0,
                    0,
                    size.x.toULong(),
                    size.y.toULong(),
                    "RGBA",
                    ImageMagick.StorageType.CharPixel,
                    rgba.asUByteArray().refTo(0)
                )
                return MagickCanvas.MagickSnapshot(img)
            }
        }

    }
}
