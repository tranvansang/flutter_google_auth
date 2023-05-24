# google_auth

Simple google authentication plugin for flutter.

# Configuration

## Android
No configuration required.

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
- To authenticate user: `GoogleAuth().signIn()`.
  - For Android, use the client id setup for **Web** app. Note: *if you use the client id setup for Android app, you will get an error saying that the developer console is not setup correctly*.

This function returns a `Future` object resolving an object of the following format.
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
