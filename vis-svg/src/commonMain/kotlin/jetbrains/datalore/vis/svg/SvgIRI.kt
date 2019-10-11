package jetbrains.datalore.vis.svg

class SvgIRI(private val myElementId: String) {

    override fun toString(): String {
        return "url(#$myElementId)"
    }
}