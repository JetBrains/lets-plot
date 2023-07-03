/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

/**
 * Context passed to the registerSynchronizers method.
 */
interface SynchronizerContext {
    val mappingContext: MappingContext
    val mapper: Mapper<*, *>
}