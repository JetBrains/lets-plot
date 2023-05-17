/*
 * Copyright (c) 2019. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.base.stat.regression

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class LoessRegressionTest {

    private val confidenceLevel = 0.95

    // @Test
    fun loessTest() {

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

          val loessRegression = newLocalPolynomialRegression(xs, ys, confidenceLevel)

          RegressionTestUtil.logRegression(xs, ys, loessRegression)
      }
    }

    @Test
    fun testException() {
        val n = 1_000
        val data = RegressionTestUtil.data(n, yRange = 0.0.rangeTo(1.0))
        val (xs, ys) = data
        assertFailsWith<java.lang.IllegalArgumentException> {
            val loessRegression = LocalPolynomialRegression(xs, ys, confidenceLevel, 0.001)
            loessRegression.evalX(0.5)
        }
    }

    @Test
    fun test() {
        run {
            val inX = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0)
            val inY = listOf(1.4058751718227287E-7, 8.773701104246433E-7, 8.740623780642785E-7, 9.612008914274215E-7, 7.442752301177566E-8, 3.102409406809993E-7, 8.033139539875544E-7, 7.275167288382123E-7, 1.1415516131617983E-7, 4.627117876192167E-7, 8.711442528382273E-7, 1.4858699158549824E-7, 1.9207199351526952E-7, 5.93483074189762E-7, 4.679757587493329E-7, 6.561719419432148E-8, 9.219390398593702E-7, 8.421737511680584E-7, 5.762930799072936E-7, 5.933886538854635E-7)
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 6.068369186455293E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 6.069041984716261E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 6.217525313656229E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 5.196058485861371E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 5.020844855865593E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 4.463223217393347E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 4.126923713625932E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 4.315480960793399E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 5.455934686044934E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 6.248572475663184E-7,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

        run {
            val inX = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0)
            val inY = listOf(1.405875171822729E-4, 8.773701104246436E-4, 8.740623780642788E-4, 9.612008914274215E-4, 7.442752301177569E-5, 3.102409406809994E-4, 8.033139539875547E-4, 7.275167288382125E-4, 1.1415516131617985E-4, 4.627117876192168E-4, 8.711442528382276E-4, 1.4858699158549826E-4, 1.9207199351526958E-4, 5.934830741897622E-4, 4.67975758749333E-4, 6.56171941943215E-5, 9.219390398593703E-4, 8.421737511680586E-4, 5.762930799072937E-4, 5.933886538854637E-4)
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 6.068369186455294E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 6.069041984716266E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 6.21752531365623E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 5.19605848586137E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 5.020844855865594E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 4.463223217393348E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 4.126923713625933E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 4.3154809607934014E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 5.455934686044935E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 6.248572475663194E-4,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

        run {
            val inX = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0)
            val inY = listOf(0.1405875171822729, 0.8773701104246435, 0.8740623780642786, 0.9612008914274214, 0.07442752301177569, 0.31024094068099933, 0.8033139539875546, 0.7275167288382125, 0.11415516131617985, 0.46271178761921666, 0.8711442528382275, 0.14858699158549826, 0.19207199351526955, 0.5934830741897621, 0.4679757587493329, 0.0656171941943215, 0.9219390398593702, 0.8421737511680585, 0.5762930799072936, 0.5933886538854636)
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 0.606836918645529,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.6069041984716265,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.6217525313656229,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.5196058485861371,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.5020844855865594,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.44632232173933484,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.41269237136259307,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.4315480960793402,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.5455934686044935,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 0.6248572475663187,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

        run {
            val inX = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0)
            val inY = listOf(140.5875171822729, 877.3701104246435, 874.0623780642786, 961.2008914274214, 74.42752301177568, 310.2409406809993, 803.3139539875546, 727.5167288382124, 114.15516131617984, 462.7117876192167, 871.1442528382274, 148.58699158549825, 192.07199351526955, 593.4830741897621, 467.9757587493329, 65.6171941943215, 921.9390398593703, 842.1737511680585, 576.2930799072936, 593.3886538854636)
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 606.8369186455294,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 606.9041984716263,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 621.7525313656229,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 519.6058485861371,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 502.08448558655937,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 446.3223217393347,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 412.6923713625931,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 431.54809607934004,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 545.5934686044938,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 624.8572475663196,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

        run {
            val inX = listOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0)
            val inY = listOf(140587.5171822729, 877370.1104246435, 874062.3780642786, 961200.8914274215, 74427.52301177567, 310240.9406809993, 803313.9539875545, 727516.7288382124, 114155.16131617985, 462711.7876192167, 871144.2528382274, 148586.99158549824, 192071.99351526954, 593483.0741897621, 467975.7587493329, 65617.19419432149, 921939.0398593702, 842173.7511680585, 576293.0799072937, 593388.6538854636)
            val actX = listOf(0.0, 1.9, 3.8, 5.699999999999999, 7.6, 9.5, 11.4, 13.3, 15.200000000000001, 17.1)
            val actY = listOf(
                EvalResult(
                    y = 606836.9186455292,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 606904.1984716263,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 621752.5313656228,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 519605.8485861372,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 502084.4855865594,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 446322.32173933485,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 412692.3713625932,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 431548.09607934026,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 545593.4686044934,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                ),
                EvalResult(
                    y = 624857.247566319,
                    ymin = Double.NaN,
                    ymax = Double.NaN,
                    se = Double.NaN
                )
            )

            assertRegression(inX, inY, actX, actY)
        }

    }

    private fun assertRegression(inX: List<Double>, inY: List<Double>, expectedX: List<Double>, expectedResult: List<EvalResult>) {

        val loessRegression = newLocalPolynomialRegression(inX, inY, confidenceLevel)

        // Compare Y only, other properties are random
        expectedX.zip(expectedResult).forEach { (x, r) ->
            assertEquals(r.y, loessRegression.evalX(x).y)
        }
    }


    private val xss: MutableList<Double?> = mutableListOf(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0)
    private val yss: MutableList<Double?> = mutableListOf(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2, 1.3, 1.4, 1.5, 1.6, 1.7, 1.8, 1.9)

    @Test(expected = IllegalArgumentException::class)
    fun testSizeDifference() {
        // Exception "X/Y must have same size" is expected
        newLocalPolynomialRegression(xss.dropLast(1), yss, confidenceLevel)
    }

    @Test
    fun testXNull() {
        val xs = xss.apply { set(lastIndex, null) }
        newLocalPolynomialRegression(xs, yss, confidenceLevel)
    }

    @Test
    fun testYNull() {
        val ys = yss.apply { set(lastIndex, null) }
        newLocalPolynomialRegression(xss, ys, confidenceLevel)
    }

    @Test
    fun testXYNullSame() {
        val xs = xss.apply { set(3, null) }
        val ys = yss.apply { set(3, null) }
        newLocalPolynomialRegression(xs, ys, confidenceLevel)
    }

    @Test
    fun testXYNullDiff() {
        val xs = xss.apply { set(lastIndex, null); set(0, null) }
        val ys = yss.apply { set(3, null) }
        newLocalPolynomialRegression(xs, ys, confidenceLevel)
    }

    @Test
    fun testNaN() {
        val xs = xss.apply { set(0, Double.NaN) }
        newLocalPolynomialRegression(xs, yss, confidenceLevel)
    }

   @Test
    fun testInfinite() {
        val xs = xss.apply { set(0, Double.POSITIVE_INFINITY) }
        newLocalPolynomialRegression(xs, yss, confidenceLevel)
    }

    private fun newLocalPolynomialRegression(xs: List<Double?>, ys: List<Double?>, confidenceLevel: Double): LocalPolynomialRegression {
        return LocalPolynomialRegression(xs, ys, confidenceLevel, 0.5)
    }

}