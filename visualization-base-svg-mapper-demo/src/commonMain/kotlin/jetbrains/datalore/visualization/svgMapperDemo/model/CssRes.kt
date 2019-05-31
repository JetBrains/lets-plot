package jetbrains.datalore.visualization.svgMapperDemo.model

import jetbrains.datalore.visualization.base.svg.SvgCssResource

class CssRes : SvgCssResource {
    override fun css(): String {
        return ".ellipse-yellow { \n" +
                "fill: yellow;\n" +
                "}"
    }
}