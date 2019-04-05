package demo

actual object PlatformObject {
    actual val name: String = "JVM"
}

actual class PlatformClass {
    actual fun getName(): String {
        return "(JVM)"
    }
}