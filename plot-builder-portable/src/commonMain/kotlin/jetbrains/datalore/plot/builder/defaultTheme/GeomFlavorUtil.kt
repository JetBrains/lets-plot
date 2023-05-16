/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.defaultTheme

import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.defaultTheme.values.ThemeOption

object GeomFlavorUtil {
    fun getFlavors(name: String): Map<Aes<*>, Any>? {
        return when (name) {
            ThemeOption.GeomFlavor.DARK -> mapOf(
                Aes.COLOR to Color.WHITE,
                Aes.FILL to Color.BLACK
            )
            ThemeOption.GeomFlavor.LIGHT -> mapOf(
                Aes.COLOR to Color.BLACK,
                Aes.FILL to Color.WHITE
            )
            else -> null
        }
    }
}