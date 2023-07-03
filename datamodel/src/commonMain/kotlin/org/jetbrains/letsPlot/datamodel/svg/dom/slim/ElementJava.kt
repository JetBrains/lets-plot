/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.datamodel.svg.dom.slim

internal open class ElementJava(elementName: String) :
        SlimBase(elementName),
    SvgSlimNode {

    private val myAttributes = arrayOfNulls<Any>(ATTR_COUNT)

    override val attributes: Iterable<SvgSlimNode.Attr>
        get() {
            return myAttributes
                    .mapIndexed { i, value ->
                        val key = ATTR_KEYS[i]
//                        val value = getAttribute(i)
                        if (value == null) {
                            null
                        } else {
                            object : SvgSlimNode.Attr {
                                override val key: String
                                    get() = key

                                override val value: String
                                    get() = value.toString()
                            }
                        }
                    }
                    .filterNotNull()


/*
            val l = ArrayList<SvgSlimNode.Attr>()
            for (i in 0 until ATTR_COUNT) {
                if (hasAttribute(i)) {
                    val key = ATTR_KEYS[i]
                    val value = getAttribute(i)
                    l.add(object : SvgSlimNode.Attr {
                        override val key: String
                            get() = key

                        override val value: String
                            get() = value.toString()
                    })
                }
            }
            return l
*/
        }

    override val slimChildren: Iterable<SvgSlimNode>
        get() = emptyList()

    override fun setAttribute(index: Int, v: Any) {
        myAttributes[index] = v
    }

    override fun hasAttribute(index: Int): Boolean {
        return myAttributes[index] != null
    }

    override fun getAttribute(index: Int): Any? {
        return myAttributes[index]
    }

    override fun appendTo(g: SvgSlimGroup) {
        (g as GroupJava).addChild(this)
    }
}
