package jetbrains.livemap.obj2entity

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.GeoUtils.toRadians
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.livemap.mapobjects.MapText
import jetbrains.livemap.projections.Client
import kotlin.math.abs
import kotlin.math.max

class TextSpec(text: MapText, textMeasurer: TextMeasurer) {

    val label = text.label
    val font = "${text.fontface} ${text.size.toInt()}px ${text.family}"
    val dimension: Vec<Client>
    val alignment: Vec<Client>
    val angle: Double = toRadians(-text.angle)

    init {
        val textSize = textMeasurer.measure(label, font)

        val hjust = 1.0 - text.hjust
        val vjust = 1.0 - text.vjust
        alignment = explicitVec(textSize.x * hjust, textSize.y * vjust)

        dimension = rotateTextSize(textSize.mul(2.0), angle)
    }

    private fun rotateTextSize(textSize: DoubleVector, angle: Double): Vec<Client> {
        val p1 = DoubleVector(textSize.x / 2, +textSize.y / 2).rotate(angle)
        val p2 = DoubleVector(textSize.x / 2, -textSize.y / 2).rotate(angle)

        val maxX = max(abs(p1.x), abs(p2.x))
        val maxY = max(abs(p1.y), abs(p2.y))
        return explicitVec(maxX * 2, maxY * 2)
    }
}