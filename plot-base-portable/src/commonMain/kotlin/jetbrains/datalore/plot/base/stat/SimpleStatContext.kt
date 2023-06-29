/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import jetbrains.datalore.base.interval.DoubleSpan
import jetbrains.datalore.plot.base.Aes
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar

class SimpleStatContext(private val myDataFrame: DataFrame) :
    StatContext {

    override fun overallXRange(): DoubleSpan? {
        return myDataFrame.range(TransformVar.X)
    }

    override fun overallYRange(): DoubleSpan? {
        return myDataFrame.range(TransformVar.Y)
    }

    override fun getMapping(): Map<Aes<*>, DataFrame.Variable> {
        return emptyMap()
    }
}
