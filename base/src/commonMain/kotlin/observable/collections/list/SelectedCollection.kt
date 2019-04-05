package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.function.Function
import jetbrains.datalore.base.observable.collections.ObservableCollection
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.observable.property.PropertyChangeEvent
import jetbrains.datalore.base.observable.property.ReadableProperty
import jetbrains.datalore.base.registration.Registration

internal abstract class SelectedCollection<ValueT, ItemT, CollectionT : ObservableCollection<*>>
protected constructor(
        private val mySource: ReadableProperty<ValueT>,
        private val mySelector: Function<ValueT, CollectionT>) :
        ObservableArrayList<ItemT>(),
        EventHandler<PropertyChangeEvent<ValueT>> {

    private var mySourcePropertyRegistration = Registration.EMPTY
    private var mySourceListRegistration = Registration.EMPTY

    protected var isFollowing = false
        private set

    override val size: Int
        get() = if (isFollowing) {
            super.size
        } else {
            select().size
        }

    protected abstract fun follow(source: CollectionT): Registration

    protected abstract fun empty(): CollectionT

    protected fun select(): CollectionT {
        val sourceVal = mySource.get()
        if (sourceVal != null) {
            val res = mySelector.apply(sourceVal)
            if (res != null) return res
        }

        return empty()
    }

    override fun onEvent(event: PropertyChangeEvent<ValueT>) {
        if (isFollowing) {
            mySourceListRegistration.remove()
            mySourceListRegistration = follow(select())
        }
    }

    override fun onListenersAdded() {
        mySourcePropertyRegistration = mySource.addHandler(this)
        isFollowing = true
        mySourceListRegistration = follow(select())
    }

    override fun onListenersRemoved() {
        mySourcePropertyRegistration.remove()
        isFollowing = false
        mySourceListRegistration.remove()
    }
}