# Changelog
All notable changes to this project will be documented in this file.

## [1.2.1](https://github.com/maptiler/maptiler-sdk-kotlin/releases/tag/1.2.1)
Released on 2026-02-06.
### Fixed
- Performance improvements on low/mid devices (leaner defaults, reduced event wiring overhead, optional throttling for high‑frequency events).

### Added
- MTDeviceProfile: device‑aware lean defaults applied only when options are unset.
- MTPerformancePresets: ready‑to‑use `leanPerformance`, `balancedPerformance`, `highFidelity` helpers.
- Event levels: `MTEventLevel` with `ESSENTIAL`, `CAMERA_ONLY` (default), `ALL`, `OFF`, plus `highFrequencyEventThrottleMs` for throttling.
- Docs: README updated to explain event levels and when to use `ALL` vs `CAMERA_ONLY`.

## [1.2.0](https://github.com/maptiler/maptiler-sdk-swift/releases/tag/1.2.0)
Released on 2025-12-12.
### Added
- Raster source: Custom raster and raster DEM data can now be added to the map.
- Raster layer: New raster layer allows visualization of custom raster data.
- MTCircle layer: Added new type of layer for visualizing data as circles.
- Clustering filters: MTCircle and MTSymbol layers now support clustering filters and expressions.

## [1.1.1](https://github.com/maptiler/maptiler-sdk-kotlin/releases/tag/1.1.1)
Released on 2025-11-10.
### Fixed
- GeoJSON parsing now works with json strings as well as remote URL.

## [1.1.0](https://github.com/maptiler/maptiler-sdk-kotlin/releases/tag/1.1.0)
Released on 2025-10-20.
### Added
- Space: The space option allows customizing the background environment of the globe, simulating deep space or skybox effects.
- Halo: The halo option adds a gradient-based atmospheric glow around the globe, simulating the visual effect of Earth's atmosphere when viewed from space.

## [1.0.0](https://github.com/maptiler/maptiler-sdk-kotlin/releases/tag/1.0.0)
Released on 2025-10-16.
### Added
- Initial public release
