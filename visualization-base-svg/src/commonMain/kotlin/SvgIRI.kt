package jetbrains.datalore.visualization.base.svg

class SvgIRI(private val myElementId: String) {

    override fun toString(): String {
        return "url(#$myElementId)"
    }
}