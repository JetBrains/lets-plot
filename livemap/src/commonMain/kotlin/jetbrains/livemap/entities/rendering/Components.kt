package jetbrains.livemap.entities.rendering

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

class PointComponent : EcsComponent {
    var shape: Int = 0
}

class TextComponent : EcsComponent {
    lateinit var textSpec: TextSpec
}

class PieSectorComponent : EcsComponent {
    var radius = 0.0
    var startAngle = 0.0
    var endAngle = 0.0
}

class StyleComponent : EcsComponent {
    var fillColor: String? = null
    var strokeColor: String? = null
    var strokeWidth: Double = 0.toDouble()
    var lineDash: DoubleArray? = null
}

fun StyleComponent.setLineDash(lineDash: List<Double>) { this.lineDash = lineDash.toDoubleArray() }
fun StyleComponent.setFillColor(fillColor: Color) { this.fillColor = fillColor.toCssColor() }
fun StyleComponent.setStrokeColor(strokeColor: Color) { this.strokeColor = strokeColor.toCssColor() }
fun StyleComponent.setStrokeWidth(strokeWidth: Double) { this.strokeWidth = strokeWidth }