package jetbrains.datalore.base.values

fun toPngDataUri(base64EncodedPngImage: String): String {
    return "data:image/png;base64,$base64EncodedPngImage"
}