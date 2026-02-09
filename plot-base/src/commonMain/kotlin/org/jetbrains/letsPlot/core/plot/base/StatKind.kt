package org.jetbrains.letsPlot.core.plot.base

import org.jetbrains.letsPlot.core.commons.enums.EnumInfoFactory

enum class StatKind {
    IDENTITY,
    COUNT,
    COUNT2D,
    BIN,
    BIN2D,
    BINHEX,
    DOTPLOT,
    SMOOTH,
    SMOOTH2,
    CONTOUR,
    CONTOURF,
    BOXPLOT,
    BOXPLOT_OUTLIER,
    DENSITYRIDGES,
    YDENSITY,
    SINA,
    YDOTPLOT,
    DENSITY,
    DENSITY2D,
    DENSITY2DF,
    POINTDENSITY,
    QQ,
    QQ2,
    QQ_LINE,
    QQ2_LINE,
    ECDF,
    SUM,
    SUMMARY,
    SUMMARYBIN;


    companion object {

        private val ENUM_INFO = EnumInfoFactory.createEnumInfo<StatKind>()

        fun safeValueOf(name: String): StatKind {
            return ENUM_INFO.safeValueOf(name) ?: throw IllegalArgumentException("Unknown stat name: '$name'")
        }
    }

}