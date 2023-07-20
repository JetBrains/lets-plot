/*
 * Copyright (c) 2022. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.config

import demoAndTestShared.parsePlotSpec
import org.jetbrains.letsPlot.core.spec.config.FontFamilyRegistryConfig
import org.jetbrains.letsPlot.core.spec.config.OptionsAccessor
import kotlin.test.Test
import kotlin.test.assertEquals

class FontFamilyRegistryConfigTest {

    @Test
    fun `no metainfo`() {
        val spec = """
            {'mapping': {},
             'data_meta': {},
             'kind': 'plot',
             'scales': [],
             'layers': [],
             'metainfo_list': []}
         """.trimIndent()

        checkFontinfo(spec, FAMILY)
        checkFontinfo(spec, FAMILY_MONO, monospaced = true)
    }

    @Test
    fun `no metainfo at all`() {
        val spec = """
            {'mapping': {},
             'data_meta': {},
             'kind': 'plot',
             'scales': [],
             'layers': []
             }
         """.trimIndent()

        checkFontinfo(spec, FAMILY)
        checkFontinfo(spec, FAMILY_MONO, monospaced = true)
    }

    @Test
    fun `set default width factor`() {
        val spec = """
            {'mapping': {},
             'data_meta': {},
             'kind': 'plot',
             'scales': [],
             'layers': [],
             'metainfo_list': [
                {'name': 'font_metrics_adjustment', 'width_correction': 2.0},
                {'name': 'font_metrics_adjustment', 'width_correction': 3.0}
                ]}
             }
         """.trimIndent()

        // the last setting wins
        checkFontinfo(spec, FAMILY, widthFactor = 3.0)
        checkFontinfo(spec, FAMILY_MONO, widthFactor = 3.0, monospaced = true)
    }

    @Test
    fun `update width factor`() {
        val spec = """
            {'mapping': {},
             'data_meta': {},
             'kind': 'plot',
             'scales': [],
             'layers': [],
             'metainfo_list': [
               {'name': 'font_family_info', 'family': '$FAMILY', 'width_correction': 1.5},
               {'name': 'font_family_info', 'family': '$FAMILY_MONO', 'width_correction': 2.0},
               {'name': 'font_family_info', 'family': '$FAMILY', 'width_correction': 2.5}
             ]}
             }
         """.trimIndent()

        checkFontinfo(spec, FAMILY, widthFactor = 2.5)  // the last setting wins.
        checkFontinfo(spec, FAMILY_MONO, widthFactor = 2.0, monospaced = true)
    }

    @Test
    fun `update monospaced flag`() {
        val spec = """
            {'mapping': {},
             'data_meta': {},
             'kind': 'plot',
             'scales': [],
             'layers': [],
             'metainfo_list': [
               {'name': 'font_family_info', 'family': '$FAMILY', 'monospaced': true},
               {'name': 'font_family_info', 'family': '$FAMILY_MONO', 'monospaced': false}
             ]}
             }
         """.trimIndent()

        checkFontinfo(spec, FAMILY, monospaced = true)
        checkFontinfo(spec, FAMILY_MONO, monospaced = false)
    }


    private fun checkFontinfo(spec: String, family: String, widthFactor: Double = 1.0, monospaced: Boolean = false) {
        val opts = parsePlotSpec(spec)
        val cfg = FontFamilyRegistryConfig(OptionsAccessor.over(opts))
        val fontRegistry = cfg.createFontFamilyRegistry()

        val fontFamily = fontRegistry.get(family)
        assertEquals(widthFactor, fontFamily.widthFactor)
        assertEquals(monospaced, fontFamily.monospaced)
    }

    private companion object {
        const val FAMILY = "Tested font family"
        const val FAMILY_MONO = "Courier" // Pre-registered
    }
}
