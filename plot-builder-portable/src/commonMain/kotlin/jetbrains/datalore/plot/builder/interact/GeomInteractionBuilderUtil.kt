/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.builder.tooltip.*

internal object GeomInteractionBuilderUtil {

    fun createTooltipLines(
        userTooltipSpec: TooltipSpecification?,
        tooltipAes: List<Aes<*>>,
        tooltipAxisAes: List<Aes<*>>,
        sideTooltipAes: List<Aes<*>>,
        tooltipConstantAes: Map<Aes<*>, Any>?,
    ): List<TooltipLine> {

        return when {
            userTooltipSpec == null -> {
                // No user tooltip specification => use default tooltips
                defaultValueSourceTooltipLines(
                    tooltipAes,
                    tooltipAxisAes,
                    sideTooltipAes,
                    userDefinedValueSources = null,
                    constantsMap = tooltipConstantAes
                )
            }
            userTooltipSpec.useDefaultTooltips() -> {
                // No user line patterns => use default tooltips with the given formatted valueSources
                defaultValueSourceTooltipLines(
                    tooltipAes,
                    tooltipAxisAes,
                    sideTooltipAes,
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
                val geomOutliers = sideTooltipAes.toMutableList()

                // Remove outlier tooltip if the mappedAes is used in the general tooltip
                userTooltipSpec.tooltipLinePatterns!!.forEach { line ->
                    val userDataAesList = line.fields.filterIsInstance<MappingValue>().map { it.aes }
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
        userDefinedValueSources: List<ValueSource>?
    ): ValueSource {
        val userDefined = userDefinedValueSources?.filterIsInstance<MappingValue>()?.find { it.aes == aes }
        return userDefined?.withFlags(isOutlier, isAxis) ?: MappingValue(
            aes,
            isOutlier = isOutlier,
            isAxis = isAxis
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
        val aesValueSources = aesListForTooltip.map { aes ->
            getMappingValueSource(aes, isOutlier = false, isAxis = false, userDefinedValueSources)
        }
        val constantValues = constantsMap?.map { (aes, value) ->
            val userDefined = userDefinedValueSources?.filterIsInstance<ConstantValue>()?.find { it.aes == aes }
            userDefined ?: ConstantValue(aes, value, format = null)
        } ?: emptyList()
        return (aesValueSources + axisValueSources + outlierValueSources + constantValues).map(TooltipLine.Companion::defaultLineForValueSource)
    }

}