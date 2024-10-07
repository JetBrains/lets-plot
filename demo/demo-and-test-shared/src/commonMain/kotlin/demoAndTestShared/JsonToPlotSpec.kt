/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demoAndTestShared

import org.jetbrains.letsPlot.commons.intern.json.JsonSupport

@Suppress("UNCHECKED_CAST")
fun parsePlotSpec(spec: String): MutableMap<String, Any> {
    // Demo specs mostly use single quotes for strings, but JSON requires double quotes.
    // Replacing single quotes with double quotes unconditionally does not work for valid JSON strings like "don't".
    // So we search for the first quote and decide based on it do we need to replace single quotes with double quotes.
    return when (spec.first { it == '\'' || it == '"' }) {
        '\'' -> JsonSupport.parseJson(spec.replace("'", "\""))
        else -> JsonSupport.parseJson(spec)
    } as MutableMap<String, Any>
}
