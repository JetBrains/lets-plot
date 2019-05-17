package jetbrains.datalore.base.domCore.dom

internal object DomCoreUtils {

    fun <T> uncheckedCast(obj: Any): T {
        return obj as T
    }
}
