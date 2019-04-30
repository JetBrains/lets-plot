package jetbrains.datalore.visualization.plot.gog.config.transform.encode

import jetbrains.datalore.visualization.plot.gog.config.Option.Plot
import jetbrains.datalore.visualization.plot.gog.config.transform.PlotSpecTransform
import jetbrains.datalore.visualization.plot.gog.config.transform.PlotSpecTransformUtil
import jetbrains.datalore.visualization.plot.gog.config.transform.SpecChange

object DataSpecEncodeTransforms {
    /*
  private static final List<SpecSelector> DATA_SPEC_SELECTORS = Arrays.asList(
      SpecSelector.of(Plot.DATA),
      SpecSelector.of(Plot.LAYERS, Layer.DATA),
      SpecSelector.of(Plot.LAYERS, Layer.GEOM, Layer.DATA),
      SpecSelector.of(Plot.LAYERS, Layer.STAT, Layer.DATA)
      //SpecSelector.of(Plot.LAYERS, Layer.GEOM, Geom.Choropleth.GEO_POSITIONS)
  );
  */

    internal fun addDataChanges(builder: PlotSpecTransform.Builder, change: SpecChange, isGGBunch: Boolean): PlotSpecTransform.Builder {
        val specSelectors = PlotSpecTransformUtil.getPlotAndLayersSpecSelectors(isGGBunch, Plot.DATA)

        //for (SpecSelector specSelector : DATA_SPEC_SELECTORS) {
        for (specSelector in specSelectors) {
            builder.change(specSelector, change)
        }
        return builder
    }

    fun clientSideDecode(isGGBunch: Boolean): PlotSpecTransform {
        val builder = PlotSpecTransform.builderForRawSpec()
        addDataChanges(builder, ClientSideDecodeChange(), isGGBunch)
        addDataChanges(builder, ClientSideDecodeOldStyleChange(), isGGBunch)
        return builder.build()
    }
}
