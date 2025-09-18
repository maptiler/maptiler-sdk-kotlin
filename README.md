# MapTiler SDK Kotlin
<p align="center">
<img src="Examples/maptiler-logo.png" alt="MapTiler" title="MapTiler"/>
</p>


The MapTiler SDK Kotlin is a native SDK written in Kotlin, designed to work with the well-established MapTiler Cloud service, which provides all the data required to fuel a complete mobile mapping experience: vector tiles, geojson, map interaction, custom styles, data visualization and more.

## Features
- [x] Map interaction
- [x] Pre-made map styles
- [x] VectorTile and GeoJSON sources
- [x] Fill, Line and Symbol layers
- [x] Custom Annotation Views
- [x] Location tracking
- [x] Globe and 3D Terrain

## Basic Usage

Make sure to set your MapTiler Cloud API key first:

```kotlin
MTConfig.apiKey = "YOUR_API_KEY"
```

### Jetpack Compose

Instantiate controller (with or without delegate) and the map view:

```kotlin
import com.maptiler.maptilersdk.map.MTMapOptions
import com.maptiler.maptilersdk.map.MTMapView
import com.maptiler.maptilersdk.map.MTMapViewController
import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

val controller = MTMapViewController(context)

MTMapView(
            MTMapReferenceStyle.STREETS,
            MTMapOptions(),
            controller,
            modifier =
                Modifier
                    .fillMaxSize(),
        )
```

### XML

Add MTMapViewClassic to your layout xml:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" >
    <com.maptiler.maptilersdk.map.MTMapViewClassic
        android:id="@+id/classicMapView"
        android:layout_height="match_parent"
        android:layout_width="match_parent" />
</LinearLayout>
```

Instantiate the MTMapViewClassic:

```kotlin
 import com.maptiler.maptilersdk.map.MTMapOptions
 import com.maptiler.maptilersdk.map.MTMapViewClassic
 import com.maptiler.maptilersdk.map.MTMapViewController
 import com.maptiler.maptilersdk.map.style.MTMapReferenceStyle

private lateinit var mapView: MTMapViewClassic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MTConfig.apiKey = "YOUR_API_KEY"
        val controller = MTMapViewController(baseContext)
        enableEdgeToEdge()
        setContentView(R.layout.main_activity_layout)

        mapView = findViewById(R.id.classicMapView)
        mapView.initialize(MTMapReferenceStyle.SATELLITE, MTMapOptions(), controller)
    }
```

For detailed functionality overview refer to the API Reference documentation.

## Sources and Layers

Sources and layers can be added to the map view style object as soon as map is initialized. Setting the style after adding layers resets them to default, so make sure style is finished loading first.

```kotlin
val sourceURL = URL("https://api.maptiler.com/tiles/v3-openmaptiles/tiles.json?key=$YOUR_API_KEY")
val source = MTVectorTileSource("openmapsource", sourceURL)
controller.style?.addSource(source)
```

```kotlin
try {
    val layer = MTFillLayer("fillLayer", "openmapsource")
    layer.color = Color.Blue.toArgb()
    layer.outlineColor = Color.Cyan.toArgb()
    layer.sourceLayer = "aeroway"
    controller.style?.addLayer(layer)
} catch (error: MTStyleError) {
    Log.e("MTStyleError", "Layer already exists.")
}
```


## Markers and Popups

```kotlin
val lngLat = LngLat(43.2352, 19.4567)
val marker = MTMarker(lngLat, Color.Blue.toArgb())
controller.style?.addMarker(marker)
```

```kotlin
val lngLat = LngLat(43.2352, 19.4567)
val popup = MTTextPopup(lngLat, "My Text")
controller.style?.addTextPopup(popup)
```

## Events

Optionally wrap the map controller in a class to observe the map events and use the wrapper class controller for map manipulation.

```kotlin
class MapController(
    private val context: Context,
) : MTMapViewDelegate {
    val controller: MTMapViewController =
        MTMapViewController(context).apply {
            delegate = this@MapController
        }

    override fun onMapViewInitialized() {
        Log.i("Init", "Map View Initialized.")
    }

    override fun onEventTriggered(
        event: MTEvent,
        data: MTData?,
    ) {
        Log.i("Event", "Map View Event Triggered: $event.")
    }
}
```

# Installation
MapTiler Kotlin SDK is a Kotlin Library and can be added as dependency in your Gradle file (**Maven Central**).

<p align="center">
<img src="Examples/streets.png" alt="MapTiler" title="MapTiler"/>
<img src="Examples/satellite.png" alt="MapTiler" title="MapTiler"/>
</p>

# License
MapTiler SDK Kotlin is released under the BSD 3-Clause license. See LICENSE for details.