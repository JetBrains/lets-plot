package jetbrains.gis.tileprotocol.json

enum class RequestTypes private constructor(private val myValue: String) {
    CONFIGURE_CONNECTION("configureConnection"),
    GET_BINARY_TILE("getBinaryTile"),
    CANCEL_BINARY_TILE("cancelBinaryTile");

    override fun toString(): String {
        return myValue
    }
}
