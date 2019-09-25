# from datalore_plot_kotlin_bridge import generate_html

SAMPLE_PLOT_SPEC = {
    'data': {
        'time': ['Lunch', 'Lunch', 'Dinner', 'Dinner', 'Dinner']
    },
    'mapping': {
        'x': 'time',
        'y': '..count..',
        'fill': '..count..'
    },
    'layers': [
        {
            'geom': 'bar'
        }
    ],
    'scales': [
        {
            'aesthetic': 'fill',
            'discrete': True,
            'scale_mapper_kind': 'color_hue'
        }
    ]
}


def plotgg():
    # display_object = HTML("<b>Hello from datalore plot (new) </b>")
    # display_object = "<b>Hello from datalore plot (plain) </b>"
    # display(display_object, raw=True)
    # display(display_object)

    # html_data = generate_html()
    # display_object = HTML(html_data)
    # # display_object = html_data
    # display(display_object)

    return PlotSpec()


class PlotSpec:
    def _repr_html_(self):
        # called by IPython.display.display
        from ..frontend_context.frontend_context import as_html
        # ToDo: self as_dict
        return as_html(SAMPLE_PLOT_SPEC)

    def show(self):
        # various actions depending on the frontend context
        # ...
        pass
