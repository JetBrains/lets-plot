/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import org.jetbrains.letsPlot.core.plot.base.stat.regression.EvalResult
import org.jetbrains.letsPlot.core.plot.base.stat.regression.LinearRegression
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class LinearRegressionTest {

    private val epsilon = 1e-12
    private val confidenceLevel = 0.95

    // @Test
    fun simple() {
        val numPoints = 20
        val dataVec = listOf(

            RegressionTestUtil.data(numPoints, yRange = 0.000_000_001.rangeTo(0.000_001)),
            RegressionTestUtil.data(numPoints, yRange = 0.000_001.rangeTo(0.001)),
            RegressionTestUtil.data(numPoints, yRange = 0.001.rangeTo(1.0)),
            RegressionTestUtil.data(numPoints, yRange = 1.0.rangeTo(1000.0)),
            RegressionTestUtil.data(numPoints, yRange = 1000.0.rangeTo(1000_000.0))
        )

        for (data in dataVec) {
            val xs = data.first
            val ys = data.second

            val simpleRegression = LinearRegression.fit(xs, ys, confidenceLevel)
            assertNotNull(simpleRegression, "Regression should be computable")

            RegressionTestUtil.logRegression(xs, ys, simpleRegression)
        }
    }

    @Test
    fun test() {

        run {
            val inX = listOf(
                0.0,
                1.0,
                2.0,
                3.0,
                4.0,
                5.0,
                6.0,
                7.0,
                8.0,
                9.0,
                10.0,
                11.0,
                12.0,
                13.0,
                14.0,
                15.0,
                16.0,
                17.0,
                18.0,
                19.0
            )
            val inY = listOf(
                0.13972724528697236,
                0.8772473579051785,
                0.873936314504721,
                0.9611620535197403,
                0.07350102496231047,
                0.309550491862621,
                0.8031170712554961,
                0.7272439730837795,
                0.11326843063265735,
                0.46217396211862355,
                0.8710152682353185,
                0.14773472716407535,
                0.19126325758077833,
                0.593076150747026,
                0.467443202483841,
                0.06468187700571001,
                0.9218609008382694,
                0.8420157670929777,
                0.5758689492802809,
                0.5929816359280029
            )
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 0.5291238314870899,
                    ymin = 0.22851045676733928,
                    ymax = 0.8297372062068404,
                    se = 0.14308640157121702
                ),
                EvalResult(
                    y = 0.5293877618045557,
                    ymin = 0.2713285434727036,
                    ymax = 0.7874469801364079,
                    se = 0.1228314108705549
                ),
                EvalResult(
                    y = 0.5296516921220215,
                    ymin = 0.31032591838351153,
                    ymax = 0.7489774658605314,
                    se = 0.10439500825711083
                ),
                EvalResult(
                    y = 0.5299156224394873,
                    ymin = 0.3431106288224919,
                    ymax = 0.7167206160564827,
                    se = 0.08891571892670648
                ),
                EvalResult(
                    y = 0.5301795527569532,
                    ymin = 0.365949555470235,
                    ymax = 0.6944095500436713,
                    se = 0.07817043856985559
                ),
                EvalResult(
                    y = 0.530443483074419,
                    ymin = 0.3744628620612771,
                    ymax = 0.6864241040875609,
                    se = 0.07424388817171237
                ),
                EvalResult(
                    y = 0.5307074133918848,
                    ymin = 0.3664774161051666,
                    ymax = 0.6949374106786029,
                    se = 0.07817043856985559
                ),
                EvalResult(
                    y = 0.5309713437093506,
                    ymin = 0.3441663500923552,
                    ymax = 0.717776337326346,
                    se = 0.08891571892670648
                ),
                EvalResult(
                    y = 0.5312352740268165,
                    ymin = 0.31190950028830655,
                    ymax = 0.7505610477653264,
                    se = 0.10439500825711083
                ),
                EvalResult(
                    y = 0.5314992043442822,
                    ymin = 0.27343998601243,
                    ymax = 0.7895584226761344,
                    se = 0.12283141087055492
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

        run {
            val inX = listOf(
                0.0,
                1.0,
                2.0,
                3.0,
                4.0,
                5.0,
                6.0,
                7.0,
                8.0,
                9.0,
                10.0,
                11.0,
                12.0,
                13.0,
                14.0,
                15.0,
                16.0,
                17.0,
                18.0,
                19.0
            )
            val inY = listOf(
                1.405875171822729E-4,
                8.773701104246436E-4,
                8.740623780642788E-4,
                9.612008914274215E-4,
                7.442752301177569E-5,
                3.102409406809994E-4,
                8.033139539875547E-4,
                7.275167288382125E-4,
                1.1415516131617985E-4,
                4.627117876192168E-4,
                8.711442528382276E-4,
                1.4858699158549826E-4,
                1.9207199351526958E-4,
                5.934830741897622E-4,
                4.67975758749333E-4,
                6.56171941943215E-5,
                9.219390398593703E-4,
                8.421737511680586E-4,
                5.762930799072937E-4,
                5.933886538854637E-4
            )
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 5.295947071851976E-4,
                    ymin = 2.29281945539854E-4,
                    ymax = 8.299074688305413E-4,
                    se = 1.4294331531258914E-4
                ),
                EvalResult(
                    y = 5.298583735726097E-4,
                    ymin = 2.720572142012882E-4,
                    ymax = 7.876595329439312E-4,
                    se = 1.2270857958239295E-4
                ),
                EvalResult(
                    y = 5.301220399600217E-4,
                    ymin = 3.110155917761438E-4,
                    ymax = 7.492284881438995E-4,
                    se = 1.0429061335314435E-4
                ),
                EvalResult(
                    y = 5.303857063474337E-4,
                    ymin = 3.437675175374371E-4,
                    ymax = 7.170038951574304E-4,
                    se = 8.88268032966066E-5
                ),
                EvalResult(
                    y = 5.306493727348458E-4,
                    ymin = 3.6658360528134856E-4,
                    ymax = 6.947151401883431E-4,
                    se = 7.809226820937802E-5
                ),
                EvalResult(
                    y = 5.309130391222578E-4,
                    ymin = 3.750883985743044E-4,
                    ymax = 6.867376796702112E-4,
                    se = 7.416964435771032E-5
                ),
                EvalResult(
                    y = 5.311767055096698E-4,
                    ymin = 3.671109380561726E-4,
                    ymax = 6.952424729631671E-4,
                    se = 7.809226820937802E-5
                ),
                EvalResult(
                    y = 5.314403718970818E-4,
                    ymin = 3.4482218308708515E-4,
                    ymax = 7.180585607070785E-4,
                    se = 8.88268032966066E-5
                ),
                EvalResult(
                    y = 5.317040382844939E-4,
                    ymin = 3.12597590100616E-4,
                    ymax = 7.508104864683717E-4,
                    se = 1.0429061335314435E-4
                ),
                EvalResult(
                    y = 5.31967704671906E-4,
                    ymin = 2.7416654530058437E-4,
                    ymax = 7.897688640432275E-4,
                    se = 1.2270857958239298E-4
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

        run {
            val inX = listOf(
                0.0,
                1.0,
                2.0,
                3.0,
                4.0,
                5.0,
                6.0,
                7.0,
                8.0,
                9.0,
                10.0,
                11.0,
                12.0,
                13.0,
                14.0,
                15.0,
                16.0,
                17.0,
                18.0,
                19.0
            )
            val inY = listOf(
                0.1405875171822729,
                0.8773701104246435,
                0.8740623780642786,
                0.9612008914274214,
                0.07442752301177569,
                0.31024094068099933,
                0.8033139539875546,
                0.7275167288382125,
                0.11415516131617985,
                0.46271178761921666,
                0.8711442528382275,
                0.14858699158549826,
                0.19207199351526955,
                0.5934830741897621,
                0.4679757587493329,
                0.0656171941943215,
                0.9219390398593702,
                0.8421737511680585,
                0.5762930799072936,
                0.5933886538854636
            )
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 0.5295947071851975,
                    ymin = 0.22928194553985393,
                    ymax = 0.8299074688305411,
                    se = 0.1429433153125891
                ),
                EvalResult(
                    y = 0.5298583735726096,
                    ymin = 0.27205721420128814,
                    ymax = 0.787659532943931,
                    se = 0.1227085795823929
                ),
                EvalResult(
                    y = 0.5301220399600216,
                    ymin = 0.3110155917761438,
                    ymax = 0.7492284881438994,
                    se = 0.10429061335314432
                ),
                EvalResult(
                    y = 0.5303857063474335,
                    ymin = 0.343767517537437,
                    ymax = 0.71700389515743,
                    se = 0.08882680329660655
                ),
                EvalResult(
                    y = 0.5306493727348456,
                    ymin = 0.36658360528134837,
                    ymax = 0.6947151401883428,
                    se = 0.078092268209378
                ),
                EvalResult(
                    y = 0.5309130391222576,
                    ymin = 0.37508839857430426,
                    ymax = 0.6867376796702109,
                    se = 0.0741696443577103
                ),
                EvalResult(
                    y = 0.5311767055096697,
                    ymin = 0.36711093805617245,
                    ymax = 0.6952424729631669,
                    se = 0.078092268209378
                ),
                EvalResult(
                    y = 0.5314403718970817,
                    ymin = 0.3448221830870852,
                    ymax = 0.7180585607070782,
                    se = 0.08882680329660655
                ),
                EvalResult(
                    y = 0.5317040382844938,
                    ymin = 0.31259759010061594,
                    ymax = 0.7508104864683716,
                    se = 0.10429061335314432
                ),
                EvalResult(
                    y = 0.5319677046719057,
                    ymin = 0.2741665453005842,
                    ymax = 0.7897688640432272,
                    se = 0.12270857958239294
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

        run {
            val inX = listOf(
                0.0,
                1.0,
                2.0,
                3.0,
                4.0,
                5.0,
                6.0,
                7.0,
                8.0,
                9.0,
                10.0,
                11.0,
                12.0,
                13.0,
                14.0,
                15.0,
                16.0,
                17.0,
                18.0,
                19.0
            )
            val inY = listOf(
                140.5875171822729,
                877.3701104246435,
                874.0623780642786,
                961.2008914274214,
                74.42752301177568,
                310.2409406809993,
                803.3139539875546,
                727.5167288382124,
                114.15516131617984,
                462.7117876192167,
                871.1442528382274,
                148.58699158549825,
                192.07199351526955,
                593.4830741897621,
                467.9757587493329,
                65.6171941943215,
                921.9390398593703,
                842.1737511680585,
                576.2930799072936,
                593.3886538854636
            )
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 529.5947071851975,
                    ymin = 229.28194553985395,
                    ymax = 829.9074688305411,
                    se = 142.9433153125891
                ),
                EvalResult(
                    y = 529.8583735726096,
                    ymin = 272.05721420128816,
                    ymax = 787.659532943931,
                    se = 122.7085795823929
                ),
                EvalResult(
                    y = 530.1220399600215,
                    ymin = 311.0155917761437,
                    ymax = 749.2284881438993,
                    se = 104.29061335314432
                ),
                EvalResult(
                    y = 530.3857063474336,
                    ymin = 343.767517537437,
                    ymax = 717.0038951574302,
                    se = 88.82680329660656
                ),
                EvalResult(
                    y = 530.6493727348457,
                    ymin = 366.58360528134847,
                    ymax = 694.7151401883428,
                    se = 78.09226820937799
                ),
                EvalResult(
                    y = 530.9130391222577,
                    ymin = 375.0883985743044,
                    ymax = 686.737679670211,
                    se = 74.1696443577103
                ),
                EvalResult(
                    y = 531.1767055096697,
                    ymin = 367.1109380561725,
                    ymax = 695.2424729631668,
                    se = 78.09226820937799
                ),
                EvalResult(
                    y = 531.4403718970817,
                    ymin = 344.82218308708514,
                    ymax = 718.0585607070783,
                    se = 88.82680329660656
                ),
                EvalResult(
                    y = 531.7040382844938,
                    ymin = 312.59759010061595,
                    ymax = 750.8104864683717,
                    se = 104.29061335314432
                ),
                EvalResult(
                    y = 531.9677046719057,
                    ymin = 274.16654530058423,
                    ymax = 789.7688640432273,
                    se = 122.70857958239293
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

        run {
            val inX = listOf(
                0.0,
                1.0,
                2.0,
                3.0,
                4.0,
                5.0,
                6.0,
                7.0,
                8.0,
                9.0,
                10.0,
                11.0,
                12.0,
                13.0,
                14.0,
                15.0,
                16.0,
                17.0,
                18.0,
                19.0
            )
            val inY = listOf(
                140587.5171822729,
                877370.1104246435,
                874062.3780642786,
                961200.8914274215,
                74427.52301177567,
                310240.9406809993,
                803313.9539875545,
                727516.7288382124,
                114155.16131617985,
                462711.7876192167,
                871144.2528382274,
                148586.99158549824,
                192071.99351526954,
                593483.0741897621,
                467975.7587493329,
                65617.19419432149,
                921939.0398593702,
                842173.7511680585,
                576293.0799072937,
                593388.6538854636
            )
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 529594.7071851975,
                    ymin = 229281.94553985394,
                    ymax = 829907.468830541,
                    se = 142943.3153125891
                ),
                EvalResult(
                    y = 529858.3735726095,
                    ymin = 272057.21420128806,
                    ymax = 787659.532943931,
                    se = 122708.57958239291
                ),
                EvalResult(
                    y = 530122.0399600216,
                    ymin = 311015.59177614376,
                    ymax = 749228.4881438995,
                    se = 104290.61335314433
                ),
                EvalResult(
                    y = 530385.7063474336,
                    ymin = 343767.51753743703,
                    ymax = 717003.8951574302,
                    se = 88826.80329660657
                ),
                EvalResult(
                    y = 530649.3727348456,
                    ymin = 366583.60528134834,
                    ymax = 694715.1401883429,
                    se = 78092.268209378
                ),
                EvalResult(
                    y = 530913.0391222576,
                    ymin = 375088.39857430424,
                    ymax = 686737.6796702109,
                    se = 74169.6443577103
                ),
                EvalResult(
                    y = 531176.7055096697,
                    ymin = 367110.93805617245,
                    ymax = 695242.472963167,
                    se = 78092.268209378
                ),
                EvalResult(
                    y = 531440.3718970817,
                    ymin = 344822.18308708514,
                    ymax = 718058.5607070783,
                    se = 88826.80329660657
                ),
                EvalResult(
                    y = 531704.0382844937,
                    ymin = 312597.59010061587,
                    ymax = 750810.4864683716,
                    se = 104290.61335314433
                ),
                EvalResult(
                    y = 531967.7046719057,
                    ymin = 274166.54530058417,
                    ymax = 789768.8640432273,
                    se = 122708.57958239295
                )
            )

            assertRegression(inX, inY, actX, actY)
        }
    }

    private fun assertRegression(
        inX: List<Double>,
        inY: List<Double>,
        expectedX: List<Double>,
        expectedResult: List<EvalResult>
    ) {

        val simpleRegression = LinearRegression.fit(inX, inY, confidenceLevel)
        assertNotNull(simpleRegression, "Regression should be computable")

        expectedX.zip(expectedResult).forEach { (x, r) ->
            val evalResult = simpleRegression.evalX(x)
            assertEquals(r.y, evalResult.y, epsilon)
            assertEquals(r.ymin, evalResult.ymin, epsilon)
            assertEquals(r.ymax, evalResult.ymax, epsilon)
            assertEquals(r.se, evalResult.se, epsilon)
        }
    }


    private val xss: MutableList<Double?> = mutableListOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0)
    private val yss: MutableList<Double?> = mutableListOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9)

    @Test(expected = IllegalArgumentException::class)
    fun testSizeDifference() {
        // Exception "X/Y must have same size" is expected
        LinearRegression.fit(xss.dropLast(1), yss, confidenceLevel)
    }


    @Test
    fun testXNull() {
        val xs = xss.apply { set(lastIndex, null) }
        LinearRegression.fit(xs, yss, confidenceLevel)
    }

    @Test
    fun testYNull() {
        val ys = yss.apply { set(lastIndex, null) }
        LinearRegression.fit(xss, ys, confidenceLevel)
    }

    @Test
    fun testXYNullSame() {
        val xs = xss.apply { set(3, null) }
        val ys = yss.apply { set(3, null) }
        LinearRegression.fit(xs, ys, confidenceLevel)
    }

    @Test
    fun testXYNullDiff() {
        val xs = xss.apply { set(lastIndex, null); set(0, null) }
        val ys = yss.apply { set(3, null) }
        LinearRegression.fit(xs, ys, confidenceLevel)
    }

    @Test
    fun testNaN() {
        val xs = xss.apply { set(0, Double.NaN) }
        LinearRegression.fit(xs, yss, confidenceLevel)
    }

    @Test
    fun testInfinite() {
        val xs = xss.apply { set(0, Double.POSITIVE_INFINITY) }
        LinearRegression.fit(xs, yss, confidenceLevel)
    }
}

