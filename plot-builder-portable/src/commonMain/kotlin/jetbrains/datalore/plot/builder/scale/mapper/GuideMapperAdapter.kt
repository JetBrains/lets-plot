/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.builder.scale.mapper

import jetbrains.datalore.plot.builder.scale.GuideMapper
import kotlin.jvm.JvmOverloads

class GuideMapperAdapter<T> @JvmOverloads constructor(
    private val myF: (Double?) -> T?,
    override val isContinuous: Boolean = false
) :
    GuideMapper<T> {

    override fun apply(value: Double?): T? {
        return myF(value)
    }
}
