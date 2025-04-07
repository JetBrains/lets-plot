/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.raster.shape

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.commons.intern.math.toRadians
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.commons.values.FontFace
import org.jetbrains.letsPlot.core.canvas.Canvas
import org.jetbrains.letsPlot.core.canvas.Context2d
import org.jetbrains.letsPlot.core.canvas.FontStyle
import org.jetbrains.letsPlot.core.canvas.FontWeight
import kotlin.math.*

internal fun sdot(a: Number, b: Number, c: Number, d: Number): Double {
    return a.toDouble() * b.toDouble() + c.toDouble() * d.toDouble()
}

internal const val SCALE_X = 0
internal const val SKEW_X = 1
internal const val TRANSLATE_X = 2
internal const val SKEW_Y = 3
internal const val SCALE_Y = 4
internal const val TRANSLATE_Y = 5
internal const val PERSP0 = 6
internal const val PERSP1 = 7
internal const val PERSP2 = 8

class Matrix33(vararg mat: Float) {
    val mat: FloatArray

    init {
        require(mat.size == 9) { "Matrix33 must have 9 elements" }
        this.mat = mat.copyOf()
    }

    fun makeConcat(other: Matrix33): Matrix33 {
        return Matrix33(
            mat[0] * other.mat[0] + mat[1] * other.mat[3] + mat[2] * other.mat[6],
            mat[0] * other.mat[1] + mat[1] * other.mat[4] + mat[2] * other.mat[7],
            mat[0] * other.mat[2] + mat[1] * other.mat[5] + mat[2] * other.mat[8],
            mat[3] * other.mat[0] + mat[4] * other.mat[3] + mat[5] * other.mat[6],
            mat[3] * other.mat[1] + mat[4] * other.mat[4] + mat[5] * other.mat[7],
            mat[3] * other.mat[2] + mat[4] * other.mat[5] + mat[5] * other.mat[8],
            mat[6] * other.mat[0] + mat[7] * other.mat[3] + mat[8] * other.mat[6],
            mat[6] * other.mat[1] + mat[7] * other.mat[4] + mat[8] * other.mat[7],
            mat[6] * other.mat[2] + mat[7] * other.mat[5] + mat[8] * other.mat[8]
        )
    }

    companion object {
        val IDENTITY = Matrix33(
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f
        )

        fun makeTranslate(dx: Float, dy: Float): Matrix33 {
            return Matrix33(1f, 0f, dx, 0f, 1f, dy, 0f, 0f, 1f)
        }

        fun makeScale(s: Float): Matrix33 {
            return makeScale(s, s)
        }

        fun makeScale(sx: Float, sy: Float): Matrix33 {
            return Matrix33(sx, 0f, 0f, 0f, sy, 0f, 0f, 0f, 1f)
        }

        fun makeSkew(sx: Float, sy: Float): Matrix33 {
            return Matrix33(1f, sx, 0f, sy, 1f, 0f, 0f, 0f, 1f)
        }

        fun makeRotate(deg: Float, pivot: DoubleVector): Matrix33 {
            return makeRotate(deg, pivot.x, pivot.y)
        }

        fun makeRotate(deg: Number, pivotx: Number, pivoty: Number): Matrix33 {
            val rad = toRadians(deg.toDouble())
            var sin = sin(rad)
            var cos = cos(rad)
            val tolerance = (1.0f / (1 shl 12)).toDouble()
            if (abs(sin) <= tolerance) sin = 0.0
            if (abs(cos) <= tolerance) cos = 0.0
            return Matrix33(
                cos.toFloat(),
                (-sin).toFloat(),
                (pivotx.toDouble() - pivotx.toDouble() * cos + pivoty.toDouble() * sin).toFloat(),
                sin.toFloat(),
                cos.toFloat(),
                (pivoty.toDouble() - pivoty.toDouble() * cos - pivotx.toDouble() * sin).toFloat(),
                0f,
                0f,
                1f
            )
        }

        fun makeRotate(deg: Float): Matrix33 {
            val rad = toRadians(deg.toDouble())
            var sin = sin(rad)
            var cos = cos(rad)
            val tolerance = (1.0f / (1 shl 12)).toDouble()
            if (abs(sin) <= tolerance) sin = 0.0
            if (abs(cos) <= tolerance) cos = 0.0
            return Matrix33(
                cos.toFloat(),
                (-sin).toFloat(),
                0f,
                sin.toFloat(),
                cos.toFloat(),
                0f,
                0f,
                0f,
                1f
            )
        }

    }
}

