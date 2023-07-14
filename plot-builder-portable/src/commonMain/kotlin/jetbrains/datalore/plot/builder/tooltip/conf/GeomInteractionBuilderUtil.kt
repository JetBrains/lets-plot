/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.tooltip.conf

import jetbrains.datalore.plot.builder.tooltip.*
import jetbrains.datalore.plot.builder.tooltip.data.ConstantField
import jetbrains.datalore.plot.builder.tooltip.data.MappingField
import jetbrains.datalore.plot.builder.tooltip.data.ValueSource
import jetbrains.datalore.plot.builder.tooltip.TooltipSpecification

internal object GeomInteractionBuilderUtil {

    fun createTooltipLines(
        userTooltipSpec: TooltipSpecification,
        tooltipAes: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        tooltipAxisAes: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        sideTooltipAes: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        tooltipConstantAes: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>?,
    ): List<TooltipLine> {

        return when {
            userTooltipSpec.useDefaultTooltips() -> {
                // No user line patterns => use default tooltips with the given formatted valueSources
                // and move content of side tooltips to the general tooltip in 'disable_splitting' mode
                defaultValueSourceTooltipLines(
                    aesListForTooltip = if (userTooltipSpec.disableSplitting) {
                        (sideTooltipAes + tooltipAes).distinct()
                    } else {
                        tooltipAes
                    },
                    tooltipAxisAes,
                    sideTooltipAes = if (userTooltipSpec.disableSplitting) {
                        emptyList()
                    } else {
                        sideTooltipAes
                    },
                    userTooltipSpec.valueSources,
                    tooltipConstantAes
                )
            }
            userTooltipSpec.hideTooltips() -> {
                // User list is empty => not show tooltips
                emptyList()
            }
            else -> {
                // Form value sources: user list + axis + side tooltips

                val geomSideTooltips = if (userTooltipSpec.disableSplitting) {
                    // Hide side tooltips in 'disable_splitting' mode with specified lines
                    emptyList()
                } else {
                    sideTooltipAes
                }.toMutableList()

                // Remove side tooltip if the mappedAes is used in the general tooltip
                userTooltipSpec.tooltipLinePatterns!!.forEach { line ->
                    val userDataAesList = line.fields.filterIsInstance<MappingField>().map(MappingField::aes)
                    geomSideTooltips.removeAll(userDataAesList)
                }
                val axisValueSources = tooltipAxisAes.map { aes ->
                    getMappingValueSource(aes, isSide = true, isAxis = true, userTooltipSpec.valueSources)
                }
                val geomSideValueSources = geomSideTooltips.map { aes ->
                    getMappingValueSource(aes, isSide = true, isAxis = false, userTooltipSpec.valueSources)
                }

                userTooltipSpec.tooltipLinePatterns +
                        (axisValueSources + geomSideValueSources)
                            .map(TooltipLine.Companion::defaultLineForValueSource)
            }
        }
    }


    private fun getMappingValueSource(
        aes: org.jetbrains.letsPlot.core.plot.base.Aes<*>,
        isSide: Boolean,
        isAxis: Boolean,
        userDefinedValueSources: List<ValueSource>?,
        label: String? = null
    ): ValueSource {
        val userDefined = userDefinedValueSources?.filterIsInstance<MappingField>()?.find { it.aes == aes }
        return userDefined?.withFlags(isSide, isAxis, label) ?: MappingField(
            aes,
            isSide = isSide,
            isAxis = isAxis,
            label = label
        )
    }

    internal fun defaultValueSourceTooltipLines(
        aesListForTooltip: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        axisAes: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        sideTooltipAes: List<org.jetbrains.letsPlot.core.plot.base.Aes<*>>,
        userDefinedValueSources: List<ValueSource>? = null,
        constantsMap: Map<org.jetbrains.letsPlot.core.plot.base.Aes<*>, Any>? = null
    ): List<TooltipLine> {
        val axisValueSources = axisAes.map { aes ->
            getMappingValueSource(aes, isSide = true, isAxis = true, userDefinedValueSources)
        }
        val sideValueSources = sideTooltipAes.map { aes ->
            getMappingValueSource(aes, isSide = true, isAxis = false, userDefinedValueSources)
        }

        // will use empty label in one-line tooltip for positional aes and constants
        val aesForGeneralTooltip = aesListForTooltip - sideTooltipAes + (constantsMap?.keys ?: emptyList())
        val isOneLineTooltip = aesForGeneralTooltip.size  == 1

        val aesValueSources = aesListForTooltip.map { aes ->
            val label = if (isOneLineTooltip && aes in listOf(org.jetbrains.letsPlot.core.plot.base.Aes.X, org.jetbrains.letsPlot.core.plot.base.Aes.Y)) "" else null
            getMappingValueSource(aes, isSide = false, isAxis = false, userDefinedValueSources, label)
        }
        val constantFields = constantsMap?.map { (aes, value) ->
            val label = if (isOneLineTooltip) "" else null
            val userDefined = userDefinedValueSources?.filterIsInstance<ConstantField>()?.find { it.aes == aes }
            userDefined?.withLabel(label) ?: ConstantField(aes, value, format = null, label = label)
        } ?: emptyList()

        return (aesValueSources + axisValueSources + sideValueSources + constantFields).map(TooltipLine.Companion::defaultLineForValueSource)
    }

}