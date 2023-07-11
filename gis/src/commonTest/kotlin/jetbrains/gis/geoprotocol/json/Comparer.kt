/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.gis.geoprotocol.json

import org.jetbrains.letsPlot.commons.intern.json.*
import org.jetbrains.letsPlot.commons.intern.json.*


internal object Comparer {
    fun areEqual(left: Any?, right: Any?) {
        areEqual(left, right, Context())
    }

    private fun areEqual(left: Any?, right: Any?, ctx: Context) {
        if (left == null || right == null) {
            areNull(left, right, ctx)
            return
        }

        if (left::class != right::class) {
            ctx.fail("${left::class.toString()} != ${right::class.toString()}")
            return
        }

        val lhs: Any = left
        val rhs: Any = right
        tryCompareAsBoolean(lhs, rhs, ctx)
        tryCompareAsNumber(lhs, rhs, ctx)
        tryCompareAsString(lhs, rhs, ctx)
        tryCompareArray(lhs, rhs, ctx)
        tryCompareObject(lhs, rhs, ctx)
    }

    private fun areNull(left: Any?, right: Any?, ctx: Context) {
        val leftValue = left.toString().replace("\"", String())
        val rightValue = right.toString().replace("\"", String())

        if (leftValue != rightValue) {
            ctx.fail("null equality failed: $leftValue != $rightValue")
        }
    }

    private fun tryCompareAsBoolean(left: Any, right: Any, ctx: Context) {
        if (isBoolean(left) && isBoolean(right)) {
            if (getAsBoolean(left) != getAsBoolean(right)) {
                ctx.fail("${getAsBoolean(left)} != ${getAsBoolean(right)}")
            }
        }
    }

    private fun tryCompareAsNumber(left: Any, right: Any, ctx: Context) {
        if (isNumber(left) && isNumber(right)) {
            if (getAsDouble(left) != getAsDouble(right)) {
                ctx.fail("${getAsDouble(left)} != ${getAsDouble(right)}")
            }
        }
    }

    private fun tryCompareAsString(left: Any, right: Any, ctx: Context) {
        if (isString(left) && isString(right)) {
            if (!getAsString(left).equals(getAsString(right))) {
                ctx.fail("${getAsString(left)} != ${getAsString(right)}")
            }
        }
    }

    private fun tryCompareObject(left: Any, right: Any, ctx: Context) {
        if (isObject(left) && isObject(right)) {
            val leftObj = left as Obj
            val rightObj = right as Obj

            if (leftObj.keys != rightObj.keys) {
                ctx.fail("Keys are not equal.\nExpected keys: " + leftObj.keys + "\nActual keys: " + rightObj.keys)
            }

            val keys = leftObj.keys
            for (key in keys) {
                areEqual(leftObj.get(key), rightObj.get(key), ctx.push(".$key"))
            }
        }
    }

    private fun tryCompareArray(left: Any, right: Any, ctx: Context) {
        if (isArray(left) && isArray(right)) {
            val leftArray = left as Arr
            val rightArray = right as Arr
            if (leftArray.size != rightArray.size) {
                ctx.fail("size not equal: ${leftArray.size} != ${rightArray.size}")
            }

            for (i in 0 until leftArray.size) {
                areEqual(leftArray.get(i), rightArray.get(i), ctx.push("[$i]"))
            }
        }
    }

    private class Context {
        private val names: Collection<String>

        private constructor(context: Collection<String>) {
            names = context.toList()
        }

        internal constructor() {
            names = listOf("/")
        }

        internal fun push(key: String) = Context(names + key)

        internal fun fail(message: String) {
            throw IllegalStateException("\nPath: " + names.joinToString() + "\nReason: " + message)
        }
    }

    private fun isObject(v: Any) = v is Obj
    private fun isArray(v: Any) = v is Arr
}
