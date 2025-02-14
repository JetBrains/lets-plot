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
    data class Strong(val text: String = "") : Node()
    data class CloseStrong(val text: String = "") : Node()
    data class Emph(val text: String = "") : Node()
    data class CloseEmph(val text: String = "") : Node()
    data class BoldItalic(val text: String) : Node()

}
