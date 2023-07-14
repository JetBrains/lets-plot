/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.point.symbol

import org.jetbrains.letsPlot.commons.geometry.DoubleVector

object Glyphs {
    fun square(location: DoubleVector, width: Double): Glyph {
        return SquareGlyph(location, width)
    }

    fun circle(location: DoubleVector, width: Double): Glyph {
        return CircleGlyph(location, width)
    }

    fun diamond(location: DoubleVector, width: Double): Glyph {
        return DiamondGlyph(location, width)
    }

    fun triangleUp(location: DoubleVector, width: Double, stroke: Double): Glyph {
        return TriangleGlyph(location, width, stroke, true)
    }

    fun triangleDown(location: DoubleVector, width: Double, stroke: Double): Glyph {
        return TriangleGlyph(location, width, stroke, false)
    }

    fun stickPlus(location: DoubleVector, width: Double): Glyph {
        return PlusGlyph(location, width)
    }

    fun stickCross(location: DoubleVector, width: Double): Glyph {
        return CrossGlyph(location, width)
    }

    fun stickSquareCross(location: DoubleVector, size: Double): Glyph {
        return GlyphPair(
            SquareGlyph(location, size),
            CrossGlyph(location, size, false)
        )
    }

    fun stickStar(location: DoubleVector, size: Double): Glyph {
        return GlyphPair(
            PlusGlyph(location, size),
            CrossGlyph(location, size)
        )
    }

    fun stickDiamondPlus(location: DoubleVector, size: Double): Glyph {
        return GlyphPair(
            DiamondGlyph(location, size),
            PlusGlyph(location, size)
        )
    }

    fun stickCirclePlus(location: DoubleVector, size: Double, stroke: Double): Glyph {
        return GlyphPair(
            CircleGlyph(location, size),
            PlusGlyph(location, size + stroke)
        )
    }

    fun stickTriangleUpDown(location: DoubleVector, size: Double, stroke: Double): Glyph {
        return GlyphPair(
            TriangleGlyph(location, size, stroke, pointingUp = true, pinnedToCentroid = true),
            TriangleGlyph(location, size, stroke, pointingUp = false, pinnedToCentroid = true)
        )
    }

    fun stickSquarePlus(location: DoubleVector, size: Double, stroke: Double): Glyph {
        return GlyphPair(
            SquareGlyph(location, size),
            PlusGlyph(location, size + stroke)
        )
    }

    fun stickCircleCross(location: DoubleVector, size: Double, stroke: Double): Glyph {
        return GlyphPair(
            CircleGlyph(location, size),
            CrossGlyph(location, size + stroke)
        )
    }

    fun stickSquareTriangleUp(location: DoubleVector, size: Double, stroke: Double): Glyph {
        return GlyphPair(
            SquareGlyph(location, size),
            TriangleGlyph(
                location,
                size,
                stroke,
                pointingUp = true,
                pinnedToCentroid = false,
                inscribedInSquare = true
            )
        )
    }
}