fun Matrix33.applyTransform(sx: Number, sy: Number): DoubleVector {
    val x = sdot(sx.toDouble(), scaleX, sy, skewX) + translateX
    val y = sdot(sx.toDouble(), skewY, sy, scaleY) + translateY
    val z = (sdot(sx, persp0, sy, persp1) + persp2).let { if (it != 0.0) 1 / it else it }
    return DoubleVector(x * z, y * z)
}

fun Matrix33.with(idx: Int, v: Float): Matrix33 {
    return Matrix33(*mat).also { it.mat[idx] = v }
}

fun Matrix33.applyTransform(r: DoubleRectangle): DoubleRectangle {
    val lt = applyTransform(r.left, r.top)
    val rt = applyTransform(r.right, r.top)
    val rb = applyTransform(r.right, r.bottom)
    val lb = applyTransform(r.left, r.bottom)

    val xs = listOf(lt.x, rt.x, rb.x, lb.x)
    val ys = listOf(lt.y, rt.y, rb.y, lb.y)

    return DoubleRectangle.LTRB(xs.min(), ys.min(), xs.max(), ys.max())
}

val Matrix33.translateX get() = mat[TRANSLATE_X]
val Matrix33.translateY get() = mat[TRANSLATE_Y]

val Matrix33.scaleX get() = mat[SCALE_X]
val Matrix33.scaleY get() = mat[SCALE_Y]

val Matrix33.skewX get() = mat[SKEW_X]
val Matrix33.skewY get() = mat[SKEW_Y]

val Matrix33.persp0 get() = mat[PERSP0]
val Matrix33.persp1 get() = mat[PERSP1]
val Matrix33.persp2 get() = mat[PERSP2]

internal fun union(rects: List<DoubleRectangle>): DoubleRectangle? =
    rects.fold<DoubleRectangle, DoubleRectangle?>(null) { acc, rect ->
        if (acc != null) {
            DoubleRectangle.LTRB(
                min(rect.left, acc.left),
                min(rect.top, acc.top),
                max(rect.right, acc.right),
                max(rect.bottom, acc.bottom)
            )
        } else {
            rect
        }
    }

internal fun breadthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(element: Element): Sequence<Element> {
        return when (element) {
            is Container -> element.children.asSequence() + element.children.asSequence().flatMap(::enumerate)
            else -> emptySequence()
        }
    }

    return sequenceOf(element) + enumerate(element)
}

internal fun reversedBreadthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(element: Element): Sequence<Element> {
        return when (element) {
            is Container -> {
                val reversed = element.children.asReversed().asSequence()
                reversed.flatMap(::enumerate) + reversed
            }

            else -> emptySequence()
        }
    }

    return enumerate(element) + sequenceOf(element)
}

internal fun depthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(el: Element): Sequence<Element> {
        return when (el) {
            is Container -> sequenceOf(el) + el.children.asSequence().flatMap(::enumerate)
            else -> sequenceOf(el)
        }
    }

    return enumerate(element)
}

internal fun reversedDepthFirstTraversal(element: Element): Sequence<Element> {
    fun enumerate(el: Element): Sequence<Element> {
        return when (el) {
            is Container -> el.children.asReversed().asSequence().flatMap(::enumerate) + sequenceOf(el)
            else -> sequenceOf(el)
        }
    }

    return enumerate(element)
}

