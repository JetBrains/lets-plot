/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.rendering

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.base.projectionGeometry.Vec
import jetbrains.datalore.base.projectionGeometry.explicitVec
import jetbrains.datalore.base.spatial.GeoUtils.toRadians
import jetbrains.livemap.projections.Client
import kotlin.math.abs
import kotlin.math.max

class TextSpec(
    val label: String,
    fontface: String,
    size: Int,
    family: String,
    degreeAngle: Double,
    hjust: Double,
    vjust: Double,
    textMeasurer: TextMeasurer
) {
    val font = "$fontface ${size}px $family"
    val dimension: Vec<Client>
    val alignment: Vec<Client>
    val angle: Double = toRadians(-degreeAngle)

    init {
        val textSize = textMeasurer.measure(label, font)

        alignment = explicitVec(textSize.x * (1.0 - hjust), textSize.y * (1.0 - vjust))

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