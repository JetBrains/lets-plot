/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

import kotlin.native.concurrent.ThreadLocal

abstract class GuideOptions {

    var isReverse: Boolean = false

    // In Kotlin Native objects a frozen by default. Annotate with `ThreadLocal` to unfreeze.
    // @link https://github.com/JetBrains/kotlin-native/blob/master/IMMUTABILITY.md
    // Required mutations:
    //      -   `isReverse` in the 'outer' class
    @ThreadLocal
    companion object {
        val NONE: GuideOptions = object : GuideOptions() {}
    }
}
