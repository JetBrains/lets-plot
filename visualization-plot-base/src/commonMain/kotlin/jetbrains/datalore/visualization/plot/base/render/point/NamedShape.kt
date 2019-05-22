package jetbrains.datalore.visualization.plot.base.render.point

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.base.svg.slim.SvgSlimObject
import jetbrains.datalore.visualization.plot.base.aes.AestheticsUtil
import jetbrains.datalore.visualization.plot.base.render.DataPointAesthetics
import jetbrains.datalore.visualization.plot.base.render.point.symbol.Glyph
import jetbrains.datalore.visualization.plot.base.render.point.symbol.Glyphs

enum class NamedShape(override val code: Int, val isSolid: Boolean = false, val isFilled: Boolean = false, val isSmall: Boolean = false) : PointShape {
    STICK_SQUARE(0),
    STICK_CIRCLE(1),
    STICK_TRIANGLE_UP(2),
    STICK_PLUS(3),
    STICK_CROSS(4),
    STICK_DIAMOND(5),
    STICK_TRIANGLE_DOWN(6),
    STICK_SQUARE_CROSS(7),
    STICK_STAR(8),
    STICK_DIAMOND_PLUS(9),
    STICK_CIRCLE_PLUS(10),
    STICK_TRIANGLE_UP_DOWN(11),
    STICK_SQUARE_PLUS(12),
    STICK_CIRCLE_CROSS(13),
    STICK_SQUARE_TRIANGLE_UP(14),

    SOLID_SQUARE(15, true, false),
    SOLID_CIRCLE(16, true, false),
    SOLID_TRIANGLE_UP(17, true, false),
    SOLID_DIAMOND(18, true, false, true),

    SOLID_CIRCLE_2(19, true, false), // same as SOLID_CIRCLE
    BULLET(20, true, false, true), // same as SOLID_CIRCLE but smaller

    FILLED_CIRCLE(21, false, true),
    FILLED_SQUARE(22, false, true),
    FILLED_DIAMOND(23, false, true),
    FILLED_TRIANGLE_UP(24, false, true),
    FILLED_TRIANGLE_DOWN(25, false, true);

    val isHollow: Boolean
        get() = !(isFilled || isSolid)


    override fun size(dataPoint: DataPointAesthetics): Double {
        return if (isSmall)
            AestheticsUtil.circleDiameterSmaller(dataPoint)
        else
            AestheticsUtil.circleDiameter(dataPoint)
    }

    override fun strokeWidth(dataPoint: DataPointAesthetics): Double {
        // 'size' aes is used for other purpose and
        // no 'stroke width' aes (?)
        return if (isSolid)
            0.0
        else
            1.0
    }

    override fun create(location: DoubleVector, p: DataPointAesthetics): SvgSlimObject {
        val glyph = createSlimGlyph(location, size(p))
        AestheticsUtil.decorate(glyph, isFilled, isSolid, p, strokeWidth(p))
        return glyph
    }

    private fun createSlimGlyph(location: DoubleVector, size: Double): Glyph {
        when (this) {
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
        throw IllegalArgumentException("Unexpected shape $this")
    }
}
