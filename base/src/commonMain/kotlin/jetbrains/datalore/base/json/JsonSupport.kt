package jetbrains.datalore.base.json

expect object JsonSupport {
    fun parseJson(json: String): MutableMap<String, Any?>
    fun toJson(o: Any): String
}
