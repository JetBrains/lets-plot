# from datalore_plot_kotlin_bridge import generate_html


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
        return as_html(self)

    def show(self):
        # various actions depending on the frontend context
        # ...
        pass
