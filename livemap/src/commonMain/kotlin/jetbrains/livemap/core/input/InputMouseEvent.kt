package jetbrains.livemap.core.input

import jetbrains.datalore.base.geometry.Vector

class InputMouseEvent(val location: Vector?) {
    var isStopped = false
        private set

    fun stopPropagation() {
        isStopped = true
    }
}