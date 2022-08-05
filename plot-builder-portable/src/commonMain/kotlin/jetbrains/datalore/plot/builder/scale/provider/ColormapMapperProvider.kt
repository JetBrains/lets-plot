/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.provider

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.base.values.Color
import jetbrains.datalore.plot.base.ContinuousTransform
import jetbrains.datalore.plot.base.DiscreteTransform
import jetbrains.datalore.plot.base.ScaleMapper
import jetbrains.datalore.plot.base.scale.MapperUtil
import jetbrains.datalore.plot.builder.scale.GuideMapper
import jetbrains.datalore.plot.builder.scale.mapper.GuideMappers
import jetbrains.datalore.plot.common.colormap.ColorMaps
import jetbrains.datalore.plot.common.colormap.ColorMaps.VIRIDIS


/**
 * @param cmapName Name of colormap.
 *      Values:
 *      - "magma" (or "A"),
 *      - "inferno" (or "B")
 *      - "plasma" (or "C")
 *      - "viridis" (or "D")
 *      - "cividis" (or "E")
 *      - "turbo"
 *      - "twilight"
 *
 * @param alpha Alpha transparency channel. (0 means transparent and 1 means opaque).
 * @param begin Corresponds to a color hue to start at.
 * @param end Corresponds to a color hue to end with.
 * @param direction Sets the order of colors in the scale. If 1, the default, colors are as output by brewer.pal.
 * If -1, the order of colors is reversed
 * @param naValue
 */
class ColormapMapperProvider(
    cmapName: String?,
    alpha: Double?,
    begin: Double?,
    end: Double?,
    private val direction: Double?,
    naValue: Color
) : MapperProviderBase<Color>(naValue) {

    private val cmapName = cmapName ?: VIRIDIS
    private val alpha = alpha ?: 1.0
    private val begin = begin ?: 0.0
    private val end = end ?: 1.0

    init {
        val r01 = DoubleSpan(0.0, 1.0)
        require(r01.contains(this.alpha)) { "'alpha' should be in range [0..1]" }
        require(r01.contains(this.begin)) { "'begin' should be in range [0..1]" }
        require(r01.contains(this.end)) { "'end' should be in range [0..1]" }
    }

    override fun createDiscreteMapper(discreteTransform: DiscreteTransform): ScaleMapper<Color> {
        val n = discreteTransform.effectiveDomain.size
        val colors = colors(n)
        return GuideMappers.discreteToDiscrete(discreteTransform, colors, naValue)
    }

    override fun createContinuousMapper(domain: DoubleSpan, trans: ContinuousTransform): GuideMapper<Color> {
        val colors = colors(n = null)

        @Suppress("NAME_SHADOWING")
        val domain = MapperUtil.rangeWithLimitsAfterTransform(domain, trans)
//        return GuideMappers.continuousToDiscrete(domain, colors, naValue)
        val gradient = ColorGradientnMapperProvider.createGradient(domain, colors, naValue, alpha)
        return GuideMappers.asContinuous(ScaleMapper.wrap(gradient))
    }

    private fun colors(n: Int? = null): List<Color> {
        val colors = ColorMaps.getColors(cmapName, alpha, DoubleSpan(begin, end), n)
        return when (direction?.let { direction < 0 } ?: false) {
            true -> colors.reversed()
            false -> colors
        }
    }
}
