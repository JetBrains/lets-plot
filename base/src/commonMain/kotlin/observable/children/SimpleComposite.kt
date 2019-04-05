package jetbrains.datalore.base.observable.children

import jetbrains.datalore.base.observable.property.DelayedValueProperty

open class SimpleComposite<ParentT, SiblingT> {
    private val myParent = DelayedValueProperty<ParentT>()
    private var myPositionData: PositionData<out SiblingT>? = null

    val position: Position<out SiblingT>
        get() {
            if (myPositionData == null) {
                throw IllegalStateException()
            }
            return myPositionData!!.get()
        }

    fun removeFromParent() {
        if (myPositionData == null) return
        myPositionData!!.remove()
    }

    fun parent(): DelayedValueProperty<ParentT> {
        return myParent
    }

    fun setPositionData(positionData: PositionData<out SiblingT>?) {
        myPositionData = positionData
    }
}