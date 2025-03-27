/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.imagick.canvas

import kotlinx.cinterop.*
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.imagick.canvas.MagickContext2d.Companion.IDENTITY
import org.jetbrains.letsPlot.raster.shape.*

internal class MagickContextState(
    var strokeColor: String,
    var strokeWidth: Double,
    lineDashPattern: CArrayPointer<DoubleVar>?,
    var lineDashPatternSize: ULong,
    var lineDashOffset: Double,
    var miterLimit: ULong,
    var lineCap: ImageMagick.LineCap,
    var lineJoin: ImageMagick.LineJoin,
    var fillColor: String,
    var fontSize: Double,
    var fontFamily: String,
    var fontStyle: ImageMagick.StyleType,
    var fontWeight: ULong,
    var transform: ImageMagick.AffineMatrix
) {
    var lineDashPattern: CArrayPointer<DoubleVar>? = lineDashPattern
        set(value) {
            field?.let { nativeHeap.free(it.rawValue) }
            field = value
        }


    fun transform(sx: Double, rx: Double, ry: Double, sy: Double, dx: Double, dy: Double) {
        val cur = Matrix33(
            transform.sx.toFloat(),
            transform.rx.toFloat(),
            transform.tx.toFloat(),
            transform.ry.toFloat(),
            transform.sy.toFloat(),
            transform.ty.toFloat(),
            0f,
            0f,
            1f
        )

        val tr = Matrix33(
            sx.toFloat(),
            rx.toFloat(),
            dx.toFloat(),
            ry.toFloat(),
            sy.toFloat(),
            dy.toFloat(),
            0f,
            0f,
            1f
        )

        val res = cur.makeConcat(tr)

        transform = nativeHeap.alloc()
        transform.sx = res.scaleX.toDouble()
        transform.sy = res.scaleY.toDouble()
        transform.rx = res.skewX.toDouble()
        transform.ry = res.skewY.toDouble()
        transform.tx = res.translateX.toDouble()
        transform.ty = res.translateY.toDouble()
    }

    fun destroy() {
        lineDashPattern?.let { nativeHeap.free(it.rawValue) }
        lineDashPattern = null
    }

    fun copy(): MagickContextState {
        return MagickContextState(
            strokeColor = strokeColor,
            strokeWidth = strokeWidth,
            lineDashPatternSize = lineDashPatternSize,
            lineDashOffset = lineDashOffset,
            miterLimit = miterLimit,
            lineCap = lineCap,
            lineJoin = lineJoin,
            fillColor = fillColor,
            fontSize = fontSize,
            fontFamily = fontFamily,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            transform = transform,
            lineDashPattern = lineDashPattern?.let { pattern ->
                nativeHeap.allocArray(lineDashPatternSize.toInt()) { i -> value = pattern[i] }
            },
        )
    }

    companion object {
        fun create(
            strokeColor: String = Color.TRANSPARENT.toCssColor(),
            strokeWidth: Double = 1.0,
            lineDashPatternSize: ULong = 0u,
            lineDashOffset: Double = 0.0,
            miterLimit: ULong = 10u,
            lineCap: ImageMagick.LineCap = ImageMagick.LineCap.ButtCap,
            lineJoin: ImageMagick.LineJoin = ImageMagick.LineJoin.MiterJoin,
            fillColor: String = Color.TRANSPARENT.toCssColor(),
            fontSize: Double = 12.0,
            fontFamily: String = "Arial",
            fontStyle: ImageMagick.StyleType = ImageMagick.StyleType.NormalStyle,
            fontWeight: ULong = 400u,
            transform: ImageMagick.AffineMatrix = IDENTITY,
            lineDashPattern: CArrayPointer<DoubleVar>? = null
        ): MagickContextState {
            return MagickContextState(
                strokeColor = strokeColor,
                strokeWidth = strokeWidth,
                lineDashPattern = lineDashPattern,
                lineDashPatternSize = lineDashPatternSize,
                lineDashOffset = lineDashOffset,
                miterLimit = miterLimit,
                lineCap = lineCap,
                lineJoin = lineJoin,
                fillColor = fillColor,
                fontSize = fontSize,
                fontFamily = fontFamily,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                transform = transform
            )
        }
    }
}