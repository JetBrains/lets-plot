/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.base.gcommon.base.Preconditions.checkState
import jetbrains.datalore.plot.config.ConfigUtil
import jetbrains.datalore.plot.config.OptionsAccessor

/**
 * Specify the display of how non-data components of the plot are a drawn.
 * See: https://ggplot2.tidyverse.org/reference/element.html
 */
internal class ViewElementConfig private constructor(
    private val name: String,
    options: Map<String, Any>
) : OptionsAccessor(options) {

    val isBlank: Boolean
        get() = BLANK == name

    init {
        checkState(BLANK == name, "Only 'element_blank' is supported")
    }

    companion object {
        private const val BLANK = "blank"

        fun create(elem: Any): ViewElementConfig {
            // element - name (like blank)
            // or
            // map with options
            if (elem is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val options = elem as Map<String, Any>
                return createForName(ConfigUtil.featureName(options), options)
            }
            return createForName(elem.toString(), HashMap())
        }

        private fun createForName(name: String, options: Map<String, Any>): ViewElementConfig {
            return ViewElementConfig(name, options)
        }
    }
}
