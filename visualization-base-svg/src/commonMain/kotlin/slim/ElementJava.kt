package jetbrains.datalore.visualization.base.svg.slim

import java.util.ArrayList
import java.util.Collections

internal class ElementJava protected constructor(elementName: String) : SlimBase(elementName), SvgSlimNode {
    private val myAttributes = arrayOfNulls<Any>(ATTR_COUNT)

    val attributes: Iterable<Attr>
        get() {
            val l = ArrayList<Attr>()
            for (i in 0 until ATTR_COUNT) {
                if (hasAttribute(i)) {
                    val key = ATTR_KEYS[i]
                    val value = getAttribute(i)
                    l.add(object : Attr() {
                        val key: String
                            get() = key

                        val value: String
                            get() = value.toString()
                    })
                }
            }
            return l
        }

    val children: Iterable<SvgSlimNode>
        get() = emptyList<SvgSlimNode>()

    protected fun setAttribute(index: Int, v: Any) {
        myAttributes[index] = v
    }

    protected fun hasAttribute(index: Int): Boolean {
        return myAttributes[index] != null
    }

    protected fun getAttribute(index: Int): Any {
        return myAttributes[index]
    }

    fun appendTo(g: SvgSlimGroup) {
        (g as GroupJava).addChild(this)
    }
}
