package jetbrains.datalore.visualization.plot.builder.scale.provider

import jetbrains.datalore.visualization.plot.base.aes.AesScaling

class SizeAreaMapperProvider(
    max: Double?,
    naValue: Double
) : DirectlyProportionalMapperProvider(max ?: DEF_MAX, naValue) {

    companion object {
        val DEF_MAX = AesScaling.sizeFromCircleDiameter(21.0)
    }
}