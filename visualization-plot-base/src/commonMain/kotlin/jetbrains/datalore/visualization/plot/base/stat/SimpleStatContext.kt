package jetbrains.datalore.visualization.plot.base.stat

import jetbrains.datalore.base.gcommon.collect.ClosedRange
import jetbrains.datalore.visualization.plot.base.DataFrame
import jetbrains.datalore.visualization.plot.base.StatContext
import jetbrains.datalore.visualization.plot.base.data.TransformVar

class SimpleStatContext(private val myDataFrame: DataFrame) : StatContext {

    override fun overallXRange(): ClosedRange<Double>? {
        return myDataFrame.range(TransformVar.X)
    }

    override fun overallYRange(): ClosedRange<Double>? {
        return myDataFrame.range(TransformVar.Y)
    }
}
