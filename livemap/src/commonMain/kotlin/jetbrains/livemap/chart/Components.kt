package jetbrains.livemap.chart

import jetbrains.datalore.base.typedGeometry.Vec
import jetbrains.datalore.base.typedGeometry.explicitVec
import jetbrains.datalore.base.values.Color
import jetbrains.livemap.Client
import jetbrains.livemap.core.ecs.EcsComponent


// Common rendering data - used for lines, polygons, pies, bars, points.
class ChartElementComponent : EcsComponent {
    var strokeWidth: Double = 0.0
    var fillColor: Color? = null
    var strokeColor: Color? = null
    var lineDash: DoubleArray? = null

    var scalable: Boolean = false
    var scaleFactor: Double = 1.0
}

class TextSpecComponent : EcsComponent {
    lateinit var textSpec: TextSpec
}

class PieSectorComponent : EcsComponent {
    var radius = 0.0
    var startAngle = 0.0
    var endAngle = 0.0
}

// Pie/Bar chart data
class SymbolComponent : EcsComponent {
    var size: Vec<Client> = explicitVec(0.0, 0.0)
    var indices: List<Int> = emptyList()
    var values: List<Double> = emptyList()
    var colors: List<Color> = emptyList()
}
