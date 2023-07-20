/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

enum class FigKind(val id: String) {
    PLOT_SPEC(Option.Meta.Kind.PLOT),
    SUBPLOTS_SPEC(Option.Meta.Kind.SUBPLOTS),
    GG_BUNCH_SPEC(Option.Meta.Kind.GG_BUNCH);

    companion object {
        fun fromOption(option: String?): FigKind {
            if (option == null) {
                throw IllegalArgumentException("Figure spec kind is not defined.")
            }
            return when (option.lowercase()) {
                PLOT_SPEC.id -> PLOT_SPEC
                SUBPLOTS_SPEC.id -> SUBPLOTS_SPEC
                GG_BUNCH_SPEC.id -> GG_BUNCH_SPEC
                else -> throw IllegalArgumentException("Unknown figure spec kind: $option.")
            }
        }
    }
}
