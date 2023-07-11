package jetbrains.livemap.chart

import org.jetbrains.letsPlot.commons.intern.typedGeometry.Geometry
import org.jetbrains.letsPlot.commons.intern.typedGeometry.Vec
import org.jetbrains.letsPlot.commons.values.Color
import jetbrains.livemap.Client
import jetbrains.livemap.World
import jetbrains.livemap.chart.path.PathRenderer.ArrowSpec
import jetbrains.livemap.chart.text.TextSpec
import jetbrains.livemap.core.ecs.EcsComponent

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
    var explodeValues: List<Double>? = null
}

class SearchResultComponent : EcsComponent {
    var hoverObjects: List<HoverObject> = emptyList()
    var zoom : Int? = null
    var cursorPosition : Vec<Client>? = null
}

class IndexComponent(val layerIndex: Int, val index: Int): EcsComponent
class LocatorComponent(val locator: Locator): EcsComponent