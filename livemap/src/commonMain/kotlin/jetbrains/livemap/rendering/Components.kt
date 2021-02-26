package jetbrains.livemap.rendering

import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsComponent

class RendererComponent(var renderer: Renderer) : EcsComponent

class LayerEntitiesComponent : EcsComponent {

    private val myEntities = HashSet<Int>()

    val entities: Collection<Int>
        get() = myEntities

    fun add(entity: Int) {
        myEntities.add(entity)
    }

    fun remove(entity: Int) {
        myEntities.remove(entity)
    }
}

class ShapeComponent : EcsComponent {
    var shape: Int = 0
}

class TextSpecComponent : EcsComponent {
    lateinit var textSpec: TextSpec
}

class PieSectorComponent : EcsComponent {
    var radius = 0.0
    var startAngle = 0.0
    var endAngle = 0.0
}

class StyleComponent : EcsComponent {
    var fillColor: Color? = null
    var strokeColor: Color? = null
    var strokeWidth: Double = 0.0
    var lineDash: DoubleArray? = null
}

fun StyleComponent.setLineDash(lineDash: List<Double>) { this.lineDash = lineDash.toDoubleArray() }
fun StyleComponent.setFillColor(fillColor: Color) { this.fillColor = fillColor }
fun StyleComponent.setStrokeColor(strokeColor: Color) { this.strokeColor = strokeColor }
fun StyleComponent.setStrokeWidth(strokeWidth: Double) { this.strokeWidth = strokeWidth }