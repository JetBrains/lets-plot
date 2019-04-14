package jetbrains.datalore.visualization.base.canvas

class DeltaTime {
    private var myLastTick: Long = 0
    private var myDt: Long = 0

    fun tick(time: Long): Long {
        if (myLastTick > 0) {
            myDt = time - myLastTick
        }

        myLastTick = time
        return myDt
    }

    fun dt(): Long {
        return myDt
    }
}
