package jetbrains.livemap.chart

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.Client
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

// Pie/Bar chart data
class SymbolComponent : EcsComponent {
    var size: Vec<Client> = explicitVec(0.0, 0.0)
    var indices: List<Int> = emptyList()
    var values: List<Double> = emptyList()
    var colors: List<Color> = emptyList()
}
