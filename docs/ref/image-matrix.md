# `gg_image_matrix()`

Create a new ggobject - a ndarray of images. Function `gg_image_matrix()` displays these images in a grid.  

`gg_image_matrix(image_data_array=np.ndarray, norm=None, scale=1)`
   
Grid dimensions are determined by shape of the input 2D ndarray.
Elements of the input 2D array are images specified by ndarrays with shape `(n, m)` or `(n, m, 3)` or `(n, m, 4)`.

### Arguments
- `image_data_array` (2D ndarray of images): Specifies dimensions of output grid
- `norm` (bool): False - disables default scaling of a luminance (grayscale) images to the (0, 255) range.
- `scale`(scalar, default = 1): Specifies magnification factor
        
### Examples  

`from datalore.plot import gg_image_matrix`

```python
rows = 2
cols = 3
X = np.empty([rows, cols], dtype=object)
X.fill(img)
```

`gg_image_matrix(X)`

![](assets/imagematrix_1.png)

Change image sizes

`for row in range(rows):
     for col in range(cols):
         v = (col + row + 1) * 10
         X[row][col] = img[v:-v,v:-v,:]`
 
`gg_image_matrix(X)`

![](assets/imagematrix_2.png)
