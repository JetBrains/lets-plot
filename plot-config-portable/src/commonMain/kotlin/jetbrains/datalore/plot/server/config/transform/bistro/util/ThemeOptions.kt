/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.server.config.transform.bistro.util

import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption
import jetbrains.datalore.plot.config.Option


class ThemeOptions : Options<ThemeOptions>() {
    var axisTitle: Element? by map(ThemeOption.AXIS_TITLE)
    var axisLine: Element? by map(ThemeOption.AXIS_LINE)

    class Element : Options<Element>() {
        var name: String? by map(Option.Meta.NAME)
    }

    companion object {
        val BLANK = Element().apply {
            name = Option.Theme.ELEMENT_BLANK
        }
    }
}
