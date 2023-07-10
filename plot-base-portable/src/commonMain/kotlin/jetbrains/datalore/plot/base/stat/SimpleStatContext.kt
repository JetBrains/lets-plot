/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat

import org.jetbrains.letsPlot.commons.interval.DoubleSpan
import jetbrains.datalore.plot.base.DataFrame
import jetbrains.datalore.plot.base.StatContext
import jetbrains.datalore.plot.base.data.TransformVar

class SimpleStatContext(private val myDataFrame: DataFrame, private val mappedStatVariables: List<DataFrame.Variable>) :
    StatContext {

    override fun overallXRange(): DoubleSpan? {
        return myDataFrame.range(TransformVar.X)
    }

    override fun overallYRange(): DoubleSpan? {
        return myDataFrame.range(TransformVar.Y)
    }

    override fun mappedStatVariables(): List<DataFrame.Variable> {
        return mappedStatVariables
    }
}
