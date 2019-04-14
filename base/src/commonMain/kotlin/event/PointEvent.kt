package jetbrains.datalore.base.event

import jetbrains.datalore.base.geometry.Vector

open class PointEvent(val x: Int, val y: Int) : Event() {

    val location: Vector
        get() = Vector(x, y)

    override fun toString(): String {
        return "{x=$x,y=$y}"
    }
}
