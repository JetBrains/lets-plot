/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.render.linetype.LineType
import jetbrains.datalore.plot.base.render.point.PointShape

interface DataPointAesthetics {
    fun index(): Int

    fun x(): Double?

    fun y(): Double?

    fun z(): Double?

    fun ymin(): Double?

    fun ymax(): Double?

    fun color(): Color?

    fun fill(): Color?

    fun alpha(): Double?

    fun shape(): PointShape?

    fun lineType(): LineType

    fun size(): Double?

    fun stacksize(): Double?

    fun width(): Double?

    fun height(): Double?

    fun binwidth(): Double?

    fun violinwidth(): Double?

    fun weight(): Double?

    fun intercept(): Double?

    fun slope(): Double?

    fun interceptX(): Double?

    fun interceptY(): Double?

    fun lower(): Double?

    fun middle(): Double?

    fun upper(): Double?

    fun mapId(): Any

    fun frame(): String

    fun speed(): Double?

    fun flow(): Double?

    fun xmin(): Double?

    fun xmax(): Double?

    fun xend(): Double?

    fun yend(): Double?

    fun label(): Any?

    fun family(): String

    fun fontface(): String

    fun hjust(): Any

    fun vjust(): Any

    fun angle(): Double?

    fun symX(): Double?

    fun symY(): Double?

    fun group(): Int?

    fun numeric(aes: Aes<Double>): Double?

    operator fun <T> get(aes: Aes<T>): T?

    fun defined(aes: Aes<*>): Boolean {
        if (aes.isNumeric) {
            val number = get(aes)
            return number != null && (number as Double).isFinite()
        }
        return true
    }
}
