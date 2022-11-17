package jetbrains.livemap.chart

import jetbrains.datalore.base.values.Color
import jetbrains.livemap.chart.Renderers.PathRenderer.ArrowSpec
import jetbrains.livemap.core.ecs.EcsComponent


// Common rendering data - used for lines, polygons, pies, bars, points.
class ChartElementComponent : EcsComponent {
    var strokeWidth: Double = 0.0
    var fillColor: Color? = null
    var strokeColor: Color? = null
    var lineDash: DoubleArray? = null
    var arrowSpec: ArrowSpec? = null
    var lineheight: Double? = null

    var sizeScalingRange: ClosedRange<Int>? = null
    var alphaScalingEnabled: Boolean = false
    var scalingSizeFactor: Double = 1.0
    var scalingAlphaValue: Int? = null
}

class TextSpecComponent : EcsComponent {
    lateinit var textSpec: TextSpec
}

class PointComponent : EcsComponent {
    var size: Double = 0.0
}

class PieSpecComponent : EcsComponent {
    var radius: Double = 0.0
    var holeSize: Double = 0.0
    var indices: List<Int> = emptyList()
    var sliceValues: List<Double> = emptyList()
    var colors: List<Color> = emptyList()
    var explodeValues: List<Double> = emptyList()
}
