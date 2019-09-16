package jetbrains.livemap.tiles

import jetbrains.datalore.base.projectionGeometry.Typed
import jetbrains.livemap.projections.Client


class TileFeature(
    val tileGeometry: Typed.TileGeometry<Client>,
    private val myKind: Int?,
    private val mySub: Int?,
    val label: String?,
    val short: String?
) {

    fun getFieldValue(key: String): Int {
        if (SUB.equals(key, ignoreCase = true)) {
            return mySub ?: throw IllegalStateException("sub is empty")
        } else if (CLASS.equals(key, ignoreCase = true)) {
            return myKind ?: throw IllegalStateException("kind is empty")
        }

        throw IllegalArgumentException("Unknown myKey kind: $key")
    }

    companion object {
        private const val CLASS = "class"
        private const val SUB = "sub"
    }
}
