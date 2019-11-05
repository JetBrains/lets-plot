/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.css.enumerables

import jetbrains.datalore.base.js.css.CssUnitQualifier

interface CssBaseValue : CssUnitQualifier

internal fun <TypeT : CssBaseValue> parse(str: String, values: Array<TypeT>): TypeT? {
    for (value in values) {
        if (value.stringQualifier.equals(str, ignoreCase = true)) {
            return value
        }
    }
    return null
}
