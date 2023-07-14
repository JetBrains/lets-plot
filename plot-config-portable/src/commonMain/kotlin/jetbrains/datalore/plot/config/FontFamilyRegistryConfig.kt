/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

import org.jetbrains.letsPlot.core.plot.builder.presentation.DefaultFontFamilyRegistry
import org.jetbrains.letsPlot.core.plot.builder.presentation.FontFamilyRegistry

internal class FontFamilyRegistryConfig(private val plotOptions: OptionsAccessor) {
    fun createFontFamilyRegistry(): FontFamilyRegistry {
        val metainfoOptionList = plotOptions.getList(Option.Plot.METAINFO_LIST)

        var defaultWidthFactor = 1.0
        val fontInfos = ArrayList<OptionsAccessor>()
        for (metainfoRaw in metainfoOptionList) {
            if (metainfoRaw is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                val metainfoOpts = OptionsAccessor(metainfoRaw as Map<String, Any>)
                when (metainfoOpts.getStringSafe(Option.Meta.NAME)) {
                    Option.PlotMetainfo.FONT_METRICS_ADJUSTMENT -> defaultWidthFactor =
                        metainfoOpts.getDoubleDef(Option.FontMetainfo.WIDTH_CORRECTION, defaultWidthFactor)
                    Option.PlotMetainfo.FONT_FAMILY_INFO -> fontInfos.add(metainfoOpts)
                }
            }
        }
        val familyRegistry = DefaultFontFamilyRegistry(defaultWidthFactor)
        fontInfos.forEach {
            val mono = if (it.has(Option.FontMetainfo.MONOSPACED)) {
                it.getBoolean(Option.FontMetainfo.MONOSPACED, false)
            } else {
                null
            }
            familyRegistry.put(
                name = it.getStringSafe(Option.FontMetainfo.FAMILY),
                isMonospased = mono,
                widthFactor = it.getDouble(Option.FontMetainfo.WIDTH_CORRECTION)
            )
        }

        return familyRegistry
    }
}