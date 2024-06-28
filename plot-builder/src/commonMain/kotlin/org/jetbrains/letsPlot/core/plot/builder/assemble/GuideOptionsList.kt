/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import org.jetbrains.letsPlot.core.plot.base.Aes


class GuideOptionsList {
    private val options = mutableListOf<GuideOptions>()

    fun add(option: GuideOptions) = options.add(option)

    fun hasNone() = options.contains(GuideOptions.NONE)

    fun getTitle(): String? {
        val optsWithTitle = options.filterIsInstance<GuideTitleOption>().ifEmpty { options }
        return optsWithTitle.findLast { it.title != null }?.title
    }

    fun getLegendOptions(): LegendOptions? {
        val legendOptionsList = options.filterIsInstance<LegendOptions>()
        if (legendOptionsList.isEmpty()) return null
        return LegendOptions.combine(legendOptionsList)
    }

    fun getColorBarOptions(): ColorBarOptions? {
        return options.filterIsInstance<ColorBarOptions>().lastOrNull()
    }

    operator fun plus(other: GuideOptionsList): GuideOptionsList {
        this.options.addAll(other.options)
        return this
    }
}

data class GuideKey internal constructor(private val key: String) {
    companion object {
        fun fromAes(aes: Aes<*>) = GuideKey(aes.name)
        fun fromName(name: String) = GuideKey(name)
    }
}