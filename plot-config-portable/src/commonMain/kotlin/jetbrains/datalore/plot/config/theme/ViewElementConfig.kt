/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config.theme

import jetbrains.datalore.plot.config.Option.Theme.ELEMENT_BLANK
import jetbrains.datalore.plot.config.OptionsAccessor

internal class ViewElementConfig private constructor(
    private val name: String,
    options: Map<String, Any>
) : OptionsAccessor(options) {

    val isBlank: Boolean
        get() = ELEMENT_BLANK == name

    init {
        check(ELEMENT_BLANK == name) { "Only 'element_blank' is supported" }
    }

    companion object {

        fun create(elem: Any): ViewElementConfig {
//            // element - name (like blank)
//            // or
//            // map with options
//            if (elem is Map<*, *>) {
//                @Suppress("UNCHECKED_CAST")
//                val options = elem as Map<String, Any>
//                return createForName(ConfigUtil.featureName(options), options)
//            }
//
//            return createForName(elem.toString(), HashMap())

            // This is a part of old 'theme' where only "blank" was supported
            return ViewElementConfig(ELEMENT_BLANK, emptyMap())
        }

//        private fun createForName(name: String, options: Map<String, Any>): ViewElementConfig {
//            return ViewElementConfig(name, options)
//        }
    }
}
