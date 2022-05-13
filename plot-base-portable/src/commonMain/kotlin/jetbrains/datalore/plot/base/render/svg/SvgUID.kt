/*
 * Copyright (c) 2020. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.render.svg

import jetbrains.datalore.base.random.RandomString.randomString
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
        require(prefix.isNotBlank()) { "ID prefix should not be blank"}
        require(prefix[0] !in '0'..'9') { "ID cannot start with a digit"}
        return "$prefix${suffixGen()}"
    }

    private class IncrementalId {
        private var nextIndex = 0
        fun next() = ("clip-${nextIndex++}")
    }
}