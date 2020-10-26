/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections.set

import jetbrains.datalore.base.observable.collections.CollectionItemEvent
import jetbrains.datalore.base.observable.collections.CollectionListener
import jetbrains.datalore.base.observable.event.EventHandler
import jetbrains.datalore.base.registration.Registration

class UnmodifiableObservableSet<ElementT>(wrappedSet: ObservableSet<ElementT>) :
        UnmodifiableSet<ElementT>(wrappedSet), ObservableSet<ElementT> {

    override val wrappedSet: ObservableSet<ElementT>
        get() = super.wrappedSet as ObservableSet<ElementT>

    override fun addListener(l: CollectionListener<in ElementT>): Registration {
        return wrappedSet.addListener(l)
    }

    override fun addHandler(handler: EventHandler<CollectionItemEvent<out ElementT>>): Registration {
        return wrappedSet.addHandler(handler)
    }

}