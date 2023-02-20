/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite

interface HasParent<ParentT : HasParent<ParentT>> {
    val parent: ParentT?
}