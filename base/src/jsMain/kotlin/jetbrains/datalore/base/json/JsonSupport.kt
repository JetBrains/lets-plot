package jetbrains.datalore.base.json

actual object JsonSupport {
    actual fun parseJson(json: String): MutableMap<String, Any> {
//        @Suppress("NAME_SHADOWING")
//        val json = json.replace("'", "\"")
////        val kJson = JSON.parse<MutableMap<String, Any>>(json)
//        val kJson = JSON.parse<Json>(json)
//        println(kJson)
//        return HashMap()
////        return kJson
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun toJson(o: Any): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
