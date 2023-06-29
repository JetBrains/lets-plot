/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework

interface MapperFactory<SourceT, TargetT> {
    fun createMapper(source: SourceT): Mapper<out SourceT, out TargetT>
}