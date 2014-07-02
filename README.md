# Terraformer

Terraformer is a modular toolkit for working with geographic data.

## Modules

The Terraformer project to broken up into a series of smaller modules.

* [Terraformer Core](http://terraformer.io/core/) - Contains methods and objects for working with GeoJSON. This also contains common methods used by other modules.
* [ArcGIS Geometry Parser](http://terraformer.io/arcgis-parser/) - Parse the [ArcGIS Geometry Format](http://resources.arcgis.com/en/help/arcgis-rest-api/#/Geometry_Objects/02r3000000n1000000/) into GeoJSON and vica-versa.

## Features

* Designed to work in Android and pure Java contexts
* Modular interface for adding support for other geometry formats

## Getting Started

Check out the getting [started guide](http://terraformer.io/getting-started/) which will give you an overview of core concepts and methods in Terraformer.

## Documentation

Check out the full documentation on the [Terraformer website](http://terraformer.io/core/) and the [getting started guide](http://terraformer.io/getting-started/).

### Converting Esri JSON to GeoJSON

```java
// Instantiate a Terraformer
Terraformer t = new Terraformer();

// Set the decoder and encoder
t.setDecoder(new EsriJson());
t.setEncoder(new GeoJson());

String esriJson = "{\"x\":100.0,\"y\":100.0}";

// Convert the Esri JSON string to GeoJSON
String geoJson = t.convert(esriJson);
```

### Converting GeoJSON to EsriJSON

```java
// Instantiate a Terraformer
Terraformer t = new Terraformer();

// Set the decoder and encoder
t.setDecoder(new GeoJson());
t.setEncoder(new EsriJson());

String geoJson = "{\"type\":\"Point\",\"coordinates\":[100.0,100.0]}";

// Convert the GeoJSON string to Esri JSON
String esriJson = t.convert(geoJson);
```

## Resources

* [Terraformer Website](http://terraformer.io)
* [@EsriPDX on Twitter](http://twitter.com/esripdx)

## Issues

Find a bug or want to request a new feature?  Please let us know by submitting an issue.

## Contributing

Esri welcomes contributions from anyone and everyone. Please see our [guidelines for contributing](https://github.com/esri/contributing).

[](Esri Tags: Terraformer GeoJSON)
[](Esri Language: Java)
