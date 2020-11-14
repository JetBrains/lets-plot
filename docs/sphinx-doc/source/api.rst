.. _api:

.. currentmodule:: lets_plot

API Reference
#############


.. _api_plotting:

Plotting
========

.. autosummary::
   :toctree: generated

   ggplot
   GGBunch
   aes
   layer
   ggsave


.. _api_geometries:

Geometries
==========

Standard
--------

.. autosummary::
   :toctree: generated

   geom_point
   geom_path
   geom_line
   geom_smooth
   geom_bar
   geom_histogram
   geom_bin2d
   geom_tile
   geom_raster
   geom_errorbar
   geom_crossbar
   geom_linerange
   geom_pointrange
   geom_contour
   geom_contourf
   geom_polygon
   geom_map
   geom_abline
   geom_hline
   geom_vline
   geom_boxplot
   geom_ribbon
   geom_area
   geom_density
   geom_density2d
   geom_density2df
   geom_jitter
   geom_freqpoly
   geom_step
   geom_rect
   geom_segment
   geom_text

Additional
----------

.. autosummary::
   :toctree: generated
   
   geom_image
   geom_livemap


.. _api_fsp:

Facets
======

.. autosummary::
   :toctree: generated

   facet_grid


Scales
======

Position Scales
---------------

.. autosummary::
   :toctree: generated

   scale_x_discrete
   scale_y_discrete
   scale_x_discrete_reversed
   scale_y_discrete_reversed
   scale_x_continuous
   scale_y_continuous
   scale_x_log10
   scale_y_log10
   scale_x_reverse
   scale_y_reverse

Color Scales
------------

.. autosummary::
   :toctree: generated
   
   scale_color_manual
   scale_color_gradient
   scale_color_continuous
   scale_color_gradient2
   scale_color_hue
   scale_color_discrete
   scale_color_grey
   scale_color_brewer

Fill Scales
-----------

.. autosummary::
   :toctree: generated
   
   scale_fill_manual
   scale_fill_gradient
   scale_fill_continuous
   scale_fill_gradient2
   scale_fill_hue
   scale_fill_discrete
   scale_fill_grey
   scale_fill_brewer

Shape Scales
------------

.. autosummary::
   :toctree: generated
   
   scale_shape
   scale_shape_manual

Size Scales
-----------

.. autosummary::
   :toctree: generated
   
   scale_size_manual
   scale_size
   scale_size_area

Alpha Scales
------------

.. autosummary::
   :toctree: generated
   
   scale_alpha_manual
   scale_alpha

Linetype Scales
---------------

.. autosummary::
   :toctree: generated
   
   scale_linetype_manual

Datetime Scales
---------------

.. autosummary::
   :toctree: generated
   
   scale_x_datetime
   scale_y_datetime

Identity Scales
---------------

.. autosummary::
   :toctree: generated

   scale_color_identity
   scale_fill_identity
   scale_shape_identity
   scale_linetype_identity
   scale_alpha_identity
   scale_size_identity

Scale Limits
------------

.. autosummary::
   :toctree: generated
   
   lims
   xlim
   ylim

Scale Guides
------------

.. autosummary::
   :toctree: generated
   
   guide_legend
   guide_colorbar


Positions
=========

.. autosummary::
   :toctree: generated
   
   position_dodge
   position_jitter
   position_nudge
   position_jitterdodge


.. _api_coordinates:

Coordinates
===========

.. autosummary::
   :toctree: generated

   coord_cartesian
   coord_fixed
   coord_map


.. _api_theme:

Theme
=====

Base
----

.. autosummary::
   :toctree: generated

   theme
   element_blank
   ggsize

Labels
------

.. autosummary::
   :toctree: generated

   ggtitle
   labs
   xlab
   ylab

Tooltips
--------

.. autosummary::
   :toctree: generated

   layer_tooltips


.. _api_helpers:

Helpers
=======

.. autosummary::
   :toctree: generated

   arrow
   lon_lat


.. _api_sampling:

Sampling
========

.. autosummary::
   :toctree: generated

   sampling_random
   sampling_random_stratified
   sampling_pick
   sampling_systematic
   sampling_group_random
   sampling_group_systematic
   sampling_vertex_vw
   sampling_vertex_dp


.. _api_geospatial:

Geospatial
==========

.. currentmodule:: lets_plot.geo_data

.. autosummary::
   :toctree: generated

   distance
   regions_builder
   regions
   regions_country
   regions_state
   regions_county
   regions_city
   regions_xy
   
   split_by_antimeridian