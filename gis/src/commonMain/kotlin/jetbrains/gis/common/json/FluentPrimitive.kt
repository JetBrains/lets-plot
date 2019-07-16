package jetbrains.gis.common.json

class FluentPrimitive : FluentValue {
    private val value: Any?

    constructor(v: Int?) {
        value = v
    }

    constructor(v: String?) {
        value = v
    }

    constructor(v: Boolean?) {
        value = v
    }

    constructor(v: Number?) {
        value = v
    }

    override fun get(): Any? {
        return value
    }
}
