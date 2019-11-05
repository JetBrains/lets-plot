/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.livemap.entities.rendering

import jetbrains.datalore.base.values.Color
import jetbrains.livemap.core.ecs.EcsComponent

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
