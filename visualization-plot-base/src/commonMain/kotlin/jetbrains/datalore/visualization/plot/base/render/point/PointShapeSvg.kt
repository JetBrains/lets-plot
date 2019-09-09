package jetbrains.datalore.visualization.plot.base.render.point

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimElements
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimObject
import jetbrains.datalore.visualization.plot.base.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.aes.AestheticsUtil
import jetbrains.datalore.visualization.plot.base.render.point.NamedShape.*
import jetbrains.datalore.visualization.plot.base.render.point.symbol.Glyph
import jetbrains.datalore.visualization.plot.base.render.point.symbol.Glyphs

object PointShapeSvg {
    fun create(shape: PointShape, location: DoubleVector, p: DataPointAesthetics): SvgSlimObject {
        if (shape.code == PointShapes.dot().code) {
            return createTinyDotShape(location, p)
        }
        if (shape is NamedShape) {
            return createNamedShape(shape, location, p)
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

    private fun createNamedShape(shape: NamedShape, location: DoubleVector, p: DataPointAesthetics): SvgSlimObject {
        val glyph = createSlimGlyph(shape, location, shape.size(p))
        AestheticsUtil.decorate(glyph, shape.isFilled, shape.isSolid, p, shape.strokeWidth(p))
        return glyph
    }

    private fun createSlimGlyph(shape: NamedShape, location: DoubleVector, size: Double): Glyph {
        when (shape) {
            STICK_SQUARE, SOLID_SQUARE, FILLED_SQUARE -> return Glyphs.square(location, size)

            STICK_CIRCLE, SOLID_CIRCLE, SOLID_CIRCLE_2     // same as SOLID_CIRCLE
                , BULLET             // same as SOLID_CIRCLE but smaller
                , FILLED_CIRCLE -> return Glyphs.circle(location, size)

            STICK_TRIANGLE_UP, SOLID_TRIANGLE_UP, FILLED_TRIANGLE_UP -> return Glyphs.triangleUp(location, size)

            STICK_TRIANGLE_DOWN, FILLED_TRIANGLE_DOWN -> return Glyphs.triangleDown(location, size)

            STICK_DIAMOND, SOLID_DIAMOND, FILLED_DIAMOND -> return Glyphs.diamond(location, size)

            STICK_PLUS -> return Glyphs.stickPlus(location, size)
            STICK_CROSS -> return Glyphs.stickCross(location, size)
            STICK_SQUARE_CROSS -> return Glyphs.stickSquareCross(location, size)
            STICK_STAR -> return Glyphs.stickStar(location, size)
            STICK_DIAMOND_PLUS -> return Glyphs.stickDiamondPlus(location, size)
            STICK_CIRCLE_PLUS -> return Glyphs.stickCirclePlus(location, size)
            STICK_TRIANGLE_UP_DOWN -> return Glyphs.stickTriangleUpDown(location, size)
            STICK_SQUARE_PLUS -> return Glyphs.stickSquarePlus(location, size)
            STICK_CIRCLE_CROSS -> return Glyphs.stickCircleCross(location, size)
            STICK_SQUARE_TRIANGLE_UP -> return Glyphs.stickSquareTriangleUp(location, size)
        }
    }
}