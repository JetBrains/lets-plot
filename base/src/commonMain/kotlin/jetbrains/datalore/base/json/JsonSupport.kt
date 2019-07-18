package jetbrains.datalore.base.json

expect object JsonSupport {
    fun parseJson(jsonString: String): MutableMap<String, Any?>
    fun formatJson(o: Any): String
}
