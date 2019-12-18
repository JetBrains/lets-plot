/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.json

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
