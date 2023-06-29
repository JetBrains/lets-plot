/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.composite

interface HasParent<ParentT : HasParent<ParentT>> {
    val parent: ParentT?
}