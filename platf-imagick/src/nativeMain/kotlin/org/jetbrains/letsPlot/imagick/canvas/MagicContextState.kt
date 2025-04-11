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
    transform: ImageMagick.AffineMatrix,
) {

    var lineDashPattern: CArrayPointer<DoubleVar>? = lineDashPattern
        set(value) {
            field?.let { nativeHeap.free(it.rawValue) }
            field = value
        }

    val affineMatrix: ImageMagick.AffineMatrix = nativeHeap.alloc<ImageMagick.AffineMatrix>()
    val transformMatrix: Matrix33 get() {
        return Matrix33(
            affineMatrix.sx.toFloat(),
            affineMatrix.rx.toFloat(),
            affineMatrix.tx.toFloat(),
            affineMatrix.ry.toFloat(),
            affineMatrix.sy.toFloat(),
            affineMatrix.ty.toFloat(),
            0f,
            0f,
            1f
        )
    }

    init {
        affineMatrix.sx = transform.sx
        affineMatrix.sy = transform.sy
        affineMatrix.rx = transform.rx
        affineMatrix.ry = transform.ry
        affineMatrix.tx = transform.tx
        affineMatrix.ty = transform.ty
    }

    fun setTransform(value: ImageMagick.AffineMatrix) {
        affineMatrix.sx = value.sx
        affineMatrix.sy = value.sy
        affineMatrix.rx = value.rx
        affineMatrix.ry = value.ry
        affineMatrix.tx = value.tx
        affineMatrix.ty = value.ty
    }

    fun setTransform(m11: Double, m12: Double, m21: Double, m22: Double, dx: Double, dy: Double) {
        affineMatrix.sx = m11
        affineMatrix.sy = m12
        affineMatrix.rx = m21
        affineMatrix.ry = m22
        affineMatrix.tx = dx
        affineMatrix.ty = dy
    }

    fun transform(sx: Double, ry: Double, rx: Double, sy: Double, dx: Double, dy: Double) {
        val cur = Matrix33(
            affineMatrix.sx.toFloat(),
            affineMatrix.rx.toFloat(),
            affineMatrix.tx.toFloat(),
            affineMatrix.ry.toFloat(),
            affineMatrix.sy.toFloat(),
            affineMatrix.ty.toFloat(),
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

        affineMatrix.sx = res.scaleX.toDouble()
        affineMatrix.sy = res.scaleY.toDouble()
        affineMatrix.rx = res.skewX.toDouble()
        affineMatrix.ry = res.skewY.toDouble()
        affineMatrix.tx = res.translateX.toDouble()
        affineMatrix.ty = res.translateY.toDouble()
    }

    fun destroy() {
        lineDashPattern?.let { nativeHeap.free(it.rawValue) }
        lineDashPattern = null

        //nativeHeap.free(affineMatrix.rawPtr)
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
            transform = affineMatrix,
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

fun ImageMagick.AffineMatrix.repr(): String {
    return "matrix(sx=$sx, ry=$ry, rx=$rx, sy=$sy, tx=$tx, ty=$ty)"
}
