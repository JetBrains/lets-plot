# Blurry text
May be caused by:
- fractional coordinates. Currently fixed via `RenderableComponent.roundCoordinates` property.
- scaling. Happens when rendered with non-original size (e.g. fix for tile gaps - drawing a 256x256 tile to the output with the size 257x257).

# Gaps between tiles
May be caused by:
- fractional coordinates. Also fixed via `RenderableComponent.roundCoordinates` property.
- scaling. Happens when the browser zoom is fractional (e.g. 125%). We can't create a tile with a fractional size. Fixed by rounding `window.devicePixelRatio` with `ceil()` function. 
