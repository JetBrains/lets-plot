/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.component

import org.jetbrains.letsPlot.core.plot.base.render.svg.SvgComponent
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgNode
import kotlin.math.abs

class RetainableComponents<T : SvgComponent>(
    private val itemFactory: () -> T,
    private val parent: SvgNode
) {
    private val components: MutableList<T> = mutableListOf()

    fun provide(requestCount: Int): List<T> {
        (components.size - requestCount).let { itemsDelta ->
            val toRemove: List<T>?
            val toAdd: List<T>?
            when {
                itemsDelta == 0 -> {
                    toRemove = emptyList()
                    toAdd = emptyList()
                }

                itemsDelta > 0 -> {
                    toRemove = components.takeLast(itemsDelta).toList()
                    toAdd = emptyList()
                }

                itemsDelta < 0 -> {
                    toRemove = emptyList()
                    toAdd = MutableList(abs(itemsDelta)) { itemFactory() }
                }

                else -> throw IllegalStateException("Can't happen")
            }

            toRemove.reversed().forEach {
                parent.children().remove(it.rootGroup)
                components.remove(it)
            }

            toAdd.forEach {
                parent.children().add(it.rootGroup)
                components.add(it)
            }
        }
        return components.toList()
    }
}
