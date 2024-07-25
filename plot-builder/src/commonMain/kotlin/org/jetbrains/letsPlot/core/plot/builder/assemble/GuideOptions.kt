/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.assemble

abstract class GuideOptions(
    val title: String?
) {
    abstract fun withTitle(title: String?): GuideOptions

    companion object {
        val NONE: GuideOptions = object : GuideOptions(null) {
            override fun withTitle(title: String?): GuideOptions = this
        }
    }
}
