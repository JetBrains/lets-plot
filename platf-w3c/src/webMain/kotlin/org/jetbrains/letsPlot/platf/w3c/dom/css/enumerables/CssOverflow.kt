/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.platf.w3c.dom.css.enumerables

enum class CssOverflow private constructor(override val stringQualifier: String) : CssBaseValue {
    VISIBLE("visible"),
    HIDDEN("hidden"),
    SCROLL("scroll"),
    AUTO("auto"),
    INITIAL("initial"),
    INHERIT("inherit");

    companion object {
        fun parse(value: String): CssOverflow? {
            return parse(value, values())
        }
    }
}
