package jetbrains.datalore.visualization.plot.gog.plot

class ColorBarOptions : GuideOptions() {
    var width: Double? = null
    var height: Double? = null
    private var myBinCount: Int? = null

    var binCount: Int
        get() = myBinCount!!
        set(binCount) {
            myBinCount = binCount
        }

    fun hasWidth(): Boolean {
        return width != null
    }

    fun hasHeight(): Boolean {
        return height != null
    }

    fun hasBinCount(): Boolean {
        return myBinCount != null
    }
}
