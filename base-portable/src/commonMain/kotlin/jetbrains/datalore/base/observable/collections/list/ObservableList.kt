/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.observable.collections.list

import jetbrains.datalore.base.observable.collections.ObservableCollection

interface ObservableList<ItemT> : MutableList<ItemT>,
    ObservableCollection<ItemT>