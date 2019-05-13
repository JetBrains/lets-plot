package jetbrains.datalore.visualization.plot.gog.server.config

import jetbrains.datalore.base.json.JsonSupport
import jetbrains.datalore.visualization.plot.gog.config.Option.Geom.Image
import jetbrains.datalore.visualization.plot.gog.config.Option.Geom.Image.Type
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImageLayerTransformTest {

    @Test
    fun specTranscode() {

        // 1 x 1 image
        val encoded = Base64.getEncoder().encodeToString(byteArrayOf(255.toByte()))
        val input = "{" +
                "  'layers': [" +
                "              {" +
                "                  'geom': 'image'," +
                "                  'mapping': {" +
                "                            'xmin': [-0.5]," +
                "                            'ymin': [-0.5]," +
                "                            'xmax': [0.5]," +
                "                            'ymax': [0.5]" +
                "                             }" +
                "                  ," +
                "                  'image_spec': {" +
                "                                'width': 1," +
                "                                'height': 1," +
                "                                'type': '" + Type.GRAY + "'," +
                "                                'bytes': '" + encoded + "'" +
                "                                }" +
                "              }" +
                "            ]" +
                "}"

        val inputSpec = JsonSupport.parseJson(input)
        val transformedSpec = PlotConfigServerSide.processTransform(inputSpec)

        val geomSpec = (transformedSpec["layers"] as List<*>)[0] as Map<*, *>

        // Make sure transform 'image_spec' --> 'href' takes place.

        assertFalse(geomSpec.containsKey("image_spec"))
        assertTrue(geomSpec.containsKey(Image.HREF))

        val expectedDataUrl = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAAC0lEQVR42mP4DwQACfsD/Wj6HMwAAAAASUVORK5CYII="
        assertEquals(expectedDataUrl, geomSpec[Image.HREF])
    }
}