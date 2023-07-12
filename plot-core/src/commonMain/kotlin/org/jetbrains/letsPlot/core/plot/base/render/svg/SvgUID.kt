/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.base.render.svg

import org.jetbrains.letsPlot.commons.intern.random.RandomString.randomString
import kotlin.native.concurrent.ThreadLocal

// In Kotlin Native objects a frozen by default. Annotate with `ThreadLocal` to unfreeze.
// See:  https://github.com/JetBrains/kotlin-native/blob/master/IMMUTABILITY.md
// Required mutations:
//      -   `suffixGen`
@ThreadLocal
object SvgUID {
    private var suffixGen: () -> Any = { randomString(6) }

    fun setUpForTest() {
        val incrementalId = IncrementalId()
        suffixGen = { incrementalId.next() }
    }

    fun get(prefix: String): String {
        val letters = ('a'..'z') + ('A'..'Z')
        require(prefix.isNotEmpty()) { "ID prefix should not be empty" }
        require(prefix.first() in letters) { "ID should start with a letter" }
        require(prefix.all { it in letters + ('0'..'9') || it == '-' || it == '_' }) {
            "ID prefix can contain only the characters [a-zA-Z0-9], the hyphen (-) and the underscore (_)"
        }
        return "$prefix${suffixGen()}"
    }

    private class IncrementalId {
        private var nextIndex = 0
        fun next() = ("clip-${nextIndex++}")
    }
}