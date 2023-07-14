/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.scale

class ScaleBreaks(
    val domainValues: List<Any>,
    val transformedValues: List<Double>,
    val labels: List<String>
) {
    val isEmpty: Boolean
        get() = domainValues.isEmpty()

    val size: Int
        get() = domainValues.size

    init {
        require(domainValues.size == transformedValues.size) {
            "Scale breaks size: ${domainValues.size} transformed size: ${transformedValues.size} but expected to be the same"
        }
        require(domainValues.size == labels.size) {
            "Scale breaks size: ${domainValues.size} labels size: ${labels.size} but expected to be the same"
        }
    }

    companion object {
        val EMPTY: ScaleBreaks = ScaleBreaks(emptyList(), emptyList(), emptyList())
    }
}
