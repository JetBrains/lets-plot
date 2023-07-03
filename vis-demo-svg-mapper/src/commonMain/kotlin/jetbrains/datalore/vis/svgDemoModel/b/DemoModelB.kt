/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.vis.svgDemoModel.b

import jetbrains.datalore.base.event.MouseEvent
import jetbrains.datalore.base.values.Color
import org.jetbrains.letsPlot.datamodel.svg.dom.SvgImageElementEx.Bitmap
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventHandler
import org.jetbrains.letsPlot.datamodel.svg.event.SvgEventSpec
import org.jetbrains.letsPlot.datamodel.svg.dom.*

object DemoModelB {
    fun createModel(): SvgSvgElement {
        val svgRoot = SvgSvgElement(400.0, 200.0)

        val ellipse = SvgEllipseElement(200.0, 80.0, 170.0, 50.0)

        // style `stroke` is not working with JavaFX Scene mapper if stroke opacity
        // is later defined via element's attribute
        // because there is no separate `stroke-opacity` attribute.
        // So the color will be just reset to black by SVG -> Scene mapper.
        // ellipse.getAttribute("style").set("stroke:#006600;")
        // ellipse.addClass("ellipse-yellow")

        // This will reset stroke color to black with JavaFX Scene mapper.
        // ellipse.strokeOpacity().set(0.6)

        ellipse.fillColor().set(Color.YELLOW)
        ellipse.strokeColor().set(Color.parseHex("#006600"))
        ellipse.strokeWidth().set(2.0)
        ellipse.strokeOpacity().set(0.6)

        val text = SvgTextElement(20.0, 20.0, "Example Text")

        val pathBuilder = SvgPathDataBuilder(false)
        pathBuilder.moveTo(150.0, 175.0, true)
            .verticalLineTo(-100.0)
            .ellipticalArc(100.0, 100.0, 0.0, false, false, -100.0, 100.0)
            .closePath()

        val path = SvgPathElement(pathBuilder.build())
        path.fill().set(SvgColors.RED)
        path.stroke().set(SvgColors.create(0, 0, 255))
        path.getAttribute("stroke-width").set("5")

        val ellipse2 = SvgEllipseElement(250.0, 65.0, 40.0, 85.0)
        ellipse2.fill().set(SvgColors.GREEN)

        val rect = SvgRectElement(180.0, 50.0, 80.0, 50.0)
        rect.fillColor().set(Color(255, 0, 0, 100))

        val image = SvgImageElement(256.0, 64.0, 128.0, 64.0)
        image.href().set(SvgUtils.pngDataURI(SampleImageData.MINDUKA_PRESENT_BLUE_PACK))

        val bitmap = Bitmap(3, 3, SampleImageData.argb3x3())
        val imageEx = SvgImageElementEx(180.0, 110.0, 110.0, 80.0, bitmap)
//        imageEx.preserveAspectRatio().set("true")

        svgRoot.children().add(ellipse)
        svgRoot.children().add(rect)
        svgRoot.children().add(ellipse2)
        svgRoot.children().add(text)
        svgRoot.children().add(path)
        svgRoot.children().add(image)
        svgRoot.children().add(imageEx)

        ellipse.addEventHandler(SvgEventSpec.MOUSE_CLICKED, object :
            SvgEventHandler<MouseEvent> {
            override fun handle(node: SvgNode, e: MouseEvent) {
                addCircle(svgRoot, e.x, e.y)
            }
        })

        return svgRoot
    }

    fun createAltModel(): SvgSvgElement {
        val svgRoot = SvgSvgElement()
        svgRoot.height().set(400.0)
        svgRoot.width().set(200.0)

        val defs = SvgDefsElement()
        val clip = SvgClipPathElement()
        clip.id().set("myClip")
        defs.children().add(clip)
        val clipCircle = SvgCircleElement(100.0, 190.0, 100.0)
        clip.children().add(clipCircle)

        val rect = SvgRectElement()
        rect.x().set(10.0)
        rect.y().set(100.0)
        rect.height().set(180.0)
        rect.width().set(180.0)
        rect.clipPath().set(SvgIRI("myClip"))

        val ellipse = SvgEllipseElement()
        ellipse.cx().set(100.0)
        ellipse.cy().set(190.0)
        ellipse.rx().set(50.0)
        ellipse.ry().set(50.0)
        ellipse.fill().set(SvgColors.RED)

        svgRoot.children().add(defs)
        svgRoot.children().add(rect)
        svgRoot.children().add(ellipse)

        ellipse.addEventHandler(SvgEventSpec.MOUSE_PRESSED, object :
            SvgEventHandler<MouseEvent> {
            override fun handle(node: SvgNode, e: MouseEvent) {
                addCircle(svgRoot, e.x, e.y)
            }
        })

        return svgRoot
    }

    private fun addCircle(svgRoot: SvgSvgElement, x: Int, y: Int) {
        val circle = SvgCircleElement(x.toDouble(), y.toDouble(), 10.0)
        circle.fillColor().set(Color.BLACK)

        svgRoot.children().add(circle)
    }
}
