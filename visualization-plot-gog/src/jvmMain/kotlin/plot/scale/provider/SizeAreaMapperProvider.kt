package jetbrains.datalore.visualization.plot.gog.plot.scale.provider

import jetbrains.datalore.visualization.plot.gog.core.render.AestheticsUtil

internal class SizeAreaMapperProvider(max: Double?, naValue: Double) : DirectlyProportionalMapperProvider(max
        ?: DEF_MAX, naValue) {
    companion object {

        val DEF_MAX = AestheticsUtil.sizeFromCircleDiameter(21.0)
    }
}