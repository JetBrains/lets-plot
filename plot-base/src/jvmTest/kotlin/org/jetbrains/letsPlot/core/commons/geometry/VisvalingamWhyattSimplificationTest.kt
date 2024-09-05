/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.commons.geometry

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.commons.geometry.TestUtil.COMPLEX_DATA
import org.jetbrains.letsPlot.core.commons.geometry.TestUtil.MEDIUM_DATA
import org.jetbrains.letsPlot.core.commons.geometry.TestUtil.SIMPLE_DATA
import kotlin.test.Test

class VisvalingamWhyattSimplificationTest {

    @Test
    fun circle() {
        val points = listOf(
            DoubleVector(12.100353824603486, 48.10035382460349),
            DoubleVector(12.590518063163529, 47.59051806316353),
            DoubleVector(13.0, 47.148453991469125),
            DoubleVector(13.061519928601555, 47.061519928601555),
            DoubleVector(13.105055241346635, 47.0),
            DoubleVector(13.21407171660163, 46.78592828339837),
            DoubleVector(13.407027661247442, 46.40702766124744),
            DoubleVector(13.614307360148764, 46.0),
            DoubleVector(13.69286437562028, 45.69286437562028),
            DoubleVector(13.825430146423361, 45.17456985357664),
            DoubleVector(13.870080410344963, 45.0),
            DoubleVector(13.864129893187641, 44.864129893187645),
            DoubleVector(13.833573530985065, 44.16642646901494),
            DoubleVector(13.826284778642556, 44.0),
            DoubleVector(13.720336697806824, 43.72033669780682),
            DoubleVector(13.599260135766825, 43.400739864233174),
            DoubleVector(13.447443221652929, 43.0),
            DoubleVector(13.25752193720465, 42.74247806279535),
            DoubleVector(13.0, 42.39329367130239),
            DoubleVector(12.806820522940217, 42.193179477059786),
            DoubleVector(12.620335448763782, 42.0),
            DoubleVector(12.253293465503084, 41.746706534496916),
            DoubleVector(12.0, 41.57191023709081),
            DoubleVector(11.60072517635259, 41.39927482364741),
            DoubleVector(11.245826379282528, 41.245826379282526),
            DoubleVector(11.0, 41.13953783788376),
            DoubleVector(10.877519417841004, 41.12248058215899),
            DoubleVector(10.000317006620635, 41.000317006620634),
            DoubleVector(10.0, 41.000272858700214),
            DoubleVector(9.999667713607382, 41.00033228639262),
            DoubleVector(9.151943373792019, 41.15194337379202),
            DoubleVector(9.0, 41.17911765562137),
            DoubleVector(8.633939986334946, 41.366060013665056),
            DoubleVector(8.456616793030845, 41.45661679303085),
            DoubleVector(8.0, 41.68980538731867),
            DoubleVector(7.831828004068675, 41.831828004068676),
            DoubleVector(7.63269197304265, 42.0),
            DoubleVector(7.353688919192399, 42.3536889191924),
            DoubleVector(7.0, 42.80205623931005),
            DoubleVector(6.928125042616329, 42.92812504261633),
            DoubleVector(6.887147343428101, 43.0),
            DoubleVector(6.840408504590006, 43.15959149540999),
            DoubleVector(6.6861868939564655, 43.68618689395647),
            DoubleVector(6.594281870126916, 44.0),
            DoubleVector(6.580064341525626, 44.41993565847437),
            DoubleVector(6.574820491661241, 44.57482049166124),
            DoubleVector(6.560425424944467, 45.0),
            DoubleVector(6.632992592818003, 45.367007407181994),
            DoubleVector(6.698546783587761, 45.69854678358776),
            DoubleVector(6.7581521368676025, 46.0),
            DoubleVector(6.826468160129299, 46.1735318398707),
            DoubleVector(7.0, 46.61432593196172),
            DoubleVector(7.135999081367333, 46.86400091863267),
            DoubleVector(7.210078388947848, 47.0),
            DoubleVector(7.550395858711483, 47.44960414128852),
            DoubleVector(7.864260136650318, 47.864260136650316),
            DoubleVector(7.96700529088495, 48.0),
            DoubleVector(7.982659263869769, 48.01734073613023),
            DoubleVector(8.0, 48.03654998925913),
            DoubleVector(8.11071600532613, 48.11071600532613),
            DoubleVector(8.576958947748766, 48.423041052251236),
            DoubleVector(9.0, 48.706426181491096),
            DoubleVector(9.237789919302642, 48.76221008069736),
            DoubleVector(9.922942134557431, 48.92294213455743),
            DoubleVector(10.0, 48.94101938615626),
            DoubleVector(10.072163058934287, 48.92783694106571),
            DoubleVector(10.795669768400879, 48.79566976840088),
            DoubleVector(11.0, 48.758343578267386),
            DoubleVector(11.48390014929072, 48.48390014929072),
            DoubleVector(11.558289933968886, 48.44171006603111),
            DoubleVector(12.0, 48.191194695633364),
            DoubleVector(12.100353824603486, 48.10035382460349)
        )

        val simplifier = PolylineSimplifier.visvalingamWhyatt(points)
        val simplified = simplifier.setCountLimit(10).indices
    }

    @Test
    fun simplificationByCountShouldNotBreakRing() {
        val indices = PolylineSimplifier.visvalingamWhyatt(SIMPLE_DATA).setCountLimit(4).indices.single()

        assertThat(indices).has(
            TestUtil.ValidRingCondition(
                SIMPLE_DATA
            )
        )
    }

    @Test
    fun simplificationByAreaShouldNotBreakRing() {
        val indices = PolylineSimplifier.visvalingamWhyatt(MEDIUM_DATA).setWeightLimit(0.001).indices.single()
        assertThat(indices).has(
            TestUtil.ValidRingCondition(
                MEDIUM_DATA
            )
        )
    }


    @Test
    fun tooManyPoints() {
        val indices = PolylineSimplifier.visvalingamWhyatt(COMPLEX_DATA).setCountLimit(13).indices.single()
        assertThat(indices)
            .has(TestUtil.ValidRingCondition(COMPLEX_DATA))
            .containsExactly(0, 17, 28, 36, 45, 53, 65, 74, 86, 93, 102, 110, 122)
    }
}