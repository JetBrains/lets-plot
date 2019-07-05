package jetbrains.gis.tileprotocol

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

interface Response {
    class ConfigureConnectionResponse(val mapStyle: String) : Response

    class GetBinaryTileResponse(private val myBinaryTile: ByteArrayOutputStream) : Response {

        val binaryTile: ByteBuffer
            get() = ByteBuffer.wrap(myBinaryTile.toByteArray())
    }

    class CancelBinaryTileResponse : Response
}
