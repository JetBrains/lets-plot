package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.plot.builder.scale.DefaultNaValue
import jetbrains.datalore.visualization.plot.base.Aes.Companion.SIZE
import jetbrains.datalore.visualization.plot.base.aes.AesScaling

class SizeMapperProvider(
    range: ClosedRange<Double>,
    naValue: Double
) : LinearNormalizingMapperProvider(range, naValue) {

    companion object {
        private val DEF_RANGE = ClosedRange.closed(
            AesScaling.sizeFromCircleDiameter(3.0),
            AesScaling.sizeFromCircleDiameter(21.0)
        )

        val DEFAULT = SizeMapperProvider(
            DEF_RANGE,
            DefaultNaValue[SIZE]
        )
    }
}