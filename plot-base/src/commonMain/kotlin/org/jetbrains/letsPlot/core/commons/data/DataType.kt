/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.data

import org.jetbrains.letsPlot.commons.formatting.string.StringFormat

enum class DataType(
    val formatter: (Any) -> String = Any::toString
){
    UNKNOWN(StringFormat.forOneArg("{}")::format),
    INTEGER(StringFormat.forOneArg("d")::format),
    FLOATING(StringFormat.forOneArg("g")::format),
    STRING(StringFormat.forOneArg("{}")::format),
    INSTANT(StringFormat.forOneArg("%Y-%m-%dT%H:%M:%S")::format),
    BOOLEAN(StringFormat.forOneArg("{}")::format),
}
