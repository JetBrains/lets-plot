SVG/HTML export to file
------------------------

.. py:function:: export_svg()

    function takes plot specification and filename as parameters and saves SVG representation of the plot to
    a file in the current working directory.

.. code-block:: python

    from lets_plot import *
    p = ggplot()...

    # export SVG to file
    from lets_plot.export.simple import export_svg

    export_svg(p, "p.svg")

