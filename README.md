# مختصات‌یاب تکنولوژیست
Kotlin + Jetpack Compose Android application.

## MVP
- Separate latitude and longitude fields
- Optional altitude
- GPS validation
- DMS display
- Open target in installed map app
- Light/dark theme

## Build
GitHub Actions workflow **Build Android APK** builds a debug APK and uploads it as an artifact.
Open Actions, select the latest workflow run, then download `technologist-mokhtasat-debug-apk`.

Neshan SDK integration, UTM/MGRS input, backend authentication and multi-tenant organization management are planned follow-up milestones. Never commit API keys.
