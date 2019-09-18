from IPython.display import HTML, display
from datalore_plot_kotlin_bridge import generate_html


def say():
    print("Hello from py!")


# def ksay():
#     print(ping())


def plotgg():
    # display_object = HTML("<b>Hello from datalore plot (new) </b>")
    # display_object = "<b>Hello from datalore plot (plain) </b>"
    # display(display_object, raw=True)
    # display(display_object)

    html_data = generate_html()
    display_object = HTML(html_data)
    # display_object = html_data
    display(display_object)
