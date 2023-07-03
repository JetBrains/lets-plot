/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections.set

import jetbrains.datalore.base.observable.collections.ObservableCollection

interface ObservableSet<T> : MutableSet<T>,
    ObservableCollection<T>