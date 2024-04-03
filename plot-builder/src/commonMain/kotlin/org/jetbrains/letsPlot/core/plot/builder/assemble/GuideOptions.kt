/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

abstract class GuideOptions(
    val isReverse: Boolean
) {
    abstract fun withReverse(reverse: Boolean): GuideOptions

    //    // In Kotlin Native objects a frozen by default. Annotate with `ThreadLocal` to unfreeze.
//    // @link https://github.com/JetBrains/kotlin-native/blob/master/IMMUTABILITY.md
//    // Required mutations:
//    //      -   `isReverse` in the 'outer' class
//    @ThreadLocal
    companion object {
        val NONE: GuideOptions = object : GuideOptions(false) {
            override fun withReverse(reverse: Boolean): GuideOptions = this // Do nothing
        }
    }
}
