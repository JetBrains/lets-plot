/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.theme2

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.builder.theme.PanelTheme

class DefaultPanelTheme(
    options: Map<String, Any>
) : ThemeValuesAccess(options), PanelTheme {
    override fun show(): Boolean {
        return false
    }

    override fun color(): Color {
        TODO("Not yet implemented")
    }

    override fun fill(): Color {
        TODO("Not yet implemented")
    }

    override fun size(): Double {
        TODO("Not yet implemented")
    }
}
