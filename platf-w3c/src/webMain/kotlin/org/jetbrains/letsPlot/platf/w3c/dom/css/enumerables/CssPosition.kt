/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables

enum class CssPosition constructor(override val stringQualifier: String) : CssBaseValue {
    ABSOLUTE("absolute"),
    FIXED("fixed"),
    RELATIVE("relative"),
    STATIC("static"),
    STICKY("sticky");

    companion object {
        fun parse(value: String): CssPosition? {
            return parse(value, values())
        }
    }
}
