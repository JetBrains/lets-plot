/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.mapper

import jetbrains.datalore.plot.builder.scale.GuideMapper

internal class GuideMapperAdapter<T> constructor(
    private val mapper: (Double?) -> T?,
    override val isContinuous: Boolean
) : GuideMapper<T> {

    override fun apply(value: Double?): T? {
        return mapper(value)
    }
}
