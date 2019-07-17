package jetbrains.datalore.base.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

import java.lang.reflect.Type

actual object JsonSupport {

    actual fun parseJson(json: String): MutableMap<String, Any?> {
        val type: Type = object : TypeToken<MutableMap<String, Any?>>() {}.type
        return Gson().fromJson(json, type)
    }

    actual fun toJson(o: Any): String {
        return GsonBuilder().serializeNulls().create().toJson(o);
    }
}