package jetbrains.datalore.visualization.plot.gog.plot.guide

import jetbrains.datalore.base.geometry.DoubleVector
import jetbrains.datalore.visualization.plot.gog.plot.LegendOptions
import jetbrains.datalore.visualization.plot.gog.plot.theme.LegendTheme
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min

class LegendComponentSpec(title: String, val breaks: List<LegendBreak>, theme: LegendTheme) : LegendBoxSpec(title, theme) {
    private val myKeySize: DoubleVector

    private var myOptions = LegendOptions()
    private var myLayout: LegendComponentLayout? = null

    override val contentSize: DoubleVector
        get() = layout.size

    override val layout: LegendComponentLayout
        get() {
            ensureInited()
            return myLayout!!
        }

    private fun pretty(v: DoubleVector): DoubleVector {
        val margin = 1.0
        return DoubleVector(
                floor(v.x / 2) * 2 + 1.0 + margin,
                floor(v.y / 2) * 2 + 1.0 + margin
        )
    }

    init {

        var keySize = DoubleVector(theme.keySize(), theme.keySize())
        for (br in this.breaks) {
            val minimumKeySize = br.minimumKeySize
            keySize = keySize.max(pretty(minimumKeySize))
        }
        myKeySize = keySize
    }

    private fun ensureInited() {
        if (myLayout == null) {
            // no setters after this point

            val legendDirection = legendDirection
            val breaks = breaks
            val breakCount = breaks.size
            val colCount: Int
            val rowCount: Int
            if (myOptions.isByRow) {
                if (myOptions.hasColCount()) {
                    colCount = min(myOptions.colCount, breakCount)
                } else if (myOptions.hasRowCount()) {
                    colCount = ceil(breakCount / myOptions.rowCount.toDouble()).toInt()
                } else if (legendDirection === LegendDirection.HORIZONTAL) {
                    colCount = breakCount
                } else {
                    colCount = 1
                }
                rowCount = ceil(breakCount / colCount.toDouble()).toInt()
            } else {
                // by column
                if (myOptions.hasRowCount()) {
                    rowCount = min(myOptions.rowCount, breakCount)
                } else if (myOptions.hasColCount()) {
                    rowCount = ceil(breakCount / myOptions.colCount.toDouble()).toInt()
                } else if (legendDirection !== LegendDirection.HORIZONTAL) {
                    rowCount = breakCount
                } else {
                    rowCount = 1
                }
                colCount = ceil(breakCount / rowCount.toDouble()).toInt()
            }

            if (legendDirection === LegendDirection.HORIZONTAL) {
                if (myOptions.hasRowCount() || myOptions.hasColCount() && myOptions.colCount < breakCount) {
                    myLayout = LegendComponentLayout.horizontalMultiRow(title, breaks, myKeySize)
                } else {
                    myLayout = LegendComponentLayout.horizontal(title, breaks, myKeySize)
                }
            } else {
                myLayout = LegendComponentLayout.vertical(title, breaks, myKeySize)
            }

            myLayout!!.colCount = colCount
            myLayout!!.rowCount = rowCount
            myLayout!!.isFillByRow = myOptions.isByRow
        }
    }

    fun setLegendOptions(legendOptions: LegendOptions) {
        myOptions = legendOptions
    }
}
