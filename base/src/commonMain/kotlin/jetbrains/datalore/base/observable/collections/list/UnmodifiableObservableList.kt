/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.CollectionListener
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration

class UnmodifiableObservableList<ElementT>(wrappedList: ObservableList<ElementT>) :
        UnmodifiableList<ElementT>(wrappedList), ObservableList<ElementT> {

    override val wrappedList: ObservableList<ElementT>
        get() = super.wrappedList as ObservableList<ElementT>


    override fun addListener(l: CollectionListener<in ElementT>): Registration {
        return wrappedList.addListener(l)
    }

    override fun addHandler(handler: EventHandler<in CollectionItemEvent<out ElementT>>): Registration {
        return wrappedList.addHandler(handler)
    }
}