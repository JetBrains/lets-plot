/*
 * Copyright (c) 2024. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.plotson

import org.jetbrains.letsPlot.core.plot.base.Aes
import org.jetbrains.letsPlot.core.spec.Option
import org.jetbrains.letsPlot.core.spec.Option.Mapping.toOption

class Mapping(
    val groupingVar: String?,
    val aesthetics: Map<Aes<*>, String>
) {
    constructor(groupingVar: String?, vararg aesthetics: Pair<Aes<*>, String>) : this(groupingVar, aesthetics.toMap())
    constructor(vararg aesthetics: Pair<Aes<*>, String>) : this(null, aesthetics.toMap())
    constructor(aesthetics: Map<Aes<*>, String>) : this(null, aesthetics)
    constructor(groupingVar: String?) : this(groupingVar, emptyMap())

    fun toSpec(): Map<String, Any> {
        val aesMapping = aesthetics.mapKeys { (aes, _) -> toOption(aes) }
        val groupMapping = groupingVar?.let { mapOf(Option.Mapping.GROUP to it) }.orEmpty()
        return aesMapping + groupMapping
    }

    operator fun plus(other: Pair<Aes<*>, String>): Mapping {
        return Mapping(groupingVar, aesthetics + other)
    }

    operator fun plus(other: Map<Aes<*>, String>?): Mapping {
        return Mapping(groupingVar, aesthetics + (other ?: emptyMap()))
    }

    operator fun minus(aes: Aes<*>): Mapping {
        return Mapping(groupingVar, aesthetics - aes)
    }

    operator fun plus(other: Mapping): Mapping {
        return Mapping(other.groupingVar, aesthetics + other.aesthetics)
    }

    companion object {
        val EMPTY = Mapping()
    }
}
