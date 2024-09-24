/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package org.jetbrains.letsPlot.core.plot.builder.sampling.method

import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.letsPlot.commons.geometry.DoubleVector
import org.jetbrains.letsPlot.core.plot.base.DataFrame
import org.jetbrains.letsPlot.core.plot.base.DataFrame.Builder
import org.jetbrains.letsPlot.core.plot.base.data.DataFrameUtil
import org.jetbrains.letsPlot.core.plot.base.data.TransformVar
import org.jetbrains.letsPlot.core.plot.builder.data.RingAssertion.Companion.assertThatRing
import org.jetbrains.letsPlot.core.plot.builder.data.createCircle
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.SamplingUtil.readPolygon
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.VertexSampling.VertexDpSampling
import org.jetbrains.letsPlot.core.plot.builder.sampling.method.VertexSampling.VertexVwSampling
import org.jetbrains.letsPlot.core.spec.back.transform.bistro.util.groupBy
import kotlin.test.Test

class VertexSamplingTest {

    @Test
    fun geomContourVW() {
        val simplifiedDf = simplifyGeomContour { VertexVwSampling(20, polygon = false) }
        simplifiedDf[0]!!.let { s4 ->
            assertThat(s4[TransformVar.X])
                .containsExactly( 34.2013544322706, 28.0, 24.0, 22.0, 15.640052185581094, 12.0, 7.0, 2.9725351471899972,
                    1.0663038684744108, 1.354174226706248, 3.413752240002105, 3.387410761596062, 0.0)

            assertThat(s4[TransformVar.Y])
                .containsExactly( 0.0, 5.35601322364421, 7.680637364327313, 7.541894855901996, 4.0, 2.581848855954589,
                    2.962608018722804, 6.0, 10.0, 14.0, 20.0, 24.0, 29.17981615139983)
        }

        simplifiedDf[1]!!.let { s12 ->
            assertThat(s12[TransformVar.X])
                .containsExactly( 12.100353824603486, 13.826284778642556, 11.0, 7.63269197304265, 6.7581521368676025,
                    9.0, 12.100353824603486)

            assertThat(s12[TransformVar.Y])
                .containsExactly(48.10035382460349, 44.0, 41.13953783788376, 42.0, 46.0, 48.706426181491096,
                    48.100353824603486)
        }
    }

    @Test
    fun geomContourDP() {
        val simplifiedDf = simplifyGeomContour { VertexDpSampling(20, polygon = false) }
        simplifiedDf[0]!!.let { s4 ->
            assertThat(s4[TransformVar.X])
                .containsExactly(34.2013544322706, 27.0, 24.0, 22.0, 11.0, 7.0, 4.0, 1.7019594701656282,
                    0.9624549017808901, 3.614936463064613, 0.0)

            assertThat(s4[TransformVar.Y])
                .containsExactly(0.0, 6.118160308376604, 7.680637364327313, 7.541894855901996, 2.4107877663066075,
                    2.962608018722804, 4.914625246350896, 8.0, 11.0, 23.0, 29.17981615139983)
        }

        simplifiedDf[1]!!.let { s12 ->
            assertThat(s12[TransformVar.X])
                .containsExactly(12.100353824603486, 13.614307360148764, 13.447443221652929, 11.0, 8.0,
                    6.594281870126916, 7.210078388947848, 10.0, 12.100353824603486)

            assertThat(s12[TransformVar.Y])
                .containsExactly(48.10035382460349, 46.0, 43.0, 41.13953783788376, 41.68980538731867, 44.0, 47.0,
                    48.94101938615626, 48.10035382460349)
        }
    }

    @Test
    fun issue1168() {
        val df = Builder()
            .put(TransformVar.X, listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0))
            .put(TransformVar.Y, listOf(0.0, 0.0, 0.0, 0.0, null, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0))
            .build()

