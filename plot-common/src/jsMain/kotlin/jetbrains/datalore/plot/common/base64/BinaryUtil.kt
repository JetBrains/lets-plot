package jetbrains.datalore.plot.common.base64

import org.khronos.webgl.*
import kotlin.browser.window

actual object BinaryUtil {
    actual fun encodeList(l: List<Double?>): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    actual fun decodeList(s: String): List<Double> {
//        byte[] bytes = BaseEncoding.base64().decode(s);
//        byte[] bytes = Base64Coder.decodeBytes(s);

        val binStr = b64decode(s)
        val length = binStr.length
//        final int length = bytes.length;

        val doubles = ArrayList<Double>(length / 8)

        val buffer = ArrayBuffer(8)
        val bytesView = createBufferByteView(buffer)
        val doublesView = createBufferDoubleView(buffer)

        for (i in 0 until length / 8) {
            val pos = i * 8
            decodeDouble(
                /*
                bytes[pos + 7],
                bytes[pos + 6],
                bytes[pos + 5],
                bytes[pos + 4],
                bytes[pos + 3],
                bytes[pos + 2],
                bytes[pos + 1],
                bytes[pos],
                */
                binStr[pos + 7].toByte(),
                binStr[pos + 6].toByte(),
                binStr[pos + 5].toByte(),
                binStr[pos + 4].toByte(),
                binStr[pos + 3].toByte(),
                binStr[pos + 2].toByte(),
                binStr[pos + 1].toByte(),
                binStr[pos].toByte(),
                bytesView
            )
            doubles.add(doublesView.get(0))
        }
        return doubles
    }

    private fun b64decode(a: String): String {
        return window.atob(a)
    }

    private fun createBufferByteView(buf: ArrayBuffer): Uint8Array {
        return Uint8Array(buf)
    }

    private fun createBufferDoubleView(buf: ArrayBuffer): Float64Array {
        return Float64Array(buf)
    }

    private fun decodeDouble(
            b0: Byte,
            b1: Byte,
            b2: Byte,
            b3: Byte,
            b4: Byte,
            b5: Byte,
            b6: Byte,
            b7: Byte,
            byteView: Uint8Array
    ) {
        byteView[0] = b0
        byteView[1] = b1
        byteView[2] = b2
        byteView[3] = b3
        byteView[4] = b4
        byteView[5] = b5
        byteView[6] = b6
        byteView[7] = b7
    }
}
