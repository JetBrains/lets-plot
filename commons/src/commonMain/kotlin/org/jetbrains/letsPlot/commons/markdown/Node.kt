/*
 * Copyright (c) 2025. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.commons.markdown

abstract class Node {
    data class Group(val children: List<Node>) : Node(){
        constructor(vararg children: Node) : this(children.toList())
    }
    data class Text(var text: String) : Node()
    data object Strong : Node()
    data object CloseStrong : Node()
    data object Emph : Node()
    data object CloseEmph : Node()
}
