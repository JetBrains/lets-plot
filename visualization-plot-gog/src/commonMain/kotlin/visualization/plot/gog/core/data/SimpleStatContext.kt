package jetbrains.datalore.visualization.plot.gog.core.data

import jetbrains.datalore.base.gcommon.collect.ClosedRange

class SimpleStatContext(private val myDataFrame: DataFrame) : StatContext {

    override fun overallXRange(): ClosedRange<Double>? {
        return myDataFrame.range(TransformVar.X)
    }

    override fun overallYRange(): ClosedRange<Double>? {
        return myDataFrame.range(TransformVar.Y)
    }
}
