/*
 * Copyright (c) 2026. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package org.jetbrains.letsPlot.pythonExtension.interop

import Python.PyList_New
import Python.PyList_SetItem
import Python.PyObject
import Python.PyUnicode_FromString
import Python.Py_BuildValue
import kotlinx.cinterop.CPointer
import org.jetbrains.letsPlot.core.commons.data.DataType
import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.plot.builder.scale.PaletteGenerator
import org.jetbrains.letsPlot.core.spec.config.ScaleConfig
import org.jetbrains.letsPlot.core.spec.conversion.AesOptionConversion
import org.jetbrains.letsPlot.core.spec.conversion.ColorOptionConverter

object ColorScalePaletteGenerator {

    @Suppress("unused") // This function is used in lets_plot_kotlin_bridge.c
    fun generatePalette(
        scaleSpecDict: CPointer<PyObject>?,
        n: Int
    ): CPointer<PyObject>? {
        return try {
            @Suppress("UNCHECKED_CAST")
            val scaleOptions = TypeUtils.pyDictToMap(scaleSpecDict) as Map<String, Any>
            val scaleConfig = ScaleConfig(
                aes = Aes.COLOR,
                options = scaleOptions,
                aopConversion = AesOptionConversion(colorConverter = ColorOptionConverter(null, null, null)),
                dataType = DataType.UNKNOWN,
                tz = null
            )

            val scaleMapperProvider = scaleConfig.createMapperProvider()
            val paletteGenerator: PaletteGenerator = scaleMapperProvider as? PaletteGenerator
                ?: throw IllegalArgumentException("Can't create PaletteGenerator from options: $scaleOptions.")

            val palette = paletteGenerator.generatePalette(n)

            // Convert List<String> to a Python list
            val pyList = PyList_New(palette.size.toLong())
            palette.forEachIndexed { index, colorHex ->
                val pyString = PyUnicode_FromString(colorHex)
                PyList_SetItem(pyList, index.toLong(), pyString)
            }
            pyList
        } catch (e: Throwable) {
            Py_BuildValue("s", "generatePalette() - Exception: ${e.message}")
        }
    }
}
