/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.util

data class DisplayHtmlPolicy constructor(
    val dynamicScriptLoading: Boolean,
    val forceImmediateRender: Boolean,
    val responsive: Boolean,
    val height100pct: Boolean,
) {

    companion object {
        fun jupyterNotebook(): DisplayHtmlPolicy {
            return DisplayHtmlPolicy(
                dynamicScriptLoading = true,
                forceImmediateRender = false,
                responsive = false,
                height100pct = false,
            )
        }

        fun entirelyStatic(): DisplayHtmlPolicy {
            return DisplayHtmlPolicy(
                dynamicScriptLoading = false,
                forceImmediateRender = false,
                responsive = false,
                height100pct = false,
            )
        }
    }
}