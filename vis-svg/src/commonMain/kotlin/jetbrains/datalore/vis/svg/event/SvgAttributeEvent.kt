/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svg.event

import jetbrains.datalore.base.event.Event
import jetbrains.datalore.vis.svg.SvgAttributeSpec

class SvgAttributeEvent<ValueT>(
    val attrSpec: SvgAttributeSpec<ValueT>,
    val oldValue: ValueT?,
    val newValue: ValueT?
) : Event()