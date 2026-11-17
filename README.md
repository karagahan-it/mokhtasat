# Mokhtasat Technologist — Android 0.6.0

Kotlin + Jetpack Compose + MapLibre Native Android.

## Map stack
- Map renderer: MapLibre Native Android 13.6.1
- Vector style: OpenFreeMap Liberty, based on OpenStreetMap data
- No Neshan SDK, API key, Maven repository or licence dependency.
- Map attribution controls remain enabled.

## Features
- Embedded full-screen vector map
- Separate latitude and longitude fields
- GPS coordinate validation and camera navigation
- Optional altitude field
- Downloadable offline map region around the selected coordinate (approximately 10 km square, zoom 10–15)

## Offline usage
Select a coordinate, tap the offline download action, and keep the device online until MapLibre finishes downloading the region. The SDK stores offline region resources in its local database. Availability depends on the style provider serving tiles, glyphs and sprites and permitting offline use.

The public OpenFreeMap style endpoint is used for this prototype, not as a guaranteed production service. For large organizational deployments, use a dedicated or self-hosted vector tile service and verify provider terms and capacity. Do not bulk-download the public OpenStreetMap standard tile service.

## Build
GitHub Actions builds a debug APK and uploads it as an artifact.

## Attribution
The map data is OpenStreetMap-derived. Keep visible attribution to OpenStreetMap contributors and comply with the active style and tile provider terms.

## Remaining work
- GPS current-location permission and marker
- Offline region progress, pause/resume/list/delete management
- UTM/MGRS conversion and input
- Organization login and backend
- Production tile hosting
