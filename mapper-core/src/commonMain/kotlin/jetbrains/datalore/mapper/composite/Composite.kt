/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite

/**
 * Generic composite structure. Examples of such structure:
 * - component tree in UI framework
 * - XML parse tree
 * - AST
 */
interface Composite<CompositeT : Composite<CompositeT>> : HasParent<CompositeT> {
    fun children(): MutableList<CompositeT>
}