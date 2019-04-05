package demo

expect class PlatformClass() {
    fun getName(): String
}

expect object PlatformObject {
    val name: String
}

fun hello(): String = "Hello! This is ${PlatformObject.name}"
