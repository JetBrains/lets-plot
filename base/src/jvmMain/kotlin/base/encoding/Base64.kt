package jetbrains.datalore.base.encoding

// ToDo: just use java.util Base64.getDecoder().decode
actual object Base64 {
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
    private val toInt = IntArray(128)

    init {
        for (i in ALPHABET.indices) {
            toInt[ALPHABET[i].toInt()] = i
        }
    }

    actual fun decode(s: String): ByteArray {

        class ByteParser(val output: MutableList<Byte>) {
            var c0: Int? = null
            var c1: Int? = null
            var c2: Int? = null
            var c3: Int? = null
            val mask = 0xFF

            fun read(v: Int) {
                if (c0 == null) {
                    c0 = toInt[v]
                    return
                }

                if (c1 == null) {
                    c1 = toInt[v]
                    output.add((c0!! shl 2 or (c1!! shr 4) and mask).toByte())
                    return
                }

                if (c2 == null) {
                    c2 = toInt[v]
                    output.add((c1!! shl 4 or (c2!! shr 2) and mask).toByte())
                    return
                }

                if (c3 == null) {
                    c3 = toInt[v]
                    output.add((c2!! shl 6 or c3!! and mask).toByte())

                    c0 = null
                    c1 = null
                    c2 = null
                    c3 = null
                }
            }
        }


        val res = ArrayList<Byte>()
        val byteParser = ByteParser(res)
        s.asSequence()
            .filterNot { ALPHABET.contains(it) }
            .forEach { byteParser.read(it.toInt()) }

        return ByteArray(res.size, res::get)
    }
}