        singleGroupPolygonDpSampling(5, df)
        singleGroupPolygonVwSampling(5, df)
        singleGroupPathDpSampling(5, df)
        singleGroupPathVwSampling(5, df)
    }

    @Test
    fun shouldNotFailWithAllNulls() {
        val df = Builder()
            .put(TransformVar.X, listOf(null, null, null, null, null, null, null))
            .put(TransformVar.Y, listOf(null, null, null, null, null, null, null))
            .build()

        singleGroupPolygonDpSampling(5, df)
        singleGroupPolygonVwSampling(5, df)
        singleGroupPathDpSampling(5, df)
        singleGroupPathVwSampling(5, df)
    }


    @Test
    fun minimumPointsCount() {
        val df = toDF(
            listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(0.0, 100.0),
                DoubleVector(100.0, 100.0),
                DoubleVector(100.0, 0.0),
                DoubleVector(0.0, 0.0)
            )
        )

        val p = readPolygon(singleGroupPolygonDpSampling(4, df))

        assertThat(p[0]).hasSize(4)
    }

    @Test
    fun dpSimplification() {
        val polygon = ArrayList(createCircle(16, 100.0))
        val df = toDF(polygon)
        val simplifiedRings = readPolygon(singleGroupPolygonDpSampling(8, df))
        assertThat(simplifiedRings).hasSize(1)
        assertThatRing(simplifiedRings[0]).hasSize(8)
    }

    @Test
    fun vwSimplification() {
        val polygon = ArrayList(createCircle(16, 100.0))
        val df = toDF(polygon)
        val simplifiedRings = readPolygon(singleGroupPolygonVwSampling(8, df))
        assertThat(simplifiedRings).hasSize(1)
        assertThatRing(simplifiedRings[0]).hasSize(8)
    }

    @Test
    fun filteredOutRingInBetween_ShouldNotBreakRings() {
        val polygon = ArrayList<DoubleVector>()
        polygon.addAll(createCircle(30, 200.0))
        polygon.addAll(
            listOf(
                DoubleVector(0.0, 0.0),
                DoubleVector(0.0, 1.0),
                DoubleVector(1.0, 1.0),
                DoubleVector(0.0, 0.0)
            )
        )
        polygon.addAll(createCircle(30, 150.0))

        val simplifiedRings = readPolygon(singleGroupPolygonDpSampling(30, toDF(polygon)))

        assertThat(simplifiedRings).hasSize(3)
        assertThatRing(simplifiedRings[0]).isClosed.hasSize(15)
        assertThatRing(simplifiedRings[1]).isClosed.hasSize(2)
        assertThatRing(simplifiedRings[2]).isClosed.hasSize(13)
    }

    private fun simplifyGeomContour(simpProvide: () -> GroupSamplingBase): Map<Any, DataFrame> {
        // See sampling_methods.ipynb, Vertex Sampling section, geom_contour() example
        val g4 = listOf(
            DoubleVector(34.2013544322706, 0.0),
            DoubleVector(34.092393206755254, 0.09239320675525314),
            DoubleVector(34.0, 0.17073763262000177),
            DoubleVector(33.55497244814715, 0.554972448147147),
            DoubleVector(33.03953367501975, 1.0),
            DoubleVector(33.01854765750716, 1.0185476575071628),
            DoubleVector(33.0, 1.034940267434067),
            DoubleVector(32.71848007442847, 1.281519925571528),
            DoubleVector(32.48554486224751, 1.4855448622475107),
            DoubleVector(32.0, 1.9108273210302444),
            DoubleVector(31.95165498543486, 1.9516549854348595),
            DoubleVector(31.89440849662911, 2.0),
            DoubleVector(31.42008376950146, 2.42008376950146),
            DoubleVector(31.0, 2.792129255021896),
            DoubleVector(30.88636607638047, 2.886366076380467),
            DoubleVector(30.749342475509664, 3.0),
            DoubleVector(30.353408140141983, 3.353408140141985),
            DoubleVector(30.0, 3.6688577042790067),
            DoubleVector(29.816934563310234, 3.816934563310233),
            DoubleVector(29.590613216884673, 4.0),
            DoubleVector(29.27915106152505, 4.2791510615250505),
            DoubleVector(29.0, 4.529342983110476),
            DoubleVector(28.735766343134276, 4.7357663431342765),
            DoubleVector(28.397532213951497, 5.0),
            DoubleVector(28.187814454086343, 5.187814454086342),
            DoubleVector(28.0, 5.35601322364421),
            DoubleVector(27.63048819499193, 5.630488194991932),
            DoubleVector(27.13303309600827, 6.0),
            DoubleVector(27.062578202190956, 6.0625782021909576),
            DoubleVector(27.0, 6.118160308376604),
            DoubleVector(26.62241501656674, 6.377584983433259),
            DoubleVector(26.477292972069304, 6.477292972069304),
            DoubleVector(26.0, 6.8052233178686325),
            DoubleVector(25.86790896434034, 6.86790896434034),
            DoubleVector(25.589567068169313, 7.0),
            DoubleVector(25.22117055615618, 7.221170556156182),
            DoubleVector(25.0, 7.353952526981933),
            DoubleVector(24.51303621264658, 7.513036212646578),
            DoubleVector(24.474313743973273, 7.525686256026729),
            DoubleVector(24.0, 7.680637364327313),
            DoubleVector(23.698104193580615, 7.6981041935806145),
            DoubleVector(23.27756458561262, 7.7224354143873795),
            DoubleVector(23.0, 7.738494508686034),
            DoubleVector(22.674501645138488, 7.6745016451384895),
            DoubleVector(22.382839108328472, 7.617160891671528),
            DoubleVector(22.0, 7.541894855901996),
            DoubleVector(21.620434439125436, 7.379565560874565),
            DoubleVector(21.199576851125585, 7.1995768511255855),
            DoubleVector(21.0, 7.114223560715455),
            DoubleVector(20.929802403252904, 7.070197596747098),
            DoubleVector(20.817875209731145, 7.0),
            DoubleVector(20.271078671892564, 6.728921328107437),
            DoubleVector(20.0, 6.594531950578628),
            DoubleVector(19.633663904989117, 6.366336095010883),
            DoubleVector(19.04556323955884, 6.0),
            DoubleVector(19.015606017579213, 5.984393982420786),
            DoubleVector(19.0, 5.9762641302987065),
            DoubleVector(18.940959306012566, 5.940959306012565),
            DoubleVector(18.389061475069358, 5.610938524930641),
            DoubleVector(18.0, 5.378289718045792),
            DoubleVector(17.769752062755074, 5.230247937244924),
            DoubleVector(17.411649692473187, 5.0),
            DoubleVector(17.147919473726997, 4.852080526273004),
            DoubleVector(17.0, 4.7691163107511985),
            DoubleVector(16.510361412864576, 4.489638587135423),
            DoubleVector(16.462080800853638, 4.462080800853637),
            DoubleVector(16.0, 4.198332612409883),
            DoubleVector(15.87212628826978, 4.1278737117302216),
            DoubleVector(15.640052185581094, 4.0),
            DoubleVector(15.21257571393765, 3.78742428606235),
            DoubleVector(15.0, 3.6817145285583313),
            DoubleVector(14.530908921682133, 3.469091078317867),
            DoubleVector(14.417841245186613, 3.417841245186613),
            DoubleVector(14.0, 3.2284476469904226),
            DoubleVector(13.83501418038962, 3.1649858196103793),
            DoubleVector(13.406089867991566, 3.0),
            DoubleVector(13.106917115788551, 2.8930828842114478),
            DoubleVector(13.0, 2.854873289372459),
            DoubleVector(12.8003692045527, 2.8003692045527),
            DoubleVector(12.328470635023669, 2.6715293649763323),
            DoubleVector(12.0, 2.581848855954589),
            DoubleVector(11.503143891383589, 2.4968561086164103),
            DoubleVector(11.495558552236572, 2.495558552236572),
            DoubleVector(11.0, 2.4107877663066075),
            DoubleVector(10.61066384530218, 2.389336154697819),
            DoubleVector(10.376430378289687, 2.3764303782896863),
            DoubleVector(10.0, 2.355689848093902),
            DoubleVector(9.617226216708458, 2.3827737832915417),
            DoubleVector(9.39826670736529, 2.3982667073652895),
            DoubleVector(9.0, 2.4264468758211883),
            DoubleVector(8.522852311306476, 2.522852311306475),
            DoubleVector(8.465575098550024, 2.5344249014499765),
            DoubleVector(8.0, 2.6284921497891993),
            DoubleVector(7.721532545364477, 2.721532545364477),
            DoubleVector(7.056153885537584, 2.943846114462416),
            DoubleVector(7.0, 2.962608018722804),
            DoubleVector(6.974429164156894, 2.974429164156894),
            DoubleVector(6.919115772628065, 3.0),
            DoubleVector(6.305378234545083, 3.3053782345450835),
            DoubleVector(6.0, 3.457325704509456),
            DoubleVector(5.666343667006094, 3.6663436670060943),
            DoubleVector(5.133726531056681, 4.0),
            DoubleVector(5.054791052649455, 4.054791052649455),
            DoubleVector(5.0, 4.092822866873066),
            DoubleVector(4.5020441605818124, 4.5020441605818124),
            DoubleVector(4.479101535693527, 4.520898464306473),
            DoubleVector(4.0, 4.914625246350896),
            DoubleVector(3.955760630193241, 4.955760630193241),
            DoubleVector(3.908183054439967, 5.0),
            DoubleVector(3.468583483537795, 5.468583483537795),
            DoubleVector(3.0, 5.968061862449311),
            DoubleVector(2.985233466505152, 5.985233466505152),
            DoubleVector(2.9725351471899972, 6.0),
            DoubleVector(2.902201928186317, 6.097798071813683),
            DoubleVector(2.5657011357245567, 6.565701135724557),
            DoubleVector(2.2533673998168857, 7.0),
            DoubleVector(2.161537174769534, 7.161537174769534),
            DoubleVector(2.0, 7.44569480172806),
            DoubleVector(1.8061755816504665, 7.806175581650466),
            DoubleVector(1.7019594701656282, 8.0),
            DoubleVector(1.5107489517477077, 8.489251048252292),
            DoubleVector(1.5047080150165242, 8.504708015016524),
            DoubleVector(1.3111365601259566, 9.0),
            DoubleVector(1.2499424719583518, 9.249942471958352),
            DoubleVector(1.0878002367705997, 9.9121997632294),
            DoubleVector(1.0663038684744108, 10.0),
            DoubleVector(1.0600097892810076, 10.060009789281008),
            DoubleVector(1.0, 10.632162553632234),
            DoubleVector(0.965932196052944, 10.965932196052943),
            DoubleVector(0.9624549017808901, 11.0),
            DoubleVector(0.9633140521095207, 11.03668594789048),
            DoubleVector(0.9855352076254058, 11.985535207625405),
            DoubleVector(0.9858739594319367, 12.0),
            DoubleVector(0.987540516228274, 12.012459483771726),
            DoubleVector(1.0, 12.105608865898448),
            DoubleVector(1.1080760519680521, 12.891923948031948),
            DoubleVector(1.122930699010188, 13.0),
            DoubleVector(1.159908506060142, 13.159908506060143),
            DoubleVector(1.2876557063970842, 13.712344293602916),
            DoubleVector(1.354174226706248, 14.0),
            DoubleVector(1.5040250457225903, 14.49597495427741),
            DoubleVector(1.5075102484961755, 14.507510248496176),
            DoubleVector(1.6563080698045565, 15.0),
            DoubleVector(1.7449610899776349, 15.255038910022366),
            DoubleVector(2.0, 15.988740316905156),
            DoubleVector(2.0030259954446152, 15.996974004555385),
            DoubleVector(2.004138091120309, 16.0),
            DoubleVector(2.0067507008587664, 16.006750700858767),
            DoubleVector(2.28200973644088, 16.71799026355912),
            DoubleVector(2.3911512017760788, 17.0),
            DoubleVector(2.557922433482419, 17.442077566517582),
            DoubleVector(2.6280973212627137, 17.628097321262715),
            DoubleVector(2.7683954782049027, 18.0),
            DoubleVector(2.826974034524312, 18.173025965475688),
            DoubleVector(3.0, 18.68410009583753),
            DoubleVector(3.0852054151908606, 18.91479458480914),
            DoubleVector(3.1166754464439275, 19.0),
            DoubleVector(3.1659860499335837, 19.165986049933583),
            DoubleVector(3.318988237286312, 19.681011762713688),
            DoubleVector(3.413752240002105, 20.0),
            DoubleVector(3.510364105946718, 20.489635894053283),
            DoubleVector(3.5154594463979296, 20.51545944639793),
            DoubleVector(3.611065931906604, 21.0),
            DoubleVector(3.637725003258075, 21.362274996741924),
            DoubleVector(3.659604898763433, 21.659604898763433),
            DoubleVector(3.6846538736393364, 22.0),
            DoubleVector(3.6610211456763033, 22.338978854323695),
            DoubleVector(3.6400324673331212, 22.64003246733312),
            DoubleVector(3.614936463064613, 23.0),
            DoubleVector(3.5015192898101177, 23.49848071018988),
            DoubleVector(3.500956079639663, 23.500956079639664),
            DoubleVector(3.387410761596062, 24.0),
            DoubleVector(3.2778638952171057, 24.277863895217106),
            DoubleVector(3.0, 24.982661273885476),
            DoubleVector(2.9953974049598, 24.9953974049598),
            DoubleVector(2.993734114830289, 25.0),
            DoubleVector(2.987028250936629, 25.01297174906337),
            DoubleVector(2.6550829921006, 25.6550829921006),
            DoubleVector(2.4767750015372347, 26.0),
            DoubleVector(2.285531359744957, 26.285531359744958),
            DoubleVector(2.0, 26.711836551560726),
            DoubleVector(1.8882451860831913, 26.888245186083193),
            DoubleVector(1.8174485469765613, 27.0),
            DoubleVector(1.4617526918112689, 27.46175269181127),
            DoubleVector(1.2052032901093495, 27.79479670989065),
            DoubleVector(1.0471317486704304, 28.0),
            DoubleVector(1.024877409994753, 28.024877409994755),
            DoubleVector(1.0, 28.052687067116732),
            DoubleVector(0.5591209357810855, 28.559120935781085),
            DoubleVector(0.175311002651652, 29.0),
            DoubleVector(0.08876750041562692, 29.088767500415628),
            DoubleVector(0.0, 29.17981615139983)
        )

        val g12 = listOf(
            DoubleVector(12.100353824603486, 48.10035382460349),
            DoubleVector(12.211216809495339, 48.0),
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
            DoubleVector(12.100353824603486, 48.10035382460349),
        )

        val df = toDF(g4, g12)
        val groupMapper = { i: Int -> df[DataFrameUtil.findVariableOrFail(df, "g")][i] as Int }

        return simpProvide()
            .apply(df, groupMapper)
            .groupBy("g")
    }

    private fun singleGroupPathDpSampling(n: Int, df: DataFrame): DataFrame {
        return VertexDpSampling(n, polygon = false).apply(df) { _ -> 0 }
    }

    private fun singleGroupPathVwSampling(n: Int, df: DataFrame): DataFrame {
        return VertexVwSampling(n, polygon = false).apply(df) { _ -> 0 }
    }

    private fun singleGroupPolygonDpSampling(n: Int, df: DataFrame): DataFrame {
        return VertexDpSampling(n, polygon = true).apply(df) { _ -> 0 }
    }

    private fun singleGroupPolygonVwSampling(n: Int, df: DataFrame): DataFrame {
        return VertexVwSampling(n, polygon = true).apply(df) { _ -> 0 }
    }

    private fun toDF(vararg points: List<DoubleVector>): DataFrame {
        val xs = mutableListOf<Double>()
        val ys = mutableListOf<Double>()
        val gs = mutableListOf<Int>()

        points.forEachIndexed { i, p ->
            p.forEach { v ->
                xs.add(v.x)
                ys.add(v.y)
                gs.add(i)
            }
        }
        return Builder()
            .put(TransformVar.X, xs)
            .put(TransformVar.Y, ys)
            .put(DataFrame.Variable("g"), gs)
            .build()
    }

}