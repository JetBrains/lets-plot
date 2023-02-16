/*
 * Copyright (c) 2023. JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package jetbrains.datalore.plot.config

internal class FigGridConfigClientSide(
    opts: Map<String, Any>,
    computationMessagesHandler: ((List<String>) -> Unit)
) : OptionsAccessor(opts) {
    val ncol: Int
    val nrow: Int
    val elementConfigs: List<OptionsAccessor?>

    init {

        @Suppress("UNCHECKED_CAST")
        val figSpecs2D = getList(Option.SubPlots.FIGURES) as List<Any>
        nrow = figSpecs2D.size
        ncol = (figSpecs2D.firstOrNull() as List<*>?)?.size ?: 0

        for ((index, row) in figSpecs2D.withIndex()) {
            require(row is List<*>) { "SupPlots row [$index] is expected as a List but was ${row::class.simpleName}" }
            require(row.size == ncol) { "SupPlots col count is $ncol but row [$index] size is ${row.size}." }
        }

        val computationMessages = ArrayList<String>()
        elementConfigs = figSpecs2D.flatMap { it as Iterable<*> }
            .map { spec ->
                if (spec is Map<*, *>) {
                    @Suppress("UNCHECKED_CAST")
                    spec as Map<String, Any>
                    when (PlotConfig.figSpecKind(spec)) {
                        FigKind.PLOT_SPEC -> PlotConfigClientSide.create(spec) { computationMessages.addAll(it) }
                        FigKind.SUBPLOTS_SPEC -> FigGridConfigClientSide(spec) { computationMessages.addAll(it) }
                        FigKind.GG_BUNCH_SPEC -> throw IllegalArgumentException("SubPlots can't contain GGBunch.")
                    }
                } else {
                    null
                }
            }

        computationMessagesHandler(computationMessages)
    }
}
