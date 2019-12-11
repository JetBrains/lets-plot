# ggsize()

Specifies overall size of plot

`ggsize(width, height)`

##### Arguments 
  
- `width` (number): Width of plot in px.
- `height` (number):  Height of plot in px.

##### Examples
    
`import numpy as np
import pandas as pd
from datalore.plot import *`

`x = np.arange(100)
y = np.random.normal(size=100)
dat = pd.DataFrame({'x':x, 'y':y})
p = ggplot(dat) + geom_line(aes('x','y')) + ggsize(600, 120)`

![](/assets/docs/ggplot/ggsize_1.png)

![](/assets/docs/ggplot/ggsize_2.png)
