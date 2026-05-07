/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.tooltip

import org.jetbrains.letsPlot.core.plot.base.*
import org.jetbrains.letsPlot.core.plot.base.scale.Scales
import org.jetbrains.letsPlot.core.plot.base.theme.AxisTheme
import org.jetbrains.letsPlot.core.plot.base.theme.Theme
import org.jetbrains.letsPlot.core.plot.base.theme.TooltipsTheme
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.DefaultTooltipBehaviorFactory
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.GeomInteractionBuilder
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.GeomInteractionUtil
import org.jetbrains.letsPlot.core.plot.base.tooltip.conf.TooltipBehavior
import org.mockito.Mockito

internal object GeomInteractionTestingFactory {
    fun createBuilder(
        geomKind: GeomKind,
        statKind: StatKind,
        renderedAes: List<Aes<*>>,
        mappings: Collection<MappedDataAccessMock.Mapping<*>>,
        constantsMap: Map<Aes<*>, Any> = emptyMap(),
        tooltipBehaviorTransform: (TooltipBehavior) -> TooltipBehavior = { it },
    ): GeomInteractionBuilder {
        val variablesByAes = renderedAes.associateWith { aes ->
            DataFrame.Variable.createOriginal(aes.name)
        }
        val bindings = mappings.associate { mapping ->
            mapping.aes to variablesByAes.getValue(mapping.aes)
        }
        val scaleMap = mappings.associate { mapping ->
            mapping.aes to mapping.createScale()
        }
        val tooltipBehavior = tooltipBehaviorTransform(
            DefaultTooltipBehaviorFactory.create(
                geomKind,
                statKind,
                TooltipBehavior.DEFAULT
            )
        )

        return GeomInteractionUtil.createGeomInteractionBuilder(
            geomKind = geomKind,
            renderedAes = renderedAes,
            bindings = bindings,
            scaleMap = scaleMap,
            constantsMap = constantsMap,
            getOriginalVariableName = { aes -> bindings[aes]?.name },
            tooltipBehavior = tooltipBehavior,
            isLiveMap = false,
            isPolarCoordSystem = false,
            isYOrientation = false,
            theme = theme()
        )
    }

    private fun MappedDataAccessMock.Mapping<*>.createScale(): Scale {
        return if (isContinuous) {
            Scales.DemoAndTest.continuousDomain(label, aes)
        } else {
            val domain = buildList {
                add(value)
                repeat(4) { index -> add("$value-$index") }
            }
            Scales.DemoAndTest.discreteDomain(label, domain)
        }
    }

    private fun theme(): Theme {
        val axisTheme = Mockito.mock(AxisTheme::class.java).also { axisTheme ->
            Mockito.`when`(axisTheme.showTooltip()).thenReturn(true)
            Mockito.`when`(axisTheme.showLabels()).thenReturn(true)
        }
        val tooltipsTheme = Mockito.mock(TooltipsTheme::class.java).also { tooltipsTheme ->
            Mockito.`when`(tooltipsTheme.show()).thenReturn(true)
        }
        return Mockito.mock(Theme::class.java).also { theme ->
            Mockito.`when`(theme.horizontalAxis(false)).thenReturn(axisTheme)
            Mockito.`when`(theme.verticalAxis(false)).thenReturn(axisTheme)
            Mockito.`when`(theme.tooltips()).thenReturn(tooltipsTheme)
        }
    }
}
