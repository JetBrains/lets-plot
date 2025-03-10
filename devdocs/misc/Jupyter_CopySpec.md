# Debugging Jupyter Plots
Unlike debugging a demo application, debugging a plot specification from Jupyter allows for quick iteration and immediate feedback on changes.

To obtain the plot specification as a JSON string in a Jupyter notebook, follow these steps:

1. Install `pickleshare` (e.g., with `!pip install pickleshare` in a Jupyter cell, then restart the kernel).
2. Run the script to install the `_repr_html_` hook using the IPython startup dir (usually `~/.ipython/profile_default/startup/`):  
    ```bash
    ./install_lets_plot_hook.sh
    ``` 
3. Run any plot cell.
4. Click the **"Copy Spec"** button in the top-right corner of the plot to copy the specification JSON string to the clipboard.
5. Set the `PLOT_SPEC` environment variable in the `Run Configuration` of `PlotSpecDebugger` using the copied JSON string, or paste it directly into the editor element in the app.


### Controlling Button Visibility  
The button’s visibility can be managed for all notebooks using the following magic command in Jupyter:
- `%toggle_copy_spec_button on` → Show the button
- `%toggle_copy_spec_button off` → Hide the button

These commands can be used in separate notebooks, such as `enable_copy_spec_button.ipynb` and `disable_copy_spec_button.ipynb`, to enable or disable the button for all notebooks.


### Removing the hook
To remove the `_repr_html_` hook, delete the `50-lets-plot-hook.py` file from the IPython startup directory (usually `~/.ipython/profile_default/startup/`) and restart the kernel.