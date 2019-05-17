package jetbrains.datalore.base.domCore.dom

class DomList<TypeT : DomNode> internal constructor(private val myNode: TypeT) : AbstractMutableList<TypeT>() {

    override fun get(index: Int): TypeT {
        return myNode.getChild(index)!!.cast()
    }

    override operator fun set(index: Int, element: TypeT): TypeT {
        if (element.parentElement != null) {
            throw IllegalStateException()
        }

        val child = get(index)
        myNode.replaceChild(child, element)
        return child
    }

    override fun add(index: Int, element: TypeT) {
        if (element.parentElement != null) {
            throw IllegalStateException()
        }

        if (index == 0) {
            myNode.insertFirst(element)
        } else {
            val prev = get(index - 1)
            myNode.insertAfter(element, prev)
        }
    }

    override fun removeAt(index: Int): TypeT {
        val child = get(index)
        myNode.removeChild(child)
        return child
    }

    override val size: Int
        get() = myNode.getChildCount()
}
