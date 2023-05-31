/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.tooltip.*

internal object GeomInteractionBuilderUtil {

    fun createTooltipLines(
        userTooltipSpec: TooltipSpecification,
        tooltipAes: List<Aes<*>>,
        tooltipAxisAes: List<Aes<*>>,
        sideTooltipAes: List<Aes<*>>,
        tooltipConstantAes: Map<Aes<*>, Any>?,
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
                    outliers = if (userTooltipSpec.disableSplitting) {
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
                // Form value sources: user list + axis + outliers

                val geomOutliers = if (userTooltipSpec.disableSplitting) {
                    // Hide outliers in 'disable_splitting' mode with specified lines
                    emptyList()
                } else {
                    sideTooltipAes
                }.toMutableList()

                // Remove outlier tooltip if the mappedAes is used in the general tooltip
                userTooltipSpec.tooltipLinePatterns!!.forEach { line ->
                    val userDataAesList = line.fields.filterIsInstance<MappingValue>().map(MappingValue::aes)
                    geomOutliers.removeAll(userDataAesList)
                }
                val axisValueSources = tooltipAxisAes.map { aes ->
                    getMappingValueSource(aes, isOutlier = true, isAxis = true, userTooltipSpec.valueSources)
                }
                val geomOutlierValueSources = geomOutliers.map { aes ->
                    getMappingValueSource(aes, isOutlier = true, isAxis = false, userTooltipSpec.valueSources)
                }

                userTooltipSpec.tooltipLinePatterns +
                        (axisValueSources + geomOutlierValueSources)
                            .map(TooltipLine.Companion::defaultLineForValueSource)
            }
        }
    }


    private fun getMappingValueSource(
        aes: Aes<*>,
        isOutlier: Boolean,
        isAxis: Boolean,
        userDefinedValueSources: List<ValueSource>?,
        label: String? = null
    ): ValueSource {
        val userDefined = userDefinedValueSources?.filterIsInstance<MappingValue>()?.find { it.aes == aes }
        return userDefined?.withFlags(isOutlier, isAxis, label) ?: MappingValue(
            aes,
            isOutlier = isOutlier,
            isAxis = isAxis,
            label = label
        )
    }

    internal fun defaultValueSourceTooltipLines(
        aesListForTooltip: List<Aes<*>>,
        axisAes: List<Aes<*>>,
        outliers: List<Aes<*>>,
        userDefinedValueSources: List<ValueSource>? = null,
        constantsMap: Map<Aes<*>, Any>? = null
    ): List<TooltipLine> {
        val axisValueSources = axisAes.map { aes ->
            getMappingValueSource(aes, isOutlier = true, isAxis = true, userDefinedValueSources)
        }
        val outlierValueSources = outliers.map { aes ->
            getMappingValueSource(aes, isOutlier = true, isAxis = false, userDefinedValueSources)
        }

        // will use empty label in one-line tooltip for positional aes and constants
        val aesForGeneralTooltip = aesListForTooltip - outliers + (constantsMap?.keys ?: emptyList())
        val isOneLineTooltip = aesForGeneralTooltip.size  == 1

        val aesValueSources = aesListForTooltip.map { aes ->
            val label = if (isOneLineTooltip && aes in listOf(Aes.X, Aes.Y)) "" else null
            getMappingValueSource(aes, isOutlier = false, isAxis = false, userDefinedValueSources, label)
        }
        val constantValues = constantsMap?.map { (aes, value) ->
            val label = if (isOneLineTooltip) "" else null
            val userDefined = userDefinedValueSources?.filterIsInstance<ConstantValue>()?.find { it.aes == aes }
            userDefined?.withLabel(label) ?: ConstantValue(aes, value, format = null, label = label)
        } ?: emptyList()

        return (aesValueSources + axisValueSources + outlierValueSources + constantValues).map(TooltipLine.Companion::defaultLineForValueSource)
    }

}