# trashnetwork-recycle-android
The android client of TrashNetwork project, used for recycling.

## Build Properties

Before building, you should set the following properties in `gradle.properties`:

- `TN_HTTP_API_BASE_URL_V1`: The base URL of HTTP API(v1 version) that client will use to interact with web server. The web server that this URL points to should running project [trashnetwork-web](https://github.com/TrashNetwork/trashnetwork-web).
- `TN_KEYSTORE_FILE`: The keystore file used to generate signed APK.
- `TN_KEYSTORE_PASSWORD`: Password of `TN_KEYSTORE_FILE`.
- `TN_KEYSTORE_ALIAS_NAME`: An alias that exist in `TN_KEYSTORE_FILE`.
- `TN_KEYSTORE_ALIAS_PASSWORD`: Password of `TN_KEYSTORE_ALIAS_NAME`.
- `BAIDU_LBS_API_KEY`: The API key of Baidu Map, for more details about Baidu Map SDK, visit [http://lbsyun.baidu.com](http://lbsyun.baidu.com)