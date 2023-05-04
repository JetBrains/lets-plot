/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.point

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.plot.base.DataPointAesthetics
import jetbrains.datalore.plot.base.aes.AestheticsUtil
import jetbrains.datalore.plot.base.render.point.NamedShape.*
import jetbrains.datalore.plot.base.render.point.symbol.Glyph
import jetbrains.datalore.plot.base.render.point.symbol.Glyphs
import jetbrains.datalore.vis.svg.slim.SvgSlimElements
import jetbrains.datalore.vis.svg.slim.SvgSlimObject

object PointShapeSvg {
    fun create(shape: PointShape, location: DoubleVector, p: DataPointAesthetics, fatten: Double = 1.0): SvgSlimObject {
        if (shape == TinyPointShape) {
            return createTinyDotShape(
                location,
                p
            )
        }
        if (shape is NamedShape) {
            val size = shape.size(p, fatten)
            check(size.isFinite()) { "Invalid point size: $size" }
            return createNamedShape(
                shape,
                location,
                size,
                p
            )
        }
        throw IllegalArgumentException("Unsupported point shape code ${shape.code} ${shape::class.simpleName}")
    }

    private fun createTinyDotShape(location: DoubleVector, p: DataPointAesthetics): SvgSlimObject {
        val r = SvgSlimElements.rect(location.x - 0.5, location.y - 0.5, 1.0, 1.0)
        val color = p.color()!!
        val alpha = AestheticsUtil.alpha(color, p)
        r.setFill(color, alpha)
        r.setStrokeWidth(0.0)
        return r
    }

    private fun createNamedShape(
        shape: NamedShape,
        location: DoubleVector,
        size: Double,
        p: DataPointAesthetics
    ): SvgSlimObject {
        val glyph = createSlimGlyph(
            shape,
            location,
            size,
            shape.strokeWidth(p)
        )
        AestheticsUtil.decorate(glyph, shape.isFilled, shape.isSolid, p, shape.strokeWidth(p))
        return glyph
    }

    private fun createSlimGlyph(shape: NamedShape, location: DoubleVector, size: Double, stroke: Double): Glyph {
        when (shape) {
            STICK_SQUARE, SOLID_SQUARE, FILLED_SQUARE -> return Glyphs.square(location, size)

            STICK_CIRCLE, SOLID_CIRCLE, SOLID_CIRCLE_2     // same as SOLID_CIRCLE
                , BULLET             // same as SOLID_CIRCLE but smaller
                , FILLED_CIRCLE -> return Glyphs.circle(location, size)

            STICK_TRIANGLE_UP, SOLID_TRIANGLE_UP, FILLED_TRIANGLE_UP -> return Glyphs.triangleUp(location, size, stroke)

            STICK_TRIANGLE_DOWN, FILLED_TRIANGLE_DOWN -> return Glyphs.triangleDown(location, size, stroke)

            STICK_DIAMOND, SOLID_DIAMOND, FILLED_DIAMOND -> return Glyphs.diamond(location, size)

            STICK_PLUS -> return Glyphs.stickPlus(location, size)
            STICK_CROSS -> return Glyphs.stickCross(location, size)
            STICK_SQUARE_CROSS -> return Glyphs.stickSquareCross(location, size)
            STICK_STAR -> return Glyphs.stickStar(location, size)
            STICK_DIAMOND_PLUS -> return Glyphs.stickDiamondPlus(location, size)
            STICK_CIRCLE_PLUS -> return Glyphs.stickCirclePlus(location, size, stroke)
            STICK_TRIANGLE_UP_DOWN -> return Glyphs.stickTriangleUpDown(location, size, stroke)
            STICK_SQUARE_PLUS -> return Glyphs.stickSquarePlus(location, size, stroke)
            STICK_CIRCLE_CROSS -> return Glyphs.stickCircleCross(location, size, stroke)
            STICK_SQUARE_TRIANGLE_UP -> return Glyphs.stickSquareTriangleUp(location, size, stroke)
        }
    }
}