fun Matrix33.repr(): String {
    val elements = mutableListOf<String>()
    if (translateX != 0f || translateY != 0f) {
        elements += "translate($translateX, $translateY)"
    }

    if (scaleX != 1f || scaleY != 1f) {
        elements += "scale($scaleX, $scaleY)"
    }

    if (skewX != 0f || skewY != 0f) {
        elements += "skew($skewX, $skewY)"
    }

    if (elements.isEmpty()) {
        return "identity"
    }

    return elements.joinToString(separator = " ")
}

fun DoubleRectangle.contains(x: Float, y: Float): Boolean {
    return x in left..right && y in top..bottom
}

fun DoubleRectangle.contains(x: Int, y: Int): Boolean {
    return x.toFloat() in left..right && y.toFloat() in top..bottom
}

internal fun applyPaint(
    paint: Paint,
    canvas: Canvas
) {
    if (paint.isStroke) {
        canvas.context2d.setLineWidth(paint.strokeWidth.toDouble())
        canvas.context2d.setStrokeStyle(paint.color)
        canvas.context2d.setStrokeMiterLimit(paint.strokeMiter.toDouble())
        canvas.context2d.setLineDash(paint.strokeDashList)

    } else {
        canvas.context2d.setFillStyle(paint.color)
    }
}

internal fun strokePaint(
    stroke: Color? = null,
    strokeWidth: Float = 1f,
    strokeOpacity: Float = 1f,
    strokeDashArray: List<Double> = emptyList(),
    strokeDashOffset: Float = 0f, // not mandatory, default works fine
    strokeMiter: Float? = null
): Paint? {
    if (stroke == null) return null
    if (strokeOpacity == 0f) return null

    if (strokeWidth == 0f) {
        // Handle zero width manually, because Skia threatens 0 as "hairline" width, i.e. 1 pixel.
        // Source: https://api.skia.org/classSkPaint.html#af08c5bc138e981a4e39ad1f9b165c32c
        return null
    }

    val paint = Paint()
    paint.isStroke = true
    paint.color = stroke.changeAlpha(strokeOpacity)
    paint.strokeWidth = strokeWidth
    strokeMiter?.let { paint.strokeMiter = it }
    strokeDashArray.let { paint.strokeDashList = it.toDoubleArray() }
    strokeDashOffset.let { paint.strokeDashOffset = it }
    return paint
}

internal fun fillPaint(fill: Color? = null, fillOpacity: Float = 1f): Paint? {
    if (fill == null) return null

    return Paint().also { paint ->
        paint.color = fill.changeAlpha(fillOpacity)
    }
}

internal fun Context2d.stroke(paint: Paint) {
    require(paint.isStroke) { "Paint must be a stroke paint" }

    setLineWidth(paint.strokeWidth.toDouble())
    setStrokeStyle(paint.color)
    setStrokeMiterLimit(paint.strokeMiter.toDouble())
    setLineDash(paint.strokeDashList)

    stroke()
}

internal fun Context2d.fill(paint: Paint) {
    require(!paint.isStroke) { "Paint must be a fill paint" }

    setFillStyle(paint.color)

    fill()
}

fun Context2d.transform(m: Matrix33) {
    transform(
        m.scaleX.toDouble(),
        m.skewY.toDouble(),
        m.skewX.toDouble(),
        m.scaleY.toDouble(),
        m.translateX.toDouble(),
        m.translateY.toDouble()
    )
}

fun toFontStyle(face: FontFace): FontStyle =
    when (face.italic) {
        true -> FontStyle.ITALIC
        false -> FontStyle.NORMAL
    }

fun toFontWeight(face: FontFace): FontWeight =
    when (face.bold) {
        true -> FontWeight.BOLD
        false -> FontWeight.NORMAL
    }

fun Color.changeAlpha(a: Float) = changeAlpha((255 * a).roundToInt())
