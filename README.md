# HealthConnectHaMqtt Android 16

MVP Android app that reads today's step count from Health Connect and publishes it to Home Assistant over MQTT.

## Important Android 14/15/16 notes

This version includes the Health Connect permissions-rationale activity in `AndroidManifest.xml`:

- `androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE`
- `android.intent.action.VIEW_PERMISSION_USAGE`
- `android.intent.category.HEALTH_PERMISSIONS`

Without these declarations Android 14+ may not list the app in Health Connect permissions and the permission button may appear to do nothing.

## Build

Upload the contents of this folder to the root of a GitHub repository, then run:

Actions -> Build debug APK -> Run workflow

Download the artifact named:

`HealthConnectHaMqtt-android16-debug-apk`

Install `app-debug.apk` on the phone.

## After installing a new debug APK

If you previously installed an older APK, uninstall it first:

Settings -> Apps -> Health MQTT -> Uninstall

Then install the new APK. This helps Android refresh Health Connect permission metadata.

## Usage

1. Open the app.
2. Enter MQTT broker URI, for example `tcp://192.168.1.10:1883`.
3. Enter MQTT user/password if needed.
4. Press Save.
5. Press Grant permission.
6. Allow Steps read access in Health Connect.
7. Press Sync now.
8. Press Schedule for periodic sync.

## Samsung Health

Samsung Health must write steps to Health Connect, otherwise this app can read Health Connect successfully but the value may be 0.

On Samsung phone, search Settings for Health Connect, then check app permissions/data access for Samsung Health and Health MQTT.
