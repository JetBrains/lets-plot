`FONT_WIDTH_SCALE_FACTOR = PLATFORM_SCALE_FACTOR * EXAGGERATION_FACTOR`

`PLATFORM_SCALE_FACTOR` - fixed factor that correspond to ratio: *mean SVG font width* to *mean wxPython font width*.

`EXAGGERATION_FACTOR` - factor for the slight exaggeration of the predictions; it is calculated in [this notebook](https://nbviewer.org/github/ASmirnov-HORIS/text-width-estimation/blob/main/notebooks/prepare_model.ipynb).