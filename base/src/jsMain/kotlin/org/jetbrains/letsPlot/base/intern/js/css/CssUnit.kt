/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.base.intern.js.css

enum class CssUnit private constructor(val stringRepresentation: String) {

    EM("em"),
    NUMBER(""),
    ENUMERABLE(""),
    PX("px"),
    PERCENT("%"),
    VW("vw"),
    VH("vh")
}
