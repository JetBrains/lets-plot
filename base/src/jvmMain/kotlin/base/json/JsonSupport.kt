/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.base.json

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

actual object JsonSupport {
    private val gson = GsonBuilder().serializeNulls().create()
    private val type: Type = object : TypeToken<MutableMap<String, Any?>>() {}.type

    actual fun parseJson(jsonString: String) = gson.fromJson<MutableMap<String, Any?>>(jsonString, type)
    actual fun formatJson(o: Any) = gson.toJson(o)
}