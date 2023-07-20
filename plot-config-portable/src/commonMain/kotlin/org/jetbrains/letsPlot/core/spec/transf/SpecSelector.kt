/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.spec.transf

class SpecSelector private constructor(builder: Builder) {

    private val myKey: String

    init {
        myKey = builder.mySelectorParts!!.joinToString("|")
    }

    fun with(): Builder {
        return Builder(myKey.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        val that = other as SpecSelector?
        return myKey == that!!.myKey
    }

    override fun hashCode(): Int {
        return listOf(myKey).hashCode()
    }

    override fun toString(): String {
        return "SpecSelector{" +
                "myKey='" + myKey + '\''.toString() +
                '}'.toString()
    }

    class Builder {
        internal var mySelectorParts: MutableList<String>? = null

        internal constructor() {
            mySelectorParts = ArrayList()
            mySelectorParts!!.add("/")  // root
        }

        internal constructor(selectorParts: Array<String>) {
            mySelectorParts = ArrayList()
            for (s in selectorParts) {
                mySelectorParts!!.add(s)
            }
        }

        fun part(s: String): Builder {
            mySelectorParts!!.add(s)
            return this
        }

        fun build(): SpecSelector {
            return SpecSelector(this)
        }
    }

    companion object {
        fun root(): SpecSelector {
            return Builder().build()
        }

        fun of(vararg parts: String): SpecSelector {
            //Builder builder = new Builder();
            //for (String part : parts) {
            //  builder.part(part);
            //}
            //return builder.build();
            return from(listOf(*parts))
        }

        fun from(parts: Iterable<String>): SpecSelector {
            val builder = Builder()
            val iterator = parts.iterator()
            while (iterator.hasNext()) {
                val part = iterator.next()
                builder.part(part)
            }
            return builder.build()
        }
    }
}
