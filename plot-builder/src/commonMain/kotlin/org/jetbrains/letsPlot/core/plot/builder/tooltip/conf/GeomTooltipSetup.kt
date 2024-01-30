/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.tooltip.conf

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.base.tooltip.GeomTargetLocator

class GeomTooltipSetup private constructor(
    val locatorLookupSpace: GeomTargetLocator.LookupSpace,
    val locatorLookupStrategy: GeomTargetLocator.LookupStrategy,
    val axisAesFromFunctionKind: List<Aes<*>>,// = emptyList()
    val axisTooltipVisibilityFromFunctionKind: Boolean, // = false
    val axisTooltipEnabled: Boolean,// = true
) {

    fun toMultilayerLookupStrategy(): GeomTooltipSetup {
        return GeomTooltipSetup(
            locatorLookupStrategy = GeomTargetLocator.LookupStrategy.NEAREST,
            locatorLookupSpace = GeomTargetLocator.LookupSpace.XY,
            axisAesFromFunctionKind = axisAesFromFunctionKind,
            axisTooltipVisibilityFromFunctionKind = axisTooltipVisibilityFromFunctionKind,
            axisTooltipEnabled = axisTooltipEnabled
        )
    }

    companion object {
        const val AREA_GEOM = true
        const val NON_AREA_GEOM = false

        private val AES_X = listOf(org.jetbrains.letsPlot.core.plot.base.Aes.X)
        private val AES_Y = listOf(org.jetbrains.letsPlot.core.plot.base.Aes.Y)
        private val AES_XY =
            listOf(org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y)

        fun xUnivariateFunction(
            lookupStrategy: GeomTargetLocator.LookupStrategy,
            axisTooltipVisibilityFromConfig: Boolean? = null
        ): GeomTooltipSetup {
            val axisTooltipVisibilityFromFunctionKind = true
            return GeomTooltipSetup(
                locatorLookupStrategy = lookupStrategy,
                locatorLookupSpace = GeomTargetLocator.LookupSpace.X,
                axisAesFromFunctionKind = AES_X,
                axisTooltipVisibilityFromFunctionKind = axisTooltipVisibilityFromFunctionKind,
                axisTooltipEnabled = isAxisTooltipEnabled(
                    axisTooltipVisibilityFromConfig,
                    axisTooltipVisibilityFromFunctionKind
                )
            )
        }

        fun yUnivariateFunction(
            lookupStrategy: GeomTargetLocator.LookupStrategy,
            axisTooltipVisibilityFromConfig: Boolean? = null
        ): GeomTooltipSetup {
            val axisTooltipVisibilityFromFunctionKind = true
            return GeomTooltipSetup(
                locatorLookupStrategy = lookupStrategy,
                locatorLookupSpace = GeomTargetLocator.LookupSpace.Y,
                axisAesFromFunctionKind = AES_Y,
                axisTooltipVisibilityFromFunctionKind = axisTooltipVisibilityFromFunctionKind,
                axisTooltipEnabled = isAxisTooltipEnabled(
                    axisTooltipVisibilityFromConfig,
                    axisTooltipVisibilityFromFunctionKind
                )
            )
        }

        fun bivariateFunction(
            area: Boolean,
            axisTooltipVisibilityFromConfig: Boolean? = null
        ): GeomTooltipSetup {
            val axisTooltipVisibilityFromFunctionKind = !area
            val locatorLookupStrategy = if (area) {
                GeomTargetLocator.LookupStrategy.HOVER
            } else {
                GeomTargetLocator.LookupStrategy.NEAREST
            }

            return GeomTooltipSetup(
                locatorLookupStrategy = locatorLookupStrategy,
                locatorLookupSpace = GeomTargetLocator.LookupSpace.XY,
                axisAesFromFunctionKind = AES_XY,
                axisTooltipVisibilityFromFunctionKind = axisTooltipVisibilityFromFunctionKind,
                axisTooltipEnabled = isAxisTooltipEnabled(
                    axisTooltipVisibilityFromConfig,
                    axisTooltipVisibilityFromFunctionKind
                )
            )
        }

        fun none(): GeomTooltipSetup {
            val axisTooltipVisibilityFromFunctionKind = true
            return GeomTooltipSetup(
                locatorLookupStrategy = GeomTargetLocator.LookupStrategy.NONE,
                locatorLookupSpace = GeomTargetLocator.LookupSpace.NONE,
                axisAesFromFunctionKind = emptyList(),
                axisTooltipVisibilityFromFunctionKind = axisTooltipVisibilityFromFunctionKind,
                axisTooltipEnabled = isAxisTooltipEnabled(
                    axisTooltipVisibilityFromConfig = null,
                    axisTooltipVisibilityFromFunctionKind
                )
            )
        }

        private fun isAxisTooltipEnabled(
            axisTooltipVisibilityFromConfig: Boolean?,
            axisTooltipVisibilityFromFunctionKind: Boolean
        ): Boolean {
            return axisTooltipVisibilityFromConfig ?: axisTooltipVisibilityFromFunctionKind
        }
    }
}