/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.livemap.chart

import org.jetbrains.letsPlot.commons.geometry.DoubleRectangle
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Scalar
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.intern.typedGeometry.times
import org.jetbrains.letsPlot.commons.values.Color
import org.jetbrains.letsPlot.livemap.Client
import org.jetbrains.letsPlot.livemap.Client.Companion.px
import org.jetbrains.letsPlot.livemap.World
import org.jetbrains.letsPlot.livemap.chart.donut.StrokeSide
import org.jetbrains.letsPlot.livemap.chart.path.ArrowSpec
import org.jetbrains.letsPlot.livemap.chart.text.TextSpec
import org.jetbrains.letsPlot.livemap.core.ecs.EcsComponent

// Predefined location of a chart element, used by map to initialize its viewport initial state
class ChartElementLocationComponent : EcsComponent {
    lateinit var geometry: Geometry<World>
}

// Common rendering data - used for lines, polygons, pies, bars, points.
class ChartElementComponent : EcsComponent {
    var strokeWidth: Double = 0.0
    var fillColor: Color? = null
    var strokeColor: Color? = null
    var lineDash: DoubleArray? = null
    var lineDashOffset: Double = 0.0
    var arrowSpec: ArrowSpec? = null
    var lineheight: Double? = null

    var startPadding: Scalar<Client> = 0.px
    var endPadding: Scalar<Client> = 0.px

    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false
    var scalingSizeFactor: Double = 1.0
    var scalingAlphaValue: Int? = null

    var nudgeClient: Vec<Client> = Vec(0.0, 0.0)
    var enableNudgeScaling: Boolean = false

    fun scaledStrokeColor() = alphaScaledColor(strokeColor!!, scalingAlphaValue)
    fun scaledFillColor() = alphaScaledColor(fillColor!!, scalingAlphaValue)
    fun scaledStrokeWidth() = strokeWidth * scalingSizeFactor
    fun scaledLineDash() = lineDash!!.map { it * scalingSizeFactor }.toDoubleArray()
    fun scaledLineDashOffset() = lineDashOffset * scalingSizeFactor

    fun scaledStartPadding() = startPadding * scalingSizeFactor
    fun scaledEndPadding() = endPadding * scalingSizeFactor

    fun scaledNudge(): Vec<Client> {
        return if (enableNudgeScaling) nudgeClient * scalingSizeFactor else nudgeClient
    }
}

class TextSpecComponent : EcsComponent {
    lateinit var textSpec: TextSpec

    fun scaledFont(scalingSizeFactor: Double) = textSpec.font.copy(fontSize = textSpec.font.fontSize * scalingSizeFactor)
    fun scaledLineHeight(scalingSizeFactor: Double) = textSpec.lineHeight * scalingSizeFactor
    fun scaledTextSize(scalingSizeFactor: Double) = textSpec.textSize.mul(scalingSizeFactor)
    fun scaledPadding(scalingSizeFactor: Double) = textSpec.padding * scalingSizeFactor
    fun scaledRectangle(scalingSizeFactor: Double): DoubleRectangle {
        return with (textSpec) {
            val width = (textSize.x + padding * 2) * scalingSizeFactor
            val height = (textSize.y + padding * 2) * scalingSizeFactor
            DoubleRectangle(
                -width * hjust,
                -height * vjust,
                width,
                height
           )
        }
    }
    fun scaledLabelSize(scalingSizeFactor: Double) = textSpec.labelSize * scalingSizeFactor
}

class PointComponent : EcsComponent {
    var size: Double = 0.0

    fun scaledRadius(scalingSizeFactor: Double) = size * scalingSizeFactor / 2.0
}

class PieSpecComponent : EcsComponent {
    var radius: Double = 0.0
    var holeSize: Double = 0.0
    var indices: List<Int> = emptyList()
    var sliceValues: List<Double> = emptyList()
    var fillColors: List<Color> = emptyList()
    var strokeColors: List<Color> = emptyList()
    var strokeWidths: List<Double> = emptyList()
    var explodeValues: List<Double>? = null
    var spacerColor: Color? = null
    var spacerWidth: Double = 0.0
    var strokeSide: StrokeSide? = null
}

class SearchResultComponent : EcsComponent {
    var hoverObjects: List<HoverObject> = emptyList()
    var zoom : Int? = null
    var cursorPosition : Vec<Client>? = null
}

class IndexComponent(val layerIndex: Int, val index: Int): EcsComponent
class LocatorComponent(val locator: Locator): EcsComponent