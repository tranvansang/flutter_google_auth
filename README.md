# google_auth

Simple google authentication plugin for flutter.

# Configuration

## Android
You must follow exactly the steps described here
- For local development:
  - In Google Cloud Console, create **Android** key, set SHA1 hash to `debug.keystore`'s SHA1 hash, and package name to the local app's package name.
  - In Google Cloud Console, create **Web** key, copy the client ID to latterly pass it to `GoogleAuth().signIn(clientId)`.
- For production:
  - In Google Cloud Console, create **Android** key, set SHA1 hash to the signing key's SHA1 hash, and package name to the production app's package name.
  Note that the production signing key is usually generated by Google Play Console, and you should take it from Play Console > Setup > App Integrity > App signing > App signing key certificate.
  - In Google Cloud Console, create **Web** key, copy the client ID to latterly pass it to `GoogleAuth().signIn(clientId)`.

The Android key setup in Google Cloud Console is not used anywhere, but it is required (with appropriate SHA1 hash and package name setup) to make the authentication work.

## iOS

Official docs: https://developers.google.com/identity/sign-in/ios/start-integrating

In `info.plist` add the following:

```xml
<key>GIDClientID</key>
<string>$(DEFINE_GOOGLE_CLIENT_ID_IOS)</string>
<key>CFBundleURLTypes</key>
<array>
    <dict>
        <key>CFBundleURLSchemes</key>
        <array>
            <string>$(DEFINE_GOOGLE_URL_SCHEME_IOS)</string>
        </array>
    </dict>
</array>
```

with `$(DEFINE_GOOGLE_CLIENT_ID_IOS)`, `$(DEFINE_GOOGLE_URL_SCHEME_IOS)` being the client id (iOS) and iOS URL scheme of your app, respectively.

# Usage

- Add the plugin to `pubspec.yaml`: `flutter pub add google_auth`.
- Import the plugin in your dart code: `import 'package:google_auth/google_auth.dart';`.
- To authenticate user: `GoogleAuth().signIn(clientId)`, where `clientId` is:
  - For Android, the client id setup for **Web** app. Note: *if you use the client id setup for Android app, you will get an error saying that the developer console is not setup correctly*.
  - For iOS, this parameter is ignored, you can pass in any string.

This function returns a `Future` object resolving an object of the following format.
- iOS:
```
{
  "idToken"?: String
  "idTokenExpire"?: int
  "accessToken": String
  "accessTokenExpire"?: int
  "userID"?: String
  "refreshToken": String
  "refreshTokenExpire"?: int
  "email"?: String
  "name"?: String
  "givenName"?: String
  "familyName"?: String
  "image"?: String
}
```
- Android:
```
{
  "idToken": String
}
```

<details>
<summary>API reference for iOS</summary>
- Token object: https://developers.google.com/identity/sign-in/ios/reference/Classes/GIDToken
- Profile object: https://developers.google.com/identity/sign-in/ios/reference/Classes/GIDProfileData.html
</details>

- To logout user: `GoogleAuth().signOut(): Future<void>`.
- Possible error code: check the error code with
```flutter
try {
  return {'idToken': await GoogleAuth().signIn(clientId)};
} on PlatformException catch (e) {
  if (e.code == 'API_CANCELED' || e.code == 'GSI_SIGN_IN_CANCELLED') {
    throw AppError('User has cancelled');
  }
  rethrow;
}
```
  - For iOS: see https://github.com/tranvansang/flutter_google_auth/tree/master/ios/Classes/GoogleAuthDelegate.swift#L4
  - For Android: see https://github.com/tranvansang/flutter_google_auth/tree/master/android/src/main/kotlin/me/transang/plugins/google_auth/GoogleAuthDelegate.kt#L20

# SDK versions
- Android
```
    implementation 'com.google.android.gms:play-services-auth:20.5.0'
```

- iOS
```
  s.dependency 'GoogleSignIn', '~> 7.0'
```
