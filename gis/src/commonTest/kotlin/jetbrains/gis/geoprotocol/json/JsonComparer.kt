package jetbrains.gis.geoprotocol.json

import jetbrains.gis.common.json.*

typealias JsonObject = Map<*, *>
typealias JsonArray = List<*>
typealias JsonValue = Any?


internal object JsonComparer {
    fun areEqual(left: JsonValue, right: JsonValue) {
        areEqual(left, right, Context())
    }

    private fun areEqual(left: JsonValue, right: JsonValue, ctx: Context) {
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

    private fun areNull(left: JsonValue, right: JsonValue, ctx: Context) {
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
            val leftObj = left as JsonObject
            val rightObj = right as JsonObject

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
            val leftArray = left as JsonArray
            val rightArray = right as JsonArray
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

        internal fun push(key: String): Context {
            val nextCtx = ArrayList(names)
            nextCtx.add(key)
            return Context(nextCtx)
        }

        internal fun fail(message: String) {

            throw IllegalStateException("\nPath: " + names.joinToString() + "\nReason: " + message)
        }
    }


    private fun isObject(v: Any): Boolean {
        return v is Map<*, *>
    }

    private fun isArray(v: Any): Boolean {
        return v is Collection<*>
    }

}
