#  Copyright (c) 2022. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.
import numbers

from lets_plot.plot.core import FeatureSpec

__all__ = ['font_metrics_adjustment', 'font_family_info']


def font_metrics_adjustment(width_correction: numbers.Real) -> FeatureSpec:
    """
    Adjust estimated width of text labels on plot.

    Allows for manual correction in a rare cases when plot layout looks broken
    due to either overestimation or underestimation of size of text labels on plot.

    Parameters
    ----------
    width_correction : number
        Correcting coefficient applied to default width estimate of a text label.

    Returns
    -------
    `FeatureSpec`
        Metainfo specification.

    Notes
    -----
    Can be mixed with other plot features in a plot-expression:

    p + ggsize(300, 500) + font_metrics_adjustment(1.3)

    """
    return FeatureSpec('metainfo', name='font_metrics_adjustment',
                       width_correction=width_correction)


def font_family_info(family: str, width_correction: numbers.Real = None, mono: bool = None) -> FeatureSpec:
    """
    Specify properies of a particular font-family to adjust estimated width of text labels on plot.

    Might be useful when some exotic font-family is used that causes issues with the plot layout.

    Allows for manual correction in a rare cases when plot layout looks broken
    due to either overestimation or underestimation of size of text labels on plot.

    Parameters
    ----------
    family : str
        Font family.
    width_correction : number, optional
        Correcting coefficient applied to default width estimate of a text label.
    mono : bool, optional
        When True - the font is marked as `monospaced`.

    Returns
    -------
    `FeatureSpec`
        Metainfo specification.

    Notes
    -----
    Can be mixed with other plot features in a plot-expression:

    p + ggsize(300, 500) + font_family_info("HyperFont", mono=True)

    """
    return FeatureSpec('metainfo', name='font_family_info',
                       family=family,
                       width_correction=width_correction,
                       monospaced=mono)
