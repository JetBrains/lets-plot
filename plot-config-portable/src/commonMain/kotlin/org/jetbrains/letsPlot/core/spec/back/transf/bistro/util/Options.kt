/*
 * Copyright (c) 2021. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.back.transf.bistro.util

import org.jetbrains.letsPlot.core.plot.base.Aes
import jetbrains.datalore.plot.config.Option
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Options(
    val properties: MutableMap<String, Any?> = mutableMapOf()
) {
    inline operator fun <reified T> get(aes: Aes<T>): T = properties[Option.Mapping.toOption(aes)] as T
    operator fun <T> set(aes: Aes<T>, v: T) { properties[Option.Mapping.toOption(aes)] = v }
}

inline fun <T: Options, reified TValue> map(key: String): ReadWriteProperty<T, TValue?> {
    return object : ReadWriteProperty<T, TValue?> {
        override fun getValue(thisRef: T, property: KProperty<*>): TValue? = thisRef.properties[key] as TValue?
        override fun setValue(thisRef: T, property: KProperty<*>, value: TValue?) = run { thisRef.properties[key] = value }
    }
}

// Option.Mapping.toOption(key) doesn't work in JS, fixed by inlining the function.
// Browser console:
//    JsConsole.java:52 (JavaScript) TypeError: Cannot read properties of undefined (reading 'toOption_896ixz$')TypeError: Cannot read properties of undefined (reading 'toOption_896ixz$')
//      at new LayerOptions
//      at CorrPlotOptionsBuilder.newCorrPlotLayerOptions_0
//      at CorrPlotOptionsBuilder.build
//      at CorrPlotSpecChange.buildCorrPlotSpec_0
//      at CorrPlotSpecChange.apply_il3x6g$
//      at PlotSpecTransform.applyChangesToSpec_0
//      at PlotSpecTransform.apply_i49brq$
//      at BackendSpecTransformUtil.processTransformIntern2_0
//      at BackendSpecTransformUtil.processTransformIntern_0
//      at BackendSpecTransformUtil.processTransform_2wxo1b$
inline fun <reified TValue> map(aes: Aes<*>): ReadWriteProperty<Options, TValue?> = map(aes.name.lowercase())
