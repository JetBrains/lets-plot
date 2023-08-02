/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec

import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory

enum class StatKind {
    IDENTITY,
    COUNT,
    COUNT2D,
    BIN,
    BIN2D,
    DOTPLOT,
    SMOOTH,
    CONTOUR,
    CONTOURF,
    BOXPLOT,
    BOXPLOT_OUTLIER,
    DENSITYRIDGES,
    YDENSITY,
    YDOTPLOT,
    DENSITY,
    DENSITY2D,
    DENSITY2DF,
    QQ,
    QQ2,
    QQ_LINE,
    QQ2_LINE,
    ECDF,
    SUMMARY,
    SUMMARYBIN;


    companion object {

        private val ENUM_INFO = EnumInfoFactory.createEnumInfo<StatKind>()

        fun safeValueOf(name: String): StatKind {
            return ENUM_INFO.safeValueOf(name) ?: throw IllegalArgumentException("Unknown stat name: '$name'")
        }
    }

}
