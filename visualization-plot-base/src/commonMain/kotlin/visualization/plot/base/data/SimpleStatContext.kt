package jetbrains.datalore.visualization.plot.base.data

import jetbrains.datalore.base.gcommon.collect.ClosedRange

class SimpleStatContext(private val myDataFrame: DataFrame) : StatContext {

    override fun overallXRange(): ClosedRange<Double>? {
        return myDataFrame.range(TransformVar.X)
    }

    override fun overallYRange(): ClosedRange<Double>? {
        return myDataFrame.range(TransformVar.Y)
    }
}
