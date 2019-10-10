package jetbrains.datalore.visualization.svgDemoModel.b

import jetbrains.datalore.vis.svg.SvgCssResource

class CssRes : SvgCssResource {
    override fun css(): String {
        return ".ellipse-yellow { \n" +
                "fill: yellow;\n" +
                "}"
    }
}