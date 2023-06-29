/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.mapping.framework.composite

/**
 * Generic composite structure. Examples of such structure:
 * - component tree in UI framework
 * - XML parse tree
 * - AST
 */
interface Composite<CompositeT : Composite<CompositeT>> : HasParent<CompositeT> {
    fun children(): MutableList<CompositeT>
}