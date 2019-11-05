/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.interact

import jetbrains.datalore.plot.base.interact.ContextualMapping
import jetbrains.datalore.plot.base.interact.MappedDataAccess

interface ContextualMappingProvider {
    fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping

    companion object {
        val NONE = object : jetbrains.datalore.plot.builder.interact.ContextualMappingProvider {
            override fun createContextualMapping(dataAccess: MappedDataAccess): ContextualMapping {
                return ContextualMapping(
                    emptyList(),
                    emptyList(),
                    dataAccess
                )
            }
        }
    }
}
