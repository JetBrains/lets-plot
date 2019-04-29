package jetbrains.datalore.visualization.plot.gog.config.transform

import java.util.Arrays
import java.util.Objects
import java.util.stream.Stream
import kotlin.collections.ArrayList
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.dropLastWhile
import kotlin.collections.joinToString
import kotlin.collections.toTypedArray

class SpecSelector private constructor(builder: Builder) {

    private val myKey: String

    init {
        myKey = builder.mySelectorParts!!.joinToString("|")
    }

    internal fun with(): Builder {
        return Builder(myKey.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as SpecSelector?
        return myKey == that!!.myKey
    }

    override fun hashCode(): Int {
        return Objects.hash(myKey)
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
            return from(Arrays.stream(parts))
        }

        fun from(parts: List<String>): SpecSelector {
            return from(parts.stream())
        }

        private fun from(parts: Stream<String>): SpecSelector {
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
