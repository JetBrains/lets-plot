/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite

/**
 * Composite which supports optimized navigation between siblings.
 *
 * If we store children in an array and a composite has a lot of children, navigation might be
 * expensive because we need to use indexOf which takes O(children). This class is an optimized
 * version of Composite tailored for such cases.
 *
 * Invariants:
 * - prevSibling, nextSibling, firstChild and lastChild must be consistent with the children collection from
 * Composite.
 */
interface NavComposite<CompositeT : NavComposite<CompositeT>> : Composite<CompositeT> {
    fun nextSibling(): CompositeT?
    fun prevSibling(): CompositeT?

    fun firstChild(): CompositeT?
    fun lastChild(): CompositeT?
}