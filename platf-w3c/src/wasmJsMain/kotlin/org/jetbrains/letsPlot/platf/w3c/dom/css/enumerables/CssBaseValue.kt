/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables

import org.jetbrains.letsPlot.platf.w3c.dom.css.CssUnitQualifier

interface CssBaseValue : CssUnitQualifier

internal fun <TypeT : CssBaseValue> parse(str: String, values: Array<TypeT>): TypeT? {
    for (value in values) {
        if (value.stringQualifier.equals(str, ignoreCase = true)) {
            return value
        }
    }
    return null
}
