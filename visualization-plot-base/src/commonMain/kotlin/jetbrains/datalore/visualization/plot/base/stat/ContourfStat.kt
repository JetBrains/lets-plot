package jetbrains.datalore.visualization.plot.base.stat

import jetbrains.datalore.visualization.plot.base.Aes
import jetbrains.datalore.visualization.plot.base.StatContext
import jetbrains.datalore.visualization.plot.base.data.DataFrame
import jetbrains.datalore.visualization.plot.base.data.TransformVar

internal class ContourfStat(binCount: Int, binWidth: Double?) : BaseStat(DEF_MAPPING) {

    private val myBinOptions: StatUtil.BinOptions

    init {
        myBinOptions = StatUtil.BinOptions(binCount, binWidth)
    }

    override fun requires(): List<Aes<*>> {
        return listOf(Aes.X, Aes.Y, Aes.Z)
    }

    override fun apply(data: DataFrame, statCtx: StatContext): DataFrame {
        //if (!(data.has(TransformVar.X) && data.has(TransformVar.Y) && data.has(TransformVar.Z))) {
        //  return DataFrameBuilder.emptyFrame();
        //}
        //
        ////List<Double> xVector = data.getNumeric(TransformVar.X);
        ////List<Double> zVector = data.getNumeric(TransformVar.Z);
        //ClosedRange<Double> zRange = data.range(TransformVar.Z);
        //
        //List<Double> levels = ContourStatUtil.computeLevels(zRange, myBinOptions);
        //if (levels == null) {
        //  return DataFrameBuilder.emptyFrame();
        //}

        val levels = ContourStatUtil.computeLevels(data, myBinOptions) ?: return DataFrame.Builder.emptyFrame()

        /*
       // num of columns
       // regular X/Y grid is expected
       int colCount = 0;
       Double x0 = null;
       for (Double x : xVector) {
         if (x0 == null) {
           x0 = x;
         } else if (x.equals(x0)) {
           break;
         }
         colCount++;
       }

       if (colCount <= 1) {
         throw new IllegalArgumentException("Data grid must be at least 2 columns wide (was " + colCount + ")");
       }
       int rowCount = xVector.size() / colCount;
       if (rowCount <= 1) {
         throw new IllegalArgumentException("Data grid must be at least 2 rows tall (was " + rowCount + ")");
       }
       */
        //Pair<Integer, Integer> shape = ContourStatUtil.estimateRegularGridShape(xVector);
        //Integer colCount = shape.first;
        //Integer rowCount = shape.second;
        //
        //
        //ClosedRange<Double> xRange = data.range(TransformVar.X);
        //ClosedRange<Double> yRange = data.range(TransformVar.Y);
        //Map<Double, List<List<DoubleVector>>> pathListByLevel = ContourStatUtil.computeContours(xRange, yRange, colCount, rowCount, zVector, levels);

        val pathListByLevel = ContourStatUtil.computeContours(data, levels)

        val xRange = data.range(TransformVar.X)!!
        val yRange = data.range(TransformVar.Y)!!
        val zRange = data.range(TransformVar.Z)!!
        val helper = ContourFillHelper(xRange, yRange)
        val fillLevels = ContourFillHelper.computeFillLevels(zRange, levels)
        val polygonListByFillLevel = helper.createPolygons(pathListByLevel, levels, fillLevels)

        return Contour.getPolygonDataFrame(fillLevels, polygonListByFillLevel)
    }

    companion object {
        private val DEF_MAPPING: Map<Aes<*>, DataFrame.Variable> = mapOf(
                Aes.X to Stats.X,
                Aes.Y to Stats.Y
        )
    }

}
