package jetbrains.datalore.base.domCore.dom

class DomCollectionIterator<TypeT> internal constructor(private val myDomCollection: DomCollection<TypeT>) : Iterator<TypeT> {
    private var myIndex = 0

    override fun hasNext(): Boolean {
        return myIndex < myDomCollection.length
    }

    override fun next(): TypeT {
        return myDomCollection.item(myIndex++)!!
    }
}
