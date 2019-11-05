/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.js.dom

import org.w3c.dom.Document

typealias DomDocument = Document

val DomDocument.clientWidth: Int
    get() = body!!.clientWidth

val DomDocument.clientHeight: Int
    get() = body!!.clientHeight
