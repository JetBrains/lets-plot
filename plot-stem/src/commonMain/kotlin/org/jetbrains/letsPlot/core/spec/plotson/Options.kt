/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.plot.base.Aes
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class Options(
    val properties: MutableMap<String, Any?> = mutableMapOf(),
    private val toSpecDelegate: (Options) -> Any? = Options::properties
) {
    val prop: PropertyDelegate = PropertyDelegate()

    // for short-form specs, e.g., "tooltip": "none" or "sampling": "random"
    fun toSpec(): Any? = toSpecDelegate(this)

    class PropSpec<T>(val key: String)
    inner class PropertyDelegate() {
        @Suppress("UNCHECKED_CAST")
        operator fun <T> get(propSpec: PropSpec<T>): T? = properties[propSpec.key] as T?
        operator fun <T> set(propSpec: PropSpec<T>, value: T?)  = run { properties[propSpec.key] = value }
    }
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
inline fun <reified TValue> map(prop: Options.PropSpec<*>): ReadWriteProperty<Options, TValue?> = map(prop.key)
