#  Copyright (c) 2023. JetBrains s.r.o.
#  Use of this source code is governed by the MIT license that can be found in the LICENSE file.


def _image_bbox(width, height):
    return dict(
        xmin=-0.5,
        ymin=-0.5,
        xmax=width - 1 + 0.5,
        ymax=height - 1 + 0.5
    )


def _image_spec(href, bbox, data_min=None, data_max=None, start=0., end=1.0, colors=None):
    layer_spec = dict(
        data_meta={},
        geom='image',
        href=href,
        mapping={},
        show_legend=True,
        inherit_aes=False,
    )
    layer_spec.update(bbox)

    if data_min is None or data_max is None:
        return layer_spec

    layer_spec['color_by'] = 'paint_c'
    layer_spec['mapping'] = dict(paint_c=[data_min, data_max])

    if colors is not None:
        scale_spec = dict(
            aesthetic='paint_c',
            colors=colors,
            name='',
            scale_mapper_kind='color_gradientn',
        )
    else:
        scale_spec = dict(
            aesthetic='paint_c',
            start=start,
            end=end,
            name='',
            scale_mapper_kind='color_grey'
        )

    return {
        'feature-list': [
            dict(layer=layer_spec),
            dict(scale=scale_spec),
        ]
    }
