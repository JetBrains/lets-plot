package demo

actual object PlatformObject {
    actual val name: String = "JS"
}

actual class PlatformClass {
    actual fun getName(): String {
        return "(JS)"
    }
}