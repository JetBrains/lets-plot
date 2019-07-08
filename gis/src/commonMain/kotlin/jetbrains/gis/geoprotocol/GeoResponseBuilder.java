package jetbrains.gis.geoprotocol;

import jetbrains.datalore.base.projectionGeometry.GeoRectangle;
import jetbrains.gis.protocol.GeoResponse.AmbiguousGeoResponse;
import jetbrains.gis.protocol.GeoResponse.AmbiguousGeoResponse.AmbiguousFeature;
import jetbrains.gis.protocol.GeoResponse.AmbiguousGeoResponse.Namesake;
import jetbrains.gis.protocol.GeoResponse.AmbiguousGeoResponse.NamesakeParent;
import jetbrains.gis.protocol.GeoResponse.SuccessGeoResponse;
import jetbrains.gis.protocol.GeoResponse.SuccessGeoResponse.GeocodedFeature;
import jetbrains.ocelot.base.geometry.DoubleVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Streams.zip;
import static java.util.stream.Collectors.toList;

public final class GeoResponseBuilder {
  private GeoResponseBuilder() {
  }

  public static class SuccessResponseBuilder {
    private List<GeocodedFeature> myGeocodedFeatures = new ArrayList<>();
    private Optional<FeatureLevel> myFeatureLevel = Optional.empty();

    public SuccessResponseBuilder addGeocodedFeature(GeocodedFeature feature) {
      myGeocodedFeatures.add(feature);
      return this;
    }

    public SuccessResponseBuilder addGeocodedFeatures(List<GeocodedFeature> features) {
      features.forEach(this::addGeocodedFeature);
      return this;
    }

    public SuccessResponseBuilder setLevel(Optional<FeatureLevel> level) {
      myFeatureLevel = level;
      return this;
    }

    public SuccessGeoResponse build() {
      return new SuccessGeoResponse(myGeocodedFeatures, myFeatureLevel);
    }
  }

  public static class AmbiguousResponseBuilder {
    private List<AmbiguousFeature> myAmbiguousFeatures = new ArrayList<>();
    private Optional<FeatureLevel> myFeatureLevel;

    public AmbiguousResponseBuilder addAmbiguousFeature(AmbiguousFeature feature) {
      myAmbiguousFeatures.add(feature);
      return this;
    }

    public AmbiguousResponseBuilder addAmbiguousFeatures(List<AmbiguousFeature> features) {
      features.forEach(this::addAmbiguousFeature);
      return this;
    }

    public AmbiguousResponseBuilder setLevel(Optional<FeatureLevel> v) {
      myFeatureLevel = v;
      return this;
    }

    public AmbiguousGeoResponse build() {
      return new AmbiguousGeoResponse(myAmbiguousFeatures, myFeatureLevel);
    }
  }

  public static class GeocodedFeatureBuilder {
    private String query;
    private String id;
    private String name;
    private DoubleVector centroid;
    private GeoRectangle limit;
    private GeoRectangle position;
    private List<String> highlights = new ArrayList<>();
    private Geometry boundary;
    private List<GeoTile> tileGeometries = new ArrayList<>();

    public GeocodedFeatureBuilder setQuery(String v) {
      this.query = v;
      return this;
    }

    public GeocodedFeatureBuilder setId(String id) {
      this.id = id;
      return this;
    }

    public GeocodedFeatureBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public GeocodedFeatureBuilder setBoundary(Geometry boundary) {
      this.boundary = boundary;
      return this;
    }

    public GeocodedFeatureBuilder addTile(GeoTile tile) {
      this.tileGeometries.add(tile);
      return this;
    }

    public GeocodedFeatureBuilder addTiles(List<GeoTile> tiles) {
      tiles.forEach(this::addTile);
      return this;
    }

    public GeocodedFeatureBuilder setCentroid(DoubleVector centroid) {
      this.centroid = centroid;
      return this;
    }

    public GeocodedFeatureBuilder setLimit(GeoRectangle limit) {
      this.limit = limit;
      return this;
    }

    public GeocodedFeatureBuilder setPosition(GeoRectangle position) {
      this.position = position;
      return this;
    }

    public GeocodedFeatureBuilder addHighlight(String v) {
      this.highlights.add(v);
      return this;
    }

    public GeocodedFeatureBuilder addHighlights(List<String> highlights) {
      highlights.forEach(this::addHighlight);
      return this;
    }

    public GeocodedFeature build() {
      return new GeocodedFeature(
          query,
          id,
          name,
          highlights,
          centroid,
          position,
          limit,
          boundary,
          tileGeometries
      );
    }
  }

  public static class AmbiguousFeatureBuilder {
    private String query;
    private int totalNamesakeCount;
    private List<Namesake> namesakeExamples = new ArrayList<>();

    public AmbiguousFeature build() {
      return new AmbiguousFeature(query, totalNamesakeCount, namesakeExamples);
    }

    public AmbiguousFeatureBuilder setQuery(String query) {
      this.query = query;
      return this;
    }

    public AmbiguousFeatureBuilder setTotalNamesakeCount(int totalNamesakeCount) {
      this.totalNamesakeCount = totalNamesakeCount;
      return this;
    }

    public AmbiguousFeatureBuilder addNamesakeExample(Namesake v) {
      this.namesakeExamples.add(v);
      return this;
    }

    public AmbiguousFeatureBuilder addNamesakeExamples(List<Namesake> namesakes) {
      namesakes.forEach(this::addNamesakeExample);
      return this;
    }
  }

  public static class NamesakeBuilder {
    private String name;
    private List<String> parentNames = new ArrayList<>();
    private List<FeatureLevel> parentLevels = new ArrayList<>();

    public Namesake build() {
      if (parentNames.size() != parentLevels.size()) {
        throw new IllegalStateException();
      }

      List<NamesakeParent> namesakeParents = zip(
          parentNames.stream(),
          parentLevels.stream(),
          NamesakeParent::new
      ).collect(toList());

      return new Namesake(name, namesakeParents);
    }

    public NamesakeBuilder setName(String name) {
      this.name = name;
      return this;
    }

    public NamesakeBuilder addParentName(String v) {
      parentNames.add(v);
      return this;
    }

    public NamesakeBuilder addParentLevel(FeatureLevel v) {
      parentLevels.add(v);
      return this;
    }

    public NamesakeBuilder addParent(String name, FeatureLevel level) {
      addParentName(name);
      addParentLevel(level);
      return this;
    }

    public NamesakeBuilder addParents(List<NamesakeParent> parents) {
      parents.forEach(parent -> addParent(parent.getName(), parent.getLevel()));
      return this;
    }
  }
}
