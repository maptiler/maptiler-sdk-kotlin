<img src="Examples/maptiler-logo.png" alt="Company Logo" height="32"/>

# MapTiler SDK Kotlin

The MapTiler SDK Kotlin is a native SDK written in Kotlin, designed to work with the well-established MapTiler Cloud service, which provides all the data required to fuel a complete mobile mapping experience: vector tiles, GeoJSON, map interaction, custom styles, data visualization and more.

[![](https://img.shields.io/badge/Kotlin-2.0.0-f2f6ff?style=for-the-badge&labelColor=D3DBEC&logo=kotlin&logoColor=333359)](./settings.gradle.kts) [![](https://img.shields.io/badge/Platforms-Android-f2f6ff?style=for-the-badge&labelColor=D3DBEC&logo=android&logoColor=333359)](https://developer.android.com)

---

📖 [Documentation](https://docs.maptiler.com/mobile-sdk/android/) &nbsp; 🌐 [Website](https://docs.maptiler.com/guides/getting-started/mobile/) &nbsp; 🔑 [Get API Key](https://cloud.maptiler.com/account/keys/)

---

<br>

<details> <summary><b>Table of Contents</b></summary>
<ul>
<li><a href="#-installation">Installation</a></li>
<li><a href="#-basic-usage">Basic Usage</a></li>
<li><a href="#-related-examples">Examples</a></li>
<li><a href="#-api-reference">API Reference</a></li>
<li><a href="#migration-guide">Migration Guide</a></li>
<li><a href="#-support">Support</a></li>
<li><a href="#-contributing">Contributing</a></li>
<li><a href="#-license">License</a></li>
<li><a href="#-acknowledgements">Acknowledgements</a></li>
</ul>
</details>

<p align="center">
<img src="Examples/streets.png" alt="MapTiler" title="MapTiler"/>
<img src="Examples/satellite.png" alt="MapTiler" title="MapTiler"/>
</p>
<br>

## 📦 Installation

MapTiler Kotlin SDK is a Kotlin library and can be added as a dependency in your Gradle file (**Maven Central**):

- Make sure you have mavenCentral() added to your repositores inside your build.gradle
- Add the library as dependency in your module build.gradle file.

```kotlin
dependencies {
  implementation("com.maptiler:maptiler-sdk-kotlin:1.1.1")
}
```

Or, use Version Catalog instead, add following to the libs.versions.toml:

```kotlin
maptilerSdkKotlin = "1.1.1"

maptiler-sdk-kotlin = { module = "com.maptiler:maptiler-sdk-kotlin", version.ref = "maptilerSdkKotlin" }
```

Then add following implementation in your build.gradle:

```kotlin
implementation(libs.maptiler.sdk.kotlin)
```

<br>

## 🚀 Basic Usage

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

Add MTMapViewClassic to your layout XML:

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

For a detailed functionality overview, refer to the API reference documentation.

<br>

## 💡 Related Examples

- [Getting Started](https://docs.maptiler.com/mobile-sdk/android/examples/get-started/)
- [Globe with milkyway and halo](https://docs.maptiler.com/mobile-sdk/android/examples/globe/)
- [Point Helper Clusters](https://docs.maptiler.com/mobile-sdk/android/examples/point-helper-cluster/)

Check out the full list of [MapTiler SDK Kotlin examples](https://docs.maptiler.com/mobile-sdk/android/examples/) or browse ready-to-use code examples at the [Examples](https://github.com/maptiler/maptiler-sdk-kotlin/tree/main/Examples) directory in this repo.

<br>

## 📘 API Reference

For detailed guides, API reference, and advanced examples, visit our comprehensive documentation:

[API documentation](https://docs.maptiler.com/mobile-sdk/android/api/)

### Sources and Layers

Sources and layers can be added to the map view style object as soon as the map is initialized. Setting the style after adding layers resets them to default, so make sure the style has finished loading first.

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

### Markers and Popups

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

### Events

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

### Custom Annotations

Render your own UI as map annotations.

Note: Custom annotations rely on camera events. They work out of the box with the default `CAMERA_ONLY` event level (provides `ON_MOVE` and `ON_ZOOM`). If you override `eventLevel`, ensure it is `CAMERA_ONLY` or `ALL`.

Compose — overlay on top of MTMapView

```kotlin
Box(Modifier.fillMaxSize()) {
    MTMapView(MTMapReferenceStyle.STREETS, MTMapOptions(), controller, Modifier.fillMaxSize())

    MTCustomAnnotationView(
        controller = controller,
        coordinates = LngLat(16.6, 49.2),
        modifier = Modifier,
    ) {
        // Your composable content here (e.g., a Card or Icon)
    }
}
```

XML (Classic) — add a view above MTMapViewClassic

```kotlin
// Create with desired size in pixels and initial coordinates
val customView = MTCustomAnnotationViewClassic(
    context = this,
    widthPx = 120,
    heightPx = 48,
    initialCoordinates = LngLat(16.6, 49.2),
)

// Attach above the WebView and start updates
customView.addTo(mapView /* MTMapViewClassic */, controller)

// Update position later
customView.setCoordinates(LngLat(16.7, 49.25), controller)

// Remove when no longer needed
customView.remove()
```

## ⚙️ Performance, Events, and Throttling

To balance responsiveness and performance, the event level and throttling are configurable via `MTMapOptions`:

- Event levels:
  - `ESSENTIAL`: Lifecycle and taps only (ready, load, moveend, resize). No per-frame camera updates.
  - `CAMERA_ONLY` (default): Essentials plus `move` and `zoom` camera events. Ideal for custom annotations.
  - `ALL`: Everything, including high-frequency touch/render events. Use with care on low-end devices.
  - `OFF`: Minimal wiring (internal lifecycle only).

- Throttle: `highFrequencyEventThrottleMs` applies to `CAMERA_ONLY` and `ALL` to limit update rate during gestures. Default is `150` ms. Set to `0–32` for smoother overlays, or increase for balanced performance.

Examples:

```kotlin
// Smooth camera tracking for overlays
val options = MTMapOptions(
    eventLevel = MTEventLevel.CAMERA_ONLY,
    highFrequencyEventThrottleMs = 16,
)

// Leanest pipeline with lifecycle only
val lean = MTMapOptions(eventLevel = MTEventLevel.ESSENTIAL)
```

You can also apply preset helpers:

```kotlin
// Start from your options and apply lean performance defaults (keeps essentials; camera events can be opted-in)
val opts = MTMapOptions(center = LngLat(16.6, 49.2), zoom = 10.0)
val leanOpts = opts.withLeanPerformanceDefaults()

// Or go for high fidelity performance settings
val hiFi = MTMapOptions().withHighFidelityDefaults()
```

### Space

The space option customizes the globe’s background, simulating deep space or skybox effects.

- Prerequisite: use globe projection. Set `projection = MTProjectionType.GLOBE` in `MTMapOptions`.

Usage — solid color background

```kotlin
val controller = MTMapViewController(context)

val options = MTMapOptions(
    projection = MTProjectionType.GLOBE,
    space = MTSpaceOption.Config(
        MTSpace(
            color = Color(0xFF111122).toArgb(),
        ),
    ),
)

MTMapView(
    MTMapReferenceStyle.STREETS,
    options,
    controller,
    modifier = Modifier.fillMaxSize(),
)
```

Presets — predefined cubemaps

- `SPACE`: Dark blue background; stars stay white. Space color changes background color.
- `STARS` (default): Black background; space color changes stars color.
- `MILKYWAY`: Black half‑transparent background with standard milky way and stars; space color tints stars and milky way.
- `MILKYWAY_SUBTLE`: Subtle milky way, fewer stars; space color tints stars and milky way.
- `MILKYWAY_BRIGHT`: Bright milky way, more stars; space color tints stars and milky way.
- `MILKYWAY_COLORED`: Full image with natural colors; space color has no effect.

```kotlin
val options = MTMapOptions(
    projection = MTProjectionType.GLOBE,
    space = MTSpaceOption.Config(MTSpace(preset = MTSpacePreset.SPACE)),
)
```

Custom cubemap — provide all faces

```kotlin
val faces = MTSpaceFaces(
    pX = "https://example.com/space/px.png",
    nX = "https://example.com/space/nx.png",
    pY = "https://example.com/space/py.png",
    nY = "https://example.com/space/ny.png",
    pZ = "https://example.com/space/pz.png",
    nZ = "https://example.com/space/nz.png",
)

val options = MTMapOptions(
    projection = MTProjectionType.GLOBE,
    space = MTSpaceOption.Config(MTSpace(faces = faces)),
)
```

Cubemap by path — files named px, nx, py, ny, pz, nz with the given format

```kotlin
val path = MTSpacePath(
    baseUrl = "https://example.com/spacebox/transparent",
    format = "png", // defaults to PNG if omitted
)

val options = MTMapOptions(
    projection = MTProjectionType.GLOBE,
    space = MTSpaceOption.Config(MTSpace(path = path)),
)
```

Dynamic updates — change space at runtime

```kotlin
// Call after the map is initialized (e.g., in MTMapViewDelegate.onMapViewInitialized)
controller.style?.setSpace(
    MTSpace(
        color = Color.Red.toArgb(),
        path = MTSpacePath(baseUrl = "https://example.com/spacebox/transparent"),
    ),
)
```

Note: When calling `setSpace`, any field not explicitly provided (e.g., color, faces, path, or preset) keeps its previous value.

### Halo

The halo option adds a gradient-based atmospheric glow around the globe, simulating the visual effect of Earth's atmosphere when viewed from space.

- Prerequisite: use globe projection. Set `projection = MTProjectionType.GLOBE` in `MTMapOptions`.

Enable during map initialization

```kotlin
val options = MTMapOptions(
    projection = MTProjectionType.GLOBE,
    halo = MTHaloOption.Enabled,
)
```

Custom gradient — scale and stops

```kotlin
val options = MTMapOptions(center = null, zoom = null, projection = MTProjectionType.GLOBE).apply {
    setHalo(
        MTHalo(
            scale = 1.5, // Controls the halo size
            stops = listOf(
                MTHaloStop(position = 0.2, color = "#00000000"),
                MTHaloStop(position = 0.2, color = "#FF0000"),
                MTHaloStop(position = 0.4, color = "#FF0000"),
                MTHaloStop(position = 0.4, color = "#00000000"),
                MTHaloStop(position = 0.6, color = "#00000000"),
                MTHaloStop(position = 0.6, color = "#FF0000"),
                MTHaloStop(position = 0.8, color = "#FF0000"),
                MTHaloStop(position = 0.8, color = "#00000000"),
                MTHaloStop(position = 1.0, color = "#00000000"),
            ),
        ),
    )
}
```

Dynamic updates — change halo at runtime

```kotlin
// Call after the map is initialized (e.g., in MTMapViewDelegate.onMapViewInitialized)
controller.style?.setHalo(
    MTHalo(
        scale = 2.0,
        stops = listOf(
            MTHaloStop(position = 0.0, color = "#87CEFA"),
            MTHaloStop(position = 0.5, color = "#0000FABF"),
            MTHaloStop(position = 0.75, color = "#FF000000"),
        ),
    ),
)
```

Disable animations — halo and space

```kotlin
// Call after initialization
controller.style?.disableHaloAnimations()
controller.style?.disableSpaceAnimations()
```

<br>

## Migration Guide

- [How To Migrate/Switch From Mapbox Android To MapTiler SDK Kotlin](https://docs.maptiler.com/mobile-sdk/android/examples/switch-from-mapbox/)
- [How To Migrate/Switch From MapLibre Native Android to MapTiler SDK Kotlin](https://docs.maptiler.com/mobile-sdk/android/examples/switch-from-maplibre/)

<br>

## 💬 Support

- 📚 [Documentation](https://docs.maptiler.com/mobile-sdk/android/) - Comprehensive guides and API reference
- ✉️ [Contact us](https://maptiler.com/contact) - Get in touch or submit a request
- 🐦 [Twitter/X](https://twitter.com/maptiler) - Follow us for updates

<br>

---

<br>

## 🤝 Contributing

We love contributions from the community! Whether it's bug reports, feature requests, or pull requests, all contributions are welcome:

- Fork the repository and create your branch from `main`
- If you've added code, add tests that cover your changes
- Ensure your code follows our style guidelines
- Give your pull request a clear, descriptive summary
- Open a Pull Request with a comprehensive description
- Read the [CONTRIBUTING](./CONTRIBUTING.md) file

<br>

## 📄 License

MapTiler SDK Kotlin is released under the BSD 3-Clause license – see the [LICENSE](./LICENSE) file for details.

<br>

## 🙏 Acknowledgements

### Features

- [x] Map interaction
- [x] Pre-made map styles
- [x] VectorTile and GeoJSON sources
- [x] Fill, Line and Symbol layers
- [x] Custom Annotation Views
- [x] Location tracking
- [x] Globe and 3D Terrain

<br>

<p align="center" style="margin-top:20px;margin-bottom:20px;"> <a href="https://cloud.maptiler.com/account/keys/" style="display:inline-block;padding:12px 32px;background:#F2F6FF;color:#000;font-weight:bold;border-radius:6px;text-decoration:none;"> Get Your API Key <sup style="background-color:#0000ff;color:#fff;padding:2px 6px;font-size:12px;border-radius:3px;">FREE</sup><br /> <span style="font-size:90%;font-weight:400;">Start building with 100,000 free map loads per month ・ No credit card required.</span> </a> </p>

<br>

<p align="center"> 💜 Made with love by the <a href="https://www.maptiler.com/">MapTiler</a> team <br />
<p align="center">
  <a href="https://www.maptiler.com/">Website</a> •
  <a href="https://docs.maptiler.com/mobile-sdk/android/">Documentation</a> •
  <a href="https://github.com/maptiler/maptiler-sdk-kotlin">GitHub</a>
</p>
