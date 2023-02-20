/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.mapper.composite


internal class SimpleComposite(
    private val name: String,
    vararg children: SimpleComposite,
    block: (SimpleComposite) -> Unit = {}) :
    NavComposite<SimpleComposite> {

    override var parent: SimpleComposite? = null
        private set

    private val children: MutableList<SimpleComposite> = mutableListOf(*children)

    init {
        for (c in children) {
            c.parent = this
        }

        block(this)
    }

    override fun nextSibling(): SimpleComposite? {
        return if (parent == null) {
            null
        } else {
            val index = parent!!.children().indexOf(this)
            when {
                index < 0 -> throw IllegalStateException("SimpleComposite isn't a child of it's parent.")
                index == parent!!.children.size - 1 -> null
                else -> parent!!.children()[index + 1]
            }
        }
    }

    override fun prevSibling(): SimpleComposite? {
        return if (parent == null) {
            null
        } else {
            val index = parent!!.children().indexOf(this)
            when {
                index < 0 -> throw IllegalStateException("SimpleComposite isn't a child of it's parent.")
                index == 0 -> null
                else -> parent!!.children()[index - 1]
            }
        }
    }

    override fun firstChild(): SimpleComposite? {
        return if (children.isEmpty()) null else children[0]
    }

    override fun lastChild(): SimpleComposite? {
        return if (children.isEmpty()) null else children[children.size - 1]
    }

    override fun children(): MutableList<SimpleComposite> {
        return children
    }

    override fun toString(): String {
        return "Composite($name)"
    }
}