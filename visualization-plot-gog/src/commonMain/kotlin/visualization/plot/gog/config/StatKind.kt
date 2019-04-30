package jetbrains.datalore.visualization.plot.gog.config

import jetbrains.datalore.base.enums.EnumInfoFactory

internal enum class StatKind {
    IDENTITY,
    COUNT,
    BIN,
    SMOOTH,
    CONTOUR,
    CONTOURF,
    BOXPLOT,
    DENSITY,
    DENSITY2D,
    DENSITY2DF;


    companion object {

        private val ENUM_INFO = EnumInfoFactory.createEnumInfo<StatKind>()

        fun safeValueOf(name: String): StatKind {
            return ENUM_INFO.safeValueOf(name) ?: throw IllegalArgumentException("Unknown stat name: '$name'")
        }
    }

}
