package jetbrains.datalore.visualization.base.svg

import jetbrains.datalore.base.observable.collections.list.ObservableArrayList
import jetbrains.datalore.base.observable.collections.list.ObservableList
import jetbrains.datalore.base.observable.property.Property
import jetbrains.datalore.base.observable.property.ValueProperty

class SvgTextNode(text: String) : SvgNode() {

    private val myContent = ValueProperty()

    init {
        myContent.set(text)
    }

    fun textContent(): Property<String> {
        return myContent
    }

    fun children(): ObservableList<SvgNode> {
        return NO_CHILDREN_LIST
    }

    fun toString(): String {
        return textContent().get()
    }

    companion object {
        private val NO_CHILDREN_LIST = object : ObservableArrayList<SvgNode>() {
            protected fun checkAdd(index: Int, item: SvgNode) {
                throw UnsupportedOperationException("Cannot add children to SvgTextNode")
            }

            protected fun checkRemove(index: Int, item: SvgNode) {
                throw UnsupportedOperationException("Cannot remove children from SvgTextNode")
            }
        }
    }
}