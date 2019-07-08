package jetbrains.gis.geoprotocol;

import com.google.common.collect.Iterators;
import jetbrains.datalore.base.projectionGeometry.LineString;
import jetbrains.datalore.base.projectionGeometry.Multipolygon;
import jetbrains.ocelot.base.geometry.DoubleRectangle;
import jetbrains.ocelot.base.geometry.DoubleRectangles;

import java.util.List;

public final class GeometryUtil {
  public static DoubleRectangle bbox(Multipolygon multipolygon) {
    List<DoubleRectangle> rects = multipolygon.getLimits();
    if (rects.isEmpty()) {
      return null;
    }

    return DoubleRectangles.boundingBox(() -> Iterators.concat(
        Iterators.transform(rects.iterator(), it -> it.origin),
        Iterators.transform(rects.iterator(), it -> it.origin.add(it.dimension))
    ));
  }

  public static LineString asLineString(Geometry geometry) {
    return new LineString(geometry.asMultipolygon().get(0).get(0));
  }

  private GeometryUtil() {
  }
}
